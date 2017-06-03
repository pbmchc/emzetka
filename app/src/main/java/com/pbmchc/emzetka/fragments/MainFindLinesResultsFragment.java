package com.pbmchc.emzetka.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pbmchc.emzetka.BusRestClient;
import com.pbmchc.emzetka.activities.SingleLineActivity;
import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.LineItemsAdapter;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.providers.AvailableLinesProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFindLinesResultsFragment extends Fragment
        implements AvailableLinesProvider.OnAvailableLinesReceivedListener {

    @BindView(R.id.progressBarLayout) FrameLayout progressBar;
    @BindView(R.id.filteredLinesRv) RecyclerView recyclerView;
    private Unbinder unbinder;

    private Line line;

    public MainFindLinesResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_find_lines_results, container, false);
        unbinder = ButterKnife.bind(this, view);
        AvailableLinesProvider provider = new AvailableLinesProvider(this);
        provider.getAvailableLines(getArguments().getString("stopId"), true);
        return view;
    }

    @Override
    public void onAvailableLinesReady(final List<Line> lines) {
        progressBar.setVisibility(View.GONE);
        LineItemsAdapter adapter = new LineItemsAdapter(getContext(), lines);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new LineItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                line = lines.get(position);
                Intent intent = new Intent(getContext(), SingleLineActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("line", line);
                extras.putString("callerId", getArguments().getString("stopId"));
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAvailableLinesError() {
        NetworkHelper.checkErrorCause(getContext());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
