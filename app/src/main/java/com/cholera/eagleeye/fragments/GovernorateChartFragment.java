package com.cholera.eagleeye.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cholera.eagleeye.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GovernorateChartFragment extends Fragment {

    private static final String ARG_GOVERNORATE = "governorate";
    private static final String ARG_PREDICTED_CASES = "predicted_cases";
    private static final String TAG = "GovernorateChartFragment";

    private String governorate;
    private int predictedCases;

    public static GovernorateChartFragment newInstance(String governorate, int predictedCases) {
        GovernorateChartFragment fragment = new GovernorateChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GOVERNORATE, governorate);
        args.putInt(ARG_PREDICTED_CASES, predictedCases);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            governorate = getArguments().getString(ARG_GOVERNORATE);
            predictedCases = getArguments().getInt(ARG_PREDICTED_CASES);
            Log.d(TAG, "Received governorate: " + governorate + " with predicted cases: " + predictedCases);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_governorate_chart, container, false);
        PieChart pieChart = view.findViewById(R.id.pie_chart);


        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(predictedCases, governorate));


        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Cholera Cases");

        // Customize PieDataSet
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(20f);
        pieDataSet.setValueTextColor(Color.BLACK);

        // Create PieData
        PieData pieData = new PieData(pieDataSet);

        // Customize PieChart appearance
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextSize(16f);
        pieChart.getLegend().setTextColor(Color.BLACK);

        // Customize labels
        pieChart.setEntryLabelTextSize(20f);
        pieChart.setEntryLabelColor(Color.BLACK);


        pieChart.getLegend().setCustom(Collections.singletonList(
                new LegendEntry(governorate + ": " + predictedCases, Legend.LegendForm.CIRCLE, 16f, 2f, null, ColorTemplate.COLORFUL_COLORS[0])
        ));

        pieChart.invalidate();

        return view;
    }
}
