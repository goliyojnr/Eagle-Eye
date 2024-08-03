package com.cholera.eagleeye.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cholera.eagleeye.R;
import com.cholera.eagleeye.models.PredictionResponse;
import com.cholera.eagleeye.retrofit.ApiClient;
import com.cholera.eagleeye.retrofit.ApiService;
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

public class Notifications extends Fragment {

    private static final String TAG = "MapFragment";
    private ApiService apiService;

    private LinearLayout morePriorityText, mediumPriorityText, lowPriorityText, highPriorityText;
    private ProgressBar progressBar;
    private List<GovernoratePrediction> predictions;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alert_fragment, container, false);
        Log.d("Map fragment", "on it: ");

        // Initialize UI elements
        morePriorityText = view.findViewById(R.id.high);
        mediumPriorityText = view.findViewById(R.id.more);
        lowPriorityText = view.findViewById(R.id.medium);
        highPriorityText = view.findViewById(R.id.low);

        progressBar = view.findViewById(R.id.progress_bar);

        apiService = ApiClient.getClient("http://10.0.2.2:5000/").create(ApiService.class);

        fetchAndDisplayPredictions();

        setOnClickListeners();

        return view;
    }

    private void setOnClickListeners() {
        morePriorityText.setOnClickListener(v -> showGovernorateDetails("More Priority"));
        mediumPriorityText.setOnClickListener(v -> showGovernorateDetails("Medium Priority"));
        lowPriorityText.setOnClickListener(v -> showGovernorateDetails("Low Priority"));
        highPriorityText.setOnClickListener(v -> showGovernorateDetails("High Priority"));
    }

    private void showGovernorateDetails(String priority) {
        // Show dialog with governorate details based on priority
        if (predictions != null && !predictions.isEmpty()) {
            List<GovernoratePrediction> filteredPredictions = new ArrayList<>();
            for (GovernoratePrediction prediction : predictions) {
                if (priority.equals("Low Priority") && prediction.getPredictedCases() >= 1 && prediction.getPredictedCases() <= 200 ||
                        priority.equals("Medium Priority") && prediction.getPredictedCases() > 200 && prediction.getPredictedCases()<= 400 ||
                        priority.equals("More Priority") && prediction.getPredictedCases()> 400 && prediction.getPredictedCases() <= 800 ||
                        priority.equals("High Priority") && prediction.getPredictedCases() > 800) {
                    filteredPredictions.add(prediction);
                }
            }
            GovernorateDetailsDialog dialog = new GovernorateDetailsDialog(filteredPredictions);
            dialog.show(getParentFragmentManager(), "GovernorateDetailsDialog");
        }
    }

    private void fetchAndDisplayPredictions() {
        Log.d(TAG, "fetchAndDisplayPredictions called");
        progressBar.setVisibility(View.VISIBLE);

        apiService.predict().enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response body: " + new Gson().toJson(response.body()));

                    predictions = new ArrayList<>();
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

                        // Apply blinking effect based on cases
                        applyBlinkingEffect(predictions);
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
                Log.e(TAG, "Error fetching predictions: " + t.getMessage(), t);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void applyBlinkingEffect(List<GovernoratePrediction> predictions) {

        resetBlinkingEffect();

        if (predictions == null || predictions.isEmpty()) {
            Log.d(TAG, "No predictions available for blinking effect.");
            return;
        }

        for (GovernoratePrediction prediction : predictions) {
            int cases = prediction.getPredictedCases();
            Log.d(TAG, "Applying effect for cases: " + cases);

            if (cases >= 1 && cases <= 200) {
                Log.d(TAG, "low Priority blinking effect applied.");
                setBlinkingEffect(lowPriorityText);
            } else if (cases > 200 && cases <= 400) {
                Log.d(TAG, "Medium Priority blinking effect applied.");
                setBlinkingEffect(mediumPriorityText);
            } else if (cases > 400 && cases <= 800) {
                Log.d(TAG, "More Priority blinking effect applied.");
                setBlinkingEffect(morePriorityText);
            } else if (cases > 800) {
                Log.d(TAG, "high Priority blinking effect applied.");
                setBlinkingEffect(highPriorityText);
            }
        }
    }

    private void resetBlinkingEffect() {
        clearBlinkingEffect(highPriorityText);
        clearBlinkingEffect(mediumPriorityText);
        clearBlinkingEffect(morePriorityText);
        clearBlinkingEffect(lowPriorityText);
    }

    private void clearBlinkingEffect(LinearLayout textView) {
        if (textView != null) {
            textView.clearAnimation();
            textView.setAlpha(1.0f);
            Log.d(TAG, "Cleared animation for view: " + textView.getId());
        } else {
            Log.d(TAG, "Text view is null, cannot clear animation.");
        }
    }

    private void setBlinkingEffect(LinearLayout textView) {
        if (textView != null) {
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            textView.startAnimation(anim);
            Log.d(TAG, "Applied blinking effect to view: " + textView.getId());
        } else {
            Log.d(TAG, "Text view is null, cannot apply animation.");
        }
    }


    static class GovernoratePrediction {
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

        @Override
        public String toString() {
            return governorate + ": " + predictedCases + " cases";
        }
    }
}

