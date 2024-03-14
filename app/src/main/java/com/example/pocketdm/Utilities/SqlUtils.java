package com.example.pocketdm.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;;
import android.net.Uri;

public class SqlUtils extends SQLiteOpenHelper{

    private static SqlUtils _instance;

    public static SqlUtils getInstance(Context context) {
        if (_instance == null) {
            _instance = new SqlUtils(context);
        }
        return _instance;
    }

    private static final String DB_NAME = "pocketdb";
    private static final int DB_VERSION = 1;


    public SqlUtils(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static void initializeSqlUtils()
    {

    }
    public void saveDatasetToSqlDb(Context context, Uri datasetUri, String datasetName) {
        String rawContent = FileUtils.getRawFileContentFromUri(context, datasetUri);
        String[] columns = rawContent.split("\n")[0].split(",");

        // create table by columns
        SQLiteDatabase db = this.getWritableDatabase();
        String st = "CREATE TABLE IF NOT EXISTS " + datasetName + " ( ";
        for (String column : columns) {
            st += column + " TEXT, ";
        }
        st = st.substring(0, st.length() - 2);
        st += " );";

        db.execSQL(st);

        // fill up data in the table
        String[][] rows = new String[rawContent.split("\n").length - 1][];
        for (int i = 1; i < rawContent.split("\n").length; i++) {
            rows[i - 1] = rawContent.split("\n")[i].split(",");
        }
        ContentValues cv = new ContentValues();
        for(int i = 0; i < rows.length; i++) {
            for(int j = 0; j < rows[i].length; j++) {
                cv.put(columns[j], rows[i][j]);
            }
        }

        db.insert(datasetName, null, cv);
    }

    public void deleteDatasetFromSqlDb(String datasetName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + datasetName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
