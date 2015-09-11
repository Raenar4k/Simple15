package com.RaenarApps.Game15;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Raenar on 30.07.2015.
 */
public class ImageHelper {


    public static String getDominantColor(Context context, String imagePath, boolean isDefault) {
        Bitmap sampledBitmap = getSampledBitmap(context, imagePath, isDefault, 200, 200);
        try {
            ImageColor imageColour = new ImageColor(sampledBitmap);
            return imageColour.returnColour();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmap(Context context, String imagePath, boolean isDefault) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            if (isDefault) {
//                inputStream = context.getAssets().open(imagePath);
                // STUB ONLY IF WE ARE GOING TO STORE PROCESSED BACKGROUNDS IN ASSESTS
            } else {
                inputStream = new FileInputStream(imagePath);
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    public static Bitmap getScaledBitmap(Context context, String imagePath, boolean isDefault, int reqWidth, int reqHeight) {
        Bitmap scaledBitmap = null;
        Bitmap sampledBitmap = getSampledBitmap(context, imagePath, isDefault, reqWidth, reqHeight);
        Bitmap cutBitmap;
        if (sampledBitmap != null) {
            if (sampledBitmap.getHeight() > sampledBitmap.getWidth()) {
                cutBitmap = Bitmap.createBitmap(sampledBitmap, 0, 0, sampledBitmap.getWidth(), sampledBitmap.getWidth());
            } else {
                cutBitmap = Bitmap.createBitmap(sampledBitmap, 0, 0, sampledBitmap.getHeight(), sampledBitmap.getHeight());
            }
            scaledBitmap = Bitmap.createScaledBitmap(cutBitmap, reqWidth, reqHeight, true);
        }
        return scaledBitmap;
    }

    public static Bitmap getSampledBitmap(Context context, String imagePath, boolean isDefault, int reqWidth, int reqHeight) {
        Bitmap sampledBitmap = null;
        InputStream inputStream = null;
        try {
            if (isDefault) {
                inputStream = context.getAssets().open(imagePath);
            } else {
                inputStream = new FileInputStream(imagePath);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            if (!isDefault) {
                inputStream.close();
                inputStream = new FileInputStream(imagePath);
            }
            sampledBitmap = BitmapFactory.decodeStream(inputStream, null, options);
//            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sampledBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
