package com.example.covid_19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Objects;

public class WorldDataActivity extends AppCompatActivity {
    private String totalCases;
    private String newCases;
    private String totalActive;
    private String totalRecovered;
    private String newRecovered;
    private String totalDeceased;
    private String newDeceased;
    private String tests;
    private String version, appURL;

    TextView textView_confirmed, textView_confirmed_new, textView_totalActive, textView_totalRecovered, textView_totalRecovered_new, textView_death, textView_death_new, textView_tests;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    public static int confirmation = 0;
    public static boolean isRefreshed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_data);

        textView_confirmed = findViewById(R.id.activity_world_data_confirmed_textView);
        textView_confirmed_new = findViewById(R.id.activity_world_data_confirmed_new_textView);
        textView_totalActive = findViewById(R.id.activity_world_data_active_textView);
        textView_totalRecovered = findViewById(R.id.activity_world_data_recovered_textView);
        textView_totalRecovered_new = findViewById(R.id.activity_world_data_recovered_new_textView);
        textView_death = findViewById(R.id.activity_world_data_death_textView);
        textView_death_new = findViewById(R.id.activity_world_data_death_new_textView);
        textView_tests = findViewById(R.id.activity_world_data_tests_textView);
        swipeRefreshLayout = findViewById(R.id.activity_world_data_refresh);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Covid-19 Tracker (World)");

        ShowProgressDialog();
        FetchData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshed = true;
                FetchData();
                swipeRefreshLayout.setRefreshing(false);
                //Toast.makeText(WorldDataActivity.this, "Data Refreshed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void FetchData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiUrl = "https://corona.lmao.ninja/v2/all";
        final PieChart mPieChart = findViewById(R.id.activity_world_data_piechart);
        mPieChart.clearChart();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (isRefreshed){
                        totalCases = response.getString("cases");
                        newCases = response.getString("todayCases");
                        totalActive = response.getString("active");
                        totalRecovered = response.getString("recovered");
                        newRecovered = response.getString("todayRecovered");
                        totalDeceased = response.getString("deaths");
                        newDeceased = response.getString("todayDeaths");
                        tests = response.getString("tests");
                        textView_confirmed.setText(totalCases);

                        Runnable progressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                String totalActiveCopy = totalActive;
                                String totalDeceasedCopy = totalDeceased;
                                String totalRecoveredCopy = totalRecovered;

                                int confirmedInt = Integer.parseInt(totalCases);
                                totalCases = NumberFormat.getInstance().format(confirmedInt);
                                textView_confirmed.setText(totalCases);

                                int newCasesInt = Integer.parseInt(newCases);
                                newCases = NumberFormat.getInstance().format(newCasesInt);
                                textView_confirmed_new.setText("+" + newCases);

                                int totalActiveInt = Integer.parseInt(totalActive);
                                totalActive = NumberFormat.getInstance().format(totalActiveInt);
                                textView_totalActive.setText(totalActive);

                                int totalRecoveredInt = Integer.parseInt(totalRecovered);
                                totalRecovered = NumberFormat.getInstance().format(totalRecoveredInt);
                                textView_totalRecovered.setText(totalRecovered);

                                int totalRecoveredNewInt = Integer.parseInt(newRecovered);
                                newRecovered = NumberFormat.getInstance().format(totalRecoveredNewInt);
                                textView_totalRecovered_new.setText("+" + newRecovered);

                                int totalDeceasedInt = Integer.parseInt(totalDeceased);
                                totalDeceased = NumberFormat.getInstance().format(totalDeceasedInt);
                                textView_death.setText(totalDeceased);

                                int totalDeceasedNewInt = Integer.parseInt(newDeceased);
                                newDeceased = NumberFormat.getInstance().format(totalDeceasedNewInt);
                                textView_death_new.setText("+" + newDeceased);

                                int testsInt = Integer.parseInt(tests);
                                tests = NumberFormat.getInstance().format(testsInt);
                                textView_tests.setText(tests);

                                mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(totalActiveCopy), Color.parseColor("#007afe")));
                                mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(totalRecoveredCopy), Color.parseColor("#08a045")));
                                mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(totalDeceasedCopy), Color.parseColor("#F6404F")));

                                mPieChart.startAnimation();
                            }
                        };
                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 0);
                    } else {
                        totalCases = response.getString("cases");
                        newCases = response.getString("todayCases");
                        totalActive = response.getString("active");
                        totalRecovered = response.getString("recovered");
                        newRecovered = response.getString("todayRecovered");
                        totalDeceased = response.getString("deaths");
                        newDeceased = response.getString("todayDeaths");
                        tests = response.getString("tests");
                        textView_confirmed.setText(totalCases);

                        if (!tests.isEmpty()){
                            Runnable progressRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                    String totalActiveCopy = totalActive;
                                    String totalDeceasedCopy = totalDeceased;
                                    String totalRecoveredCopy = totalRecovered;

                                    int confirmedInt = Integer.parseInt(totalCases);
                                    totalCases = NumberFormat.getInstance().format(confirmedInt);
                                    textView_confirmed.setText(totalCases);

                                    int newCasesInt = Integer.parseInt(newCases);
                                    newCases = NumberFormat.getInstance().format(newCasesInt);
                                    textView_confirmed_new.setText("+" + newCases);

                                    int totalActiveInt = Integer.parseInt(totalActive);
                                    totalActive = NumberFormat.getInstance().format(totalActiveInt);
                                    textView_totalActive.setText(totalActive);

                                    int totalRecoveredInt = Integer.parseInt(totalRecovered);
                                    totalRecovered = NumberFormat.getInstance().format(totalRecoveredInt);
                                    textView_totalRecovered.setText(totalRecovered);

                                    int totalRecoveredNewInt = Integer.parseInt(newRecovered);
                                    newRecovered = NumberFormat.getInstance().format(totalRecoveredNewInt);
                                    textView_totalRecovered_new.setText("+" + newRecovered);

                                    int totalDeceasedInt = Integer.parseInt(totalDeceased);
                                    totalDeceased = NumberFormat.getInstance().format(totalDeceasedInt);
                                    textView_death.setText(totalDeceased);

                                    int totalDeceasedNewInt = Integer.parseInt(newDeceased);
                                    newDeceased = NumberFormat.getInstance().format(totalDeceasedNewInt);
                                    textView_death_new.setText("+" + newDeceased);

                                    int testsInt = Integer.parseInt(tests);
                                    tests = NumberFormat.getInstance().format(testsInt);
                                    textView_tests.setText(tests);

                                    mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(totalActiveCopy), Color.parseColor("#007afe")));
                                    mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(totalRecoveredCopy), Color.parseColor("#08a045")));
                                    mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(totalDeceasedCopy), Color.parseColor("#F6404F")));

                                    mPieChart.startAnimation();
                                }
                            };
                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, 0);
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

    public void ShowProgressDialog() {
        progressDialog = new ProgressDialog(WorldDataActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (confirmation != 1) {
                    progressDialog.cancel();
                    //Toast.makeText(WorldDataActivity.this, "Internet slow/not available", Toast.LENGTH_SHORT).show();
                }
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 8000);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void openCountryData(View view) {
        Intent intent = new Intent(this, CountrywiseDataActivity.class);
        startActivity(intent);
    }
}