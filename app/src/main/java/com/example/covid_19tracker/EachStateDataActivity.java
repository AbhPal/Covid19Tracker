package com.example.covid_19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.Objects;

import static com.example.covid_19tracker.StateWiseDataActivity.STATE_ACTIVE;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_CONFIRMED;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_DECEASED;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_LAST_UPDATE;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_NAME;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_NEW_CONFIRMED;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_NEW_DECEASED;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_NEW_RECOVERED;
import static com.example.covid_19tracker.StateWiseDataActivity.STATE_RECOVERED;

public class EachStateDataActivity extends AppCompatActivity {

    private TextView perStateConfirmed, perStateActive, perStateDeceased, perStateNewConfirmed, perStateNewRecovered,
            perStateNewDeceased, perStateUpdate, perStateRecovered, perstateName, perstateNewActive;
    private PieChart mPieChart;
    private String stateName;

    private String version, appURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_state_data);

        Intent intent = getIntent();
        stateName = intent.getStringExtra(STATE_NAME);
        String stateConfirmed = intent.getStringExtra(STATE_CONFIRMED);
        String stateActive = intent.getStringExtra(STATE_ACTIVE);
        String stateDeceased = intent.getStringExtra(STATE_DECEASED);
        String stateNewConfirmed = intent.getStringExtra(STATE_NEW_CONFIRMED);
        String stateNewRecovered = intent.getStringExtra(STATE_NEW_RECOVERED);
        String stateNewDeceased = intent.getStringExtra(STATE_NEW_DECEASED);
        String stateLastUpdate = intent.getStringExtra(STATE_LAST_UPDATE);
        String stateRecovery = intent.getStringExtra(STATE_RECOVERED);

        Objects.requireNonNull(getSupportActionBar()).setTitle(stateName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        perStateConfirmed = findViewById(R.id.activity_each_state_confirmed_textView);
        perStateActive = findViewById(R.id.activity_each_state_active_textView);
        perStateRecovered = findViewById(R.id.activity_each_state_recovered_textView);
        perStateDeceased = findViewById(R.id.activity_each_state_death_textView);
        perStateUpdate = findViewById(R.id.activity_each_state_lastupdate_textView);
        perStateNewConfirmed = findViewById(R.id.activity_each_state_confirmed_new_textView);
        perStateNewRecovered = findViewById(R.id.activity_each_state_recovered_new_textView);
        perStateNewDeceased = findViewById(R.id.activity_each_state_death_new_textView);
        perstateName = findViewById(R.id.activity_each_state_district_data_title);
        perstateNewActive = findViewById(R.id.activity_each_state_active_new_textView);
        mPieChart = findViewById(R.id.activity_each_state_piechart);

        String activeCopy = stateActive;
        String recoveredCopy = stateRecovery;
        String deceasedCopy = stateDeceased;

        int stateActiveInt = Integer.parseInt(stateActive);
        stateActive = NumberFormat.getInstance().format(stateActiveInt);

        int stateDeceasedInt = Integer.parseInt(stateDeceased);
        stateDeceased = NumberFormat.getInstance().format(stateDeceasedInt);

        int stateRecoveredInt = Integer.parseInt(stateRecovery);
        stateRecovery = NumberFormat.getInstance().format(stateRecoveredInt);

        //int newActive = (Integer.parseInt(stateNewConfirmed)) - ((Integer.parseInt(stateNewRecovered)) + Integer.parseInt(stateNewDeceased));

        //assert stateActive != null;
        mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(activeCopy), Color.parseColor("#007afe")));
        mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
        mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deceasedCopy), Color.parseColor("#F6404F")));

        mPieChart.startAnimation();

        MainActivity object = new MainActivity();
        String formatDate = object.formatDate(stateLastUpdate, 0);
        perStateConfirmed.setText(stateConfirmed);
        perStateActive.setText(stateActive);
        perStateDeceased.setText(stateDeceased);
        perStateRecovered.setText(stateRecovery);
        perStateUpdate.setText(formatDate);
        perStateNewConfirmed.setText("+" + stateNewConfirmed);
        perStateNewRecovered.setText("+" + stateNewRecovered);
        perStateNewDeceased.setText("+" + stateNewDeceased);
        perstateName.setText("District data of "+stateName);
        //perstateNewActive.setText("+" + NumberFormat.getInstance().format(newActive));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void openDistrictData(View view){
        Intent intent = new Intent(this, DistrictwiseDataActivity.class);
        intent.putExtra(STATE_NAME, stateName);
        startActivity(intent);
    }
}