package com.project.real_calculator.database.models;

public class UserModel {

    private int id;
    private String password;
    private String iv;
    private byte[] bmkey;
    private String mkey;


    public UserModel(String iv, byte[] bmkey, String password){
        this.password = password;
        this.iv = iv;
        this.bmkey = bmkey;
    }
    public UserModel(int id, String iv, byte[] bmkey, String password){
        this.id = id;
        this.password = password;
        this.iv = iv;
        this.bmkey = bmkey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    public byte[] getBmkey() {
        return bmkey;
    }

    public void setBmkey(byte[] bmkey) {
        this.bmkey = bmkey;
    }


}
