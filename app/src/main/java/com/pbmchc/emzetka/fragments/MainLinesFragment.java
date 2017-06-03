package com.pbmchc.emzetka.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pbmchc.emzetka.activities.SingleLineActivity;
import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.LineItemsAdapter;
import com.pbmchc.emzetka.adapters.SectionedRecyclerViewAdapter;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.providers.LinesProvider;
import com.pbmchc.emzetka.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainLinesFragment extends Fragment implements LinesProvider.OnLinesReceivedListener {

    @BindView(R.id.busLinesRview) RecyclerView rvBusLines;
    @BindView(R.id.progressBarLayout) FrameLayout progressBar;
    private Unbinder unbinder;

    public MainLinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_lines, container, false);
        unbinder = ButterKnife.bind(this, view);
        LinesProvider provider = new LinesProvider(this);
        provider.getLines();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar()
                    .setTitle(getActivity().getString(R.string.title_fragment_lines));
        }
    }

    @Override
    public void onLinesReady(final List<Line> lines) {
        LineItemsAdapter lineItemsAdapter = new LineItemsAdapter(getContext(), lines);
        rvBusLines.setAdapter(lineItemsAdapter);
        rvBusLines.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBusLines.setHasFixedSize(true);
        setUpSectionsAdapter(lines, lineItemsAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void setUpSectionsAdapter(final List<Line> lines, LineItemsAdapter lineItemsAdapter) {
        List<SectionedRecyclerViewAdapter.Section> sections = new ArrayList<>();
        for(int i = 0; i<= lines.size()-2; i+=2){
            String name = StringUtils.fixLineNameForDisplaying(lines.get(i).getLineName());
            sections.add(new SectionedRecyclerViewAdapter.Section(i,
                    getString(R.string.recyclerview_section_title) + " " + name));
        }
        SectionedRecyclerViewAdapter.Section[] dummy = new SectionedRecyclerViewAdapter.Section[sections.size()];
        final SectionedRecyclerViewAdapter mSectionedAdapter = new
                SectionedRecyclerViewAdapter(getContext(),R.layout.item_line_section,R.id.section_text, lineItemsAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        lineItemsAdapter.setOnItemClickListener(new LineItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Line line = lines.get(mSectionedAdapter.sectionedPositionToPosition(position));
                Intent intent = new Intent(getContext(), SingleLineActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("line", line);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
        rvBusLines.setAdapter(mSectionedAdapter);
    }

    @Override
    public void onLinesError() {
        NetworkHelper.checkErrorCause(getContext());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
