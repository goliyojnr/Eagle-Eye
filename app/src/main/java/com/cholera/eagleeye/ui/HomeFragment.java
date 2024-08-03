package com.cholera.eagleeye.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cholera.eagleeye.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mapView;
    private DatabaseReference databaseReference;
    private TextView casesCountTextView;
    private TextView riskLevelTextView;
    private TextView alertsCountTextView;
    private TextView predictionInfoTextView;
    private TextView preventionTipsTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        databaseReference = FirebaseDatabase.getInstance().getReference("prediction");


        casesCountTextView = view.findViewById(R.id.cases_count);
        riskLevelTextView = view.findViewById(R.id.risk_level);
        alertsCountTextView = view.findViewById(R.id.alerts_count);
        predictionInfoTextView = view.findViewById(R.id.prediction_info);
        preventionTipsTextView = view.findViewById(R.id.prevention_tips);


        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        loadDataFromFirebase();


        preventionTipsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.health.gov.mw/"));
                startActivity(browserIntent);
            }
        });

        return view;
    }


    private void loadDataFromFirebase() {
        databaseReference.orderByKey().limitToLast(28).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataSnapshot> snapshots = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshots.add(snapshot);
                }
                Collections.reverse(snapshots);

                int totalCases = 0;
                int alerts = Math.min(9, snapshots.size());
                StringBuilder latestPredictionInfo = new StringBuilder();

                for (int i = 0; i < alerts; i++) {
                    DataSnapshot snapshot = snapshots.get(i);
                    String governorate = snapshot.child("governorate").getValue(String.class);
                    Integer predictedCasesValue = snapshot.child("predicted_cases").getValue(Integer.class);
                    int predictedCases = predictedCasesValue != null ? predictedCasesValue : 0;

                    totalCases += predictedCases;

                    if (latestPredictionInfo.length() > 0) {
                        latestPredictionInfo.append("\n\n");
                    }
                    latestPredictionInfo.append("• ").append(governorate).append(": ").append(predictedCases);
                }

                casesCountTextView.setText("Cases: " + totalCases);
                alertsCountTextView.setText("Alerts: " + alerts);

                String riskLevel = calculateRiskLevel(totalCases);
                riskLevelTextView.setText("Risk Level: " + riskLevel);


                predictionInfoTextView.setText(latestPredictionInfo.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String calculateRiskLevel(int cases) {
        return cases > 100 ? "High" : "Low";
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        LatLng exampleLocation = new LatLng(-15.7833, 35.0333);
        mMap.addMarker(new MarkerOptions().position(exampleLocation).title("Blantyre"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(exampleLocation, 10));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
