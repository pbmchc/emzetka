package com.pbmchc.emzetka.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pbmchc.emzetka.helpers.NetworkHelper;
import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.adapters.ScheduleItemsAdapter;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.providers.SingleLineProvider;
import com.pbmchc.emzetka.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SingleLineDialogFragment extends BottomSheetDialogFragment
        implements SingleLineProvider.OnStopListReceivedListener {

    @BindView(R.id.fullLineRv) RecyclerView fullLineRv;
    @BindView(R.id.lineNumber) TextView lineNameTxt;
    @BindView(R.id.lineDirection) TextView lineDirectionTxt;
    @BindView(R.id.closeModalBtn) TextView closeModalBtn;
    private Unbinder unbinder;

    private View contentView;

    @Override
    public void setupDialog(final Dialog dialog, int style){
        super.setupDialog(dialog,style);
        contentView = View.inflate(getContext(), R.layout.fragment_full_line_bottomsheet, null);
        unbinder = ButterKnife.bind(this, contentView);

        String line = getArguments().getString("line");

        lineNameTxt.setText(StringUtils.fixLineNameForDisplaying(line.substring(line.length()-2)));
        lineDirectionTxt.setText(getArguments().getString("direction"));
        fullLineRv.setMinimumHeight(getContext().getResources().getDisplayMetrics().heightPixels);

        SingleLineProvider provider = new SingleLineProvider(this);
        provider.getStopsByLine(line);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                if(bottomSheet!=null) {
                    BottomSheetBehavior.from(bottomSheet)
                            .setPeekHeight(getContext()
                            .getResources().getDisplayMetrics().heightPixels);
                    BottomSheetBehavior.from(bottomSheet).setHideable(false);
                }
            }
        });

        closeModalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(contentView);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public static SingleLineDialogFragment newInstance(int currentStopTime, String lineId,
                                                       String direction, int stopOrder) {
        Bundle args = new Bundle();
        args.putInt("stopTime", currentStopTime);
        args.putString("line", lineId);
        args.putString("direction", direction);
        args.putInt("stopOrder", stopOrder);
        SingleLineDialogFragment fragment = new SingleLineDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStopListReady(List<Stop> stops) {
        ScheduleItemsAdapter adapter =
                new ScheduleItemsAdapter(contentView.getContext(), stops,
                        getArguments().getInt("stopTime"), true, getArguments().getInt("stopOrder"));
        fullLineRv.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(contentView.getContext());
        manager.scrollToPosition(getArguments().getInt("stopOrder"));
        fullLineRv.setLayoutManager(manager);
    }

    @Override
    public void onStopListError() {
        NetworkHelper.checkErrorCause(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
