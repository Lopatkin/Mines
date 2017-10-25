package com.work.andre.mines;

class Buildings {
    String buildingID;
    String buildingName;
    String buildingType;
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

    public String getBuildingType() {
        return buildingType;
    }
}