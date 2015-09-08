package com.RaenarApps.Game15;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Raenar on 03.08.2015.
 */
public class TaskLoadImage extends AsyncTask<Void, Void, Void> {
    private Activity activity;
    private String imagePath;
    private boolean isDefault;
    private boolean isProcessed;
    private ArrayList<Bitmap> chunkedImages;
    private String backgroundColor;
    Bitmap cheatImage;
    private AlertDialog dialog;
    String newPath;

    int rowCount = 4;
    int columnCount = 4;

    public TaskLoadImage(Activity activity, String imagePath, boolean isDefault, boolean isProcessed,
                         ArrayList<Bitmap> chunkedImages, String backgroundColor) {
        this.activity = activity;
        this.imagePath = imagePath;
        this.isDefault = isDefault;
        this.isProcessed = isProcessed;
        this.chunkedImages = chunkedImages;
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void onPreExecute() {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(activity, android.R.style.Theme_Holo);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        chunkedImages = getChunksFromImage(imagePath, isDefault, isProcessed);
        String dominantColor = ImageHelper.getDominantColor(activity, imagePath, !isProcessed);
        if (dominantColor != null) {
            backgroundColor = dominantColor;
        } else {
            backgroundColor = "#f1122e06";
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((FifteenActivity) activity).chunkedImages = chunkedImages;
        ((FifteenActivity) activity).backgroundColor = backgroundColor;
        ((FifteenActivity) activity).updateUI();
        ((ImageView) activity.findViewById(R.id.ivCheatImage)).setImageBitmap(cheatImage);
        dialog.dismiss();
    }

    private ArrayList<Bitmap> getChunksFromImage(String imagePath, boolean isDefault, boolean isProcessed) {


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
        Bitmap scaledBitmap;
        Log.d("LOAD IMAGE", "imagePath = "+imagePath);

        if (!isProcessed) {
            scaledBitmap = ImageHelper.getScaledBitmap(activity, imagePath, isDefault, reqWidth, reqHeight);
            String fileName = new Date().getTime() + ".jpg";
            newPath = saveBitmap(scaledBitmap,fileName, false);

            DBHelper dbHelper = new DBHelper(activity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(Image.IMAGE_PATH, newPath);
            cv.put(Image.IS_PROCESSED, 1);
            String selection = Image.IMAGE_PATH + " LIKE ?";
            String[] selectionArgs = {imagePath};
            db.update(DBHelper.TABLE_NAME, cv, selection, selectionArgs);
            db.close();
            ((FifteenActivity) activity).isProcessedGlobal = true;
            ((FifteenActivity) activity).imagePathGlobal = newPath;

        } else {
            scaledBitmap = ImageHelper.loadBitmap(activity, imagePath, false);
        }
        if (scaledBitmap == null) {
            Log.d("LOAD IMAGE", "scaledBitmap = null");
        }
        cheatImage = scaledBitmap;

        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(rowCount * columnCount);
        int chunkHeight = scaledBitmap.getHeight() / rowCount;
        int chunkWidth = scaledBitmap.getWidth() / rowCount;

        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for (int x = 0; x < rowCount; x++) {
            int xCoord = 0;
            for (int y = 0; y < rowCount; y++) {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        return chunkedImages;
    }

    private String saveBitmap(Bitmap scaledImage, String name, boolean isThumbnail) {
        File pictureFile = createFile(name, isThumbnail);
        String TAG = "SaveBitmap";
        if (pictureFile == null) {
            Log.d(TAG, "Creating file : ERROR");
            return null;
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                if (scaledImage == null) {
                    Log.d(TAG, "Scaled image = null");
                }
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


