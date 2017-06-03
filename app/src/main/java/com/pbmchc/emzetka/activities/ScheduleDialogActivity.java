package com.pbmchc.emzetka.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pbmchc.emzetka.mzkandroid.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleDialogActivity extends AppCompatActivity {

    @BindView(R.id.legend) TextView legend;
    @BindView(R.id.hours) TextView hours;
    @BindView(R.id.minutes) TextView minutes;
    @BindView(R.id.finishButton) Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_dialog);
        ButterKnife.bind(this);

        if (!getIntent().getStringExtra("legend").equals(""))
            legend.setText(getIntent().getStringExtra("legend"));
        else
            legend.setText(getString(R.string.schedule_no_legend_message));

        hours.setText(getIntent().getStringExtra("hours"));
        minutes.setText(getIntent().getStringExtra("minutes"));

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
