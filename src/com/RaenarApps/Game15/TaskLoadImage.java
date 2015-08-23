package com.RaenarApps.Game15;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Raenar on 03.08.2015.
 */
public class TaskLoadImage extends AsyncTask<Void, Void, Void> {
    private Activity activity;
    private String imagePath;
    private boolean isDefault;
    private ArrayList<Bitmap> chunkedImages;
    private String backgroundColor;
    Bitmap cheatImage;

    private AlertDialog dialog;

    int rowCount = 4;
    int columnCount = 4;

    public TaskLoadImage(Activity activity, String imagePath, boolean isDefault, ArrayList<Bitmap> chunkedImages, String backgroundColor) {
        this.activity = activity;
        this.imagePath = imagePath;
        this.isDefault = isDefault;
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
        chunkedImages = getChunksFromImage(imagePath, isDefault);
        String dominantColor = ImageHelper.getDominantColor(activity, imagePath, isDefault);
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

    private ArrayList<Bitmap> getChunksFromImage(String imagePath, boolean isDefault) {


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

        if (isDefault) {
            scaledBitmap = ImageHelper.getScaledBitmap(activity, imagePath, isDefault, reqWidth, reqHeight);
        } else {
            scaledBitmap = ImageHelper.loadBitmap(activity, imagePath, isDefault);
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


}


