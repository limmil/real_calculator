package com.limmil.real_calculator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.limmil.real_calculator.database.UserContract.*;
import com.limmil.real_calculator.database.models.*;
import com.limmil.real_calculator.encryption.AES;
import com.limmil.real_calculator.encryption.Util;

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
            Album.COLUMN_COUNT + " INTEGER NOT NULL, " +
            Album.COLUMN_IV + " BLOB NOT NULL)";
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
            Photo.COLUMN_SIZE + " BLOB, " +
            Photo.COLUMN_IV + " BLOB NOT NULL, " +
            Photo.COLUMN_THUMB_IV + " BLOB NOT NULL, " +
            Photo.COLUMN_CONTENT_IV + " BLOB NOT NULL, " +
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

    private static final String SQL_CREATE_FOLDER = "CREATE TABLE " +
            Folder.TABLE_NAME + " (" +
            Folder._ID + " INTEGER PRIMARY KEY, " +
            Folder.COLUMN_NAME + " BLOB NOT NULL, " +
            Folder.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            Folder.COLUMN_COUNT + " INTEGER NOT NULL, " +
            Folder.COLUMN_IV + " BLOB NOT NULL)";
    private static final String SQL_DELETE_FOLDER =
            "DROP TABLE IF EXISTS " + Folder.TABLE_NAME;
    // files has foreign key from folder
    private static final String SQL_CREATE_FILE = "CREATE TABLE " +
            MyFile.TABLE_NAME + " (" +
            MyFile._ID + " INTEGER PRIMARY KEY, " +
            MyFile.COLUMN_NAME + " BLOB NOT NULL, " +
            MyFile.COLUMN_CONTENT + " BLOB, " +
            MyFile.COLUMN_FILETYPE + " BLOB, " +
            MyFile.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            MyFile.COLUMN_FOLDER + " INTEGER, " +
            MyFile.COLUMN_SIZE + " BLOB, " +
            MyFile.COLUMN_IV + " BLOB NOT NULL, " +
            MyFile.COLUMN_CONTENT_IV + " BLOB NOT NULL, " +
            " FOREIGN KEY ("+MyFile.COLUMN_FOLDER+") REFERENCES "+Folder.TABLE_NAME+"("+Folder._ID+"))";
    private static final String SQL_DELETE_FILE =
            "DROP TABLE IF EXISTS " + MyFile.TABLE_NAME;
    // create folder index on file table
    private static final String SQL_CREATE_FILE_FOLDER_INDEX = "CREATE INDEX " +
            MyFile.INDEX_FOLDER + " ON " +
            MyFile.TABLE_NAME + " (" +
            MyFile.COLUMN_FOLDER + ")";
    private static final String SQL_DELETE_FILE_FOLDER_INDEX =
            "DROP INDEX IF EXISTS " + MyFile.INDEX_FOLDER;

    private static final String SQL_CREATE_NOTES = "CREATE TABLE " +
            Note.TABLE_NAME + " (" +
            Note._ID + " INTEGER PRIMARY KEY, " +
            Note.COLUMN_TITLE_IV + " BLOB NOT NULL, " +
            Note.COLUMN_CONTENT_IV + " BLOB NOT NULL, " +
            Note.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
    private static final String SQL_DELETE_NOTES =
            "DROP TABLE IF EXISTS " + Note.TABLE_NAME;



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
        
        sqLiteDatabase.execSQL(SQL_CREATE_FOLDER);
        sqLiteDatabase.execSQL(SQL_CREATE_FILE);
        sqLiteDatabase.execSQL(SQL_CREATE_FILE_FOLDER_INDEX);

        sqLiteDatabase.execSQL(SQL_CREATE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_USER);
        
        sqLiteDatabase.execSQL(SQL_DELETE_PHOTO_ALBUM_INDEX);
        sqLiteDatabase.execSQL(SQL_DELETE_PHOTO);
        sqLiteDatabase.execSQL(SQL_DELETE_ALBUM);

        sqLiteDatabase.execSQL(SQL_DELETE_FILE_FOLDER_INDEX);
        sqLiteDatabase.execSQL(SQL_DELETE_FILE);
        sqLiteDatabase.execSQL(SQL_DELETE_FOLDER);

        sqLiteDatabase.execSQL(SQL_DELETE_NOTES);
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
        // get nonce
        byte[] iv = album.getIv();
        // encrypt name, thumbnail
        AES.setIV(iv);
        byte[] albumName = Util.encryptToByte(album.getAlbumName());

        cv.put(Album.COLUMN_NAME, albumName);
        cv.put(Album.COLUMN_COUNT, album.getItemsCount());
        cv.put(Album.COLUMN_IV, iv);

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
                byte[] iv = cursor.getBlob(5);
                //decrypt data
                AES.setIV(iv);
                String name = Util.decryptToString(bName);

                AlbumModel album = new AlbumModel(id, name, time, count);
                album.setIv(iv);
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
        AES.setIV(album.getIv());
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
    public long addPhoto(PhotoModel photo, AlbumModel albumModel){
        List<PhotoModel> tmp = new ArrayList<>();
        tmp.add(photo);
        return addPhotos(tmp, albumModel);
    }
    public long addPhotos(List<PhotoModel> photos, AlbumModel albumModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert = -1;
        for (PhotoModel tmp : photos){
            byte[] iv = tmp.getIv();
            // encrypt data
            AES.setIV(iv);
            byte[] name = Util.encryptToByte(tmp.getName());
            byte[] content = Util.encryptToByte(tmp.getContent());
            byte[] fileType = Util.encryptToByte(tmp.getFileType());
            byte[] thumbnail = Util.encryptToByte(tmp.getThumbnail());
            byte[] size = Util.encryptToByte(tmp.getSize());
            byte[] tiv = tmp.getThumbIv();
            byte[] civ = tmp.getContentIv();
            int album = albumModel.getId();
            // insert data
            cv.put(Photo.COLUMN_NAME, name);
            cv.put(Photo.COLUMN_CONTENT, content);
            cv.put(Photo.COLUMN_FILETYPE, fileType);
            cv.put(Photo.COLUMN_THUMBNAIL, thumbnail);
            cv.put(Photo.COLUMN_ALBUM, album);
            cv.put(Photo.COLUMN_SIZE, size);
            cv.put(Photo.COLUMN_IV, iv);
            cv.put(Photo.COLUMN_THUMB_IV, tiv);
            cv.put(Photo.COLUMN_CONTENT_IV, civ);

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
                byte[] bThumbnail = cursor.getBlob(3);
                byte[] bFileType = cursor.getBlob(4);
                String time = cursor.getString(5);
                int albumId = cursor.getInt(6);
                byte[] bSize = cursor.getBlob(7);
                byte[] iv = cursor.getBlob(8);
                byte[] tiv = cursor.getBlob(9);
                byte[] civ = cursor.getBlob(10);
                //decrypt data
                AES.setIV(iv);
                String name = Util.decryptToString(bName);
                String content = Util.decryptToString(bContent);
                String fileType = Util.decryptToString(bFileType);
                String thumbnail = Util.decryptToString(bThumbnail);
                String size = Util.decryptToString(bSize);

                PhotoModel photo = new PhotoModel(id, name, content, thumbnail, fileType, time, albumId);
                photo.setSize(size);
                photo.setIv(iv);
                photo.setThumbIv(tiv);
                photo.setContentIv(civ);

                photos.add(photo);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return photos;
    }
    public List<PhotoModel> getPhotoIdsFromAlbum(AlbumModel album){
        List<PhotoModel> photos = new ArrayList<>();
        String q = "SELECT " + Photo._ID +", "+
                Photo.COLUMN_THUMB_IV +" FROM " +
                Photo.TABLE_NAME + " WHERE " +
                Photo.COLUMN_ALBUM + " = " +
                album.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                byte[] tiv = cursor.getBlob(1);

                PhotoModel photo = new PhotoModel(id, "", "", "", "", "", 0);
                photo.setThumbIv(tiv);
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
        AES.setIV(photoModel.getIv());
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
        cv.put(Photo.COLUMN_ALBUM, photoModel.getAlbum());
        int updated = db.update(Photo.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean updatePhotoAlbumIds(List<PhotoModel> photoModels, int albumId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Photo.COLUMN_ALBUM, albumId);

        String[] ids = new String[photoModels.size()];
        for (int i=0; i<photoModels.size(); i++){
            ids[i] = String.valueOf(photoModels.get(i).getId());
        }

        String whereClause = Photo._ID + " IN (" + TextUtils.join(",", ids) + ")";
        int updated = db.update(Photo.TABLE_NAME, cv, whereClause, null);

        db.close();
        return  updated > 0;

    }
    public boolean updatePhoto(PhotoModel photoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(photoModel.getId());

        AES.setIV(photoModel.getIv());
        cv.put(Photo.COLUMN_ALBUM, photoModel.getAlbum());
        cv.put(Photo.COLUMN_NAME, Util.encryptToByte(photoModel.getName()));
        cv.put(Photo.COLUMN_THUMBNAIL, Util.encryptToByte(photoModel.getThumbnail()));
        cv.put(Photo.COLUMN_FILETYPE, Util.encryptToByte(photoModel.getFileType()));
        cv.put(Photo.COLUMN_CONTENT, Util.encryptToByte(photoModel.getContent()));
        cv.put(Photo.COLUMN_SIZE, Util.encryptToByte(photoModel.getSize()));
        int updated = db.update(Photo.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }

    /**
     * @param photoModel only needs id fields within each PhotoModel object
     * @return true if deleted
     */
    public boolean deletePhoto(PhotoModel photoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] id = { String.valueOf(photoModel.getId()) };
        int deleted = db.delete(Photo.TABLE_NAME, "_id = ?", id);

        db.close();
        return deleted > 0;
    }
    public boolean deletePhotos(List<PhotoModel> photoModels){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] ids = new String[photoModels.size()];
        for (int i=0; i<photoModels.size(); i++){
            ids[i] = String.valueOf(photoModels.get(i).getId());
        }

        String whereClause = Photo._ID + " IN (" + TextUtils.join(",", ids) + ")";
        int deleted = db.delete(Photo.TABLE_NAME, whereClause, null);

        db.close();
        return  deleted > 0;
    }
    public boolean deleteAllPhotosFromAlbum(AlbumModel albumModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String albumId = String.valueOf(albumModel.getId());

        // Define 'where' part of query.
        String selection = Photo.COLUMN_ALBUM + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { albumId };
        // Issue SQL statement.
        int deletedRows = db.delete(Photo.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows > 0;
    }


    // FOLDER TABLE ________________________________________________________________________________
    /* FOLDER CRUD */
    public boolean addFolder(FolderModel folder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        byte[] iv = folder.getIv();
        // encrypt name
        AES.setIV(iv);
        byte[] folderName = Util.encryptToByte(folder.getFolderName());

        cv.put(Folder.COLUMN_NAME, folderName);
        cv.put(Folder.COLUMN_COUNT, folder.getItemsCount());
        cv.put(Folder.COLUMN_IV, iv);

        long insert = db.insert(Folder.TABLE_NAME, null, cv);
        db.close();
        return insert != -1;
    }
    public List<FolderModel> getFolders(){
        List<FolderModel> folderModels = new ArrayList<>();
        String q = "SELECT * FROM " + Folder.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                byte[] bName = cursor.getBlob(1);
                String time = cursor.getString(2);
                int count = cursor.getInt(3);
                byte[] iv = cursor.getBlob(4);
                //decrypt data
                AES.setIV(iv);
                String name = Util.decryptToString(bName);

                FolderModel folder = new FolderModel(id, name, time, count);
                folderModels.add(folder);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return folderModels;
    }
    public boolean updateFolder(FolderModel folder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(folder.getId());
        // encrypt new name
        AES.setIV(folder.getIv());
        cv.put(Folder.COLUMN_NAME, Util.encryptToByte(folder.getFolderName()));
        int updated = db.update(Folder.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean deleteFolder(FolderModel folder){
        SQLiteDatabase db = this.getWritableDatabase();
        String id = String.valueOf(folder.getId());
        int deleted = db.delete(Folder.TABLE_NAME, "_id = ?", new String[]{id});
        db.close();

        return deleted > 0;
    }

    // FILE TABLE __________________________________________________________________________________
    /* FILE CRUD */
    public long addFile(MyFileModel file, FolderModel folder){
        List<MyFileModel> tmp = new ArrayList<>();
        tmp.add(file);
        return addFiles(tmp, folder);
    }
    public long addFiles(List<MyFileModel> files, FolderModel folder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert = -1;
        for (MyFileModel tmp : files){
            // encrypt data
            byte[] iv = tmp.getIv();
            AES.setIV(iv);
            byte[] name = Util.encryptToByte(tmp.getName());
            byte[] content = Util.encryptToByte(tmp.getContent());
            byte[] fileType = Util.encryptToByte(tmp.getFileType());
            byte[] size = Util.encryptToByte(tmp.getSize());
            int folderIn = folder.getId();
            // insert data
            cv.put(MyFile.COLUMN_NAME, name);
            cv.put(MyFile.COLUMN_CONTENT, content);
            cv.put(MyFile.COLUMN_FILETYPE, fileType);
            cv.put(MyFile.COLUMN_FOLDER, folderIn);
            cv.put(MyFile.COLUMN_SIZE, size);
            cv.put(MyFile.COLUMN_IV, iv);
            cv.put(MyFile.COLUMN_CONTENT_IV, tmp.getContentIv());

            insert = db.insert(MyFile.TABLE_NAME, null, cv);
            if (insert == -1){return insert;}
        }
        db.close();
        return insert;
    }
    public List<MyFileModel> getFilesFromFolder(FolderModel folderModel){
        List<MyFileModel> files = new ArrayList<>();
        String q = "SELECT * FROM " +
                MyFile.TABLE_NAME + " WHERE " +
                MyFile.COLUMN_FOLDER + " = " +
                folderModel.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                byte[] bName = cursor.getBlob(1);
                byte[] bContent = cursor.getBlob(2);
                byte[] bFileType = cursor.getBlob(3);
                String time = cursor.getString(4);
                int folderId = cursor.getInt(5);
                byte[] bSize = cursor.getBlob(6);
                byte[] iv = cursor.getBlob(7);
                byte[] civ = cursor.getBlob(8);
                //decrypt data
                AES.setIV(iv);
                String name = Util.decryptToString(bName);
                String content = Util.decryptToString(bContent);
                String fileType = Util.decryptToString(bFileType);
                String size = Util.decryptToString(bSize);

                MyFileModel file = new MyFileModel(id, name, content, fileType, time, folderId);
                file.setSize(size);
                file.setContentIv(civ);

                files.add(file);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return files;
    }
    public List<MyFileModel> getFileIdsFromFolder(FolderModel folder){
        List<MyFileModel> files = new ArrayList<>();
        String q = "SELECT " + MyFile._ID + " FROM " +
                MyFile.TABLE_NAME + " WHERE " +
                MyFile.COLUMN_FOLDER + " = " +
                folder.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);

                MyFileModel file = new MyFileModel(id, "", "", "", "", 0);
                files.add(file);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return files;
    }
    public boolean updateFileName(MyFileModel myFileModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(myFileModel.getId());
        // encrypt updates
        AES.setIV(myFileModel.getIv());
        cv.put(Photo.COLUMN_NAME, Util.encryptToByte(myFileModel.getName()));
        int updated = db.update(MyFile.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean updateFileFolderId(MyFileModel myFileModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(myFileModel.getId());
        // id is not encrypted
        cv.put(MyFile.COLUMN_FOLDER, myFileModel.getFolder());
        int updated = db.update(MyFile.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }
    public boolean updateFileFolderIds(List<MyFileModel> myFileModels, int folderId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyFile.COLUMN_FOLDER, folderId);

        String[] ids = new String[myFileModels.size()];
        for (int i = 0; i< myFileModels.size(); i++){
            ids[i] = String.valueOf(myFileModels.get(i).getId());
        }

        String whereClause = MyFile._ID + " IN (" + TextUtils.join(",", ids) + ")";
        int updated = db.update(MyFile.TABLE_NAME, cv, whereClause, null);

        db.close();
        return  updated > 0;

    }
    public boolean updateFile(MyFileModel myFileModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String id = String.valueOf(myFileModel.getId());

        // encrypt updated data
        AES.setIV(myFileModel.getIv());
        cv.put(MyFile.COLUMN_FOLDER, myFileModel.getFolder());
        cv.put(MyFile.COLUMN_NAME, Util.encryptToByte(myFileModel.getName()));
        cv.put(MyFile.COLUMN_FILETYPE, Util.encryptToByte(myFileModel.getFileType()));
        cv.put(MyFile.COLUMN_CONTENT, Util.encryptToByte(myFileModel.getContent()));
        cv.put(MyFile.COLUMN_SIZE, Util.encryptToByte(myFileModel.getSize()));
        int updated = db.update(MyFile.TABLE_NAME, cv, "_id = ?", new String[]{id});
        db.close();

        return updated > 0;
    }

    /**
     * @param myFileModel only needs id fields within each PhotoModel object
     * @return true if deleted
     */
    public boolean deleteFile(MyFileModel myFileModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] id = { String.valueOf(myFileModel.getId()) };
        int deleted = db.delete(MyFile.TABLE_NAME, "_id = ?", id);

        db.close();
        return deleted > 0;
    }
    public boolean deleteFiles(List<MyFileModel> myFileModels){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] ids = new String[myFileModels.size()];
        for (int i=0; i<myFileModels.size(); i++){
            ids[i] = String.valueOf(myFileModels.get(i).getId());
        }

        String whereClause = MyFile._ID + " IN (" + TextUtils.join(",", ids) + ")";
        int deleted = db.delete(MyFile.TABLE_NAME, whereClause, null);

        db.close();
        return  deleted > 0;
    }
    public boolean deleteAllFilesFromFolder(FolderModel folderModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String folderId = String.valueOf(folderModel.getId());

        // Define 'where' part of query.
        String selection = MyFile.COLUMN_FOLDER + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { folderId };
        // Issue SQL statement.
        int deletedRows = db.delete(MyFile.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows > 0;
    }


    // NOTES TABLE _________________________________________________________________________________
    /* NOTES CRUD */
    public long addNote(NoteModel noteModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert = -1;
        cv.put(Note.COLUMN_TITLE_IV, noteModel.getTitleIv());
        cv.put(Note.COLUMN_CONTENT_IV, noteModel.getContentIv());
        insert = db.insert(Note.TABLE_NAME, null, cv);
        db.close();

        return insert;
    }
    public List<NoteModel> getAllNotes(){
        List<NoteModel> noteModels = new ArrayList<>();
        String q = "SELECT * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                byte[] tiv = cursor.getBlob(1);
                byte[] civ = cursor.getBlob(2);
                String time = cursor.getString(3);

                NoteModel noteModel = new NoteModel(id,tiv,civ,time);
                noteModels.add(noteModel);
            }while(cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return noteModels;
    }
    public boolean deleteNote(NoteModel noteModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] id = { String.valueOf(noteModel.getId()) };
        int deleted = db.delete(Note.TABLE_NAME, "_id = ?", id);

        db.close();
        return deleted > 0;
    }
}
