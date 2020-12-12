package com.project.real_calculator.database.models;

public class FolderModel {

    private int id;
    private String folderName;
    private int itemsCount;
    private String timestamp;
    private boolean isSelected;

    public FolderModel(int id, String folderName, String timestamp, int itemsCount){
        this.id = id;
        this.folderName = folderName;
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

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
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

