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

import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_ACTIVE;
import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_CONFIRMED;
import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_DECEASED;
import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_NAME;
import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_NEW_CONFIRMED;
import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_NEW_DECEASED;
import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_NEW_RECOVERED;
import static com.example.covid_19tracker.DistrictwiseDataActivity.DISTRICT_RECOVERED;

public class EachDistrictDataActivity extends AppCompatActivity {

    TextView perDistrictConfirmed, perDistrictActive, perDistrictDeceased, perDistrictNewConfirmed,
            perDistrictNewRecovered, perDistrictNewDeceased, perDistrictUpdate, perDistrictRecovered, perDistrictName;
    PieChart mPieChart;
    String districtName;
    private String version, appURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_district_data);


        Intent intent = getIntent();
        districtName = intent.getStringExtra(DISTRICT_NAME);
        String districtConfirmed = intent.getStringExtra(DISTRICT_CONFIRMED);
        String districtActive = intent.getStringExtra(DISTRICT_ACTIVE);
        String districtDeceased = intent.getStringExtra(DISTRICT_DECEASED);
        String districtNewConfirmed = intent.getStringExtra(DISTRICT_NEW_CONFIRMED);
        String districtNewRecovered = intent.getStringExtra(DISTRICT_NEW_RECOVERED);
        String districtNewDeceased = intent.getStringExtra(DISTRICT_NEW_DECEASED);
        String districtRecovery = intent.getStringExtra(DISTRICT_RECOVERED);

        Objects.requireNonNull(getSupportActionBar()).setTitle(districtName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        perDistrictConfirmed = findViewById(R.id.activity_each_district_confirmed_textView);
        perDistrictActive = findViewById(R.id.activity_each_district_active_textView);
        perDistrictRecovered = findViewById(R.id.activity_each_district_recovered_textView);
        perDistrictDeceased = findViewById(R.id.activity_each_district_death_textView);
        perDistrictNewConfirmed = findViewById(R.id.activity_each_district_confirmed_new_textView);
        perDistrictNewRecovered = findViewById(R.id.activity_each_district_recovered_new_textView);
        perDistrictNewDeceased = findViewById(R.id.activity_each_district_death_new_textView);
        mPieChart = findViewById(R.id.activity_each_district_piechart);

        String activeCopy = districtActive;
        String recoveredCopy = districtRecovery;
        String deceasedCopy = districtDeceased;

        int districtActiveInt = Integer.parseInt(districtActive);
        districtActive = NumberFormat.getInstance().format(districtActiveInt);

        int districtDeceasedInt = Integer.parseInt(districtDeceased);
        districtDeceased = NumberFormat.getInstance().format(districtDeceasedInt);

        int districtRecoveredInt = Integer.parseInt(districtRecovery);
        districtRecovery = NumberFormat.getInstance().format(districtRecoveredInt);

        int confirmedInt = Integer.parseInt(districtConfirmed);
        districtConfirmed = NumberFormat.getInstance().format(confirmedInt);

        //assert districtActive != null;
        mPieChart.addPieSlice(new PieModel("Active", Integer.parseInt(activeCopy), Color.parseColor("#007afe")));
        mPieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recoveredCopy), Color.parseColor("#08a045")));
        mPieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(deceasedCopy), Color.parseColor("#F6404F")));

        mPieChart.startAnimation();

        MainActivity object = new MainActivity();
        perDistrictConfirmed.setText(districtConfirmed);
        perDistrictActive.setText(districtActive);
        perDistrictDeceased.setText(districtDeceased);
        perDistrictNewConfirmed.setText("+" + districtNewConfirmed);
        perDistrictNewRecovered.setText("+" + districtNewRecovered);
        perDistrictNewDeceased.setText("+" + districtNewDeceased);
        perDistrictRecovered.setText(districtRecovery);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}