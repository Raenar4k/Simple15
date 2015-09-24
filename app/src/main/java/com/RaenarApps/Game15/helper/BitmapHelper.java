package com.RaenarApps.Game15.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.Display;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Raenar on 23.09.2015.
 */
public class BitmapHelper {
    private static final String TAG = "Simple 15: BitmapHelper";

    public static Bitmap getScaledBackground(Activity activity, String imagePath) throws IOException {
        Display display = activity.getWindowManager().getDefaultDisplay();
        android.graphics.Point point = new android.graphics.Point();
        display.getSize(point);
        int displayHeight = point.x;
        int displayWidth = point.y;
        int reqSize = Math.min(displayHeight, displayWidth);
        return Picasso.with(activity)
                .load(new File(imagePath))
                .resize(reqSize,reqSize)
                .centerCrop()
                .get();
    }

    public static String getDominantColor(Context context, String imagePath) {
        try {
            Bitmap smallBitmap = Picasso.with(context)
                    .load(new File(imagePath))
                    .resize(200,200)
                    .get();
            ImageColor imageColour = new ImageColor(smallBitmap);
            return imageColour.returnColor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String saveBitmap(Activity activity, Bitmap scaledImage, String name, boolean isThumbnail) {
        File pictureFile = createFile(activity, name, isThumbnail);
        if (pictureFile == null) {
            Log.d(TAG, "Creating file : File was not created");
            return null;
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                if (scaledImage == null) {
                    Log.d(TAG, "Scaled image = null");
                } else {
                    scaledImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                }
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            return pictureFile.getAbsolutePath();
        }
    }


    public static File createFile(Activity activity, String name, boolean isThumbnail) {
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
