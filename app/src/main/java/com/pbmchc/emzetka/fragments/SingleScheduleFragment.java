package com.pbmchc.emzetka.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pbmchc.emzetka.activities.ScheduleActivity;
import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.DepartureItemsAdapter;
import com.pbmchc.emzetka.models.Departure;
import com.pbmchc.emzetka.providers.DeparturesProvider;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SingleScheduleFragment extends Fragment
        implements DeparturesProvider.OnDeparturesReceivedListener {

    @BindView(R.id.rvSchedule) RecyclerView rvSchedules;
    @BindView(R.id.noDeparturesLayout) FrameLayout noSchedule;
    private Unbinder unbinder;

    private DeparturesProvider provider;

    public SingleScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_schedule, container, false);
        unbinder = ButterKnife.bind(this, view);
        String stopId = getArguments().getString("stop");
        int day = getArguments().getInt("day");
        provider = new DeparturesProvider(this);
        provider.getDepartures(stopId, day);
        return view;

    }

    public static SingleScheduleFragment newInstance(String stop, int day, String hours, String minutes){
        SingleScheduleFragment fragment = new SingleScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stop", stop);
        bundle.putInt("day", day);
        bundle.putString("hours", hours);
        bundle.putString("minutes", minutes);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SingleScheduleFragment newInstance(String stop, int day){
        SingleScheduleFragment fragment = new SingleScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stop", stop);
        bundle.putInt("day", day);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDeparturesReady(List<Departure> departures) {
        DepartureItemsAdapter adapter;
        if (getArguments().getString("hours") != null)
            adapter = new DepartureItemsAdapter(getContext(), departures,
                    ((ScheduleActivity)getActivity()).legends, getArguments().getString("hours"),
                    getArguments().getString("minutes"));
        else
            adapter = new DepartureItemsAdapter(getContext(), departures,
                    ((ScheduleActivity)getActivity()).legends);
        rvSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedules.setAdapter(adapter);
        ((ScheduleActivity)getActivity()).hideProgressBar();
    }

    @Override
    public void onNoDeparturesAvailable() {
        rvSchedules.setVisibility(View.GONE);
        noSchedule.setVisibility(View.VISIBLE);
        ((ScheduleActivity)getActivity()).hideProgressBar();

    }

    @Override
    public void onDeparturesError() {
        ((ScheduleActivity)getActivity()).hideProgressBar();
        NetworkHelper.checkErrorCause(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        provider.cancelOngoingRequest();
        unbinder.unbind();
    }
}
