package com.pbmchc.emzetka.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.activities.ScheduleActivity;
import com.pbmchc.emzetka.models.Schedule;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.LineItemsAdapter;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.providers.AvailableLinesProvider;
import com.pbmchc.emzetka.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SingleStopLinesFragment extends Fragment implements
        AvailableLinesProvider.OnAvailableLinesReceivedListener {

    @BindView(R.id.availableLinesRv) RecyclerView availableLines;
    private Unbinder unbinder;

    private String stopName;
    private String stop;
    private AvailableLinesProvider provider;

    public SingleStopLinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_single_stop_lines, container, false);
        unbinder = ButterKnife.bind(this, view);

        stop = getArguments().getString("stop");
        stopName = getArguments().getString("stopName");

        provider = new AvailableLinesProvider(this);
        provider.getAvailableLines(stop, false);
        return view;
    }

    public static SingleStopLinesFragment newInstance(String stop, String stopName){
        SingleStopLinesFragment fragment = new SingleStopLinesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stop", stop);
        bundle.putString("stopName", stopName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAvailableLinesReady(final List<Line> lines) {

        LineItemsAdapter adapter = new LineItemsAdapter(getContext(), lines);
        availableLines.setAdapter(adapter);
        availableLines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new LineItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Line line = lines.get(position);
                Intent intent = new Intent(getContext(), ScheduleActivity.class);
                String stopLineId = stop + "|" + line.getLineId();
                Bundle extras = new Bundle();
                Schedule schedule = new Schedule();
                schedule.setDirection(line.getEndStation());
                schedule.setStopName(stopName);
                schedule.setScheduleId(stopLineId);
                schedule.setLineNumber(StringUtils.extractLineNumber(stopLineId));
                extras.putParcelable("schedule", schedule);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAvailableLinesError() {
        NetworkHelper.checkErrorCause(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        provider.cancelOngoingRequest();
        unbinder.unbind();
    }
}
