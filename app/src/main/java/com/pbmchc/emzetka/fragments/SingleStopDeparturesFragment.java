package com.pbmchc.emzetka.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.activities.ScheduleActivity;
import com.pbmchc.emzetka.models.Schedule;
import com.pbmchc.emzetka.models.UpcomingDeparture;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.SingleStopDepartureItemAdapter;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.providers.UpcomingDeparturesProvider;
import com.pbmchc.emzetka.utils.StringUtils;
import com.pbmchc.emzetka.utils.DateUtils;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SingleStopDeparturesFragment extends Fragment implements
        UpcomingDeparturesProvider.OnUpcomingDeparturesReceivedListener {

    @BindView(R.id.rvClosest) RecyclerView rvUpcoming;
    @BindView(R.id.progressBarLayout) FrameLayout progressBar;
    @BindView(R.id.noUpcomingDeparturesLayout) FrameLayout noDeparturesLayout;
    private Unbinder unbinder;

    private SingleStopDepartureItemAdapter adapter;
    private BroadcastReceiver receiver;
    private UpcomingDeparturesProvider provider;

    public SingleStopDeparturesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_single_stop_departures, container, false);
        unbinder = ButterKnife.bind(this, view);
        Date date = new Date();
        int day = DateUtils.calculateDayIndex(date);
        int hours = date.getHours();
        String hoursFrom = StringUtils.calculateHours(hours);
        hours = hours == 23 ? 0 : hours+1;
        String hoursTo = StringUtils.calculateHours(hours);
        provider = new UpcomingDeparturesProvider(this);
        provider.getUpcomingDepartures(getArguments().getString("stop"), hoursFrom, hoursTo, day);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
            }
        };
        getContext().registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));

    }

    @Override
    public void onStop(){
        super.onStop();
        try {
            getContext().unregisterReceiver(receiver);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void onUpcomingDeparturesReady(final List<UpcomingDeparture> departures, final List<Line> lines) {
        adapter = new SingleStopDepartureItemAdapter(getContext(), departures, lines);
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcoming.setAdapter(adapter);

        adapter.setOnItemClickListener(new SingleStopDepartureItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                UpcomingDeparture departure = departures.get(position);
                Line line = lines.get(position);
                Intent intent = new Intent(getContext(), ScheduleActivity.class);
                Bundle extras = new Bundle();
                Schedule schedule = new Schedule();
                schedule.setDirection(line.getEndStation());
                schedule.setStopName(getArguments().getString("stopName"));
                schedule.setScheduleId(departure.getStopLineId());
                schedule.setLineNumber(StringUtils.extractLineNumber(departure.getStopLineId()));
                extras.putParcelable("schedule", schedule);
                extras.putString("hours", departure.getDepartureHour());
                extras.putString("minutes", departure.getDepartureMinutes());
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNoUpcomingDeparturesAvailable() {
        noDeparturesLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUpcomingDeparturesError() {
        NetworkHelper.checkErrorCause(getContext());
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        provider.cancelOngoingRequests();
        unbinder.unbind();
    }

    public static SingleStopDeparturesFragment newInstance(String stop, String stopName){
        SingleStopDeparturesFragment fragment = new SingleStopDeparturesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stop", stop);
        bundle.putString("stopName", stopName);
        fragment.setArguments(bundle);
        return fragment;
    }
}
