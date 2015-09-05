package com.RaenarApps.Game15;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.ListView;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Raenar on 02.08.2015.
 */
public class TaskAddImage extends AsyncTask<Uri, Void, Void> {

    private Activity activity;
    private AlertDialog dialog;
    private ArrayList<Image> imageList_TASK;
    private static final String TAG = "SAVING NEW FILE:";


    public TaskAddImage(ArrayList<Image> imageList, Activity activity) {
        this.imageList_TASK = imageList;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        dialog = builder.create();
        dialog.show();

    }

    @Override
    protected Void doInBackground(Uri... uris) {

        Uri uri = uris[0]; // For now, only 1 image is processed in this Task

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int pathIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgPath = cursor.getString(pathIndex);

        cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        String imgTitle = cursor.getString(nameIndex);

        Bitmap thumbnail = ImageHelper.getScaledBitmap(activity, imgPath, false, 200, 200);
        String thumbnailPath = saveBitmap(thumbnail, imgTitle, true);
        Bitmap background = getScaledBackground(imgPath, false);
        String backgroundPath = saveBitmap(background, imgTitle, false);
        imageList_TASK.add(new Image(imgTitle, backgroundPath, thumbnailPath, false));

        File dataFile = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + activity.getPackageName() + File.separator
                + ListActivity.DATA_FILE);
        saveData(dataFile);
        cursor.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //        ((ListActivity) activity).fillImageList();
        ListView lvImages = (ListView) activity.findViewById(R.id.lvImages);
        lvImages.setSelection(lvImages.getCount() - 1);
        dialog.dismiss();
        super.onPostExecute(aVoid);
    }

    private void saveData(File dataFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dataFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream oos;
        if (fos != null) {
            try {
                oos = new ObjectOutputStream(fos);
                oos.writeObject(imageList_TASK);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private Bitmap getScaledBackground(String imagePath, boolean isDefault) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        android.graphics.Point point = new android.graphics.Point();
        display.getSize(point);
        int displayHeight = point.x;
        int displayWidth = point.y;
        int reqHeight;
        int reqWidth;
        if (displayHeight > displayWidth) {
            reqHeight = reqWidth = displayWidth;
        } else {
            reqHeight = reqWidth = displayHeight;
        }
        return ImageHelper.getScaledBitmap(activity, imagePath, isDefault, reqWidth, reqHeight);
    }

    private String saveBitmap(Bitmap scaledImage, String name, boolean isThumbnail) {
        File pictureFile = createFile(name, isThumbnail);
        if (pictureFile == null) {
            Log.d(TAG, "Creating file : ERROR");
            return null;
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                scaledImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            return pictureFile.getAbsolutePath();
        }
    }


    private File createFile(String name, boolean isThumbnail) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String subDir;
        String fileType;
        if (isThumbnail) {
            subDir = "/thumbnails";
            fileType = "thumbnail_";
        } else {
            subDir = "/backgrounds";
            fileType = "background_";
        }
        File storageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + subDir);
        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }
        File thumbnailFile;
        String thumbnailName = fileType + name;
        thumbnailFile = new File(storageDir.getPath() + File.separator + thumbnailName);
        return thumbnailFile;
    }
}