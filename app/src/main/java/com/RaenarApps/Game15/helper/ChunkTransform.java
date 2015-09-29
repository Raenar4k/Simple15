package com.RaenarApps.Game15.helper;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by Raenar on 22.09.2015.
 */
public class ChunkTransform implements Transformation{
    int chunkRow;
    int chunkColumn;

    public ChunkTransform() {
    }

    public ChunkTransform(int chunkRow, int chunkColumn) {
        this.chunkRow = chunkRow;
        this.chunkColumn = chunkColumn;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int chunkHeight = source.getHeight() / 4;
        int chunkWidth = source.getWidth() / 4;
        int xCoord = chunkWidth*chunkColumn;
        int yCoord = chunkHeight*chunkRow;
        Bitmap result = Bitmap.createBitmap(source, xCoord, yCoord, chunkWidth, chunkHeight);
        if (result!=source) {
            source.recycle();
        }
        return null;

    }

    @Override
    public String key() {
        return "Chunk()";
    }
}
