package com.pbmchc.emzetka.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.pbmchc.emzetka.helpers.DatabaseHelper;
import com.pbmchc.emzetka.activities.ScheduleActivity;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.FavouritesItemAdapter;
import com.pbmchc.emzetka.models.Schedule;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainFavouritesFragment extends Fragment implements ActionMode.Callback {

    @BindView(R.id.noFavsLayout) FrameLayout noFavouritesLayout;
    @BindView(R.id.favRecyclerView) RecyclerView recyclerView;
    private Unbinder unbinder;

    private DatabaseHelper helper;
    private List<Schedule> scheduleList;
    private FavouritesItemAdapter adapter;
    private ActionMode actionMode;

    public MainFavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_favourites, container, false);
        unbinder = ButterKnife.bind(this, view);
        helper = new DatabaseHelper(getContext());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle(getString(R.string.title_fragment_favs));
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleList = helper.getSchedules();
        if (scheduleList.size() == 0) showNoFavouritesLayout();
        else {
            adapter = new FavouritesItemAdapter(getContext(), scheduleList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter.setOnItemClickListener(new FavouritesItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    if (actionMode == null) {
                        Schedule schedule = scheduleList.get(position);
                        Intent intent = new Intent(getContext(), ScheduleActivity.class);
                        Bundle extras = new Bundle();
                        extras.putParcelable("schedule", schedule);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                    else {
                        myToggleSelection(position);
                        if(adapter.getSelectedItemCount() == 0)
                            actionMode.finish();
                    }
                }
                @Override
                public void onItemLongClick(View itemView, final int position) {
                    if(actionMode == null){
                        actionMode = getActivity().startActionMode(MainFavouritesFragment.this);
                        myToggleSelection(position);
                    }
                }
            });
        }
    }

    private void myToggleSelection(int idx) {
        adapter.toggleSelection(idx);
        String title = "Rozkłady: " + String.valueOf(adapter.getSelectedItemCount());
        if (adapter.getSelectedItemCount() > 0)
            actionMode.setTitle(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (actionMode != null)
            actionMode.finish();
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_action_mode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.statusBarActionMode));
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.favourites_remove_title));
                builder.setMessage(getString(R.string.favourites_remove_message));
                builder.setCancelable(true);

                builder.setPositiveButton(getString(R.string.favourites_remove_button_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        final List<Integer> selectedItemPositions = adapter.getSelectedItems();
                        int currPos;
                        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                            currPos = selectedItemPositions.get(i);
                            Schedule schedule = scheduleList.get(currPos);
                            helper.deleteSchedule(schedule.getScheduleId());
                            adapter.deleteItem(currPos);
                        }
                        if (scheduleList.size() == 0)
                            showNoFavouritesLayout();
                        actionMode.finish();
                    }
                });

                builder.setNegativeButton(getString(R.string.favourites_cancel_button_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        actionMode.finish();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                    }
                });
                dialog.show();
                return true;
            default:
                return false;
        }
    }

    protected void showNoFavouritesLayout(){
        noFavouritesLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        adapter.clearSelections();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}