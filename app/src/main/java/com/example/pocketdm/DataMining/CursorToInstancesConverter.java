package com.example.pocketdm.DataMining;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pocketdm.Utilities.HelperDb;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.List;

public class CursorToInstancesConverter {

    /**
     * Converts data from a Cursor object to a Weka Instances object.
     * @param context The context for accessing the database.
     * @param tableName The name of the table to query.
     * @param selectedColumns The array of column names to include in the Instances object.
     * @param targetColumn The name of the target column (class attribute).
     * @param percentage The percentage of rows to include (1-100).
     * @return The converted Instances object.
     */
    public static Instances convertCursorToInstances(Context context, String tableName, String[] selectedColumns, String targetColumn, int percentage) {
        // Retrieve data from SQLite database using the existing helper class
        HelperDb helperDb = new HelperDb(context);
        SQLiteDatabase db = helperDb.getReadableDatabase();

        // Construct the query using the selected columns
        Cursor cursor = db.query(tableName, selectedColumns, null, null, null, null, null);

        // Define a list of Weka attributes based on the selected columns
        List<Attribute> attributes = new ArrayList<>();
        for (String columnName : selectedColumns) {
            Attribute attribute = new Attribute(columnName);
            attributes.add(attribute);
        }

        // Create a Weka Instances object with the attribute list
        Instances data = new Instances("Data", (ArrayList<Attribute>) attributes, cursor.getCount());

        // Set the class index (target column)
        int targetIndex = findTargetIndex(selectedColumns, targetColumn);
        data.setClassIndex(targetIndex);

        // Calculate the number of rows to use based on the percentage
        int numInstances = (int) (cursor.getCount() * (percentage / 100.0));
        numInstances = Math.min(numInstances, cursor.getCount()); // Ensure it doesn't exceed total rows

        // Convert the data from the Cursor to Instances
        if (cursor.moveToFirst()) {
            int instanceCount = 0;
            do {
                // Create an instance array for each row
                double[] instanceValues = new double[selectedColumns.length];

                for (int i = 0; i < selectedColumns.length; i++) {
                    // Retrieve the column value from the Cursor and convert to double
                    String value = cursor.getString(Math.abs(cursor.getColumnIndex(selectedColumns[i])));

                    if (value == null || value.isEmpty()) {
                        instanceValues[i] = 0;
                    } else {
                        instanceValues[i] = Double.parseDouble(value);
                    }
                }

                // Create and add the instance to the data
                DenseInstance instance = new DenseInstance(1.0, instanceValues);
                data.add(instance);

                // Increment instance count and stop if we've added enough instances
                instanceCount++;
                if (instanceCount >= numInstances) {
                    break;
                }
            } while (cursor.moveToNext());
        }

        // Close the cursor and database connection
        cursor.close();
        db.close();

        // Return the Instances object
        return data;
    }

    /**
     * Finds the index of the target column in the list of selected columns.
     * @param selectedColumns The array of selected columns.
     * @param targetColumn The target column name.
     * @return The index of the target column, or -1 if not found.
     */
    private static int findTargetIndex(String[] selectedColumns, String targetColumn) {
        for (int i = 0; i < selectedColumns.length; i++) {
            if (selectedColumns[i].equals(targetColumn)) {
                return i;
            }
        }
        return -1;
    }
}