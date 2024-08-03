package com.cholera.eagleeye.services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.cholera.eagleeye.retrofit.ApiClient;
import com.cholera.eagleeye.retrofit.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionWorker extends Worker {

    private ApiService apiService;

    public PredictionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        String baseUrl = "http://10.0.2.2:5000/";
        apiService = ApiClient.getClient(baseUrl).create(ApiService.class);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Trigger the API call
        apiService.predict().enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle the predictions
                    handlePredictions(response.body());
                } else {
                    // Handle the failure
                    handleFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                // Handle the error
                handleFailure(t.getMessage());
            }
        });


        return Result.retry();
    }

    private void handlePredictions(Map<String, Integer> predictions) {

        for (Map.Entry<String, Integer> entry : predictions.entrySet()) {
            String governorate = entry.getKey();
            Integer predictedCases = entry.getValue();

        }
    }

    private void handleFailure(String message) {

    }
}


