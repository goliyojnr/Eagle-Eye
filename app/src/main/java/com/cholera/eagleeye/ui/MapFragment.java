package com.cholera.eagleeye.ui;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cholera.eagleeye.R;
import com.cholera.eagleeye.models.PredictionResponse;
import com.cholera.eagleeye.retrofit.ApiClient;
import com.cholera.eagleeye.retrofit.ApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.util.Log;  // Add this import statement

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import com.google.gson.Gson;  // Add this import statement

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapFragment";
    private GoogleMap mMap;
    private LinearLayout waterBodiesContainer;
    private LinearLayout borderCrossingsContainer;
    private ProgressBar progressBar;
    private ApiService apiService;

    // Data for water bodies
    private List<WaterBody> waterBodies = Arrays.asList(
            new WaterBody("Kamuzu Dam I", new WaterBodyDetails(Arrays.asList("Karonga"), "pond")),
            new WaterBody("Kamuzu Dam II", new WaterBodyDetails(Arrays.asList("Karonga"), "pond")),
            new WaterBody("Mpira-Balaka Dam", new WaterBodyDetails(Arrays.asList("Balaka"), "pond")),
            new WaterBody("Nkula Dam", new WaterBodyDetails(Arrays.asList("Nkhotakota"), "pond")),
            new WaterBody("Lunzu Dam", new WaterBodyDetails(Arrays.asList("Blantyre"), "pond")),
            new WaterBody("Mudi Dam", new WaterBodyDetails(Arrays.asList("Blantyre"), "pond")),
            new WaterBody("Shire River", new WaterBodyDetails(Arrays.asList("Chikwawa"), "dock")),
            new WaterBody("Ruo River", new WaterBodyDetails(Arrays.asList("Nsanje"), "dock")),
            new WaterBody("Bua River", new WaterBodyDetails(Arrays.asList("Kasungu"), "dock")),
            new WaterBody("Lake Malombe", new WaterBodyDetails(Arrays.asList("Machinga", "Balaka", "Nkhotakota"), "lake")),
            new WaterBody("Lake Malawi", new WaterBodyDetails(Arrays.asList("Mangochi", "Machinga", "Salima", "Nkhotakota", "Kasungu"), "lake")),
            new WaterBody("Lake Chilwa", new WaterBodyDetails(Arrays.asList("Zomba", "Chikwawa", "Nsanje"), "lake"))
    );

    // Data for border crossings
    private List<BorderCrossing> borderCrossings = Arrays.asList(
            new BorderCrossing("Kasumulu Border Crossing (Tanzania)", new LatLng(-9.5, 33.5), Arrays.asList("Karonga")),
            new BorderCrossing("Nkhata Bay Border Crossing (Tanzania)", new LatLng(-11.0, 34.0), Arrays.asList("Nkhata Bay")),
            new BorderCrossing("Zócalo Border Crossing (Mozambique)", new LatLng(-13.5, 34.5), Arrays.asList("Machinga", "Zomba")),
            new BorderCrossing("Kasungu Border Crossing (Zambia)", new LatLng(-13.0, 33.0), Arrays.asList("Kasungu")),
            new BorderCrossing("Mwanza Border Crossing (Zambia)", new LatLng(-15.0, 35.3), Arrays.asList("Mwanza")),
            new BorderCrossing("Chipata Border Crossing (Zambia)", new LatLng(-13.6, 32.6), Arrays.asList("Mchinji", "Kasungu")),
            new BorderCrossing("Chirundu Border Crossing (Zimbabwe)", new LatLng(-15.0, 29.2), Arrays.asList("Nsanje", "Chikwawa"))
    );

    public MapFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bodies, container, false);

        waterBodiesContainer = view.findViewById(R.id.water_bodies_container);
        borderCrossingsContainer = view.findViewById(R.id.weather_stations_container);
        progressBar = view.findViewById(R.id.progressBar);


        apiService = ApiClient.getClient("http://10.0.2.2:5000/").create(ApiService.class);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        populateWaterBodies();


        populateBorderCrossings();

        return view;
    }

    private void populateWaterBodies() {
        for (final WaterBody waterBody : waterBodies) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_water_body, waterBodiesContainer, false);

            ImageView imageView = itemView.findViewById(R.id.image);
            TextView textView = itemView.findViewById(R.id.name);

            imageView.setImageResource(waterBody.getImageResId());
            textView.setText(waterBody.getName());

            itemView.setOnClickListener(v -> {
                if (mMap != null) {
                    for (String district : waterBody.getDetails().getSurroundingDistricts()) {
                        LatLng location = getLatLngFromDistrict(district);
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                            fetchAndDisplayPredictions(waterBody.getDetails().getSurroundingDistricts());
                            break;
                        }
                    }
                }
            });

            waterBodiesContainer.addView(itemView);
        }
    }

    private void populateBorderCrossings() {
        for (final BorderCrossing borderCrossing : borderCrossings) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_border_crossing, borderCrossingsContainer, false);

            TextView textView = itemView.findViewById(R.id.name);

            textView.setText(borderCrossing.getName());

            itemView.setOnClickListener(v -> {
                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(borderCrossing.getLocation(), 10));
                    fetchAndDisplayPredictions(borderCrossing.getSurroundingDistricts());
                }
            });

            borderCrossingsContainer.addView(itemView);
        }
    }



    private void fetchAndDisplayPredictions(List<String> districts) {
        if (apiService == null) {
            Log.e(TAG, "ApiService is not initialized");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        apiService.predict().enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response body: " + new Gson().toJson(response.body()));

                    List<GovernoratePrediction> predictions = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : response.body().entrySet()) {
                        String governorate = entry.getKey();
                        int predictedCases = entry.getValue();

                        if (districts.contains(governorate)) {
                            predictions.add(new GovernoratePrediction(governorate, predictedCases));
                        }
                    }
                    displayPredictions(predictions);
                } else {
                    handleServerError(response);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "API call failed: " + t.getMessage());
                showErrorDialog("Network Error", "Failed to fetch data. Please check your internet connection and try again.");
            }
        });
    }

    private void handleServerError(Response<?> response) {
        int statusCode = response.code();
        String message = "An error occurred";

        if (statusCode == 502) {
            message = "The server encountered a temporary error. Please try again later.";
        } else if (response.errorBody() != null) {
            try {
                message = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e(TAG, "Server error: " + message);
        showErrorDialog("Server Error", message);
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void displayPredictions(List<GovernoratePrediction> predictions) {
        for (GovernoratePrediction prediction : predictions) {
            showPredictionDialog(prediction.getGovernorate(), prediction.getPredictedCases());
        }
    }

    private void showPredictionDialog(String governorate, int predictedCases) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(governorate)
                .setMessage("Predicted cases: " + predictedCases)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private LatLng getLatLngFromDistrict(String district) {
        for (BorderCrossing borderCrossing : borderCrossings) {
            if (borderCrossing.getSurroundingDistricts().contains(district)) {
                return borderCrossing.getLocation();
            }
        }
        return null;
    }

    private LatLng getLatLngFromGovernorate(String governorate) {
        for (BorderCrossing borderCrossing : borderCrossings) {
            if (borderCrossing.getSurroundingDistricts().contains(governorate)) {
                return borderCrossing.getLocation();
            }
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (WaterBody waterBody : waterBodies) {
            LatLng defaultLocation = new LatLng(-13.0, 34.0);
            mMap.addMarker(new MarkerOptions()
                    .position(defaultLocation)
                    .title(waterBody.getName()));
        }

        for (BorderCrossing borderCrossing : borderCrossings) {
            mMap.addMarker(new MarkerOptions()
                    .position(borderCrossing.getLocation())
                    .title(borderCrossing.getName()));
        }

        LatLng defaultLocation = new LatLng(-13.0, 34.0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 6));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
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

            if (title != null && !title.isEmpty()) {
                tvTitle.setText(title);
            }

            String snippet = marker.getSnippet();
            TextView tvSnippet = view.findViewById(R.id.snippet);

            if (snippet != null && !snippet.isEmpty()) {
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

    public class WaterBody {
        private String name;
        private int imageResId;
        private List<String> locations;
        private WaterBodyDetails details;

        public WaterBody(String name, WaterBodyDetails details) {
            this.name = name;
            this.details = details;
            this.locations = details.getSurroundingDistricts();
            this.imageResId = getImageResourceForType(details.getType());
        }

        public WaterBody(String name, int imageResId, List<String> locations) {
            this.name = name;
            this.imageResId = imageResId;
            this.locations = locations;
        }

        public String getName() {
            return name;
        }

        public int getImageResId() {
            return imageResId;
        }

        public List<String> getLocations() {
            return locations;
        }

        public WaterBodyDetails getDetails() {
            return details;
        }

        private int getImageResourceForType(String type) {
            switch (type) {
                case "pond":
                    return R.drawable.pond;
                case "dock":
                    return R.drawable.dock;
                case "lake":
                    return R.drawable.lake;
                default:
                    return R.drawable.lake;
            }
        }
    }

    public class BorderCrossing {
        private String name;
        private LatLng location;
        private List<String> surroundingDistricts;

        public BorderCrossing(String name, LatLng location, List<String> surroundingDistricts) {
            this.name = name;
            this.location = location;
            this.surroundingDistricts = surroundingDistricts;
        }

        public String getName() {
            return name;
        }

        public LatLng getLocation() {
            return location;
        }

        public List<String> getSurroundingDistricts() {
            return surroundingDistricts;
        }
    }

    public class WaterBodyDetails {
        private List<String> surroundingDistricts;
        private String type;

        public WaterBodyDetails(List<String> surroundingDistricts, String type) {
            this.surroundingDistricts = surroundingDistricts;
            this.type = type;
        }

        public List<String> getSurroundingDistricts() {
            return surroundingDistricts;
        }

        public String getType() {
            return type;
        }
    }

    public class GovernoratePrediction {
        private String governorate;
        private int predictedCases;

        public GovernoratePrediction(String governorate, int predictedCases) {
            this.governorate = governorate;
            this.predictedCases = predictedCases;
        }

        String getGovernorate() {
            return governorate;
        }

        int getPredictedCases() {
            return predictedCases;
        }
    }
}




