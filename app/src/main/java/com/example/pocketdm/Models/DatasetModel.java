package com.example.pocketdm.Models;

import android.content.Context;
import android.database.Cursor;

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
    public DatasetModel(String datasetName, String datasetNickname, String datasetDescription, double datasetVersion, String filePath, int rowsCount, int columnsCount) {
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
        SQLUtils sqlUtils = new SQLUtils(helperDb.getReadableDatabase());


        String[][] data = new String[rowsCount][columnsCount];
        return data;
    }
    //endregion

    //region Methods

    //endregion
}
