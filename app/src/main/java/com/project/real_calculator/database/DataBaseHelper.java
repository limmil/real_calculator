package com.project.real_calculator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.project.real_calculator.database.UserContract.*;
import com.project.real_calculator.database.models.*;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_FOLDER = "/databases/";
    public static final String DATABASE_NAME = "appdata.db";

    /* creates and deletes */
    private static final String SQL_CREATE_USER = "CREATE TABLE " +
            User.TABLE_NAME + " (" +
            User._ID + " INTEGER PRIMARY KEY, " +
            User.COLUMN_IV + " TEXT NOT NULL, " +
            User.COLUMN_MKEY + " BLOB NOT NULL, " +
            User.COLUMN_PASSWORD + " TEXT NOT NULL)";
    private static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + User.TABLE_NAME;

    private static final String SQL_CREATE_ALBUM = "CREATE TABLE " +
            Album.TABLE_NAME + " (" +
            Album._ID + " INTEGER PRIMARY KEY, " +
            Album.COLUMN_NAME + " BLOB NOT NULL, " +
            Album.COLUMN_THUMBNAIL + " BLOB NOT NULL, " +
            Album.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            Album.COLUMN_COUNT + " INTEGER NOT NULL)";
    private static final String SQL_DELETE_ALBUM =
            "DROP TABLE IF EXISTS " + Album.TABLE_NAME;
    // photo has foreign key from album
    private static final String SQL_CREATE_PHOTO = "CREATE TABLE " +
            Photo.TABLE_NAME + " (" +
            Photo._ID + " INTEGER PRIMARY KEY, " +
            Photo.COLUMN_NAME + " BLOB NOT NULL, " +
            Photo.COLUMN_CONTENT + " BLOB, " +
            Photo.COLUMN_THUMBNAIL + " BLOB, " +
            Photo.COLUMN_EXTENSION + " BLOB, " +
            Photo.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            Photo.COLUMN_ALBUM + " INTEGER, " +
            " FOREIGN KEY ("+Photo.COLUMN_ALBUM+") REFERENCES "+Album.TABLE_NAME+"("+Album._ID+"))";
    private static final String SQL_DELETE_PHOTO =
            "DROP TABLE IF EXISTS " + Photo.TABLE_NAME;



    public DataBaseHelper(@Nullable Context context) {
        super(context,
                context.getExternalFilesDir(null)
                        + File.separator + DATABASE_FOLDER
                        + File.separator + DATABASE_NAME,
                null,
                VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USER);
        sqLiteDatabase.execSQL(SQL_CREATE_ALBUM);
        sqLiteDatabase.execSQL(SQL_CREATE_PHOTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_USER);
        sqLiteDatabase.execSQL(SQL_DELETE_ALBUM);
        sqLiteDatabase.execSQL(SQL_DELETE_PHOTO);
    }

    // USER TABLE
    public boolean userExist(){
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT COUNT(*) FROM " + User.TABLE_NAME;
        //Cursor c = db.query(User.TABLE_NAME, null, null, null, null, null, null);
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        cursor.close();
        db.close();
        return icount > 0; // first row returns 1
    }
    /* CRUD */
    // user table
    public boolean addUser(UserModel userModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(User.COLUMN_PASSWORD, userModel.getPassword());
        cv.put(User.COLUMN_MKEY, userModel.getBmkey());
        cv.put(User.COLUMN_IV, userModel.getIv());

        long insert = db.insert(User.TABLE_NAME, null, cv);
        db.close();
        return insert != -1;
    }
    public List<UserModel> getUsers(){
        List<UserModel> result = new ArrayList<>();
        String q = "SELECT * FROM " + User.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String iv = cursor.getString(1);
                byte[] bmkey = cursor.getBlob(2);
                String pw = cursor.getString(3);
                UserModel user = new UserModel(id, iv, bmkey, pw);
                result.add(user);
            }while(cursor.moveToNext());
        }
        cursor.close();
        //db.close();

        return result;
    }
}
