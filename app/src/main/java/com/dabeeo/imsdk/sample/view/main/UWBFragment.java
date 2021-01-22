package com.dabeeo.imsdk.sample.view.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabeeo.imsdk.imenum.LocationStatus;
import com.dabeeo.imsdk.location.LocationCallback;
import com.dabeeo.imsdk.location.LocationSourceUwb;
import com.dabeeo.imsdk.map.MapCallback;
import com.dabeeo.imsdk.map.MapView;
import com.dabeeo.imsdk.map.interfaces.IMMoveListener;
import com.dabeeo.imsdk.model.common.FloorInfo;
import com.dabeeo.imsdk.model.gl.Marker;
import com.dabeeo.imsdk.model.map.Poi;
import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.view.layout.MarkerTestView;
import com.dabeeo.imsdk.sample.view.main.adapter.FloorListAdapter;
import com.dabeeo.imsdk.sample.view.main.manager.MarkerManagerWrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class UWBFragment extends Fragment implements View.OnClickListener, IMMoveListener {

    public static final String TAG = UWBFragment.class.getSimpleName();

    private View rootView;
    private Marker marker;

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
    private double x = 100.0;

    private MarkerManagerWrapper markerManagerWrapper;

    private Button addRotate;
    private Button minusRotate;
    private Button zoomIn;
    private Button zoomOut;

    private TextView locationTextView;
    private TextView rotationTextView;
    private TextView zoomLevelTextView;

    private Button rotateActive;
    private Button zoomActive;

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

        addRotate = rootView.findViewById(R.id.addRotate);
        minusRotate = rootView.findViewById(R.id.minusRotate);
        zoomIn = rootView.findViewById(R.id.zoomIn);
        zoomOut = rootView.findViewById(R.id.zoomOut);

        locationTextView = rootView.findViewById(R.id.locationTextView);
        rotationTextView = rootView.findViewById(R.id.rotationTextView);
        zoomLevelTextView = rootView.findViewById(R.id.zoomLevelTextView);

        rotateActive = rootView.findViewById(R.id.rotateActive);
        zoomActive = rootView.findViewById(R.id.zoomActive);

        addRotate.setOnClickListener(this);
        minusRotate.setOnClickListener(this);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        rotateActive.setOnClickListener(this);
        zoomActive.setOnClickListener(this);
        mapView.setOnMoveListener(this);

        markerManagerWrapper = new MarkerManagerWrapper(mapView);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        markerManagerWrapper.clearMarkers();
//        mapView.clearMemory();
        Log.i("SAHONMU", "onDestroy");
        System.gc();
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
//                mapView.initPosition(locationSourceUwb, locationCallback);
            });

            rotateActive.setText("rotate = " + mapView.enableRotation());
            zoomActive.setText("zoom = " + mapView.enableZoom());
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
//            if (locationSourceUwb != null) {
//                locationSourceUwb.pushLocationData(x, y, 0.0, currentFloor);
//            }
        }

        @Override
        public void onLongClick(double x, double y, Poi poi) {
//            if (locationSourceUwb != null) {
//                locationSourceUwb.pushLocationData(x, y, 0.0, currentFloor);
//            }
            drawMarker(x, y);
            mapView.translate(x, y, true);
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

        Marker marker01 = markerManagerWrapper.createMarker(inflateView, x - (locationDiff * 2), y, currentFloor);
        Marker marker02 = markerManagerWrapper.createMarker(javaCodeView, x, y, currentFloor);
        Marker marker03 = markerManagerWrapper.createMarker(customView, x + (locationDiff * 2), y, currentFloor);

        marker01.setFixedRotation(false);
        marker02.setFixedRotation(false);
        marker03.setFixedRotation(true);
        marker01.setFixedZoom(false);
        marker02.setFixedZoom(false);
        marker03.setFixedZoom(true);
        marker01.setRotation(0);
        marker02.setRotation(45);
        marker03.setRotation(90);

        marker01.getRotation();
        marker01.getFixedZoom();
        marker01.getFixedRotation();
        marker01.setPosition(x, y - 200);

        markerManagerWrapper.drawMarkers();
    }

    @Override
    public void onMove(double x, double y) {
        locationTextView.setText("coord = " + (int)x + ", " + (int)y);
    }

    @Override
    public void onZoomLevel(double zoomLevel) {
        zoomLevelTextView.setText("zoomLevel = " + zoomLevel);
    }

    @Override
    public void onRotation(double rotation) {
        rotationTextView.setText("angle = " + (int)rotation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addRotate:
                mapView.rotate(mapView.getAngle() + 10, true);
                break;
            case R.id.minusRotate:
                mapView.rotate(mapView.getAngle() - 10, true);
                break;
            case R.id.zoomIn:
                mapView.zoomLevel(mapView.getZoomLevel() - 0.1, true);
                break;
            case R.id.zoomOut:
                mapView.zoomLevel(mapView.getZoomLevel() + 0.1, true);
                break;
            case R.id.rotateActive:
                mapView.enableRotation(!mapView.enableRotation());
                rotateActive.setText("rotate = " + mapView.enableRotation());
                break;
            case R.id.zoomActive:
                mapView.enableZoom(!mapView.enableZoom());
                zoomActive.setText("zoom = " + mapView.enableZoom());
                break;
        }
    }

}