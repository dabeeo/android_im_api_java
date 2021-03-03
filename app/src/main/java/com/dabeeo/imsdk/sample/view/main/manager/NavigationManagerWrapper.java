package com.dabeeo.imsdk.sample.view.main.manager;


import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.dabeeo.imsdk.common.error.IMError;
import com.dabeeo.imsdk.imenum.TransType;
import com.dabeeo.imsdk.location.LocationSourceUwb;
import com.dabeeo.imsdk.map.MapView;
import com.dabeeo.imsdk.navigation.Location;
import com.dabeeo.imsdk.navigation.NavigationListener;
import com.dabeeo.imsdk.navigation.PathRequest;
import com.dabeeo.imsdk.navigation.PathResult;
import com.dabeeo.imsdk.navigation.data.NodeData;
import com.dabeeo.imsdk.navigation.data.NodeDirection;
import com.dabeeo.imsdk.navigation.data.Path;
import com.dabeeo.imsdk.navigation.data.PathData;
import com.dabeeo.imsdk.navigation.data.Route;
import com.dabeeo.imsdk.sample.R;

import org.rajawali3d.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;


public class NavigationManagerWrapper {

    private final String TAG = getClass().getSimpleName();
    private volatile static NavigationManagerWrapper instance = null;
    private MapView mapView;

    private Location originLocation = null;
    private Location destinationLocation = null;
    private boolean isNavigating = false;

    private double finishDistance = 10 * 100; // 10m


    synchronized public static NavigationManagerWrapper getInstance(MapView mapView) {
        if (instance == null) {
            instance = new NavigationManagerWrapper(mapView);
        }
        return instance;
    }

    public NavigationManagerWrapper(MapView mapView) {
        this.mapView = mapView;
    }

    public void showNavigationDialog(Context context, final double x, final double y, final int floorLevel) {
        if (originLocation == null || destinationLocation == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("출발도착 설정")
                    .setNegativeButton("출발지", (dialogInterface, i) -> {
                        originLocation = new Location(new Vector3(x, y, 0.0), floorLevel, "");
                        if (destinationLocation != null) {
                            requestPath(originLocation, destinationLocation);
                        }

                    })
                    .setPositiveButton("도착지", (dialogInterface, i) -> {
                        destinationLocation = new Location(new Vector3(x, y, 0.0), floorLevel, "");
                        if (originLocation != null) {
                            requestPath(originLocation, destinationLocation);
                        }
                    }).show();
        }
    }

    private void requestPath(Location origin, Location destination) {
        PathRequest pathRequest = new PathRequest(origin, destination, new ArrayList<>(), TransType.ALL);
        mapView.findPath(pathRequest, new NavigationListener() {
            @Override
            public void onPathResult(PathResult pathResult) {
                if (pathResult.isSuccess()) {
                    PathData pathData = pathResult.getPathData();
                    List<Route> routeList = pathData.getRouteList();

                    for (Route route: routeList) {
                        List<Path> pathList = route.getPathList();
                        for (Path path: pathList) {
                            List<NodeData> nodeDataList = path.getNodeDatas();
                            for (NodeData nodeData: nodeDataList) {
                                mapView.addMarker(
                                        R.drawable.pin,
                                        nodeData.getPosition().x,
                                        nodeData.getPosition().y,
                                        5.0,
                                        6.0,
                                        nodeData.getFloorLevel()
                                );
                            }
                        }
                    }

                    addNavigationMarker(origin, destination);
                }
            }

            @Override
            public void onStart() {
                isNavigating = true;
            }

            @Override
            public void onFinish() {
                clearNavigationData();
            }

            @Override
            public void onCancel() {
                clearNavigationData();
            }

            @Override
            public void onUpdate(Route currentRoute, Path currentPath, NodeData currentNodeData, Vector3 snapPosition) {
                if(currentPath.getNextNode() != null) {
                    NodeData cNode = currentNodeData;
                    NodeData nNode = currentPath.getNextNode();

                    double distance = cNode.getRemainingDistance() / 100;

                    String message = nNode.getNextDirection() == NodeDirection.GoStraight ?
                            String.format("%.1fm 이동", distance) :
                            String.format("%.1fm 이동 후 %s", distance, getDirectionString(nNode.getNextDirection()));
                    Log.i("SAHONMU", "onUpdate : " + message);
                }


                if(currentPath.getRemainingDistance() < finishDistance && destinationLocation.getFloorLevel() == currentPath.getFloorLevel()) {
                    // 도착 처리
                }

            }

            @Override
            public void onRescan() {

            }

            @Override
            public void onError(IMError e) {

            }
        });
        mapView.startNavigation();
    }

    private String getDirectionString(NodeDirection direction) {
        if(direction == NodeDirection.LeftTurn) {
            return "좌회전";
        } else if(direction == NodeDirection.RightTurn) {
            return "우회전";
        }
        return "";
    }

    public boolean isNavigating() {
        return isNavigating;
    }

    private void clearNavigationData() {
        isNavigating = false;
        mapView.removeMarker();
        originLocation = null;
        destinationLocation = null;
    }

    public void stopNavigation() {
        if(isNavigating) {
            mapView.cancelNavigation();
        }
    }

    private void addNavigationMarker(Location origin, Location destination) {
        mapView.addMarker(
                R.drawable.pin,
                origin.getOriginPosition().x,
                origin.getOriginPosition().y,
                51.0,
                65.0,
                origin.getFloorLevel()
        );
        mapView.addMarker(
                R.drawable.pin,
                destination.getOriginPosition().x,
                destination.getOriginPosition().y,
                51.0,
                65.0,
                destination.getFloorLevel()
        );
        mapView.drawMarker();
    }


}

