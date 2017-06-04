package com.RaenarApps.Game15.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.RaenarApps.Game15.R;
import com.RaenarApps.Game15.activity.FifteenActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.IOException;

/**
 * Created by Raenar on 30.07.2015.
 */
public class ColorHelper {


    public static String getDominantColor(Context context, String imagePath, boolean isDefault) {
        try {
            RequestCreator requestCreator = isDefault
                    ? Picasso.with(context).load("file:///android_asset/" + imagePath)
                    : Picasso.with(context).load(new File(imagePath));

            Bitmap bitmap = requestCreator
                    .resize(200, 200)
                    .get();

            int dominantColor = Palette.from(bitmap)
                    .generate()
                    .getDominantColor(ContextCompat.getColor(context, R.color.green_main));

            return Integer.toString(dominantColor);

        } catch (IOException e) {
            e.printStackTrace();
            return FifteenActivity.BACKGROUND_COLOR_DEFAULT;
        }
    }
}
