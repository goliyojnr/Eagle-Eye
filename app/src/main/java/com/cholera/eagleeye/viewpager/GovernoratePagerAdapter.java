package com.cholera.eagleeye.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cholera.eagleeye.fragments.GovernorateChartFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GovernoratePagerAdapter extends FragmentPagerAdapter {

    private final Map<String, Integer> governorateData;

    public GovernoratePagerAdapter(FragmentManager fm, Map<String, Integer> governorateData) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.governorateData = governorateData;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(governorateData.entrySet());
        String governorate = entries.get(position).getKey();
        int predictedCases = entries.get(position).getValue();
        return GovernorateChartFragment.newInstance(governorate, predictedCases);
    }

    @Override
    public int getCount() {
        return governorateData.size();
    }
}

