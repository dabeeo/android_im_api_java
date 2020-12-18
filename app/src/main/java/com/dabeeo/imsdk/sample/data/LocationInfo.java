package com.dabeeo.imsdk.sample.data;

import com.dabeeo.imsdk.navigation.Location;

import java.io.Serializable;

public class LocationInfo implements Serializable {
    private Location location;
    private String floorName;

    public LocationInfo() {

    }

    public LocationInfo(Location location, String floorName) {
        this.location = location;
        this.floorName = floorName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }
}
