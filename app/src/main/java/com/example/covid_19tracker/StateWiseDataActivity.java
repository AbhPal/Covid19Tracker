package com.example.covid_19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid_19tracker.Adapters.StateWiseAdapter;
import com.example.covid_19tracker.Models.StateWiseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

public class StateWiseDataActivity extends AppCompatActivity implements StateWiseAdapter.OnItemClickListner {

    public static final String STATE_NAME = "stateName";
    public static final String STATE_CONFIRMED = "stateConfirmed";
    public static final String STATE_ACTIVE = "stateActive";
    public static final String STATE_DECEASED = "stateDeaceased";
    public static final String STATE_NEW_CONFIRMED = "stateNewConfirmed";
    public static final String STATE_NEW_RECOVERED = "stateNewRecovered";
    public static final String STATE_NEW_DECEASED = "stateNewDeceased";
    public static final String STATE_LAST_UPDATE = "stateLastUpdate";
    public static final String STATE_RECOVERED = "stateRecovered";

    private RecyclerView recyclerView;
    private StateWiseAdapter statewiseAdapter;
    private ArrayList<StateWiseModel> statewiseModelArrayList;
    private RequestQueue requestQueue;
    ProgressDialog progressDialog;
    public static int confirmation = 0;
    public static String testValue;
    public static boolean isRefreshed;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText search;
    String stateLastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select State");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_state_wise_data);

        recyclerView = findViewById(R.id.activity_state_wise_recyclerview);
        swipeRefreshLayout = findViewById(R.id.activity_state_wise_refresh);
        search = findViewById(R.id.activity_state_wise_search_editText);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statewiseModelArrayList = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(this);
        ShowProgressDialog();

        ExtractData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshed = true;
                ExtractData();
                swipeRefreshLayout.setRefreshing(false);
                //Toast.makeText(StateWiseDataActivity.this, "Data Refreshed!", Toast.LENGTH_SHORT).show();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Filter(s.toString());
            }
        });

    }

    private void Filter(String text) {
        ArrayList<StateWiseModel> filteredList = new ArrayList<>();
        for (StateWiseModel item : statewiseModelArrayList) {
            if (item.getState().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        statewiseAdapter.filterList(filteredList);
    }

    private void ExtractData() {
        String dataURL = "https://api.covid19india.org/data.json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, dataURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("statewise");
                    statewiseModelArrayList.clear();
                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONObject statewise = jsonArray.getJSONObject(i);

                        String stateName = statewise.getString("state");
                        String stateConfirmed = statewise.getString("confirmed");
                        String stateActive = statewise.getString("active");
                        String stateDeceased = statewise.getString("deaths");
                        String stateRecovered = statewise.getString("recovered");
                        String stateNewConfirmed = statewise.getString("deltaconfirmed");
                        String stateNewRecovered = statewise.getString("deltarecovered");
                        String stateNewDeceased = statewise.getString("deltadeaths");
                        stateLastUpdate = statewise.getString("lastupdatedtime");
                        testValue = stateLastUpdate;

                        int stateConfirmedInt = Integer.parseInt(stateConfirmed);
                        stateConfirmed = NumberFormat.getInstance().format(stateConfirmedInt);

                        int stateNewConfirmedInt = Integer.parseInt(stateNewConfirmed);
                        stateNewConfirmed = NumberFormat.getInstance().format(stateNewConfirmedInt);

                        int stateNewRecoveredInt = Integer.parseInt(stateNewRecovered);
                        stateNewRecovered = NumberFormat.getInstance().format(stateNewRecoveredInt);

                        int stateNewDeceasedInt = Integer.parseInt(stateNewDeceased);
                        stateNewDeceased = NumberFormat.getInstance().format(stateNewDeceasedInt);


                        statewiseModelArrayList.add(new StateWiseModel(stateName, stateConfirmed, stateActive, stateDeceased, stateNewConfirmed, stateNewRecovered, stateNewDeceased, stateLastUpdate, stateRecovered));
                    }

                    if (!testValue.isEmpty()) {
                        Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                progressDialog.cancel();
                                statewiseAdapter = new StateWiseAdapter(StateWiseDataActivity.this, statewiseModelArrayList);
                                recyclerView.setAdapter(statewiseAdapter);
                                statewiseAdapter.setOnItemClickListner(StateWiseDataActivity.this);
                            }
                        };
                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 500);
                        confirmation = 1;
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
        progressDialog = new ProgressDialog(StateWiseDataActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                if (confirmation != 1) {
                    progressDialog.cancel();
                    Toast.makeText(StateWiseDataActivity.this, "Internet slow/not available", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(int position) {
        Intent perStateIntent = new Intent(this, EachStateDataActivity.class);
        StateWiseModel clickedItem = statewiseModelArrayList.get(position);

        perStateIntent.putExtra(STATE_NAME, clickedItem.getState());
        perStateIntent.putExtra(STATE_CONFIRMED, clickedItem.getConfirmed());
        perStateIntent.putExtra(STATE_ACTIVE, clickedItem.getActive());
        perStateIntent.putExtra(STATE_DECEASED, clickedItem.getDeceased());
        perStateIntent.putExtra(STATE_NEW_CONFIRMED, clickedItem.getNewConfirmed());
        perStateIntent.putExtra(STATE_NEW_RECOVERED, clickedItem.getNewRecovered());
        perStateIntent.putExtra(STATE_NEW_DECEASED, clickedItem.getNewDeceased());
        perStateIntent.putExtra(STATE_LAST_UPDATE, clickedItem.getLastupdate());
        perStateIntent.putExtra(STATE_RECOVERED, clickedItem.getRecovered());

        search.setText("");
        startActivity(perStateIntent);
    }
}