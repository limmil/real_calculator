package com.project.real_calculator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.real_calculator.database.UserContract.*;
import com.project.real_calculator.database.models.*;
import com.project.real_calculator.encryption.Util;

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
            Album.COLUMN_THUMBNAIL + " BLOB, " +
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
            Photo.COLUMN_FILETYPE + " BLOB, " +
            Photo.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            Photo.COLUMN_ALBUM + " INTEGER, " +
            " FOREIGN KEY ("+Photo.COLUMN_ALBUM+") REFERENCES "+Album.TABLE_NAME+"("+Album._ID+"))";
    private static final String SQL_DELETE_PHOTO =
            "DROP TABLE IF EXISTS " + Photo.TABLE_NAME;
    // create album index on photo table
    private static final String SQL_CREATE_PHOTO_ALBUM_INDEX = "CREATE INDEX " +
            Photo.INDEX_ALBUM + " ON " +
            Photo.TABLE_NAME + " (" +
            Photo.COLUMN_ALBUM + ")";
    private static final String SQL_DELETE_PHOTO_ALBUM_INDEX =
            "DROP INDEX IF EXISTS " + Photo.INDEX_ALBUM;



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
        sqLiteDatabase.execSQL(SQL_CREATE_PHOTO_ALBUM_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_USER);
        sqLiteDatabase.execSQL(SQL_DELETE_PHOTO_ALBUM_INDEX);
        sqLiteDatabase.execSQL(SQL_DELETE_PHOTO);
        sqLiteDatabase.execSQL(SQL_DELETE_ALBUM);

    }

    // USER TABLE __________________________________________________________________________________
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
    /* USER CRUD */
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
        db.close();

        return result;
    }


    // ALBUM TABLE _________________________________________________________________________________
    /* ALBUM CRUD */
    public boolean addAlbum(AlbumModel album){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // encrypt name, thumbnail
        byte[] albumName = Util.encryptToByte(album.getAlbumName());

        cv.put(Album.COLUMN_NAME, albumName);
        cv.put(Album.COLUMN_COUNT, album.getItemsCount());

        long insert = db.insert(Album.TABLE_NAME, null, cv);
        db.close();
        return insert != -1;
    }
    public List<AlbumModel> getAlbums(){
        List<AlbumModel> albumModels = new ArrayList<>();
        String q = "SELECT * FROM " + Album.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                byte[] bName = cursor.getBlob(1);
                String time = cursor.getString(3);
                int count = cursor.getInt(4);
                //decrypt data
                String name = Util.decryptToString(bName);

                AlbumModel album = new AlbumModel(id, name, time, count);
                albumModels.add(album);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return albumModels;
    }
    public boolean updateAlbum(AlbumModel album){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(album.getId());
        // encrypt new name
        cv.put(Album.COLUMN_NAME, Util.encryptToByte(album.getAlbumName()));
        int updated = db.update(Album.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean deleteAlbum(AlbumModel album){
        SQLiteDatabase db = this.getWritableDatabase();
        String id = String.valueOf(album.getId());
        int deleted = db.delete(Album.TABLE_NAME, "_id = ?", new String[]{id});
        db.close();

        return deleted > 0;
    }


    // PHOTO TABLE _________________________________________________________________________________
    /* PHOTO CRUD */
    public long addPhoto(PhotoModel photoModel, AlbumModel albumModel){
        List<PhotoModel> tmp = new ArrayList<>();
        tmp.add(photoModel);
        return addPhotos(tmp, albumModel);
    }
    public long addPhotos(List<PhotoModel> photos, AlbumModel albumModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert = -1;
        for (PhotoModel tmp : photos){
            // encrypt data
            byte[] name = Util.encryptToByte(tmp.getName());
            byte[] content = Util.encryptToByte(tmp.getContent());
            byte[] fileType = Util.encryptToByte(tmp.getFileType());
            byte[] thumbnail = Util.encryptToByte(tmp.getThumbnail());
            int album = albumModel.getId();
            // insert data
            cv.put(Photo.COLUMN_NAME, name);
            cv.put(Photo.COLUMN_CONTENT, content);
            cv.put(Photo.COLUMN_FILETYPE, fileType);
            cv.put(Photo.COLUMN_THUMBNAIL, thumbnail);
            cv.put(Photo.COLUMN_ALBUM, album);

            insert = db.insert(Photo.TABLE_NAME, null, cv);
            if (insert == -1){return insert;}
        }
        db.close();
        return insert;
    }
    public List<PhotoModel> getPhotosFromAlbum(AlbumModel album){
        List<PhotoModel> photos = new ArrayList<>();
        String q = "SELECT * FROM " +
                Photo.TABLE_NAME + " WHERE " +
                Photo.COLUMN_ALBUM + " = " +
                album.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                byte[] bName = cursor.getBlob(1);
                byte[] bContent = cursor.getBlob(2);
                byte[] bFileType = cursor.getBlob(3);
                byte[] bThumbnail = cursor.getBlob(4);
                String time = cursor.getString(5);
                int albumId = cursor.getInt(6);
                //decrypt data
                String name = Util.decryptToString(bName);
                String content = Util.decryptToString(bContent);
                String fileType = Util.decryptToString(bFileType);
                String thumbnail = Util.decryptToString(bThumbnail);

                PhotoModel photo = new PhotoModel(id, name, content, thumbnail, fileType, time, albumId);
                photos.add(photo);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return photos;
    }
    public List<PhotoModel> getPhotoIdFromAlbum(AlbumModel album){
        List<com.project.real_calculator.database.models.PhotoModel> photos = new ArrayList<>();
        String q = "SELECT " + Photo._ID + " FROM " +
                Photo.TABLE_NAME + " WHERE " +
                Photo.COLUMN_ALBUM + " = " +
                album.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);

                PhotoModel photo = new PhotoModel(id, "", "", "", "", "", 0);
                photos.add(photo);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return photos;
    }
    public boolean updatePhotoName(PhotoModel photoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(photoModel.getId());
        // encrypt updates
        cv.put(Photo.COLUMN_NAME, Util.encryptToByte(photoModel.getName()));
        int updated = db.update(Photo.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean updatePhotoAlbumId(PhotoModel photoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(photoModel.getId());
        // id is not encrypted
        cv.put(Photo.COLUMN_ALBUM, photoModel.getId());
        int updated = db.update(Photo.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean updatePhoto(PhotoModel photoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(photoModel.getId());

        cv.put(Photo.COLUMN_ALBUM, photoModel.getId());
        cv.put(Photo.COLUMN_NAME, Util.encryptToByte(photoModel.getName()));
        cv.put(Photo.COLUMN_THUMBNAIL, Util.encryptToByte(photoModel.getThumbnail()));
        cv.put(Photo.COLUMN_CONTENT, Util.encryptToByte(photoModel.getContent()));
        int updated = db.update(Photo.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean deletePhotos(List<PhotoModel> photoModels){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] ids = new String[photoModels.size()];
        for (int i=0; i<photoModels.size(); i++){
            ids[i] = String.valueOf(photoModels.get(i).getId());
        }
        int deleted = db.delete(Photo.TABLE_NAME, "_id = ?", ids);
        db.close();
        return deleted > 0;
    }
}
