package com.pbmchc.emzetka.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pbmchc.emzetka.activities.NearStopsMapActivity;
import com.pbmchc.emzetka.activities.SingleStopActivity;
import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.StopItemsAdapter;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.providers.StopsProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainStopsFragment extends Fragment implements StopsProvider.OnStopsReceivedListener {

    @BindView(R.id.searchBox) EditText searchBox;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.progressBarLayout) FrameLayout progressBar;
    @BindView(R.id.stopsRv) RecyclerView stopRv;
    private Unbinder unbinder;

    private StopItemsAdapter adapter;

    public MainStopsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_stops, container, false);
        unbinder = ButterKnife.bind(this, view);

        StopsProvider provider = new StopsProvider(this);
        provider.getStops();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(adapter != null)
                    adapter.getFilter().filter(searchBox.getText());
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getContext(),
                        getString(R.string.main_stops_fragment_fab_info), Toast.LENGTH_LONG).show();
                return true;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar()
                    .setTitle(getActivity().getString(R.string.title_fragment_stops));
        }
    }

    @Override
    public void onStopsReady(List<Stop> stops) {

        adapter = new StopItemsAdapter(getContext(), stops);
        stopRv.setAdapter(adapter);
        stopRv.setLayoutManager(new LinearLayoutManager(getContext()));
        stopRv.setHasFixedSize(true);

        adapter.setOnItemClickListener(new StopItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(getContext(), SingleStopActivity.class);
                Stop stop = adapter.getItem(position);
                intent.putExtra("stopId", stop.getStopId());
                intent.putExtra("stopName", stop.getStopName());
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NearStopsMapActivity.class);
                startActivity(intent);
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStopsError() {
        progressBar.setVisibility(View.GONE);
        NetworkHelper.checkErrorCause(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
