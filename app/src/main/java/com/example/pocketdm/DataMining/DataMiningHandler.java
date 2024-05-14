package com.example.pocketdm.DataMining;

import android.content.Context;

import weka.core.Instances;

public interface DataMiningHandler {
    // Method to train a model and return it
    Instances trainModel(Context context, String tableName, String[] selectedColumns, String targetColumn, int percentage);

    // Method to predict based on trained model and return the predictions
    double[] predict(Context context, Instances testInstances);
}
