package com.RaenarApps.Game15;

import android.app.Activity;
import android.content.Intent;
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
        File dataFile = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName() + File.separator
                + DATA_FILE);
        if (dataFile.exists()) {
            loadData(dataFile);
            fillImageList();
        } else {
            createImageList();
            fillImageList();
        }
    }

    private void loadData(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(fis);
            imageList = (ArrayList<Image>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        File dataDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName());
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File dataFile = new File(dataDir.getPath() + File.separator
                + DATA_FILE);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dataFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream oos;
        if (fos == null) {
            Toast.makeText(this, "FOS = NULL", Toast.LENGTH_LONG).show();
        } else {
            try {
                oos = new ObjectOutputStream(fos);
                oos.writeObject(imageList);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createImageList() {
        imageList = new ArrayList<Image>();

        imageList.add(new Image("Mountains", "backgrounds/mountains.jpg", "thumbnails/thumbnail_Mountains.jpg", true));
        imageList.add(new Image("Funny Cat", "backgrounds/funny_cat.jpg", "thumbnails/thumbnail_Funny_Cat.jpg", true));
        imageList.add(new Image("Pollen", "backgrounds/pollen.jpg", "thumbnails/thumbnail_Pollen.jpg", true));
        imageList.add(new Image("Colors", "backgrounds/colors.jpg", "thumbnails/thumbnail_Colors.jpg", true));

        imageList.add(new Image("Blueberries", "backgrounds/blueberries.jpeg", "thumbnails/thumbnail_Blueberries.jpg", true));
        imageList.add(new Image("Castle", "backgrounds/castle.jpg", "thumbnails/thumbnail_Castle.jpg", true));
        imageList.add(new Image("Cherries", "backgrounds/cherries.jpeg", "thumbnails/thumbnail_Cherries.jpg", true));
        imageList.add(new Image("Fruit", "backgrounds/fruit.jpeg", "thumbnails/thumbnail_Fruit.jpg", true));
        imageList.add(new Image("Islands", "backgrounds/islands.jpeg", "thumbnails/thumbnail_Islands.jpg", true));
        imageList.add(new Image("Maldives", "backgrounds/maldives.jpeg", "thumbnails/thumbnail_Maldives.jpg", true));
        imageList.add(new Image("Milky Way", "backgrounds/milkyway.jpeg", "thumbnails/thumbnail_Milky_Way.jpg", true));
        imageList.add(new Image("Mountain Ridge", "backgrounds/mountains2.jpg", "thumbnails/thumbnail_Mountain_Ridge.jpg", true));
        imageList.add(new Image("Raspberry", "backgrounds/raspberry.jpeg", "thumbnails/thumbnail_Raspberry.jpg", true));
        imageList.add(new Image("Space", "backgrounds/space.jpg", "thumbnails/thumbnail_Space.jpg", true));
        imageList.add(new Image("Zen", "backgrounds/zen.jpg", "thumbnails/thumbnail_Zen.jpg", true));

        saveData();
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
                        //todo: learn how AsyncTask synchronizes imageList
//                        ArrayList<Image> imageList2 = new ArrayList<>();
//                        imageList2.add(new Image("Mountains", "backgrounds/mountains.jpg", "thumbnails/thumbnail_Mountains.jpg", true));
                        // If we pass imageList2 as a param, adapter wont be synchronized
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
