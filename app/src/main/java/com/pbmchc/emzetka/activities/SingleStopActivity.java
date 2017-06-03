package com.pbmchc.emzetka.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.pbmchc.emzetka.fragments.SingleStopLinesFragment;
import com.pbmchc.emzetka.fragments.SingleStopDeparturesFragment;
import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.ScheduleViewPagerAdapter;
import com.pbmchc.emzetka.providers.OppositeStopProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleStopActivity extends AppCompatActivity
        implements OppositeStopProvider.OnOppositeStopReceived {

    @BindView(R.id.stopName) TextView stopNameTxt;
    @BindView(R.id.singleStopPager) ViewPager viewPager;
    @BindView(R.id.singleStopTabs) TabLayout tabLayout;

    private String stopId;
    private String stopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_stop);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stopId = getIntent().getStringExtra("stopId");
        stopName = getIntent().getStringExtra("stopName");
        stopNameTxt.setText(stopName);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ScheduleViewPagerAdapter adapter = new ScheduleViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SingleStopDeparturesFragment.newInstance(stopId, stopName),
                getString(R.string.single_stop_departures_title));
        adapter.addFragment(SingleStopLinesFragment.newInstance(stopId, stopName),
                getString(R.string.single_stop_lines_title));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.action_change).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return false;
            case R.id.action_change:
                OppositeStopProvider provider = new OppositeStopProvider(this);
                provider.getOppositeStop(stopName, stopId);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOppositeStopFound(String oppositeStopId) {
        Intent intent = new Intent(SingleStopActivity.this, SingleStopActivity.class);
        intent.putExtra("stopName", stopName);
        intent.putExtra("stopId", oppositeStopId);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        Toast.makeText(this, getString(R.string.single_stop_change_to_opposite), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoOppositeStopFound() {
        Toast.makeText(this, getString(R.string.single_stop_no_opposite), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOppositeStopError() {
        NetworkHelper.checkErrorCause(this);
    }
}
