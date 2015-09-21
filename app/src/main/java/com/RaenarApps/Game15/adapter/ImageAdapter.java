package com.RaenarApps.Game15.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.RaenarApps.Game15.R;
import com.RaenarApps.Game15.activity.ListActivity;
import com.RaenarApps.Game15.model.Image;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Raenar on 23.07.2015.
 */
public class ImageAdapter extends BaseAdapter {
    private ArrayList<Image> imageArrayList;
    private Context context;
    final String TAG = "THUMBNAIL FILES:";

    public ImageAdapter(ArrayList<Image> imageArrayList, Context context) {
        this.imageArrayList = imageArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return imageArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int itemIndex, View listItem, ViewGroup viewGroup) {
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.listitem_image, null);
        }

        final int i = itemIndex;
        Image image = imageArrayList.get(i);
        ImageView thumbnail = (ImageView) listItem.findViewById(R.id.imageThumbnail);
        TextView title = (TextView) listItem.findViewById(R.id.imageTitle);

        Bitmap scaledImage = loadThumbnail(image.getThumbnailPath(), image.isDefault());
//        Bitmap scaledImage = ImageHelper.getScaledBitmap(context,image.getImagePath(), image.isDefault(), 200,200);
        if (scaledImage != null) {
            thumbnail.setImageBitmap(scaledImage);
//            saveThumbnail(scaledImage, image.getTitle());
        } else {
//            Toast.makeText(context, "NO THUMBNAIL CREATED", Toast.LENGTH_SHORT).show();
        }

        String s = image.getTitle();
        title.setText(s);

        ImageButton editButton = (ImageButton) listItem.findViewById(R.id.editButton);
        ImageButton deleteButton = (ImageButton) listItem.findViewById(R.id.deleteButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper wrapper = new ContextThemeWrapper(context, android.R.style.Theme_Holo_Dialog);

                final Dialog dialog = new Dialog(wrapper);
                dialog.setContentView(R.layout.edit_dialog);
                dialog.setTitle(wrapper.getString(R.string.item_edit_Title));
                final EditText editText = (EditText) dialog.findViewById(R.id.editTitle);
                editText.setText(((ListActivity) context).imageList.get(i).getTitle());

                Button okButton = (Button) dialog.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String imagePath = ((ListActivity) context).imageList.get(i).getImagePath();
                        ((ListActivity) context).imageList.get(i).setTitle(editText.getText().toString());
                        ((ListActivity) context).updateTitle(imagePath, editText.getText().toString());
                        notifyDataSetChanged();
                        dialog.hide();
                    }
                });
                dialog.show();
                Button clearButton = (Button) dialog.findViewById(R.id.clearButton);
                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editText.setText("");
                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.item_delete_delete))
                        .setPositiveButton(context.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int q) {
                                String imagePath = ((ListActivity) context).imageList.get(i).getImagePath();
                                ((ListActivity) context).imageList.remove(i);
                                ((ListActivity) context).deleteImage(imagePath);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });



        return listItem;
    }

    private void saveThumbnail(Bitmap scaledImage, String name) {
        File pictureFile = createFile(name);
        if (pictureFile == null) {
            Log.d(TAG, "Creating file : ERROR");
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
            scaledImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private File createFile(String name) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File storageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Thumbnails");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File thumbnailFile;
        String thumbnailName = "Thumbnail_" + name + ".jpg";
        thumbnailFile = new File(storageDir.getPath() + File.separator + thumbnailName);
        return thumbnailFile;
    }

    private Bitmap loadThumbnail(String imagePath, boolean isDefault) {
        InputStream inputStream = null;
        Bitmap sampledBitmap = null;
        try {
            if (isDefault) {
                inputStream = context.getAssets().open(imagePath);
            } else {
                inputStream = new FileInputStream(imagePath);
            }
            sampledBitmap = BitmapFactory.decodeStream(inputStream);

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

}
