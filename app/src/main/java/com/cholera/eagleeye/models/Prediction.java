package com.cholera.eagleeye.models;

import java.util.List;

public class Prediction {

    private String governorate;
    private List<Integer> predictedCases;
    private String date;

    public Prediction() {

    }

    public Prediction(String governorate, List<Integer> predictedCases, String date) {
        this.governorate = governorate;
        this.predictedCases = predictedCases;
        this.date = date;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public List<Integer> getPredictedCases() {
        return predictedCases;
    }

    public void setPredictedCases(List<Integer> predictedCases) {
        this.predictedCases = predictedCases;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

