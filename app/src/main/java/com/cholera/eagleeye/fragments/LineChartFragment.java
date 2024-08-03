package com.cholera.eagleeye.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cholera.eagleeye.R;
import com.cholera.eagleeye.models.DataEntry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartFragment extends Fragment {

    private static final String ARG_GOVERNORATE = "governorate";
    private static final String ARG_DATA = "data";

    private String governorate;
    private List<DataEntry> data;

    public static LineChartFragment newInstance(String governorate, List<DataEntry> data) {
        LineChartFragment fragment = new LineChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GOVERNORATE, governorate);
        args.putParcelableArrayList(ARG_DATA, new ArrayList<>(data));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            governorate = getArguments().getString(ARG_GOVERNORATE);
            data = getArguments().getParcelableArrayList(ARG_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView governorateTitle = view.findViewById(R.id.governorateTitle);
        governorateTitle.setText(governorate);
        governorateTitle.setTextSize(34);
        governorateTitle.setTextColor(Color.BLACK);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LineChart lineChart = view.findViewById(R.id.lineChart);
        setupLineChart(lineChart);

        return view;
    }

    private void setupLineChart(LineChart lineChart) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            DataEntry dataEntry = data.get(i);
            entries.add(new Entry(i, dataEntry.predictedCases));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Predicted Cases");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize X-axis and Y-axis labels
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.BLACK);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(14f);
        yAxis.setTextColor(Color.BLACK);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextSize(16f);

        lineChart.invalidate();
    }
}

