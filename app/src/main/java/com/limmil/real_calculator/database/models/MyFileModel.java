package com.limmil.real_calculator.database.models;

import com.limmil.real_calculator.encryption.Util;

public class MyFileModel {

    private int id;
    private int folder;
    private String name;
    private String content;
    private String fileType;
    private String folderName;
    private String timeStamp;
    private String size;
    private byte[] iv, civ;
    private boolean selected;
    private boolean checkBoxVisibility = false;
    private boolean checkBox = false;
    final private String DEFAULT_STR = "N/A";

    public MyFileModel(){
        this.id = 0;
        this.name = DEFAULT_STR;
        this.content = DEFAULT_STR;
        this.fileType = DEFAULT_STR;
        this.timeStamp = DEFAULT_STR;
        this.folder = 0;
        this.size = "unknown";
        this.iv = Util.makeRandom12ByteNonce();
        this.civ = Util.makeRandom12ByteNonce();
    }

    public MyFileModel(int id, String name, String content, String fileType, String timeStamp, int folder){
        this.id = id;
        this.name = name;
        this.content = content;
        this.fileType = fileType;
        this.timeStamp = timeStamp;
        this.folder = folder;
        this.size = "unknown";
        this.iv = new byte[0];
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

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
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
