package com.example.pocketdm.DataMining;


import android.content.Context;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

public class LinearRegressionHandler implements DataMiningHandler {
    private LinearRegression linearRegression;
    private Instances trainData;

    public LinearRegressionHandler() {
        // Initialize the linear regression classifier
        linearRegression = new LinearRegression();
    }

    @Override
    public Instances trainModel(Context context, String tableName, String[] selectedColumns, String targetColumn, int percentage) {
        // Convert data from the SQL database to Instances using CursorToInstancesConverter
        Instances data = CursorToInstancesConverter.convertCursorToInstances(context, tableName, selectedColumns, targetColumn, percentage);

        // Set the class index (target column)
        data.setClassIndex(data.attribute(targetColumn).index());

        // Train the linear regression model
        try {
            linearRegression.buildClassifier(data);
            trainData = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public double[] predict(Context context, Instances testInstances) {
        // Predict outcomes using the trained linear regression model
        double[] predictions = new double[testInstances.numInstances()];
        try {
            for (int i = 0; i < testInstances.numInstances(); i++) {
                predictions[i] = linearRegression.classifyInstance(testInstances.instance(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictions;
    }
}