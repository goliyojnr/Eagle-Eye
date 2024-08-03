package com.cholera.eagleeye.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cholera.eagleeye.R;
import com.cholera.eagleeye.models.DataEntry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;


public class GovernorateLineChartFragment extends Fragment {

    private static final String ARG_GOVERNORATE = "governorate";
    private static final String ARG_PREDICTED_CASES = "predicted_cases";

    private String governorate;
    private List<DataEntry> dataEntries;

    public static GovernorateLineChartFragment newInstance(String governorate, List<DataEntry> dataEntries) {
        GovernorateLineChartFragment fragment = new GovernorateLineChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GOVERNORATE, governorate);
        args.putParcelableArrayList(ARG_PREDICTED_CASES, new ArrayList<>(dataEntries));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            governorate = getArguments().getString(ARG_GOVERNORATE);
            dataEntries = getArguments().getParcelableArrayList(ARG_PREDICTED_CASES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_governorate_line_chart, container, false);
        LineChart lineChart = view.findViewById(R.id.line_chart);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataEntries.size(); i++) {
            DataEntry entry = dataEntries.get(i);

           entries.add(new Entry(i, entry.predictedCases));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Predicted Cases for " + governorate);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setText("Predicted Cases Over Time");
        lineChart.invalidate();

        return view;
    }
}



