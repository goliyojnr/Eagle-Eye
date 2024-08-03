package com.cholera.eagleeye.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cholera.eagleeye.R;
import com.cholera.eagleeye.viewpager.GovernoratePagerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class ChartFragment extends Fragment {

    private static final String TAG = "ChartFragment";

    private Map<String, Integer> firstPredictedCasesMap = new HashMap<>();
    private BarChart barChart;
    private LineChart lineChart;
    private PieChart pieChart;
    private TextView title, subtitle;
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private GovernoratePagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visuals, container, false);

        barChart = view.findViewById(R.id.bar_chart);
        lineChart = view.findViewById(R.id.line_chart);
        pieChart = view.findViewById(R.id.pie_chart);
        title = view.findViewById(R.id.title);
        subtitle = view.findViewById(R.id.subtitle);
        viewPager = view.findViewById(R.id.view_pager);
        progressBar = view.findViewById(R.id.progress_bar);

        title.setText("Cholera Case");
        subtitle.setText("Data overview for Regions");


        progressBar.setVisibility(View.VISIBLE);

        loadDataFromFirebase();

        return view;
    }

    private void loadDataFromFirebase() {
        Log.d(TAG, "loadDataFromFirebase: Loading data from Firebase...");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("prediction");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Data received from Firebase.");

                // Clear previous data
                firstPredictedCasesMap.clear();

                // Iterate through the data to find the predicted cases for each governorate
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String governorate = snapshot.child("governorate").getValue(String.class);
                    Integer predictedCases = snapshot.child("predicted_cases").getValue(Integer.class);

                    if (governorate != null && predictedCases != null) {
                        firstPredictedCasesMap.put(governorate, predictedCases);
                    }
                }

                // Set up ViewPager with the governorate data
                pagerAdapter = new GovernoratePagerAdapter(getChildFragmentManager(), firstPredictedCasesMap);
                viewPager.setAdapter(pagerAdapter);


                createBarChart();
                createLineChart();
                createPieChart();


                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Error fetching data", databaseError.toException());
            }
        });
    }

    private void createBarChart() {
        List<BarEntry> barEntries = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : firstPredictedCasesMap.entrySet()) {
            barEntries.add(new BarEntry(index, entry.getValue()));
            index++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Predicted Cases");
        barDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        barDataSet.setValueTextSize(14f);
        barDataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Customize X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(16f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(1f);

        // Customize Y-axis
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setTextSize(16f);
        yAxis.setTextColor(Color.BLACK);
        barChart.getAxisRight().setEnabled(false);

        // Customize chart appearance
        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        barChart.invalidate();
    }

    private void createLineChart() {
        List<Entry> lineEntries = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : firstPredictedCasesMap.entrySet()) {
            lineEntries.add(new Entry(index, entry.getValue()));
            index++;
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Predicted Cases");
        lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        lineDataSet.setValueTextSize(14f);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setLineWidth(2f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Customize X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(16f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(1f);

        // Customize Y-axis
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(16f);
        yAxis.setTextColor(Color.BLACK);
        lineChart.getAxisRight().setEnabled(false);

        // Customize chart appearance
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        lineChart.invalidate();
    }

    private void createPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : firstPredictedCasesMap.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Predicted Cases");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        // Customize chart appearance
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setDrawCenterText(true);

        pieChart.invalidate();
    }
}

