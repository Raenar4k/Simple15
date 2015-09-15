package com.RaenarApps.Game15;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.util.ArrayList;

public class ListActivity extends Activity {

    public static final String DATA_FILE = "imageList.dat";
    private static final String TAG = "SAVING NEW FILE:";
    final int RESULT_GALLERY = 0; //stub for now


    public ArrayList<Image> imageList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_gallery);

        File database = getApplicationContext().getDatabasePath(DBHelper.DB_NAME);
        if (database.exists()) {
            loadList();
            fillImageList();
        } else {
            createImageList();
            fillImageList();
        }
    }

    public void saveList() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < imageList.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(Image.TITLE, imageList.get(i).getTitle());
            cv.put(Image.IMAGE_PATH, imageList.get(i).getImagePath());
            cv.put(Image.THUMBNAIL_PATH, imageList.get(i).getThumbnailPath());
            cv.put(Image.IS_DEFAULT, (imageList.get(i).isDefault() ? 1 : 0));
            cv.put(Image.IS_PROCESSED, (imageList.get(i).isProcessed() ? 1 : 0));
            cv.put(Image.DOMINANT_COLOR, imageList.get(i).getDominantColor());
            db.insert(DBHelper.TABLE_NAME, null, cv);
        }
        db.close();
    }

    public void loadList() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            int titleIndex = cursor.getColumnIndex(Image.TITLE);
            int imageIndex = cursor.getColumnIndex(Image.IMAGE_PATH);
            int thumbnailIndex = cursor.getColumnIndex(Image.THUMBNAIL_PATH);
            int isDefaultIndex = cursor.getColumnIndex(Image.IS_DEFAULT);
            int isProcessedIndex = cursor.getColumnIndex(Image.IS_PROCESSED);
            int colorIndex = cursor.getColumnIndex(Image.DOMINANT_COLOR);
            imageList = new ArrayList<Image>();
            do {
                String title = cursor.getString(titleIndex);
                String image = cursor.getString(imageIndex);
                String thumbnail = cursor.getString(thumbnailIndex);
                boolean isDefault = cursor.getInt(isDefaultIndex) != 0;
                boolean isProcessed = cursor.getInt(isProcessedIndex) != 0;
                String dominantColor = cursor.getString(colorIndex);
                imageList.add(new Image(title, image, thumbnail, isDefault, isProcessed, dominantColor));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "empty table", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }

    public void updateTitle(String imagePath, String newTitle) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Image.TITLE, newTitle);
        String selection = Image.IMAGE_PATH + " LIKE ?";
        String[] selectionArgs = {imagePath};
        db.update(DBHelper.TABLE_NAME, cv, selection, selectionArgs);
        db.close();
    }

    public void deleteImage(String imagePath) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = Image.IMAGE_PATH + " LIKE ?";
        String[] selectionArgs = {imagePath};
        db.delete(DBHelper.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    private void createImageList() {
        imageList = new ArrayList<Image>();

        imageList.add(new Image("Mountains", "backgrounds/mountains.jpg", "thumbnails/thumbnail_mountains.jpg"));
        imageList.add(new Image("Funny Cat", "backgrounds/funny_cat.jpg", "thumbnails/thumbnail_funny_cat.jpg"));
        imageList.add(new Image("Pollen", "backgrounds/pollen.jpg", "thumbnails/thumbnail_pollen.jpg"));
        imageList.add(new Image("Colors", "backgrounds/colors.jpg", "thumbnails/thumbnail_colors.jpg"));

        imageList.add(new Image("Blueberries", "backgrounds/blueberries.jpeg", "thumbnails/thumbnail_blueberries.jpg"));
        imageList.add(new Image("Castle", "backgrounds/castle.jpg", "thumbnails/thumbnail_castle.jpg"));
        imageList.add(new Image("Cherries", "backgrounds/cherries.jpeg", "thumbnails/thumbnail_cherries.jpg"));
        imageList.add(new Image("Fruit", "backgrounds/fruit.jpeg", "thumbnails/thumbnail_fruit.jpg"));
        imageList.add(new Image("Islands", "backgrounds/islands.jpeg", "thumbnails/thumbnail_islands.jpg"));
        imageList.add(new Image("Apricots", "backgrounds/apricots.jpeg", "thumbnails/thumbnail_apricots.jpg"));
        imageList.add(new Image("Milky Way", "backgrounds/milkyway.jpeg", "thumbnails/thumbnail_milky_way.jpg"));
        imageList.add(new Image("Mountain Ridge", "backgrounds/mountains2.jpg", "thumbnails/thumbnail_mountain_ridge.jpg"));
        imageList.add(new Image("Raspberry", "backgrounds/raspberry.jpeg", "thumbnails/thumbnail_raspberry.jpg"));
        imageList.add(new Image("Space", "backgrounds/space.jpg", "thumbnails/thumbnail_space.jpg"));
        imageList.add(new Image("Zen", "backgrounds/zen.jpg", "thumbnails/thumbnail_zen.jpg"));

        saveList();
    }

    public void fillImageList() {
        ListView lvImages = (ListView) findViewById(R.id.lvImages);
        ImageAdapter imageAdapter = new ImageAdapter(imageList, this);
        lvImages.setAdapter(imageAdapter);
        lvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.putExtra(Image.IMAGE_PATH, imageList.get(i).getImagePath());
                intent.putExtra(Image.IS_DEFAULT, imageList.get(i).isDefault());
                intent.putExtra(Image.IS_PROCESSED, imageList.get(i).isProcessed());
                intent.putExtra(Image.DOMINANT_COLOR, imageList.get(i).getDominantColor());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void openGallery(View view) {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, RESULT_GALLERY);
//// Always show the chooser (if there are multiple options available)
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_GALLERY:
                try {
                    if (resultCode == RESULT_OK && null != data) {
                        Uri selectedImageUri = data.getData();

                        TaskAddImage taskAddImage = new TaskAddImage(imageList, ListActivity.this);
                        taskAddImage.execute(selectedImageUri);

                    } else {
                        Toast.makeText(this, getString(R.string.toast_No_Image_Picked),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.toast_Something_Went_Wrong), Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

}
