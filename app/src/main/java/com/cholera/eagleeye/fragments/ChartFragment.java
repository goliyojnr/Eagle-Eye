package com.cholera.eagleeye.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cholera.eagleeye.R;
import com.cholera.eagleeye.models.DataEntry;
import com.cholera.eagleeye.viewpager.GovernorateLinePagerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChartFragment extends Fragment {

    private DatabaseReference databaseReference;
    private HashMap<String, List<DataEntry>> governorateDataMap;
    private ViewPager viewPager;
    private ProgressBar progressBar;

    private static final int MAX_ENTRIES_PER_GOVERNORATE = 7;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        databaseReference = FirebaseDatabase.getInstance().getReference("predictions");

        // Initialize HashMap to store governorate data
        governorateDataMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_series, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve data from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String governorate = snapshot.child("governorate").getValue(String.class);

                    Integer predictedCasesValue = snapshot.child("predicted_cases").getValue(Integer.class);
                    int predictedCases = predictedCasesValue != null ? predictedCasesValue : 0;

                    String date = snapshot.child("date").getValue(String.class);


                    if (!governorateDataMap.containsKey(governorate)) {
                        governorateDataMap.put(governorate, new ArrayList<>());
                    }


                    List<DataEntry> dataList = governorateDataMap.get(governorate);


                    dataList.add(new DataEntry(date, predictedCases));


                    if (dataList.size() > MAX_ENTRIES_PER_GOVERNORATE) {
                        dataList.remove(0); // Remove the oldest entry
                    }
                }

                // Create and set up ViewPager adapter
                GovernorateLinePagerAdapter adapter = new GovernorateLinePagerAdapter(getChildFragmentManager(), governorateDataMap);
                viewPager.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible error
                Log.e("ChartFragment", "Error fetching data", databaseError.toException());
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }
}



