package com.limmil.real_calculator.database.models;

import com.limmil.real_calculator.encryption.Util;

public class AlbumModel {

    private int id;
    private String albumName;
    private int itemsCount;
    private String timestamp;
    private boolean isSelected;
    private byte[] iv;
    final private String DEFAULT_STR = "N/A";

    public AlbumModel(){
        this.id = 0;
        this.albumName = DEFAULT_STR;
        this.timestamp = DEFAULT_STR;
        this.itemsCount = 0;
        this.isSelected = false;
        this.iv = Util.makeRandom12ByteNonce();
    }

    public AlbumModel(int id){
        this.id = id;
        this.albumName = DEFAULT_STR;
        this.timestamp = DEFAULT_STR;
        this.itemsCount = 0;
        this.isSelected = false;
        this.iv = new byte[0];
    }

    public AlbumModel(int id, String albumName, String timestamp, int itemsCount){
        this.id = id;
        this.albumName = albumName;
        this.timestamp = timestamp;
        this.itemsCount = itemsCount;
        this.isSelected = false;
        this.iv = new byte[0];
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

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}

