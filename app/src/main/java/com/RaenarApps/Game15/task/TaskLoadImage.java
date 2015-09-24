package com.RaenarApps.Game15.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.RaenarApps.Game15.R;
import com.RaenarApps.Game15.activity.FifteenActivity;
import com.RaenarApps.Game15.helper.BitmapHelper;
import com.RaenarApps.Game15.helper.DBHelper;
import com.RaenarApps.Game15.helper.ColorHelper;
import com.RaenarApps.Game15.helper.SquareTransform;
import com.RaenarApps.Game15.model.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Raenar on 03.08.2015.
 */
public class TaskLoadImage extends AsyncTask<Void, Void, Void> {
    public static final String TAG = "TASK LOAD IMAGE";
    private Activity activity;
    private String imagePath;
    private boolean isDefault;
    private boolean isProcessed;
    private ArrayList<Bitmap> chunkedImages;
    private String dominantColor;
    Bitmap cheatImage;
    private AlertDialog dialog;
    String newPath;

    int rowCount = 4;
    int columnCount = 4;

    public TaskLoadImage(Activity activity, String imagePath, boolean isDefault, boolean isProcessed,
                         ArrayList<Bitmap> chunkedImages, String dominantColor) {
        this.activity = activity;
        this.imagePath = imagePath;
        this.isDefault = isDefault;
        this.isProcessed = isProcessed;
        this.chunkedImages = chunkedImages;
        this.dominantColor = dominantColor;
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
        Bitmap processedBitmap = getProcessedBitmap(imagePath, isProcessed);
        cheatImage = processedBitmap;
        chunkedImages = getChunksFromImage(processedBitmap);

        if (!isProcessed) {
            DBHelper dbHelper = new DBHelper(activity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            ((FifteenActivity) activity).imagePathGlobal = newPath;
            ((FifteenActivity) activity).isProcessedGlobal = true;
            cv.put(Image.IMAGE_PATH, newPath);
            cv.put(Image.IS_PROCESSED, 1);
            dominantColor = ColorHelper.getDominantColor(activity, imagePath, !isProcessed);
            cv.put(Image.DOMINANT_COLOR, dominantColor);
            String selection = Image.IMAGE_PATH + " LIKE ?";
            String[] selectionArgs = {imagePath};
            db.update(DBHelper.TABLE_NAME, cv, selection, selectionArgs);
            db.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        ((FifteenActivity) activity).chunkedImages = chunkedImages;
        ((FifteenActivity) activity).dominantColorGlobal = dominantColor;
        ((FifteenActivity) activity).updateUI();
        ((ImageView) activity.findViewById(R.id.ivCheatImage)).setImageBitmap(cheatImage);
        dialog.dismiss();
    }

    private Bitmap getProcessedBitmap(String imagePath, boolean isProcessed) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        android.graphics.Point point = new android.graphics.Point();
        display.getSize(point);
        int displayHeight = point.x;
        int displayWidth = point.y;
        int reqSize = Math.min(displayHeight, displayWidth);

        Bitmap scaledBitmap = null;
        Log.d(TAG, "imagePath = " + imagePath);
        Log.d(TAG, "isProcessed  = " + isProcessed);
        Log.d(TAG, "reqSize  = " + reqSize);

        if (!isProcessed) {
            try {
                scaledBitmap = Picasso.with(activity)
                        .load("file:///android_asset/" + imagePath)
                        .transform(new SquareTransform(reqSize))
                        .get();
//                scaledBitmap = Picasso.with(activity)
//                        .load("file:///android_asset/" + imagePath)
//                        .resize(reqSize,reqSize)
//                        .centerCrop()
//                        .get();
                Log.d(TAG, "scaled size  = " + scaledBitmap.getWidth());
                String fileName = new Date().getTime() + ".jpg";
                newPath = new BitmapHelper().saveBitmap(activity, scaledBitmap, fileName, false);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException, loading from assets");
            }
        } else {
            try {
                scaledBitmap = Picasso.with(activity).load(new File(imagePath)).get();
            } catch (IOException e) {
                Log.d(TAG, "IOException, loading from data folder");
                e.printStackTrace();
            }
        }
        if (scaledBitmap == null) {
            Log.d(TAG, "scaledBitmap = null");
        }
        return scaledBitmap;
    }

    private ArrayList<Bitmap> getChunksFromImage(Bitmap processedBitmap) {

        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(rowCount * columnCount);

        int chunkHeight = processedBitmap.getHeight() / rowCount;
        int chunkWidth = processedBitmap.getWidth() / rowCount;
        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for (int x = 0; x < rowCount; x++) {
            int xCoord = 0;
            for (int y = 0; y < rowCount; y++) {
                chunkedImages.add(Bitmap.createBitmap(processedBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        return chunkedImages;
    }
}


