package com.project.real_calculator.database.models;

import com.project.real_calculator.encryption.Util;

public class PhotoModel {

    private int id;
    private int album;
    private String name;
    private String content;
    private String thumbnail;
    private String fileType;
    private String albumName;
    private String timeStamp;
    private String size;
    private byte[] iv, tiv, civ;
    private boolean selected;
    private boolean checkBoxVisibility = false;
    private boolean checkBox = false;
    final private String DEFAULT_STR = "N/A";

    public PhotoModel(){
        this.id = 0;
        this.name = DEFAULT_STR;
        this.content = DEFAULT_STR;
        this.thumbnail = DEFAULT_STR;
        this.fileType = DEFAULT_STR;
        this.timeStamp = DEFAULT_STR;
        this.album = 0;
        this.size = "unknown";
        this.iv = Util.makeRandom12ByteNonce();
        this.tiv = Util.makeRandom12ByteNonce();
        this.civ = Util.makeRandom12ByteNonce();
    }

    public PhotoModel(int id, String name, String content, String thumbnail, String fileType, String timeStamp, int album){
        this.id = id;
        this.name = name;
        this.content = content;
        this.thumbnail = thumbnail;
        this.fileType = fileType;
        this.timeStamp = timeStamp;
        this.album = album;
        this.size = "unknown";
        this.iv = new byte[0];
        this.tiv = new byte[0];
        this.civ = new byte[0];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getThumbIv() {
        return tiv;
    }

    public void setThumbIv(byte[] tiv) {
        this.tiv = tiv;
    }

    public byte[] getContentIv() {
        return civ;
    }

    public void setContentIv(byte[] civ) {
        this.civ = civ;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(boolean checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
    }

    public boolean getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }
}
