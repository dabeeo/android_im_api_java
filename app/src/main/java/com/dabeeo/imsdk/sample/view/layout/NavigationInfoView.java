package com.dabeeo.imsdk.sample.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import com.dabeeo.imsdk.imenum.TransType;
import com.dabeeo.imsdk.navigation.PathResult;
import com.dabeeo.imsdk.navigation.data.PathData;
import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.util.TimeUtils;

public class NavigationInfoView extends LinearLayout {

    public interface NavigationInfoListener {
        void onStartPreview();

        void onStartNavigation();

        void onChangeTransType(TransType transType);
    }

    private NavigationInfoListener listener;
    private RadioGroup radioGroup;
    private AppCompatImageButton buttonDetail;

    private TextView textTime;
    private TextView textDistance;

    private AppCompatButton buttonStartPreview;
    private AppCompatButton buttonStartNavigation;


    public NavigationInfoView(Context context) {
        super(context);
        init();
    }

    public NavigationInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_navigation_info, this, false);

        addView(view);

        textTime = view.findViewById(R.id.textTime);
        textDistance = view.findViewById(R.id.textDistance);

        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            TransType transType;
            if (checkedId == R.id.radioButtonAll) {
                transType = TransType.ALL;
            } else if (checkedId == R.id.radioButtonEscalator) {
                transType = TransType.ESCALATOR;
            } else if (checkedId == R.id.radioButtonElevator) {
                transType = TransType.ELEVATOR;
            } else if (checkedId == R.id.radioButtonStairs) {
                transType = TransType.STAIRS;
            } else {
                transType = TransType.ALL;
            }

            if (listener != null) {
                listener.onChangeTransType(transType);
            }
        });
        buttonDetail = view.findViewById(R.id.btnDetail);

        buttonStartPreview = view.findViewById(R.id.btnStartPreview);
        buttonStartPreview.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStartPreview();
            }
        });

        buttonStartNavigation = view.findViewById(R.id.btnStartNavigation);
        buttonStartNavigation.setEnabled(false);
    }

    public void bind(PathData pathData) {
        int totalSeconds = pathData.getTotalEstimatedTimeSeconds();
        textTime.setText(TimeUtils.timeToString(totalSeconds));

        double totalMeters = Math.ceil(pathData.getTotalDistance() / 100);
        if (totalMeters == 0.0) {
            totalMeters = 1.0;
        }
        textDistance.setText(totalMeters + "m");

        show();
    }

    public void setListener(NavigationInfoListener listener) {
        this.listener = listener;
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
    }
}
