package com.dabeeo.imsdk.sample.view.main.manager;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dabeeo.imsdk.map.MapView;
import com.dabeeo.imsdk.model.gl.Marker;
import com.dabeeo.imsdk.sample.R;
import com.dabeeo.imsdk.sample.view.layout.MarkerTestView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MarkerManagerWrapper {

    private final String TAG = getClass().getSimpleName();
    private volatile static MarkerManagerWrapper instance = null;

    private MapView mapView;
    private HashMap<Integer, List<Marker>> markerMaps = new HashMap<>();

    synchronized public static MarkerManagerWrapper getInstance(MapView mapView) {
        if (instance == null) {
            instance = new MarkerManagerWrapper(mapView);
        }
        return instance;
    }

    public MarkerManagerWrapper(MapView mapView) {
        this.mapView = mapView;
    }

    /**
     *
     * @param resource
     * @param x
     * @param y
     * @param width
     * @param height
     * @param floorLevel
     * @return
     */
    public Marker createMarker(int resource, double x, double y, double width, double height, int floorLevel) {
        Marker marker = mapView.addMarker(resource, x, y, width, height, floorLevel);
        List<Marker> markers = markerMaps.get(floorLevel);
        if(markers == null) {
            markers = new ArrayList<>();
            markerMaps.put(floorLevel, markers);
        }
        markers.add(marker);
        return marker;
    }

    /**
     *
     * @param view
     * @param x
     * @param y
     * @param floorLevel
     * @return
     */
    public Marker createMarker(View view, double x, double y, int floorLevel) {
        Marker marker = mapView.addMarker(view, x, y, floorLevel);
        List<Marker> markers = markerMaps.get(floorLevel);
        if(markers == null) {
            markers = new ArrayList<>();
            markerMaps.put(floorLevel, markers);
        }
        markers.add(marker);
        return marker;
    }

    public List<Marker> getMarkers() {
        List<Marker> markers = new ArrayList<>();
        Iterator<Integer> keys = markerMaps.keySet().iterator();
        while( keys.hasNext() ){
            int key = keys.next();
            markers.addAll(markerMaps.get(key));
        }
        return markers;
    }

    public List<Marker> getMarkersByFloor(int floor) {
        List<Marker> markers = new ArrayList<>();
        Iterator<Integer> keys = markerMaps.keySet().iterator();
        while( keys.hasNext() ){
            int key = keys.next();
            if(key == floor) {
                markers.addAll(markerMaps.get(key));
            }
        }
        return markers;
    }

    /**
     *
     */
    public void clearMarkers() {
        Iterator<Integer> keys = markerMaps.keySet().iterator();
        while( keys.hasNext() ){
            int key = keys.next();
            markerMaps.get(key).clear();
        }
        markerMaps.clear();
        mapView.removeMarker();
    }

    /**
     *
     */
    public void drawMarkers() {
        mapView.drawMarker();
    }

    public final View getJavaCodeView(Context context) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setBackgroundColor(Color.YELLOW);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        TextView textView = new TextView(context);
        textView.setText("Test");
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(params);
        textView.setTextSize(10);
        relativeLayout.addView(textView);
        return relativeLayout;
    }

    public final View getInflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_marker_test, null, false);
        return view;
    }

    public final MarkerTestView getCustomView(Context context) {
        return new MarkerTestView(context);
    }


}

