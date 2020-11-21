package com.project.real_calculator.database.models;

public class PhotoModel {

    private int id;
    private byte[] bName;
    private String name;
    private byte[] bContent;
    private String content;
    private byte[] bThumbnail;
    private String thumbnail;
    private byte[] bExtension;
    private String extension;
    private int album;
    private String albumName;

    public PhotoModel(int id, byte[] name, byte[] content, byte[] thumbnail, byte[] extension, int album){
        this.bName = name;
        this.bContent = content;
        this.bThumbnail = thumbnail;
        this.bExtension = extension;
        this.album = album;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getbName() {
        return bName;
    }

    public void setbName(byte[] bName) {
        this.bName = bName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getbContent() {
        return bContent;
    }

    public void setbContent(byte[] bContent) {
        this.bContent = bContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getbThumbnail() {
        return bThumbnail;
    }

    public void setbThumbnail(byte[] bThumbnail) {
        this.bThumbnail = bThumbnail;
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

    public byte[] getbExtension() {
        return bExtension;
    }

    public void setbExtension(byte[] bExtension) {
        this.bExtension = bExtension;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
