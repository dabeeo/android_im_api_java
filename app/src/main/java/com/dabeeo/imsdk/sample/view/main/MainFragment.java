package com.dabeeo.imsdk.sample.view.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dabeeo.imsdk.map.MapCallback;
import com.dabeeo.imsdk.map.MapView;
import com.dabeeo.imsdk.model.common.FloorInfo;
import com.dabeeo.imsdk.model.map.Poi;
import com.dabeeo.imsdk.navigation.Location;
import com.dabeeo.imsdk.navigation.NavigationListener;
import com.dabeeo.imsdk.navigation.PathRequest;
import com.dabeeo.imsdk.navigation.PathResult;
import com.dabeeo.imsdk.navigation.data.NodeData;
import com.dabeeo.imsdk.navigation.data.Path;
import com.dabeeo.imsdk.navigation.data.Route;

import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.data.LocationInfo;
import com.dabeeo.imsdk.sample.view.layout.LocationInfoView;
import com.dabeeo.imsdk.sample.view.layout.LocationSettingView;
import com.dabeeo.imsdk.sample.view.layout.NavigationInfoView;
import com.dabeeo.imsdk.sample.view.main.adapter.FloorListAdapter;

import org.rajawali3d.math.vector.Vector3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment {

    public static final String TAG = MainFragment.class.getSimpleName();

    private View rootView;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private MapView mapView;
    private int currentFloor;
    private RecyclerView floorListView;
    private LocationInfoView layoutPoiInfo;
    private LocationSettingView layoutLocationSetting;
    private NavigationInfoView layoutNavigationInfo;

    private List<FloorInfo> floorInfoList;

    private LocationInfo startLocation;
    private LocationInfo endLocation;
    private List<Location> wayPoints = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        floorListView = rootView.findViewById(R.id.recyclerFloors);

        layoutPoiInfo = rootView.findViewById(R.id.layoutPoiInfo);
        layoutLocationSetting = rootView.findViewById(R.id.layoutLocationSetting);
        layoutNavigationInfo = rootView.findViewById(R.id.layoutNavigationInfo);

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

        // map handle
        mapView.postDelayed(() -> {

            try {
                StringBuilder sb = new StringBuilder();
                InputStream is = getResources().getAssets().open("mapdata.json");
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
        }, 100);

        // zoom buttons
        rootView.findViewById(R.id.btnZoomIn)
                .setOnClickListener(v -> mapView.zoomIn());

        rootView.findViewById(R.id.btnZoomOut)
                .setOnClickListener(v -> mapView.zoomOut());

        // location info view
        layoutPoiInfo.setListener(new LocationInfoView.PoiInfoListener() {
            @Override
            public void onSelectStart(LocationInfo locationInfo) {
                mapView.removeMarker();
                startLocation = locationInfo;
                mapView.drawStartMarker(locationInfo.getLocation().getOriginPosition().x, locationInfo.getLocation().getOriginPosition().y, locationInfo.getLocation().getFloorLevel());

                layoutLocationSetting.bind(startLocation, endLocation);
                tryPathRequest(TransType.ALL);
            }

            @Override
            public void onSelectEnd(LocationInfo locationInfo) {
                mapView.removeMarker();
                endLocation = locationInfo;
                mapView.drawEndMarker(locationInfo.getLocation().getOriginPosition().x, locationInfo.getLocation().getOriginPosition().y, locationInfo.getLocation().getFloorLevel());

                layoutLocationSetting.bind(startLocation, endLocation);
                tryPathRequest(TransType.ALL);
            }

            @Override
            public void onClose() {
                mapView.removeMarker();
                startLocation = null;
                endLocation = null;
            }
        });

        // location setting view
        layoutLocationSetting.setListener(new LocationSettingView.LocationSettingViewListener() {
            @Override
            public void onSwap(LocationInfo start, LocationInfo end) {
                startLocation = start;
                endLocation = end;
                tryPathRequest(TransType.ALL);
            }

            @Override
            public void onClose() {
                mapView.removeMarker();
                startLocation = null;
                endLocation = null;

                layoutNavigationInfo.hide();

                mapView.cancelNavigationPreview();
            }
        });

        layoutNavigationInfo.setListener(new NavigationInfoView.NavigationInfoListener() {
            @Override
            public void onStartPreview() {
                mapView.startNavigationPreview();
            }

            @Override
            public void onStartNavigation() {

            }

            @Override
            public void onChangeTransType(TransType transType) {
                tryPathRequest(transType);
            }
        });
    }

    private void tryPathRequest(TransType transType) {
        if (startLocation != null && endLocation != null) {
            if (transType == null) {
                transType = TransType.ALL;
            }

            PathRequest pathRequest = new PathRequest(startLocation.getLocation(), endLocation.getLocation(), wayPoints, transType);
            mapView.findPath(pathRequest, navigationListener);
        }
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
        }

        @Override
        public void onError( Exception e) {

        }

        @Override
        public void changeFloor(int floor) {
            currentFloor = floor;
            ((FloorListAdapter) Objects.requireNonNull(floorListView.getAdapter())).setFloor(floor);
        }

        @Override
        public void onClick(double x, double y, Poi poi) {
            mapView.removeMarker();
            mapView.addMarker(R.drawable.pin, x, y, currentFloor);
            mapView.drawMarker();

            String name;
            if (poi != null) {
                name = poi.getTitles().get(0).getText();
            } else {
                name = "선택위치";
            }

            Vector3 position = new Vector3(x, y, 0);

            Location location = new Location(position, currentFloor, name);
            String floorName = floorInfoList
                    .stream()
                    .filter(floorInfo -> floorInfo.getLevel() == currentFloor)
                    .map(floorInfo -> floorInfo.getName().get(0).getText())
                    .findFirst()
                    .orElse("");

            LocationInfo locationInfo = new LocationInfo(location, floorName);
            layoutPoiInfo.bind(locationInfo);
            if (layoutNavigationInfo.isShown()) {
                layoutNavigationInfo.hide();
            }
        }

        @Override
        public void onLongClick(double x, double y, Poi poi) {
            mapView.removeMarker();
            mapView.addMarker(R.drawable.pin, x, y, currentFloor);
            mapView.drawMarker();

            String name;
            if (poi != null) {
                name = poi.getTitles().get(0).getText();
            } else {
                name = "선택위치";
            }

            Vector3 position = new Vector3(x, y, 0);

            Location location = new Location(position, currentFloor, name);
            String floorName = floorInfoList
                    .stream()
                    .filter(floorInfo -> floorInfo.getLevel() == currentFloor)
                    .map(floorInfo -> floorInfo.getName().get(0).getText())
                    .findFirst()
                    .orElse("");

            LocationInfo locationInfo = new LocationInfo(location, floorName);
            layoutPoiInfo.bind(locationInfo);
            if (layoutNavigationInfo.isShown()) {
                layoutNavigationInfo.hide();
            }
        }
    };

    private final NavigationListener navigationListener = new NavigationListener() {
        @Override
        public void onPathResult( PathResult pathResult) {
            if (pathResult.isSuccess()) {
                layoutNavigationInfo.bind(pathResult.getPathData());
            }
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onUpdate( Route route,  Path path,  NodeData nodeData,  Vector3 vector3) {

        }

        @Override
        public void onRescan() {

        }

        @Override
        public void onError(IMError error) {
            Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onError( Exception e) {

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
        public void onLocationStatus( LocationStatus locationStatus) {

        }
    };
}