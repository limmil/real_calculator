package com.limmil.real_calculator.database;

import android.provider.BaseColumns;

public final class UserContract {

    // To prevent someone from instantiating the contract class
    // make the constructor private
    private UserContract() {}

    /* Inner classes that defines the table contents */
    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "USER";
        public static final String COLUMN_PASSWORD = "PASSWORD";
        public static final String COLUMN_IV = "IV";
        public static final String COLUMN_MKEY = "MKEY";
    }

    public static class Album implements BaseColumns {
        public static final String TABLE_NAME = "ALBUM";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_THUMBNAIL = "THUMBNAIL";
        public static final String COLUMN_COUNT = "COUNT";
        public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
        public static final String COLUMN_IV = "IV";
    }
    public static class Photo implements BaseColumns {
        public static final String TABLE_NAME = "PHOTOS";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_CONTENT = "CONTENT";
        public static final String COLUMN_THUMBNAIL = "THUMBNAIL";
        public static final String COLUMN_FILETYPE = "FILETYPE";
        public static final String COLUMN_ALBUM = "ALBUM";
        public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
        public static final String COLUMN_SIZE = "SIZE";
        public static final String COLUMN_IV = "IV";
        public static final String COLUMN_THUMB_IV = "TIV";
        public static final String COLUMN_CONTENT_IV = "CIV";
        public static final String INDEX_ALBUM = "PHOTOS_ALBUM_IDX";
    }

    public static class Folder implements  BaseColumns {
        public static final String TABLE_NAME = "FOLDER";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_COUNT = "COUNT";
        public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
        public static final String COLUMN_IV = "IV";
    }
    public static class MyFile implements BaseColumns {
        public static final String TABLE_NAME = "FILES";
        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_CONTENT = "CONTENT";
        public static final String COLUMN_FILETYPE = "FILETYPE";
        public static final String COLUMN_FOLDER = "FOLDER";
        public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
        public static final String COLUMN_SIZE = "SIZE";
        public static final String COLUMN_IV = "IV";
        public static final String COLUMN_CONTENT_IV = "CIV";
        public static final String INDEX_FOLDER = "PHOTOS_FOLDER_IDX";
    }

    public static class Note implements  BaseColumns {
        public static final String TABLE_NAME = "NOTES";
        public static final String COLUMN_TITLE_IV = "TIV";
        public static final String COLUMN_CONTENT_IV = "CIV";
        public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
    }
}
