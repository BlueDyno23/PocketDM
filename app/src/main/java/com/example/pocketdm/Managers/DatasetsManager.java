package com.example.pocketdm.Managers;

import android.content.ContentValues;
import android.content.Context;

import com.example.pocketdm.Models.DatasetModel;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;

public class DatasetsManager {
    private static final String TABLE_NAME = "secret_datasets_table";
    private static DatasetsManager _instance;

    private DatasetsManager() {

    }
    public static DatasetsManager getInstance() {
        if (_instance == null) {
            _instance = new DatasetsManager();
        }
        return _instance;
    }


    public static void addDataset(Context context, DatasetModel dataset) {
        HelperDb helperDb = new HelperDb(context);
        SQLUtils sqlUtils = new SQLUtils(helperDb.getWritableDatabase());


        sqlUtils.createTable(TABLE_NAME, new String[]{"nickname", "name", "description", "version", "dataset_path", "columns", "rows"});
        ContentValues cv = new ContentValues();
        cv.put("nickname", dataset.getDatasetNickname());
        cv.put("name", dataset.getDatasetName());
        cv.put("description", dataset.getDatasetDescription());
        cv.put("version", dataset.getDatasetVersion());
        cv.put("dataset_path", dataset.getFilePath());
        cv.put("columns", dataset.getColumnsCount());
        cv.put("rows", dataset.getRowsCount());
        sqlUtils.insertData(TABLE_NAME, cv);
    }

}
