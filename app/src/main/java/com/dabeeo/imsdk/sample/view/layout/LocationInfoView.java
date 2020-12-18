package com.dabeeo.imsdk.sample.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.dabeeo.imsdk.navigation.Location;
import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.data.LocationInfo;

public class LocationInfoView extends RelativeLayout {
    public PoiInfoListener listener;
    private TextView textName;
    private TextView textFloor;

    private AppCompatButton buttonStart;
    private AppCompatButton buttonEnd;
    private AppCompatImageView buttonClose;

    private LocationInfo locationInfo;

    public interface PoiInfoListener {
        void onSelectStart(LocationInfo location);
        void onSelectEnd(LocationInfo location);
        void onClose();
    }

    public LocationInfoView(Context context) {
        super(context);
        init();
    }

    public LocationInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_poi_info, this, false);

        addView(view);

        textName = view.findViewById(R.id.textName);
        textFloor = view.findViewById(R.id.textFloor);

        buttonStart = view.findViewById(R.id.btnStart);
        buttonStart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelectStart(locationInfo);
            }
            hide();
        });
        buttonEnd = view.findViewById(R.id.btnEnd);
        buttonEnd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelectEnd(locationInfo);
            }
            hide();
        });
        buttonClose = view.findViewById(R.id.btnClose);
        buttonClose.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClose();
            }
            hide();
        });
    }

    public void setListener(PoiInfoListener listener) {
        this.listener = listener;
    }

    public void bind(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;

        textName.setText(locationInfo.getLocation().getName());
        textFloor.setText(locationInfo.getFloorName());

        show();
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
    }
}
