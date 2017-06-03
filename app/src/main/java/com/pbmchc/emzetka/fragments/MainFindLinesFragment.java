package com.pbmchc.emzetka.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.AutocompleteAdapter;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.providers.StopsProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainFindLinesFragment extends Fragment
        implements StopsProvider.OnStopsReceivedListener {

    @BindView(R.id.findLinesLayout) LinearLayout layout;
    @BindView(R.id.startImage) ImageView img;
    @BindView(R.id.resultContainer) FrameLayout resultContainer;
    @BindView(R.id.destinationStopAtc) AutoCompleteTextView destinationStopAtc;
    @BindView(R.id.buttonSearch) Button searchButton;
    private Unbinder unbinder;

    private List<Stop> stopList;
    private String destinationStop;

    public MainFindLinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_find_lines, container, false);
        unbinder = ButterKnife.bind(this, view);

        destinationStopAtc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN &&
                        img.getVisibility() == View.VISIBLE)
                    hideStartImage();
                StopsProvider provider = new StopsProvider(MainFindLinesFragment.this);
                if (stopList == null)
                    provider.getStops();
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (destinationStop != null && !destinationStopAtc.getText().toString().equals("")) {
                    layout.setTranslationY(0);
                    img.setVisibility(View.GONE);
                    showResultsFragment();
                    resultContainer.setVisibility(View.VISIBLE);
                }
                else {
                    destinationStop = null;
                    showStartImage();
                }
            }
        });
        return view;
    }

    private void showResultsFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MainFindLinesResultsFragment fragment = new MainFindLinesResultsFragment();
        Bundle arguments = new Bundle();
        arguments.putString("stopId", destinationStop);
        fragment.setArguments(arguments);
        transaction.replace(R.id.resultContainer, fragment);
        transaction.commit();
    }

    private void showStartImage() {
        layout.animate().translationY(0);
        resultContainer.setVisibility(View.GONE);
    }

    private void hideStartImage() {
        int distance = img.getHeight() * -1;
        layout.animate().translationY(distance);
        resultContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopsReady(List<Stop> stops) {
        stopList = stops;
        AutocompleteAdapter adapter = new AutocompleteAdapter(getContext(),
                R.layout.item_stop, stopList);
        destinationStopAtc.setThreshold(1);
        destinationStopAtc.setAdapter(adapter);
        destinationStopAtc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Stop bs = (Stop) adapterView.getItemAtPosition(i);
                destinationStop = bs.getStopId();
            }
        });
    }

    @Override
    public void onStopsError() {
        NetworkHelper.checkErrorCause(getContext());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle(getActivity().getString(R.string.title_fragment_search));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
