package com.work.andre.mines;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.Marker;

class Buildings {
    String buildingID;
    String buildingName;
    String buildingType;
    GroundOverlay groundOverlay;
    GroundOverlay groundOverlayArea;
    Marker marker;
    long buildingLVL;
    int photoId;

    Buildings(String buildingID, String buildingName, long buildingLVL, int photoId) {
        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.buildingLVL = buildingLVL;
        this.photoId = photoId;
    }

    Buildings(String buildingType) {
        this.buildingType = buildingType;
    }

    Buildings(String buildingID, GroundOverlay groundOverlay, GroundOverlay groundOverlayArea, Marker marker) {
        this.buildingID = buildingID;
        this.groundOverlay = groundOverlay;
        this.groundOverlayArea = groundOverlayArea;
        this.marker = marker;
    }

    public String getBuildingType() {
        return buildingType;
    }
}