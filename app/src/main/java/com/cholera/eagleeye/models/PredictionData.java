package com.cholera.eagleeye.models;

import com.google.gson.annotations.SerializedName;

public class PredictionData {
    @SerializedName("temperature")
    private float temperature;

    @SerializedName("rainfall")
    private float rainfall;

    @SerializedName("humidity")
    private float humidity;

    @SerializedName("predicted_cases")
    private int predictedCases;

    @SerializedName("date")
    private String date;

    @SerializedName("governorate")
    private String governorate;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getRainfall() {
        return rainfall;
    }

    public void setRainfall(float rainfall) {
        this.rainfall = rainfall;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public int getPredictedCases() {
        return predictedCases;
    }

    public void setPredictedCases(int predictedCases) {
        this.predictedCases = predictedCases;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }
}

