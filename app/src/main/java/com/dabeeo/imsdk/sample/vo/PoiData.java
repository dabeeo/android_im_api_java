package com.dabeeo.imsdk.sample.vo;

class PoiData {

    public enum DisplayType {
        ICON,
        TITLE,
        ICON_AND_TITLE
    }

    private String id;
    private String mapId;
    private String floorId;
    private String objectId;
    private boolean isActive;
    private DisplayType displayType;
    private String title;
    private String iconUrl;
    private double iconWidth;
    private double iconHeight;
    private String iconFileName;
    private double x;
    private double y;

    public PoiData(
            String id,
            String mapId,
            String floorId,
            String objectId,
            boolean isActive,
            DisplayType displayType,
            String title,
            String iconUrl,
            double iconWidth,
            double iconHeight,
            String iconFileName,
            double x,
            double y
    ) {

    }

}
