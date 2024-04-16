package com.example.pocketdm.DataMining;

import android.content.Context;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class ClassificationHandler implements DataMiningHandler {
    private Classifier classifier;
    private Instances trainData;

    @Override
    public void trainModel(Context context, String tableName, String[] selectedColumns, String targetColumn, int percentage) {
        // Convert data from SQL to Instances using CursorToInstancesConverter
        Instances data = CursorToInstancesConverter.convertCursorToInstances(context, tableName, selectedColumns, targetColumn, percentage);

        // Train a decision tree classifier (J48) as an example
        classifier = new J48();
        try {
            classifier.buildClassifier(data);
            trainData = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public double[] predict(Context context, Instances testInstances) {
        // Predict outcomes using the trained classifier
        double[] predictions = new double[testInstances.numInstances()];
        try {
            for (int i = 0; i < testInstances.numInstances(); i++) {
                predictions[i] = classifier.classifyInstance(testInstances.instance(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictions;
    }
}