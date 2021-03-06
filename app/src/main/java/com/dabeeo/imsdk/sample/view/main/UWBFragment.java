package com.dabeeo.imsdk.sample.view.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dabeeo.imsdk.common.error.IMError;
import com.dabeeo.imsdk.imenum.LocationStatus;
import com.dabeeo.imsdk.imenum.TransType;
import com.dabeeo.imsdk.location.LocationCallback;
import com.dabeeo.imsdk.location.LocationSourceUwb;
import com.dabeeo.imsdk.map.MapCallback;
import com.dabeeo.imsdk.map.MapView;
import com.dabeeo.imsdk.map.interfaces.IMMarkerListener;
import com.dabeeo.imsdk.map.interfaces.IMMoveListener;
import com.dabeeo.imsdk.model.common.FloorInfo;
import com.dabeeo.imsdk.model.gl.Marker;
import com.dabeeo.imsdk.model.map.Poi;
import com.dabeeo.imsdk.navigation.Location;
import com.dabeeo.imsdk.navigation.NavigationListener;
import com.dabeeo.imsdk.navigation.PathRequest;
import com.dabeeo.imsdk.navigation.PathResult;
import com.dabeeo.imsdk.navigation.data.NodeData;
import com.dabeeo.imsdk.navigation.data.Path;
import com.dabeeo.imsdk.navigation.data.Route;
import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.view.layout.MarkerTestView;
import com.dabeeo.imsdk.sample.view.main.adapter.FloorListAdapter;
import com.dabeeo.imsdk.sample.view.main.manager.MarkerManagerWrapper;

import org.jetbrains.annotations.NotNull;
import org.rajawali3d.math.vector.Vector3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UWBFragment extends Fragment implements View.OnClickListener, IMMoveListener {

    public static final String TAG = UWBFragment.class.getSimpleName();

    private View rootView;
    private boolean isNavigating = false;
    private Marker mMarker = null;

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
    private ArrayList<NodeData> nodeDatas = new ArrayList<>();

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
            InputStream is = getResources().getAssets().open("mapdata.json");
//            InputStream is = getResources().getAssets().open("mapdata_error.json");
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

            locationSourceUwb = new LocationSourceUwb();네
            mapView.initPosition(locationSourceUwb, locationCallback);*/


        // zoom buttons
        rootView.findViewById(R.id.btnZoomIn)
                .setOnClickListener(v -> mapView.zoomIn());

        rootView.findViewById(R.id.btnZoomOut)
                .setOnClickListener(v -> mapView.zoomOut());

        mapView.setOnMarkerListener(new IMMarkerListener() {
            @Override
            public void onMarkerLongClick(Marker marker) {

            }

            @Override
            public void onMarkerClick(Marker marker) {
                mapView.removeMarker(marker);
                mMarker = null;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        markerManagerWrapper.clearMarkers();
        mapView.destroy();
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
                mapView.initPosition(locationSourceUwb, null);
            });

            rotateActive.setText("rotate = " + mapView.enableRotation());
            zoomActive.setText("zoom = " + mapView.enableZoom());
            mapView.setMaxZoom(5.0);
            mapView.setMinZoom(0.5);
            mapView.zoomLevel(1.5, false);

            markerManagerWrapper.parseReadData(getActivity(), "reeumRealData.json");
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

        }

        @Override
        public void onLongClick(double x, double y, Poi poi) {
            if(mMarker == null) {
                mMarker = markerManagerWrapper.createMarker(R.drawable.icon_mylocation, x, y, 100, 100, mapView.getFloorLevel());
                mMarker.setFixedZoom(true);
                markerManagerWrapper.drawMarkers();
            }

//            if(!isNavigating) {
//                Vector3 originVector = new Vector3(x, y, 0);
//                Vector3 destinationVector = new Vector3(1527, 1506, 0);
//                Location originLocation = new Location(originVector, currentFloor, "");
//                Location destinationLocation = new Location(destinationVector, currentFloor, "");
//                List<Location> wayPoints = new ArrayList<>();
//                PathRequest pathRequest = new PathRequest(originLocation, destinationLocation, wayPoints, TransType.ALL);
//                mapView.findPath(pathRequest, mNavigationListener);
//                mapView.startNavigation();
//            } else {
//                locationSourceUwb.pushLocationData(x, y,0, mapView.getFloorLevel());
//            }
//            boolean passable = mapView.isPassableArea(x, y, mapView.getFloorLevel());
        }
    };

    private void drawMarker(double x, double y) {
//        markerManagerWrapper.clearMarkers();
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
        marker02.setFixedRotation(true);
        marker03.setFixedRotation(false);
        marker01.setFixedZoom(false);
        marker02.setFixedZoom(true);
        marker03.setFixedZoom(false);
        marker01.setRotation(0.0);
        marker02.setRotation(45.0);
        marker03.setRotation(90);

        markerManagerWrapper.drawMarkers();

        mapView.setOnMarkerListener(new IMMarkerListener() {
            @Override
            public void onMarkerLongClick(Marker marker) {

            }

            @Override
            public void onMarkerClick(Marker marker) {
//                Log.i(getClass().getSimpleName(), "MARKER INFO / " + marker.toString());
//                markerManagerWrapper.clearMarker(marker, currentFloor);
                mapView.removeMarker(marker);
                mMarker = null;
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

    public NavigationListener mNavigationListener = new NavigationListener() {
        @Override
        public void onPathResult(PathResult pathResult) {
            markerManagerWrapper.clearMarkers();
            if(pathResult.isSuccess()) {
                nodeDatas = pathResult.getPathData().getCurrentRoute().getCurrentPath().getNodeDatas();
                for (int i = 0; i < nodeDatas.size(); i++) {
                    NodeData nodeData = nodeDatas.get(i);
                    final View inflateView = markerManagerWrapper.getJavaCodeView(getActivity());
                    markerManagerWrapper.createMarker(inflateView, nodeData.getPosition().x, nodeData.getPosition().y, currentFloor);
                }
            }
            markerManagerWrapper.drawMarkers();
        }

        @Override
        public void onStart() {
            showToast("onStart");
            isNavigating = true;
        }

        @Override
        public void onFinish() {
            showToast("onFinish");
            isNavigating = false;
            markerManagerWrapper.clearMarkers();
        }

        @Override
        public void onCancel() {
            showToast("onCancel");
            isNavigating = false;
            markerManagerWrapper.clearMarkers();
        }

        @Override
        public void onUpdate(Route route, Path path, NodeData nodeData, Vector3 vector3) {
            mapView.translate(vector3.x, vector3.y, true);
            Log.i(TAG, "remainingDistance = " + path.getRemainingDistance());
        }

        @Override
        public void onRescan() {
            showToast("onRescan");
        }

        @Override
        public void onError(IMError imError) {

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