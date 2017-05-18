package com.nebbs.counterstrikestats.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.nebbs.counterstrikestats.R;

public class BasicStatsFragment extends Fragment {

    private static final String TAG = "BasicStatsFragment";

    private TextView totalKills;
    private TextView totalDeaths;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basicstats_fragment, container, false);

        totalKills = (TextView)container.findViewById(R.id.totalKills);
        totalDeaths = (TextView)container.findViewById(R.id.totalDeaths);

        return view;
    }





}
