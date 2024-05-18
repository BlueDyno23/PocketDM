package com.example.pocketdm.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;

import java.util.Date;

public class DatasetModel {
    //region Fields
    private String datasetName;
    private String datasetNickname;
    private String datasetDescription;
    private double datasetVersion;
    private int rowsCount;
    private int columnsCount;
    //endregion

    //region Constructors
    public DatasetModel() {

    }
    public DatasetModel(String datasetName, String datasetNickname, String datasetDescription, double datasetVersion, int rowsCount, int columnsCount) {
        this.datasetName = datasetName;
        this.datasetNickname = datasetNickname;
        this.datasetDescription = datasetDescription;
        this.datasetVersion = datasetVersion;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
    }
    //endregion

    //region Getters and Setters
    public String getDatasetName() {
        return datasetName;
    }
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetNickname() {
        return datasetNickname;
    }

    public void setDatasetNickname(String datasetNickname) {
        this.datasetNickname = datasetNickname;
    }

    public String getDatasetDescription() {
        return datasetDescription;
    }

    public void setDatasetDescription(String datasetDescription) {
        this.datasetDescription = datasetDescription;
    }

    public double getDatasetVersion() {
        return datasetVersion;
    }

    public void setDatasetVersion(double datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public int getColumnsCount() {
        return columnsCount;
    }

    public void setColumnsCount(int columnsCount) {
        this.columnsCount = columnsCount;
    }

    public String[][] getData(Context context) {
        HelperDb helperDb = new HelperDb(context);
        SQLiteDatabase db = helperDb.getReadableDatabase();

        try (Cursor c = db.query(datasetNickname, null, null, null, null, null, null)) {
            int rowCount = c.getCount();
            int columnCount = c.getColumnCount();
            String[][] data = new String[rowCount+1][columnCount];

            for (int i = 0; i < columnCount; i++) {
                data[0][i] = c.getColumnName(i);
            }

            if (c.moveToFirst()) {
                int row = 1;
                do {
                    for (int i = 0; i < columnCount; i++) {
                        data[row][i] = c.getString(i);
                    }
                    row++;
                } while (c.moveToNext());
            }
            return data;
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            return null;
        } finally {

        }
    }

    //endregion

    //region Methods

    //endregion
}
