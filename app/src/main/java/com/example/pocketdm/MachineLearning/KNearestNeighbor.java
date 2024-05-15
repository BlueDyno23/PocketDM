package com.example.pocketdm.MachineLearning;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pocketdm.Activities.BaseActivity;
import com.example.pocketdm.Utilities.HelperDb;
import com.example.pocketdm.Utilities.SQLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNearestNeighbor {
    private int K;
    private HelperDb helperDb;
    private SQLiteDatabase database;
    private String tableName;
    private String labelColumn;
    private final String tempName = "temp_knn";
    public KNearestNeighbor( int k, HelperDb helperDb, String tableName, String labelColumn) {
        this.K = k;
        this.database = database;
        this.tableName = tableName;
        this.labelColumn = labelColumn;
        this.helperDb = helperDb;
        this.database = helperDb.getReadableDatabase();
    }

    public String predict(double[] inputs, String labelColumn) {
        SQLUtils sqlUtils = new SQLUtils(database);
        sqlUtils.createTemporaryTableFromExistingWithData(tableName, tempName);
        sqlUtils.dropColumn(tempName, labelColumn);

        double[] distances = new double[sqlUtils.getRowCount(tempName)];
        int ind = 0;

        Cursor cursor = sqlUtils.queryData(tempName, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double[] row = new double[cursor.getColumnCount()];
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    row[i] = cursor.getDouble(i);
                }
                distances[ind] = distance(inputs, row);
                ind++;
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        int[] indices = findSmallestIndex(distances, K);
        Map<String, Integer> counts = new HashMap<>();
        for(int i = 0; i < K; i++) {
            counts.put(findLabelByIndex(indices[i]), counts.getOrDefault(findLabelByIndex(indices[i]), 0) + 1);
        }

        String maxKey = null;
        int maxValue = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > maxValue || (entry.getValue() == maxValue && maxKey == null)) {
                maxKey = entry.getKey();
                maxValue = entry.getValue();
            }
        }

        return maxKey;
    }


    private double distance(double[] a, double[] b){
        double d = 0;
        for (int i = 0; i < a.length; i++) {
            d += Math.pow(a[i] - b[i], 2);
        }
        return d;
    }

    private String findLabelByIndex(int index){
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToPosition(index)) {
            return cursor.getString(Math.abs(cursor.getColumnIndex(labelColumn)));
        }
        return null;
    }
    private int[] findSmallestIndex(double[] array, int n) {
        double[] copyArray = Arrays.copyOf(array, array.length);

        Arrays.sort(copyArray);
        int[] indices = new int[n];

        for (int i = 0; i < n; i++) {
            double smallest = copyArray[i];
            for (int j = 0; j < array.length; j++) {
                if (array[j] == smallest) {
                    indices[i] = j;
                    break;
                }
            }
        }
        return indices;
    }

    public void setK(int k) {
        this.K = k;
    }

    public int getK() {
        return this.K;
    }

}
