package com.RaenarApps.Game15.helper;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by Raenar on 23.09.2015.
 */
public class SquareTransform implements Transformation{
    int reqSize;

    public SquareTransform(int reqSize) {
        this.reqSize = reqSize;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap cutBitmap;
        Bitmap scaledBitmap;
        int size = Math.min(source.getWidth(), source.getHeight());
        cutBitmap = Bitmap.createBitmap(source, 0, 0, size, size);
        scaledBitmap = Bitmap.createScaledBitmap(cutBitmap, reqSize, reqSize, true);
        cutBitmap.recycle();
        source.recycle();
        return scaledBitmap;

        //Cant use in TaskAddImage
        //Source can be big enough to run out of memory while resizing and cutting
    }

    @Override
    public String key() {
        return "square()";
    }
}
