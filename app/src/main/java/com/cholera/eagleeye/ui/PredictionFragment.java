package com.cholera.eagleeye.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cholera.eagleeye.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PredictionFragment extends Fragment {

    private EditText editGovernorate, editTemperature, editRainfall, editHumidity, editPopulationDensity, editWaterQualityIndex, editCasesLag1, editCasesLag2;
    private Button buttonPredict;
    private RequestQueue requestQueue;

    private CardView cardViewPrediction;
    private TextView textGovernorate, textPredictedCases;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prediction, container, false);

        editGovernorate = view.findViewById(R.id.editGovernorate);
        editTemperature = view.findViewById(R.id.editTemperature);
        editRainfall = view.findViewById(R.id.editRainfall);
        editHumidity = view.findViewById(R.id.editHumidity);
        editPopulationDensity = view.findViewById(R.id.editPopulationDensity);
        editWaterQualityIndex = view.findViewById(R.id.editWaterQualityIndex);
        editCasesLag1 = view.findViewById(R.id.editCasesLag1);
        editCasesLag2 = view.findViewById(R.id.editCasesLag2);
        buttonPredict = view.findViewById(R.id.buttonPredict);
        requestQueue = Volley.newRequestQueue(getContext());
        cardViewPrediction = view.findViewById(R.id.cardViewPrediction);
        textGovernorate = view.findViewById(R.id.textGovernorate);
        textPredictedCases = view.findViewById(R.id.textPredictedCases);


        buttonPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String governorate = editGovernorate.getText().toString().trim();
                String temperature = editTemperature.getText().toString().trim();
                String rainfall = editRainfall.getText().toString().trim();
                String humidity = editHumidity.getText().toString().trim();
                String populationDensity = editPopulationDensity.getText().toString().trim();
                String waterQualityIndex = editWaterQualityIndex.getText().toString().trim();
                String casesLag1 = editCasesLag1.getText().toString().trim();
                String casesLag2 = editCasesLag2.getText().toString().trim();

                if (!governorate.isEmpty() && !temperature.isEmpty() && !rainfall.isEmpty() && !humidity.isEmpty() &&
                        !populationDensity.isEmpty() && !waterQualityIndex.isEmpty() && !casesLag1.isEmpty() && !casesLag2.isEmpty()) {
                    try {
                        JSONObject input = new JSONObject();
                        input.put("Governorate", governorate);
                        input.put("Temperature_(øC)", Double.parseDouble(temperature));
                        input.put("Rainfall_(mm)", Double.parseDouble(rainfall));
                        input.put("Humidity_(%)", Double.parseDouble(humidity));
                        input.put("Population_Density", Double.parseDouble(populationDensity));
                        input.put("Water_Quality_Index", Double.parseDouble(waterQualityIndex));
                        input.put("Cases_Lag_1", Integer.parseInt(casesLag1));
                        input.put("Cases_Lag_2", Integer.parseInt(casesLag2));
                        predict(input);
                    } catch (JSONException | NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Input format error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }




    private void predict(JSONObject input) {
        String url = "http://10.0.2.2:5000/predict-json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("Governorate") && response.has("prediction")) {
                                String governorate = response.getString("Governorate");
                                int predictedCases = response.getInt("prediction");

                                // Update the CardView with the prediction result
                                textGovernorate.setText("Governorate: " + governorate);
                                textPredictedCases.setText("Predicted Cases: " + predictedCases);
                                cardViewPrediction.setVisibility(View.VISIBLE);

                                Toast.makeText(getContext(), "Governorate: " + governorate + "\nPredicted Cases: " + predictedCases, Toast.LENGTH_LONG).show();
                                Log.d("Prediction", "Governorate: " + governorate + ", Predicted Cases: " + predictedCases);
                                Log.d("Prediction", "Full response: " + response.toString());
                            } else {
                                Toast.makeText(getContext(), "Unexpected response format", Toast.LENGTH_SHORT).show();
                                Log.d("Prediction", "Unexpected response format: " + response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Response parsing error", Toast.LENGTH_SHORT).show();
                            Log.d("Prediction", "Response parsing error: " + e.getMessage());
                            Log.d("Prediction", "Full response: " + response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Prediction error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Prediction", "Prediction error: " + error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }



}
