package com.dabeeo.imsdk.sample.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.data.LocationInfo;

public class LocationSettingView extends RelativeLayout {
    public LocationSettingViewListener listener;
    private TextView textStartLocation;
    private TextView textEndLocation;

    private AppCompatImageButton buttonSwap;
    private AppCompatImageButton buttonClose;

    private LocationInfo startLocation;
    private LocationInfo endLocation;

    public interface LocationSettingViewListener {
        void onSwap(LocationInfo start, LocationInfo end);

        void onClose();
    }

    public LocationSettingView(Context context) {
        super(context);
        init();
    }

    public LocationSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_location_setting, this, false);

        addView(view);

        textStartLocation = view.findViewById(R.id.textStartLocation);
        textEndLocation = view.findViewById(R.id.textEndLocation);

        buttonSwap = view.findViewById(R.id.btnSwap);
        buttonSwap.setOnClickListener(v -> {
            LocationInfo temp = startLocation;
            startLocation = endLocation;
            endLocation = temp;
            bind(startLocation, endLocation);

            if (listener != null) {
                listener.onSwap(startLocation, endLocation);
            }
        });
        buttonClose = view.findViewById(R.id.btnClose);
        buttonClose.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClose();
            }
            hide();
        });
    }

    public void setListener(LocationSettingViewListener listener) {
        this.listener = listener;
    }

    public void bind(LocationInfo start, LocationInfo end) {
        startLocation = start;
        endLocation = end;

        if (start != null) {
            String str = String.format("[%s] %s", start.getFloorName(), start.getLocation().getName());
            textStartLocation.setText(str);
        } else {
            textStartLocation.setText("");
        }

        if (end != null) {
            String str = String.format("[%s] %s", end.getFloorName(), end.getLocation().getName());
            textEndLocation.setText(str);
        } else {
            textEndLocation.setText("");
        }

        show();
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
    }


}
