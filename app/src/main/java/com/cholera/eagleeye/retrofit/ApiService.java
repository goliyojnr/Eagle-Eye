package com.cholera.eagleeye.retrofit;

import com.cholera.eagleeye.models.PredictionData;
import com.cholera.eagleeye.models.PredictionResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("predict/default")
    Call<Map<String, Integer>> predict();


    @GET("/data")
    Call<List<PredictionData>> getAllData();

    @GET("/selectivedata")
    Call<List<PredictionData>> getSelectiveData(
            @Query("governorate") String governorate,
            @Query("year") String year,
            @Query("month") String month
    );
}
