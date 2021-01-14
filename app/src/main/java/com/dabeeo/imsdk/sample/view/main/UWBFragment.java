package com.dabeeo.imsdk.sample.view.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabeeo.imsdk.common.error.IMError;
import com.dabeeo.imsdk.imenum.LocationStatus;
import com.dabeeo.imsdk.imenum.TransType;
import com.dabeeo.imsdk.location.LocationCallback;
import com.dabeeo.imsdk.location.LocationSourceUwb;
import com.dabeeo.imsdk.map.MapCallback;
import com.dabeeo.imsdk.map.MapView;
import com.dabeeo.imsdk.model.common.FloorInfo;
import com.dabeeo.imsdk.model.map.Poi;
import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.view.layout.MarkerTestView;
import com.dabeeo.imsdk.sample.view.main.adapter.FloorListAdapter;
import com.dabeeo.imsdk.sample.view.main.manager.MarkerManagerWrapper;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UWBFragment extends Fragment {

    public static final String TAG = UWBFragment.class.getSimpleName();

    private View rootView;

    public UWBFragment() {
        // Required empty public constructor
    }

    public static UWBFragment newInstance() {
        return new UWBFragment();
    }

    private MapView mapView;
    private int currentFloor;
    private RecyclerView floorListView;
    private List<FloorInfo> floorInfoList;

    private LocationSourceUwb locationSourceUwb;
    private MarkerManagerWrapper markerManagerWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_uwb, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        floorListView = rootView.findViewById(R.id.recyclerFloors);
        markerManagerWrapper = MarkerManagerWrapper.getInstance(mapView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // recycler view
        floorListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.shape_divider));
        floorListView.addItemDecoration(divider);


        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = getResources().getAssets().open("reeum_m1.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();

            mapView.syncMapDataByJson(sb.toString(), mapCallback);


        } catch (Exception e) {
            e.printStackTrace();
        }

            /*String filePath = "/mnt/sdcard/dabeeomaps/";
            mapView.syncMapByLocal(filePath, mapCallback);

            locationSourceUwb = new LocationSourceUwb();
            mapView.initPosition(locationSourceUwb, locationCallback);*/


        // zoom buttons
        rootView.findViewById(R.id.btnZoomIn)
                .setOnClickListener(v -> mapView.zoomIn());

        rootView.findViewById(R.id.btnZoomOut)
                .setOnClickListener(v -> mapView.zoomOut());

    }


    private final MapCallback mapCallback = new MapCallback() {
        @Override
        public void onSuccess(List<FloorInfo> list) {
            FloorListAdapter adapter = new FloorListAdapter();
            adapter.setItems(list);
            adapter.setCallback(item -> mapView.setFloor(item.getLevel()));

            floorListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            floorInfoList = list;


            mapView.post(() -> {
                locationSourceUwb = new LocationSourceUwb();
                mapView.initPosition(locationSourceUwb, locationCallback);
            });
        }

        @Override
        public void onError(Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void changeFloor(int floor) {
            currentFloor = floor;
            ((FloorListAdapter) Objects.requireNonNull(floorListView.getAdapter())).setFloor(floor);
        }

        @Override
        public void onClick(double x, double y, Poi poi) {
            if (locationSourceUwb != null) {
                locationSourceUwb.pushLocationData(x, y, 0.0, currentFloor);
            }
        }

        @Override
        public void onLongClick(double x, double y, Poi poi) {
            if (locationSourceUwb != null) {
                locationSourceUwb.pushLocationData(x, y, 0.0, currentFloor);
            }
            drawMarker(x, y);
        }
    };


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onChangeFloor(int floor) {

        }

        @Override
        public void initLocation(double x, double y, double angle, int floor) {

        }

        @Override
        public void onLocation(double x, double y, double angle, int floor) {

        }

        @Override
        public void onLocationStatus(LocationStatus locationStatus) {

        }
    };

    private void drawMarker(double x, double y) {
        markerManagerWrapper.clearMarkers();
        int locationDiff = 80;

        final View inflateView = markerManagerWrapper.getInflateView(getActivity());
        final View javaCodeView = markerManagerWrapper.getJavaCodeView(getActivity());
        final MarkerTestView customView = markerManagerWrapper.getCustomView(getActivity());
        customView.setResource(R.drawable.icon_start);
        customView.setTitle("TITLE");

        markerManagerWrapper.createMarker(inflateView, x - (locationDiff * 2), y, currentFloor).setRotateAngle(90);
        markerManagerWrapper.createMarker(javaCodeView, x, y, currentFloor);
        markerManagerWrapper.createMarker(customView, x + (locationDiff * 2), y, currentFloor).setRotateAngle(270);
        markerManagerWrapper.createMarker(R.drawable.icon_arrive, x, (y + locationDiff * 2), 50, 50, currentFloor);
        markerManagerWrapper.createMarker(R.drawable.icon_arrive, x, (y + locationDiff * 2), 50, 50, currentFloor + 1);
        markerManagerWrapper.drawMarkers();
    }
}