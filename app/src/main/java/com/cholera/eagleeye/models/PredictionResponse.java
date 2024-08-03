package com.cholera.eagleeye.models;

public class PredictionResponse {
    private String date;
    private int humidity;
    private int predicted_cases;
    private int rainfall;
    private double temperature;
    private String error;
    private int population;
    private int water_quality_index;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPredicted_cases() {
        return predicted_cases;
    }

    public void setPredicted_cases(int predicted_cases) {
        this.predicted_cases = predicted_cases;
    }

    public int getRainfall() {
        return rainfall;
    }

    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

//    public int getPopulation_density() {
//        return population_density;
//    }
//
//    public void setPopulation_density(int population_density) {
//        this.population_density = population_density;
//    }

    public int getWater_quality_index() {
        return water_quality_index;
    }

    public void setWater_quality_index(int water_quality_index) {
        this.water_quality_index = water_quality_index;
    }

//    public int getNumber_of_boreholes() {
//        return number_of_boreholes;
//    }
//
//    public void setNumber_of_boreholes(int number_of_boreholes) {
//        this.number_of_boreholes = number_of_boreholes;
//    }
//
//    public int getNumber_of_latrines() {
//        return number_of_latrines;
//    }
//
//    public void setNumber_of_latrines(int number_of_latrines) {
//        this.number_of_latrines = number_of_latrines;
//    }
//
//    public int getNumber_of_hand_wash_stations() {
//        return number_of_hand_wash_stations;
//    }
//
//    public void setNumber_of_hand_wash_stations(int number_of_hand_wash_stations) {
//        this.number_of_hand_wash_stations = number_of_hand_wash_stations;
//    }
}
