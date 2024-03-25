package com.example.pocketdm.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class SQLUtils {
    private SQLiteDatabase database;

    public SQLUtils(SQLiteDatabase database) {
        this.database = database;
    }

    public void createTable(String tableName, String[] columns)
    {
        StringBuilder createTableQuery = new StringBuilder();
        createTableQuery.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        for (int i = 0; i < columns.length; i++) {
            createTableQuery.append(columns[i]);
            if (i < columns.length - 1) {
                createTableQuery.append(", ");
            }
        }

        createTableQuery.append(");");
        database.execSQL(createTableQuery.toString());
    }

    public long insertData(String tableName, ContentValues values) {
        return database.insert(tableName, null, values);
    }

    public Cursor queryData(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return database.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int deleteData(String tableName, String selection, String[] selectionArgs) {
        return database.delete(tableName, selection, selectionArgs);
    }

    public int updateData(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(tableName, values, selection, selectionArgs);
    }

    public int getRowCount(String tableName) {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        int rowCount = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            rowCount = cursor.getInt(0);
            cursor.close();
        }
        return rowCount;
    }

    public int getColumnCount(String tableName) {
        Cursor cursor = database.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        int columnCount = 0;
        if (cursor != null) {
            columnCount = cursor.getCount();
            cursor.close();
        }
        return columnCount;
    }
    public void fillTable(Context context, String tableName, Uri fileAdress){
        String raw = FileUtils.getRawFileContentFromUri(context,fileAdress);
        String[] tableRows = raw.split("\n");
        String[] columnNames = tableRows[0].split(",");

        createTable(tableName, columnNames);
        for (int i=1; i<tableRows.length; i++)
        {
            ContentValues cv = new ContentValues();
            for(int j=0; j<columnNames.length; j++)
            {
                cv.put(columnNames[j], tableRows[i].split(",")[j]);
            }
            insertData(tableName, cv);
        }
    }

    /**
     * Get all table names in the SQLite database
     * @return An array of table names
     */
    public String[] getAllTableNames() {
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        String[] tableNames = null;
        if (cursor != null) {
            int count = cursor.getCount();
            tableNames = new String[count];
            int index = 0;
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(cursor.getColumnIndex("name"));
                tableNames[index++] = tableName;
            }
            cursor.close();
        }
        return tableNames;
    }
}