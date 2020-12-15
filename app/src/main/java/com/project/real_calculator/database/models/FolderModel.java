package com.project.real_calculator.database.models;

import com.project.real_calculator.encryption.Util;

public class FolderModel {

    private int id;
    private String folderName;
    private int itemsCount;
    private String timestamp;
    private byte[] iv;
    private boolean isSelected;
    final private String DEFAULT_STR = "N/A";

    public FolderModel(){
        this.id = 0;
        this.folderName = DEFAULT_STR;
        this.timestamp = DEFAULT_STR;
        this.itemsCount = 0;
        this.iv = Util.makeRandom12ByteNonce();
        this.isSelected = false;
    }

    public FolderModel(int id){
        this.id = id;
        this.folderName = DEFAULT_STR;
        this.timestamp = DEFAULT_STR;
        this.itemsCount = 0;
        this.iv = new byte[0];
        this.isSelected = false;
    }

    public FolderModel(int id, String folderName, String timestamp, int itemsCount){
        this.id = id;
        this.folderName = folderName;
        this.timestamp = timestamp;
        this.itemsCount = itemsCount;
        this.iv = new byte[0];
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

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}

