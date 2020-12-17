package com.limmil.real_calculator.database.models;

import com.limmil.real_calculator.encryption.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NoteModel {

    private int id;
    private String title = "";
    private String contentPreview = "";
    private String dateTime = "";
    private byte[] civ, tiv;

    public NoteModel(){
        this.id = 0;
        this.civ = Util.makeRandom12ByteNonce();
        this.tiv = Util.makeRandom12ByteNonce();
    }

    public NoteModel(int id, byte[] titleIv, byte[] contentIv, String dateTime){
        this.id = id;
        this.tiv = titleIv;
        this.civ = contentIv;
        this.dateTime = localTime(dateTime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentPreview() {
        return contentPreview;
    }

    public void setContentPreview(String contentPreview) {
        this.contentPreview = contentPreview;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public byte[] getTitleIv() {
        return tiv;
    }

    public void setTitleIv(byte[] iv) {
        this.tiv = iv;
    }

    public byte[] getContentIv() {
        return civ;
    }

    public void setContentIv(byte[] iv) {
        this.civ = iv;
    }

    public String localTime(String utcTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = null;
        try {
            myDate = simpleDateFormat.parse(utcTime);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
        }catch (ParseException ignored){}

        if (myDate!=null){
            return simpleDateFormat.format(myDate);
        }else {
            return "";
        }
    }
}
