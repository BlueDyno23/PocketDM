package com.example.pocketdm.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;;
import android.net.Uri;

import com.example.pocketdm.Models.DatasetModel;

public class SqlUtils extends SQLiteOpenHelper{
    private static final String DB_NAME = "pocketdb";
    private static final int DB_VERSION = 1;
    private static SqlUtils _instance;

    public static SqlUtils getInstance(Context context) {
        if (_instance == null) {
            _instance = new SqlUtils(context);
        }
        return _instance;
    }
    private SqlUtils(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void saveDatasetAsSQL(DatasetModel datasetModel){

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
