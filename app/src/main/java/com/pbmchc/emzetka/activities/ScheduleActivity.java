package com.pbmchc.emzetka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pbmchc.emzetka.fragments.SingleLineDialogFragment;
import com.pbmchc.emzetka.fragments.SingleScheduleFragment;
import com.pbmchc.emzetka.helpers.DatabaseHelper;
import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.ScheduleItemsAdapter;
import com.pbmchc.emzetka.adapters.ScheduleViewPagerAdapter;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.models.Legend;
import com.pbmchc.emzetka.models.Schedule;
import com.pbmchc.emzetka.providers.AdditionalScheduleInfoProvider;
import com.pbmchc.emzetka.utils.StringUtils;
import com.pbmchc.emzetka.utils.DateUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ScheduleActivity extends AppCompatActivity implements
        AdditionalScheduleInfoProvider.OnScheduleAdditionalDataReceivedListener {

    private static final String[] DAY_TITLES = {"pon-pt", "sob", "ndz i św"};

    @BindView(R.id.stopName) TextView stopName;
    @BindView(R.id.legendsTxt) TextView legendsTxt;
    @BindView(R.id.lineName) TextView lineNameTxt;
    @BindView(R.id.stopDirection) TextView stopDirection;
    @BindView(R.id.restOfLineIndicator) TextView indicator;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.bottom_sheet) View bottomSheetLegend;
    @BindView(R.id.bottom_sheet_lines) View bottomSheetLines;
    @BindView(R.id.fabFav) FloatingActionButton fabFav;
    @BindView(R.id.restLineRv) RecyclerView linesRv;
    @BindView(R.id.schedulePager) ViewPager viewPager;
    @BindView(R.id.scheduleTabs) TabLayout tabLayout;
    @BindView(R.id.progressBarLayout) FrameLayout progressBar;

    private BottomSheetBehavior legendSheetBehaviour;
    private BottomSheetBehavior lineSheetBehaviour;
    private DatabaseHelper helper;
    private boolean hasAdditionalExtra = false;
    private boolean imageState;
    private int dayNumber;
    private int currentTime = -1;
    private AdditionalScheduleInfoProvider provider;
    public List<Legend> legends;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        legendSheetBehaviour = setupBottomSheetBehaviour(bottomSheetLegend);
        lineSheetBehaviour = setupBottomSheetBehaviour(bottomSheetLines);
        schedule = getIntent().getParcelableExtra("schedule");

        stopName.setText(schedule.getStopName());
        stopDirection.setText(schedule.getDirection());
        lineNameTxt.setText(StringUtils.fixLineNameForDisplaying(schedule.getLineNumber()));

        if (getIntent().hasExtra("hours")) hasAdditionalExtra = true;

        provider = new AdditionalScheduleInfoProvider(this);
        provider.getLegend(schedule.getScheduleId());

        helper = new DatabaseHelper(this);
        imageState = helper.getSchedule(schedule.getScheduleId());
        if (imageState) fabFav.setImageResource(R.drawable.ic_favorite_24dp);

        fabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageState = toggleImageState(imageState);
            }
        });

        fabFav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(ScheduleActivity.this,
                        getString(R.string.schedule_fav_info_message),
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public void onLegendReady(List<Legend> legends) {
        this.legends = legends;
        Date date = new Date();
        dayNumber = DateUtils.calculateDayIndex(date);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab = tabLayout.getTabAt(dayNumber-1);

        if (tab != null) tab.select();

        if (legends == null || legends.toString().equals("[]") )
            legendsTxt.setText(getString(R.string.schedule_no_legend_message));
        else{
            for (Legend l : legends){
                legendsTxt.append(l.toString());
            }
        }
    }

    private boolean toggleImageState(boolean imageState) {
        if (!imageState) {
            fabFav.setImageResource(R.drawable.ic_favorite_24dp);
            addScheduleToFavourites();
            imageState = true;
            Toast.makeText(ScheduleActivity.this,
                    getString(R.string.schedule_add_to_favs_message),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            fabFav.setImageResource(R.drawable.ic_favorite_border_24dp);
            helper.deleteSchedule(schedule.getScheduleId());
            imageState = false;
            Toast.makeText(ScheduleActivity.this,
                    getString(R.string.schedule_remove_from_favs_message),
                    Toast.LENGTH_SHORT).show();
        }
        return imageState;
    }

    private BottomSheetBehavior setupBottomSheetBehaviour(View view) {
        BottomSheetBehavior behavior = BottomSheetBehavior.from(view);
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        return behavior;
    }

    private void setupViewPager(ViewPager viewPager){
        ScheduleViewPagerAdapter adapter = new ScheduleViewPagerAdapter(getSupportFragmentManager());
        for (int i = 1; i<=DAY_TITLES.length; i++) {
            if (hasAdditionalExtra && i == dayNumber)
                adapter.addFragment(SingleScheduleFragment.newInstance(schedule.getScheduleId(), i,
                        getIntent().getStringExtra("hours"),
                        getIntent().getStringExtra("minutes")), DAY_TITLES[i - 1]);
            else
                adapter.addFragment(SingleScheduleFragment.newInstance(schedule.getScheduleId(), i),
                        DAY_TITLES[i - 1]);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.action_legend).setVisible(true);
        menu.findItem(R.id.action_line).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_legend:
                toggleBottomSheet(legendSheetBehaviour);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_line:
                provider.getRestOfLine(schedule.getScheduleId());
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onRestOfLineReady(final List<Stop> stops) {
        if (StringUtils.tryParse(stops.get(0).getStopTime()))
            currentTime = Integer.parseInt(stops.get(0).getStopTime());

        ScheduleItemsAdapter stopAdapter;
        if (stops.size() > 6)
            stopAdapter = new ScheduleItemsAdapter(ScheduleActivity.this,
                    stops.subList(1,6), currentTime, false);
        else {
            stopAdapter = new ScheduleItemsAdapter(ScheduleActivity.this,
                    stops.subList(1, stops.size()), currentTime, true);
            indicator.setTextSize(12);
            indicator.setAllCaps(true);
            indicator.setText(getString(R.string.schedule_end_of_route_message));
        }
        linesRv.setAdapter(stopAdapter);
        linesRv.setLayoutManager(new LinearLayoutManager(ScheduleActivity.this));
        toggleBottomSheet(lineSheetBehaviour);
        Button showFullLineBtn = (Button) findViewById(R.id.showFullLineBtn);
        showFullLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment fragment =
                        SingleLineDialogFragment
                                .newInstance(currentTime, schedule.getScheduleId().split("\\|")[1],
                                        getIntent().getStringExtra("direction"),
                                        stops.get(0).getStopOrder());
                fragment.show(getSupportFragmentManager(), fragment.getTag());
                lineSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    @Override
    public void onScheduleAdditionalDataError() {
        hideProgressBar();
        NetworkHelper.checkErrorCause(this);
    }

    private void toggleBottomSheet(BottomSheetBehavior behavior) {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (behavior.equals(legendSheetBehaviour))
                lineSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
            else
                legendSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void addScheduleToFavourites(){
        helper.insertSchedule(schedule);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
