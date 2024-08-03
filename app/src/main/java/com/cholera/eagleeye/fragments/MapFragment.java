package com.cholera.eagleeye.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cholera.eagleeye.R;
import com.cholera.eagleeye.retrofit.ApiClient;
import com.cholera.eagleeye.retrofit.ApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private GoogleMap mMap;
    private MapView mapView;
    private ApiService apiService;

    private TextView lowCaseGov1, lowCaseGov2, lowCaseGov3;
    private TextView highCaseGov1, highCaseGov2, highCaseGov3;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
        Log.d(TAG, "onCreateView: ");

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize UI elements
        lowCaseGov1 = view.findViewById(R.id.low_case_gov1);
        lowCaseGov2 = view.findViewById(R.id.low_case_gov2);
        lowCaseGov3 = view.findViewById(R.id.low_case_gov3);
        highCaseGov1 = view.findViewById(R.id.high_case_gov1);
        highCaseGov2 = view.findViewById(R.id.high_case_gov2);
        highCaseGov3 = view.findViewById(R.id.high_case_gov3);
        progressBar = view.findViewById(R.id.progress_bar);

        apiService = ApiClient.getClient("http://10.0.2.2:5000/").create(ApiService.class);
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng malawiCenter = new LatLng(-13.2543, 34.3015);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malawiCenter, 7));

        fetchAndDisplayPredictions();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
    }

    private void fetchAndDisplayPredictions() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.predict().enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Response body: " + new Gson().toJson(response.body()));

                    List<GovernoratePrediction> predictions = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : response.body().entrySet()) {
                        String governorate = entry.getKey();
                        int predictedCases = entry.getValue();
                        predictions.add(new GovernoratePrediction(governorate, predictedCases));
                    }

                    if (!predictions.isEmpty()) {
                        // Sort predictions
                        Collections.sort(predictions, new Comparator<GovernoratePrediction>() {
                            @Override
                            public int compare(GovernoratePrediction o1, GovernoratePrediction o2) {
                                return Integer.compare(o1.getPredictedCases(), o2.getPredictedCases());
                            }
                        });

                        List<GovernoratePrediction> lowCases = predictions.subList(0, Math.min(3, predictions.size()));
                        List<GovernoratePrediction> highCases = predictions.subList(Math.max(predictions.size() - 3, 0), predictions.size());

                        // Update the UI
                        updateGovernorateUI(lowCases, highCases);

                        // Update the map with low and high cases
                        updateMapWithPredictions(lowCases, highCases);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Failed to fetch predictions: " + response.message() + " - " + errorBody);
                        Toast.makeText(getActivity(), "Failed to fetch predictions", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error fetching predictions: " + t.getMessage(), t);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGovernorateUI(List<GovernoratePrediction> lowCases, List<GovernoratePrediction> highCases) {
        // Update low cases
        if (lowCases.size() > 0) lowCaseGov1.setText(lowCases.get(0).getGovernorate());
        if (lowCases.size() > 1) lowCaseGov2.setText(lowCases.get(1).getGovernorate());
        if (lowCases.size() > 2) lowCaseGov3.setText(lowCases.get(2).getGovernorate());

        // Update high cases
        if (highCases.size() > 0) highCaseGov1.setText(highCases.get(0).getGovernorate());
        if (highCases.size() > 1) highCaseGov2.setText(highCases.get(1).getGovernorate());
        if (highCases.size() > 2) highCaseGov3.setText(highCases.get(2).getGovernorate());
    }

    private void updateMapWithPredictions(List<GovernoratePrediction> lowCases, List<GovernoratePrediction> highCases) {
        // Add markers for low cases
        for (GovernoratePrediction prediction : lowCases) {
            LatLng location = getLatLngFromGovernorate(prediction.getGovernorate());
            if (location != null) {
                String description = "Predicted cases: " + prediction.getPredictedCases();
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(prediction.getGovernorate())
                        .snippet(description)
                );
            }
        }

        // Add markers for high cases
        for (GovernoratePrediction prediction : highCases) {
            LatLng location = getLatLngFromGovernorate(prediction.getGovernorate());
            if (location != null) {
                String description = "Predicted cases: " + prediction.getPredictedCases();
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(prediction.getGovernorate())
                        .snippet(description)
                );
            }
        }
    }

    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        private Context mContext;

        public CustomInfoWindowAdapter(Context context) {
            mContext = context;
            mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        }

        private void renderWindowText(Marker marker, View view) {
            String title = marker.getTitle();
            TextView tvTitle = view.findViewById(R.id.title);

            if (!title.equals("")) {
                tvTitle.setText(title);
            }

            String snippet = marker.getSnippet();
            TextView tvSnippet = view.findViewById(R.id.snippet);

            if (!snippet.equals("")) {
                tvSnippet.setText(snippet);
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            renderWindowText(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            renderWindowText(marker, mWindow);
            return mWindow;
        }
    }

    private LatLng getLatLngFromGovernorate(String governorate) {
        switch (governorate) {
            case "Lilongwe":
                return new LatLng(-13.9626, 33.7741);
            case "Blantyre":
                return new LatLng(-15.7861, 35.0058);
            case "Zomba":
                return new LatLng(-15.6167, 34.5167);
            case "Karonga":
                return new LatLng(-11.4447, 34.0167);
            case "Mulanje":
                return new LatLng(-15.9795, 35.2731);
            case "Mzimba":
                return new LatLng(-13.9669, 34.5962);
            case "Balaka":
                return new LatLng(-15.2354, 34.4176);
            case "Kasungu":
                return new LatLng(-13.0133, 33.7650);
            case "Nkhotakota":
                return new LatLng(-12.7282, 34.0086);
            case "Dedza":
                return new LatLng(-14.0667, 33.9500);
            case "Ntcheu":
                return new LatLng(-13.6908, 33.5525);
            case "Dowa":
                return new LatLng(-13.2700, 33.9200);
            case "Mzuzu":
                return new LatLng(-11.4620, 34.0205);
            case "Thyolo":
                return new LatLng(-16.0779, 35.5087);
            case "Mchinji":
                return new LatLng(-13.7767, 32.8802);
            case "Salima":
                return new LatLng(-13.7000, 34.6000);
            case "Nsanje":
                return new LatLng(-16.5000, 35.5000);
            case "Chikwawa":
                return new LatLng(-15.8000, 35.3000);
            case "Ntchisi":
                return new LatLng(-13.7833, 33.7500);
            case "Chiradzulu":
                return new LatLng(-15.4400, 35.2000);
            case "Nkhata Bay":
                return new LatLng(-11.6060, 34.2900);
            case "Rumphi":
                return new LatLng(-10.8740, 33.7720);
            case "Phalombe":
                return new LatLng(-15.6827, 35.6508);
            case "Mwanza":
                return new LatLng(-15.5860, 34.5247);
            case "Likoma":
                return new LatLng(-12.0647, 34.7366);
            case "Mangochi":
                return new LatLng(-14.4782, 35.2645);
            default:
                return null;
        }
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

    private static class GovernoratePrediction {
        private String governorate;
        private int predictedCases;

        public GovernoratePrediction(String governorate, int predictedCases) {
            this.governorate = governorate;
            this.predictedCases = predictedCases;
        }

        public String getGovernorate() {
            return governorate;
        }

        public int getPredictedCases() {
            return predictedCases;
        }
    }
}







