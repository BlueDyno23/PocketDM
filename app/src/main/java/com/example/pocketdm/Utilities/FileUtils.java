package com.example.pocketdm.Utilities;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> getAllFilesInFolder(Context context, String folderName) {
        List<String> filePaths = new ArrayList<>();

        // Specify the path of the folder
        String folderPath = context.getFilesDir() + File.separator + folderName;

        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Add the absolute path of each file to the list
                    filePaths.add(file.getAbsolutePath());
                }
            }
        }

        return filePaths;
    }

    public static String saveFile(Context context, Uri uri, String folderName) {
        String destinationPath = context.getFilesDir() + File.separator + folderName;

        // Create the app folder if it doesn't exist
        File folder = new File(destinationPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.e("FileUtil", "Failed to create directory: " + destinationPath);
                return null;
            }
        }

        // Get the file name from the URI
        String fileName = getFileName(context, uri);
        if (fileName == null) {
            Log.e("FileUtil", "Failed to retrieve file name from URI");
            return null;
        }

        // Generate a unique destination file path
        String destinationFilePath = destinationPath + File.separator + fileName;

        // Check if the file already exists
        for (String filePath : getAllFilesInFolder(context, folderName)) {
            if (filePath.equals(destinationFilePath)) {
                Log.e("FileUtil", "File already exists");
                return null;
            }
        }

        // Copy the file to the app folder
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(new File(destinationFilePath));

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return destinationFilePath;
        } catch (IOException e) {
            Log.e("FileUtil", "Error copying file: " + e.getMessage());
            return null;
        }
    }
    public static String saveString(Context context, String content, String folderName, String fileName) {
        String destinationPath = context.getFilesDir() + File.separator + folderName;

        // Create the app folder if it doesn't exist
        File folder = new File(destinationPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.e("FileUtil", "Failed to create directory: " + destinationPath);
                return null;
            }
        }

        String destinationFilePath = destinationPath + File.separator + fileName;

        // Check if the file already exists
        File existingFile = new File(destinationFilePath);
        if (existingFile.exists()) {
            // Update the existing file with new content
            try (FileOutputStream outputStream = new FileOutputStream(existingFile)) {
                outputStream.write(content.getBytes());
                return destinationFilePath;
            } catch (IOException e) {
                Log.e("FileUtil", "Error updating file: " + e.getMessage());
                return null;
            }
        } else {
            // Create a new file and write the content
            try (FileOutputStream outputStream = new FileOutputStream(new File(destinationFilePath))) {
                outputStream.write(content.getBytes());
                return destinationFilePath;
            } catch (IOException e) {
                Log.e("FileUtil", "Error writing string to file: " + e.getMessage());
                return null;
            }
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme != null && scheme.equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
                if (index != -1) {
                    fileName = cursor.getString(index);
                }
                cursor.close();
            }
        } else if (scheme != null && scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        }

        return fileName;
    }
    public static String getFileName(String filePath){
        return filePath.substring(filePath.lastIndexOf("/")+1);
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) file.delete();
    }
}
