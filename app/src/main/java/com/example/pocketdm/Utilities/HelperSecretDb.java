package com.example.pocketdm.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperSecretDb extends SQLiteOpenHelper {

    public static final String DB_NAME = "secret_db";
    public static final String HTBL_NAME = "secret_table";
    public static final String TBL_NAME = "name";
    public static final String TBL_NICKNAME = "nickname";
    public static final String TBL_DESCRIPTION = "description";
    public static final String TBL_VERSION = "version";
    public static final String TBL_COLUMNS = "columns";
    public static final String TBL_ROWS = "rows";


    public HelperSecretDb(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLUtils sqlUtils = new SQLUtils(getWritableDatabase());
        sqlUtils.createTable(HTBL_NAME, new String[]{TBL_NAME, TBL_NICKNAME, TBL_DESCRIPTION, TBL_VERSION, TBL_COLUMNS, TBL_ROWS});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
