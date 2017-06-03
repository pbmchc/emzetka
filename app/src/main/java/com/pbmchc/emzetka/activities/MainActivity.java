package com.pbmchc.emzetka.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pbmchc.emzetka.fragments.MainFavouritesFragment;
import com.pbmchc.emzetka.fragments.MainFindLinesFragment;
import com.pbmchc.emzetka.fragments.MainLinesFragment;
import com.pbmchc.emzetka.fragments.MainStopsFragment;
import com.pbmchc.emzetka.helpers.SharedPrefHelper;
import com.pbmchc.emzetka.mzkandroid.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    private boolean isPressedOnce;
    private int selectedTab;
    private Toast leavingToast;
    private Handler exitHandler = new Handler();
    private Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            isPressedOnce = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        boolean isFirstTime = Boolean.valueOf(SharedPrefHelper.readSharedSetting(this, "first_time", "true"));
        if (isFirstTime) showOnboardingAcivity();

        if (savedInstanceState == null) {
            MainFindLinesFragment mainFindLinesFragment = new MainFindLinesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mainFindLinesFragment);
            transaction.commit();
        }
        else if (savedInstanceState.getInt("tab") != 0){
            bottomNavigationView.getMenu().findItem(savedInstanceState.getInt("tab")).setChecked(true);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        selectedTab = item.getItemId();
                        switch (item.getItemId()) {
                            case R.id.action_lines:
                                MainLinesFragment mainLinesFragment = new MainLinesFragment();
                                transaction.replace(R.id.fragment_container, mainLinesFragment);
                                transaction.commit();
                                break;

                            case R.id.action_stops:
                                MainStopsFragment mainStopsFragment = new MainStopsFragment();
                                transaction.replace(R.id.fragment_container, mainStopsFragment);
                                transaction.commit();
                                break;

                            case R.id.action_favs:
                                MainFavouritesFragment mainFavouritesFragment = new MainFavouritesFragment();
                                transaction.replace(R.id.fragment_container, mainFavouritesFragment);
                                transaction.commit();
                                break;

                            case R.id.action_search:
                                MainFindLinesFragment searchFragment = new MainFindLinesFragment();
                                transaction.replace(R.id.fragment_container, searchFragment);
                                transaction.commit();
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", selectedTab);
    }

    private void showOnboardingAcivity() {
            Intent introIntent = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(introIntent);
    }

    @Override
    public void onBackPressed() {
        if(isPressedOnce) {
            leavingToast.cancel();
            super.onBackPressed();
        }
        else {
            isPressedOnce = true;
            leavingToast = Toast.makeText(this, getString(R.string.main_app_leave_message),
                    Toast.LENGTH_SHORT);
            leavingToast.show();
            exitHandler.postDelayed(exitRunnable, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(exitHandler != null)
            exitHandler.removeCallbacks(exitRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
