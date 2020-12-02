package com.project.real_calculator.database.models;

public class AlbumModel {

    private int id;
    private String albumName;
    private int itemsCount;
    private String timestamp;
    private boolean isSelected;

    public AlbumModel(int id, String albumName, String timestamp, int itemsCount){
        this.id = id;
        this.albumName = albumName;
        this.timestamp = timestamp;
        this.itemsCount = itemsCount;
        this.isSelected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}

