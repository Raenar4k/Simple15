package com.RaenarApps.Game15.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.widget.ListView;
import com.RaenarApps.Game15.R;
import com.RaenarApps.Game15.helper.BitmapHelper;
import com.RaenarApps.Game15.helper.ColorHelper;
import com.RaenarApps.Game15.helper.DBHelper;
import com.RaenarApps.Game15.helper.SquareTransform;
import com.RaenarApps.Game15.model.Image;
import com.squareup.picasso.Picasso;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

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

        String fileName = new Date().getTime() + ".jpg";
        Bitmap thumbnail = null;
        Bitmap background = null;
        BitmapHelper bitmapHelper = new BitmapHelper();
        try {
//            thumbnail = Picasso.with(activity).load(new File(imgPath))
//                    .transform(new SquareTransform(200)).get();
            thumbnail = Picasso.with(activity).load(new File(imgPath))
                    .resize(200,200)
                    .centerInside()
                    .get();
            background = bitmapHelper.getScaledBackground(activity, imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        thumbnail = ColorHelper.getScaledBitmap(activity, imgPath, false, 200, 200);
        String thumbnailPath = bitmapHelper.saveBitmap(activity, thumbnail, fileName, true);
        String backgroundPath = bitmapHelper.saveBitmap(activity, background, fileName, false);
        String dominantColor = ColorHelper.getDominantColor(activity, imgPath, false);
        imageList_TASK.add(new Image(imgTitle, backgroundPath, thumbnailPath, false, true, dominantColor));

        DBHelper dbHelper = new DBHelper(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Image.TITLE, imgTitle);
        cv.put(Image.IMAGE_PATH, backgroundPath);
        cv.put(Image.THUMBNAIL_PATH, thumbnailPath);
        cv.put(Image.IS_DEFAULT, 0);
        cv.put(Image.IS_PROCESSED, 1);
        cv.put(Image.DOMINANT_COLOR, dominantColor);
        db.insert(DBHelper.TABLE_NAME, null, cv);

    cursor.close();
    return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //        ((ListActivity) activity).fillImageList();
        ListView lvImages = (ListView) activity.findViewById(R.id.lvImages);
        lvImages.deferNotifyDataSetChanged();
        lvImages.setSelection(lvImages.getCount() - 1);
        dialog.dismiss();
        super.onPostExecute(aVoid);
    }

}
