package com.project.real_calculator.database.models;

public class AlbumModel {

    private int id;
    private byte[] bAlbumName;
    private String albumName;
    private byte[] bCoverURI;
    private String coverURI;
    private int itemsCount;
    private boolean isSelected;

    public AlbumModel(){
    }
    public AlbumModel(int id, byte[] albumName, byte[] coverURI, int itemsCount){
        this.id = id;
        this.bAlbumName = albumName;
        this.bCoverURI = coverURI;
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

    public String getCoverURI() {
        return coverURI;
    }

    public void setCoverURI(String coverURI) {
        this.coverURI = coverURI;
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

    public byte[] getbAlbumName() {
        return bAlbumName;
    }

    public void setbAlbumName(byte[] bAlbumName) {
        this.bAlbumName = bAlbumName;
    }

    public byte[] getbCoverURI() {
        return bCoverURI;
    }

    public void setbCoverURI(byte[] bCoverURI) {
        this.bCoverURI = bCoverURI;
    }
}

