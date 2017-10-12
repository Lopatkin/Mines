package com.work.andre.mines;

class Buildings {
    String buildingID;
    String buildingName;
    long buildingLVL;
    int photoId;

    Buildings(String buildingID, String buildingName, long buildingLVL, int photoId) {
        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.buildingLVL = buildingLVL;
        this.photoId = photoId;
    }
}