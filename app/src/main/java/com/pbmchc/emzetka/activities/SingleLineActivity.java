package com.pbmchc.emzetka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.models.Schedule;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.SingleLineStopItemsAdapter;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.providers.SingleLineProvider;
import com.pbmchc.emzetka.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleLineActivity extends AppCompatActivity
        implements SingleLineProvider.OnStopListReceivedListener {

    @BindView(R.id.busStopsRv) RecyclerView busStopRv;
    @BindView(R.id.lineNumber) TextView lineNameTxt;
    @BindView(R.id.lineDirection) TextView lineDirectionTxt;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progressBarLayout) FrameLayout progressBar;

    private List<Stop> stopList;
    private Line currentLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_line);
        ButterKnife.bind(this);

        currentLine = getIntent().getParcelableExtra("line");
        String lineName = StringUtils.fixLineNameForDisplaying(currentLine.getLineName());
        lineNameTxt.setText(lineName);
        lineDirectionTxt.setText(currentLine.getEndStation());

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SingleLineProvider provider = new SingleLineProvider(this);
        provider.getStopsByLine(currentLine.getLineId());
    }

    @Override
    public void onStopListReady(List<Stop> stops) {
        stopList = stops;
        SingleLineStopItemsAdapter stopsItemsAdapter;
        LinearLayoutManager manager = new LinearLayoutManager(SingleLineActivity.this);
        if (getIntent().hasExtra("callerId")){
            int caller = findCallingStopOrder(stops, getIntent().getStringExtra("callerId"));
            stopsItemsAdapter = new SingleLineStopItemsAdapter(SingleLineActivity.this, stops, caller);
            if (caller > 6) manager.scrollToPosition(caller - 6);
        }
        else
            stopsItemsAdapter = new SingleLineStopItemsAdapter(SingleLineActivity.this, stops);

        busStopRv.setLayoutManager(manager);
        busStopRv.setAdapter(stopsItemsAdapter);
        busStopRv.setHasFixedSize(true);

        stopsItemsAdapter.setOnItemClickListener(new SingleLineStopItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Stop stop = stopList.get(position);
                Intent intent = new Intent(SingleLineActivity.this, ScheduleActivity.class);
                Bundle extras = new Bundle();
                Schedule schedule = new Schedule();
                schedule.setDirection(currentLine.getEndStation());
                schedule.setStopName(stop.getStopName());
                schedule.setScheduleId(stop.getStopLineId());
                schedule.setLineNumber(StringUtils.extractLineNumber(stop.getStopLineId()));
                extras.putParcelable("schedule", schedule);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
        hideProgressBar();
    }

    private int findCallingStopOrder(List<Stop> stops, String pattern) {
        for (Stop s : stops) {
            if (s.getStopId().equals(pattern))
                return s.getStopOrder();
        }
        return 0;
    }

    @Override
    public void onStopListError() {
        hideProgressBar();
        NetworkHelper.checkErrorCause(this);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.action_map).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_map:
                if (stopList != null) {
                    Intent mapIntent = new Intent(SingleLineActivity.this, SingleLineMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("stops", (ArrayList<Stop>) stopList);
                    bundle.putString("line", currentLine.getLineName());
                    bundle.putString("direction", currentLine.getEndStation());
                    mapIntent.putExtras(bundle);
                    startActivity(mapIntent);
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
