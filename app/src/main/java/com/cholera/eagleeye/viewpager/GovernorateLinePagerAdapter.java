package com.cholera.eagleeye.viewpager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cholera.eagleeye.fragments.LineChartFragment;
import com.cholera.eagleeye.models.DataEntry;

import java.util.HashMap;
import java.util.List;

public class GovernorateLinePagerAdapter extends FragmentPagerAdapter {

    private HashMap<String, List<DataEntry>> governorateDataMap;
    private String[] governorates;

    public GovernorateLinePagerAdapter(FragmentManager fm, HashMap<String, List<DataEntry>> governorateDataMap) {
        super(fm);
        this.governorateDataMap = governorateDataMap;
        this.governorates = governorateDataMap.keySet().toArray(new String[0]);
    }

    @Override
    public Fragment getItem(int position) {
        String governorate = governorates[position];
        return LineChartFragment.newInstance(governorate, governorateDataMap.get(governorate));
    }

    @Override
    public int getCount() {
        return governorates.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return governorates[position];
    }

    public String getGovernorateName(int position) {
        return governorates[position];
    }
}






