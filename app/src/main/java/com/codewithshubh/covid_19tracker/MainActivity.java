package com.codewithshubh.covid_19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    private String confirmed, active, date, recovered, deaths, newConfirmed, newDeaths, newRecovered, totalTests, totalTestsCopy, oldTests;
    private int testsInt;
    public static int confirmation = 0;
    public static boolean isRefreshed;
    private long backPressTime;
    private Toast backToast;

    private TextView textView_confirmed, textView_confirmed_new, textView_active,
            textView_active_new, textView_recovered, textView_recovered_new, textView_death,
            textView_death_new, textView_tests, textView_date, textView_tests_new, textview_time;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String version;
    private String appURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckForUpdate();

        //setting navigation bar color
        //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        
        //setting toolbar text
        Objects.requireNonNull(getSupportActionBar()).setTitle("Covid-19 Tracker (India)");
        
        //Initialize views
        Init();
        
        //show progress dialog on activity main startup
        ShowProgressDialog();
        
        //Fetch data from API
        FetchData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshed = true;
                FetchData();
                swipeRefreshLayout.setRefreshing(false);
                //Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FetchData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiUrl = "https://api.covid19india.org/data.json";
        final PieChart mPieChart = findViewById(R.id.activity_main_piechart);
        mPieChart.clearChart();

        //Fetching the API from URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Since the objects of JSON are in an Array we need to define the array from which we can fetch objects
                    JSONArray jsonArray = response.getJSONArray("statewise");
                    JSONObject statewise = jsonArray.getJSONObject(0);


                    if (isRefreshed) {
                        //Inserting the fetched data into variables
                        confirmed = statewise.getString("confirmed");
                        active = statewise.getString("active");
                        date = statewise.getString("lastupdatedtime");
                        recovered = statewise.getString("recovered");
                        deaths = statewise.getString("deaths");
                        newConfirmed = statewise.getString("deltaconfirmed");
                        newDeaths = statewise.getString("deltadeaths");
                        newRecovered = statewise.getString("deltarecovered");
                        Runnable progressRunnable = new Runnable() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                progressDialog.cancel();

                                String activeCopy = active;
                                String deathsCopy = deaths;
                                String recoveredCopy = recovered;
                                String confirmedNewCopy = newConfirmed;


                                int confirmedInt = Integer.parseInt(confirmed);
                                confirmed = NumberFormat.getInstance().format(confirmedInt);
                                textView_confirmed.setText(confirmed);

                                int newConfirmedInt = Integer.parseInt(newConfirmed);
                                newConfirmed = NumberFormat.getInstance().format(newConfirmedInt);
                                textView_confirmed_new.setText("+" + newConfirmed);

                                int activeInt = Integer.parseInt(active);
                                active = NumberFormat.getInstance().format(activeInt);
                                textView_active.setText(active);

                                //We need to calculate new active cases since it doesn't exist in API
                                int newActive = (Integer.parseInt(confirmedNewCopy)) - ((Integer.parseInt(newRecovered)) + Integer.parseInt(newDeaths));
                                textView_active_new.setText("+" + NumberFormat.getInstance().format(newActive));

                                int recoveredInt = Integer.parseInt(recovered);
                                recovered = NumberFormat.getInstance().format(recoveredInt);
                                textView_recovered.setText(recovered);

                                int recoveredNewInt = Integer.parseInt(newRecovered);
                                newRecovered = NumberFormat.getInstance().format(recoveredNewInt);
                                textView_recovered_new.setText("+" + newRecovered);

                                int deathsInt = Integer.parseInt(deaths);
                                deaths = NumberFormat.getInstance().format(deathsInt);
                                textView_death.setText(deaths);

                                int deathsNewInt = Integer.parseInt(newDeaths);
                                newDeaths = NumberFormat.getInstance().format(deathsNewInt);
                                textView_death_new.setText("+" + newDeaths);

                                String dateFormat = formatDate(date, 1);
                                textView_date.setText(dateFormat);

                                String timeFormat = formatDate(date, 2);
                                textview_time.setText(timeFormat);

                                mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(activeCopy), Color.parseColor("#007afe")));
                                mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
                                mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deathsCopy), Color.parseColor("#F6404F")));

                                mPieChart.startAnimation();
                                fetchTests();
                            }
                        };
                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 0);
                    } else {
                        //Inserting the fetched data into variables
                        confirmed = statewise.getString("confirmed");
                        active = statewise.getString("active");
                        date = statewise.getString("lastupdatedtime");
                        recovered = statewise.getString("recovered");
                        deaths = statewise.getString("deaths");
                        newConfirmed = statewise.getString("deltaconfirmed");
                        newDeaths = statewise.getString("deltadeaths");
                        newRecovered = statewise.getString("deltarecovered");
                        if (!date.isEmpty()) {
                            Runnable progressRunnable = new Runnable() {

                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    progressDialog.cancel();

                                    String activeCopy = active;
                                    String deathsCopy = deaths;
                                    String recoveredCopy = recovered;
                                    String confirmedNewCopy = newConfirmed;

                                    int confirmedInt = Integer.parseInt(confirmed);
                                    confirmed = NumberFormat.getInstance().format(confirmedInt);
                                    textView_confirmed.setText(confirmed);

                                    int newConfirmedInt = Integer.parseInt(newConfirmed);
                                    newConfirmed = NumberFormat.getInstance().format(newConfirmedInt);
                                    textView_confirmed_new.setText("+" + newConfirmed);

                                    int activeInt = Integer.parseInt(active);
                                    active = NumberFormat.getInstance().format(activeInt);
                                    textView_active.setText(active);

                                    //We need to calculate new active cases since it doesn't exist in API
                                    int newActive = (Integer.parseInt(confirmedNewCopy)) - ((Integer.parseInt(newRecovered)) + Integer.parseInt(newDeaths));
                                    textView_active_new.setText("+" + NumberFormat.getInstance().format(newActive));

                                    int recoveredInt = Integer.parseInt(recovered);
                                    recovered = NumberFormat.getInstance().format(recoveredInt);
                                    textView_recovered.setText(recovered);

                                    int recoveredNewInt = Integer.parseInt(newRecovered);
                                    newRecovered = NumberFormat.getInstance().format(recoveredNewInt);
                                    textView_recovered_new.setText("+" + newRecovered);

                                    int deathsInt = Integer.parseInt(deaths);
                                    deaths = NumberFormat.getInstance().format(deathsInt);
                                    textView_death.setText(deaths);

                                    int deathsNewInt = Integer.parseInt(newDeaths);
                                    newDeaths = NumberFormat.getInstance().format(deathsNewInt);
                                    textView_death_new.setText("+" + newDeaths);


                                    String dateFormat = formatDate(date, 1);
                                    textView_date.setText(dateFormat);

                                    String timeFormat = formatDate(date, 2);
                                    textview_time.setText(timeFormat);

                                    mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(activeCopy), Color.parseColor("#007afe")));
                                    mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
                                    mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deathsCopy), Color.parseColor("#F6404F")));

                                    mPieChart.startAnimation();
                                    fetchTests();
                                }
                            };
                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, 1000);
                            confirmation = 1;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_info) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (backPressTime + 1500 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressTime = System.currentTimeMillis();
    }

    private void fetchTests() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiUrl = "https://api.covid19india.org/data.json";
        JsonObjectRequest jsonObjectRequestTests = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("tested");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject statewise = jsonArray.getJSONObject(i);
                        totalTests = statewise.getString("totalsamplestested");
                    }

                    for (int i = 0; i < jsonArray.length() - 1; i++) {
                        JSONObject statewise = jsonArray.getJSONObject(i);
                        oldTests = statewise.getString("totalsamplestested");
                    }
                    if (totalTests.isEmpty()) {
                        for (int i = 0; i < jsonArray.length() - 1; i++) {
                            JSONObject statewise = jsonArray.getJSONObject(i);
                            totalTests = statewise.getString("totalsamplestested");
                        }
                        totalTestsCopy = totalTests;
                        testsInt = Integer.parseInt(totalTests);
                        totalTests = NumberFormat.getInstance().format(testsInt);
                        textView_tests.setText(totalTests);


                        for (int i = 0; i < jsonArray.length() - 2; i++) {
                            JSONObject statewise = jsonArray.getJSONObject(i);
                            oldTests = statewise.getString("totalsamplestested");
                        }
                        int testsNew = (Integer.parseInt(totalTestsCopy)) - (Integer.parseInt(oldTests));
                        textView_tests_new.setText("[+" + NumberFormat.getInstance().format(testsNew) + "]");

                    } else {
                        totalTestsCopy = totalTests;
                        testsInt = Integer.parseInt(totalTests);
                        totalTests = NumberFormat.getInstance().format(testsInt);
                        textView_tests.setText(totalTests);

                        if (oldTests.isEmpty()) {
                            for (int i = 0; i < jsonArray.length() - 2; i++) {
                                JSONObject statewise = jsonArray.getJSONObject(i);
                                oldTests = statewise.getString("totalsamplestested");
                            }
                        }
                        long testsNew = (Integer.parseInt(totalTestsCopy)) - (Integer.parseInt(oldTests));
                        textView_tests_new.setText("+" + NumberFormat.getInstance().format(testsNew));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequestTests);
    }

    public String formatDate(String date, int testCase) {
        Date mDate = null;
        String dateFormat;
        try {
            mDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(date);
            if (testCase == 0) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(mDate);
                return dateFormat;
            } else if (testCase == 1) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US).format(mDate);
                return dateFormat;
            } else if (testCase == 2) {
                dateFormat = new SimpleDateFormat("hh:mm a", Locale.US).format(mDate);
                return dateFormat;
            } else {
                Log.d("error", "Wrong input! Choose from 0 to 2");
                return "Error";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public void openStatewise(View view) {
        Intent intent = new Intent(this, StateWiseDataActivity.class);
        startActivity(intent);
    }

    public void openWorldData(View view) {
        Intent intent = new Intent(this, WorldDataActivity.class);
        startActivity(intent);
    }

    private void ShowProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                if (confirmation != 1) {
                    progressDialog.cancel();
                    Toast.makeText(MainActivity.this, "Internet slow/not available", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 8000);
    }

    private void Init() {
        textView_confirmed = findViewById(R.id.activity_main_confirmed_textView);
        textView_confirmed_new = findViewById(R.id.activity_main_confirmed_new_textView);
        textView_active = findViewById(R.id.activity_main_active_textView);
        textView_active_new = findViewById(R.id.activity_main_active_new_textView);
        textView_recovered = findViewById(R.id.activity_main_recovered_textView);
        textView_recovered_new = findViewById(R.id.activity_main_recovered_new_textView);
        textView_death = findViewById(R.id.activity_main_death_textView);
        textView_death_new = findViewById(R.id.activity_main_death_new_textView);
        textView_tests = findViewById(R.id.activity_main_tests_textView);
        textView_date = findViewById(R.id.activity_main_date_textView);
        textView_tests_new = findViewById(R.id.activity_main_tests_new_textView);
        swipeRefreshLayout = findViewById(R.id.activity_main_refreshLayout);
        textview_time = findViewById(R.id.activity_main_time_textView);
    }

    private void CheckForUpdate() {
        try {
            version = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.d("Version: ", version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Version").child("versionName");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String versionName = (String) dataSnapshot.getValue();

                if(!versionName.equals(version)) {
                    //Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("New Version Available!")
                            .setMessage("Please update our app to the latest version for continuous use.")
                            .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Version").child("appURL");
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            appURL = dataSnapshot.getValue().toString();
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURL)));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();

                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);

                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}