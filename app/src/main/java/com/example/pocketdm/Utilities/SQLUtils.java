package com.example.pocketdm.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

import com.example.pocketdm.Enums.ColumnType;
import com.example.pocketdm.Models.DatasetModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLUtils {
    private SQLiteDatabase database;

    public SQLUtils(SQLiteDatabase database) {
        this.database = database;
    }

    public void createTable(String tableName, String[] columns) {
        StringBuilder createTableQuery = new StringBuilder();
        createTableQuery.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        for (int i = 0; i < columns.length; i++) {
            String sanitizedColumn = columns[i].replace('.', '_');
            createTableQuery.append(sanitizedColumn);
            if (i < columns.length - 1) {
                createTableQuery.append(", ");
            }
        }

        createTableQuery.append(");");
        database.execSQL(createTableQuery.toString());
    }


    /*public long insertData(String tableName, ContentValues values) {
        return database.insert(tableName, null, values);
    }*/

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
    public String[] getColumnNames(String tableName) {
        Cursor cursor = null;
        String[] columnNames = null;

        try {
            cursor = database.rawQuery("PRAGMA table_info(" + tableName + ")", null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnCount = cursor.getCount();
                columnNames = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = cursor.getString(Math.abs(cursor.getColumnIndex("name")));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return columnNames;
    }
    public void fillTable(Context context, String tableName, Uri fileAdress) {
        String raw = FileUtils.getRawFileContentFromUri(context, fileAdress);
        String[] tableRows = raw.split("\n");
        String[] columnNames = tableRows[0].split(",");

        // Replace dots with underscores in column names
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = columnNames[i].replace('.', '_');
        }

        createTable(tableName, columnNames);

        for (int i = 1; i < tableRows.length; i++) {
            ContentValues cv = new ContentValues();
            String[] rowValues = tableRows[i].split(",");
            for (int j = 0; j < columnNames.length; j++) {
                cv.put(columnNames[j], rowValues[j]);
            }
            insertData(tableName, cv);
        }
    }

    public void insertData(String tableName, ContentValues values) {
        StringBuilder insertQuery = new StringBuilder();
        insertQuery.append("INSERT INTO ")
                .append(tableName)
                .append(" (");

        String[] columns = values.keySet().toArray(new String[0]);
        for (int i = 0; i < columns.length; i++) {
            insertQuery.append(columns[i]);
            if (i < columns.length - 1) {
                insertQuery.append(", ");
            }
        }

        insertQuery.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            insertQuery.append("?");
            if (i < columns.length - 1) {
                insertQuery.append(", ");
            }
        }
        insertQuery.append(");");

        SQLiteStatement statement = database.compileStatement(insertQuery.toString());
        for (int i = 0; i < columns.length; i++) {
            Object value = values.get(columns[i]);
            if (value instanceof String) {
                statement.bindString(i + 1, (String) value);
            } else if (value instanceof Integer) {
                statement.bindLong(i + 1, (Integer) value);
            } else if (value instanceof Double) {
                statement.bindDouble(i + 1, (Double) value);
            } else if (value == null) {
                statement.bindNull(i + 1);
            }
        }
        statement.executeInsert();
    }

    public String[][] toStringArray(String datasetName) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + datasetName, null);
        int rowCount = cursor.getCount();
        int columnCount = cursor.getColumnCount();

        String[][] data = new String[rowCount + 1][columnCount];

        if (cursor != null) {
            String[] columnNames = cursor.getColumnNames();
            for (int j = 0; j < columnCount; j++) {
                data[0][j] = columnNames[j];
            }

            cursor.moveToFirst();
            for (int i = 1; i <= rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    data[i][j] = cursor.getString(j);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        return data;
    }


    public void addColumn(String tableName, String columnName, String columnType) {
        database.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
    }

    public void deleteRowByRowNum(String tableName, int rowNum) {
        database.beginTransaction();
        try {
            String deleteRowQuery = "DELETE FROM " + tableName + " WHERE ROWID = (SELECT ROWID FROM " + tableName + " LIMIT 1 OFFSET " + (rowNum-1) + ");";
            database.execSQL(deleteRowQuery);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public ColumnType getColumnType(String tableName, String columnName) {
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String s = cursor.getString(Math.abs(cursor.getColumnIndex(columnName)));
                String[] diffs = findDifferentValues(tableName, columnName);
                int diff = diffs.length;
                if(tryParseInt(s)!=null || tryParseDouble(s)!=null) {
                    if(diff < 3) {
                        return ColumnType.BINARY;
                    }
                    else{
                        return ColumnType.NUMERIC;
                    }
                }
                else if (diff < getRowCount(tableName)/ 5) {
                    return ColumnType.CATEGORICAL;
                }
                else if(diff < 3) {
                    return ColumnType.BINARY_TEXT;
                }
                else {
                    return ColumnType.TEXT;
                }

            } while (cursor.moveToNext());
        }

        return ColumnType.UNKNOWN;
    }

    private Double tryParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public String[] findDifferentValues(String tableName, String columnName) {
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        Map<String, Integer> map = new HashMap<String, Integer>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String s = cursor.getString(Math.abs(cursor.getColumnIndex(columnName)));
                map.put(s, map.getOrDefault(s, 0) + 1);
            } while (cursor.moveToNext());
        }

        cursor.close();
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(entry.getKey());
        }
        return list.toArray(new String[0]);
    }
    public void dropTable(String tableName) {
        database.execSQL("DROP TABLE IF EXISTS " + tableName);
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
                String tableName = cursor.getString(Math.abs(cursor.getColumnIndex("name")));
                tableNames[index++] = tableName;
            }
            cursor.close();
        }
        return tableNames;
    }

    public List<DatasetModel> getDatasetModels(String tableName) {
        List<DatasetModel> datasetModelList = new ArrayList<>();
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Create DatasetModel object from cursor data
                DatasetModel datasetModel = new DatasetModel();
                datasetModel.setDatasetName(cursor.getString(Math.abs(cursor.getColumnIndex("name"))));
                datasetModel.setDatasetNickname(cursor.getString(Math.abs(cursor.getColumnIndex("nickname"))));
                datasetModel.setDatasetDescription(cursor.getString(Math.abs(cursor.getColumnIndex("description"))));
                datasetModel.setDatasetVersion(cursor.getDouble(Math.abs(cursor.getColumnIndex("version"))));
                datasetModel.setRowsCount(cursor.getInt(Math.abs(cursor.getColumnIndex("rows"))));
                datasetModel.setColumnsCount(cursor.getInt(Math.abs(cursor.getColumnIndex("columns"))));

                // Add DatasetModel object to list
                datasetModelList.add(datasetModel);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return datasetModelList;
    }

    public DatasetModel getDatasetModel(String datasetNickname) {
        Cursor cursor = database.query(HelperSecretDb.HTBL_NAME, null, HelperSecretDb.TBL_NICKNAME + " = ?", new String[]{datasetNickname}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    DatasetModel datasetModel = new DatasetModel();
                    datasetModel.setDatasetName(cursor.getString(Math.abs(cursor.getColumnIndex("name"))));
                    datasetModel.setDatasetNickname(cursor.getString(Math.abs(cursor.getColumnIndex("nickname"))));
                    datasetModel.setDatasetDescription(cursor.getString(Math.abs(cursor.getColumnIndex("description"))));
                    datasetModel.setDatasetVersion(cursor.getDouble(Math.abs(cursor.getColumnIndex("version"))));
                    datasetModel.setRowsCount(cursor.getInt(Math.abs(cursor.getColumnIndex("rows"))));
                    datasetModel.setColumnsCount(cursor.getInt(Math.abs(cursor.getColumnIndex("columns"))));
                    return datasetModel;
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public void duplicateTable(String tableName, String newTableName) {
        database.beginTransaction();
        try {
            String[] columns = getColumnNames(tableName);
            createTable(newTableName, columns);

            try (Cursor cursor = database.query(tableName, null, null, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        ContentValues cv = new ContentValues();
                        for (String column : columns) {
                            int columnIndex = cursor.getColumnIndex(column);
                            if (columnIndex != -1) {
                                cv.put(column, cursor.getString(columnIndex));
                            }
                        }
                        insertData(newTableName, cv);
                    } while (cursor.moveToNext());
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void dropColumn(String tableName, String columnName) {
        String[] columns = getColumnNames(tableName);
        String[] newColumns = excludeColumn(columns, columnName);

        String tempTableName = "temp_" + tableName;

        createTable(tempTableName, newColumns);

        Cursor cursor = database.query(tableName, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ContentValues cv = new ContentValues();
                for (String column : newColumns) {
                    cv.put(column, cursor.getString(cursor.getColumnIndex(column)));
                }
                insertData(tempTableName, cv);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Drop the old table
        dropTable(tableName);

        // Rename the temp table to the original table name
        database.execSQL("ALTER TABLE " + tempTableName + " RENAME TO " + tableName);
    }

    public void renameTable(String tableName, String newTableName) {
        database.execSQL("ALTER TABLE " + tableName + " RENAME TO " + newTableName);
    }


    public String[] excludeColumn(String[] columns, String columnName) {
        List<String> list = new ArrayList<>(Arrays.asList(columns));
        list.remove(columnName);
        return list.toArray(new String[0]);
    }

    public void discardAllTemps() {
        for (String s: getAllTableNames()) {
            if(s.contains("temp_") || s.contains("edit")) {
                dropTable(s);
            }
        }
    }
}