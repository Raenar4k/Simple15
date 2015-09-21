package com.RaenarApps.Game15.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.RaenarApps.Game15.R;
import com.RaenarApps.Game15.helper.DBHelper;
import com.RaenarApps.Game15.helper.ImageHelper;
import com.RaenarApps.Game15.model.Image;

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
        Bitmap thumbnail = ImageHelper.getScaledBitmap(activity, imgPath, false, 200, 200);
        String thumbnailPath = saveBitmap(thumbnail, fileName, true);
        Bitmap background = getScaledBackground(imgPath, false);
        String backgroundPath = saveBitmap(background, fileName, false);
        String dominantColor = ImageHelper.getDominantColor(activity, imgPath, false);
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
