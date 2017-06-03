package com.pbmchc.emzetka.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.ScheduleViewPagerAdapter;
import com.pbmchc.emzetka.utils.ImageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OnboardingActivity extends AppCompatActivity {

    @BindView(R.id.intro_btn_next) Button nextButton;
    @BindView(R.id.intro_btn_skip) Button skipButton;
    @BindView(R.id.intro_btn_finish) Button finishButton;
    @BindView(R.id.onboardContainer) ViewPager mViewPager;
    @BindViews({R.id.intro_indicator_0, R.id.intro_indicator_1, R.id.intro_indicator_2})
    List<ImageView> indicators;

    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        setupViewPager(mViewPager);
        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);
                nextButton.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                finishButton.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
                updateIndicators(page);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //SharedPrefHelper.saveSharedSetting(OnboardingActivity.this, "first_time", "false");
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //SharedPrefHelper.saveSharedSetting(OnboardingActivity.this, "first_time", "false");
            }
        });

    }

    protected void updateIndicators(int position){

        for (int i = 0; i < indicators.size(); i++) {
            indicators.get(i).setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    private void setupViewPager(ViewPager viewPager){
        ScheduleViewPagerAdapter adapter = new ScheduleViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PlaceholderFragment.newInstance(0), "1");
        adapter.addFragment(PlaceholderFragment.newInstance(1), "1");
        adapter.addFragment(PlaceholderFragment.newInstance(2), "1");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        @BindView(R.id.section_label) TextView sectionTxt;
        @BindView(R.id.section_description) TextView descriptionTxt;
        @BindView(R.id.onbardingImage) ImageView onboardingImage;
        private Unbinder unbinder;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);
            unbinder = ButterKnife.bind(this, rootView);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            sectionTxt.setText(getResources().getStringArray(R.array.titles)[sectionNumber]);
            descriptionTxt.setText(getResources().getStringArray(R.array.descriptions)[sectionNumber]);
            String imageTitle = getResources().getStringArray(R.array.images)[sectionNumber];
            onboardingImage.setImageResource(ImageUtils.getImageResourceId(rootView.getContext(), imageTitle));
            return rootView;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }
    }
}
