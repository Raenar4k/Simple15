package com.RaenarApps.Game15.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.RaenarApps.Game15.R;
import com.RaenarApps.Game15.activity.ListActivity;
import com.RaenarApps.Game15.helper.BitmapHelper;
import com.RaenarApps.Game15.model.Image;
import com.squareup.picasso.Picasso;

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

    public class ListRow extends RelativeLayout {
        ImageView thumbnail;
        TextView title;
        ImageButton editButton;
        ImageButton deleteButton;
        Dialog editDialog;
        Button okButton;
        Button clearButton;
        EditText editText;

        public ListRow(Context context,int i) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.listitem_image, this);
            thumbnail = (ImageView) findViewById(R.id.imageThumbnail);
            title = (TextView) findViewById(R.id.imageTitle);
            editButton = (ImageButton) findViewById(R.id.editButton);
            deleteButton = (ImageButton) findViewById(R.id.deleteButton);

            ContextThemeWrapper wrapper = new ContextThemeWrapper(context, R.style.MyAppCompatDialog);
            editDialog = new Dialog(wrapper);
            editDialog.setContentView(R.layout.edit_dialog);
            editDialog.setTitle(wrapper.getString(R.string.item_edit_Title));
            editText = (EditText) editDialog.findViewById(R.id.editTitle);
            okButton = (Button) editDialog.findViewById(R.id.okButton);
            clearButton = (Button) editDialog.findViewById(R.id.clearButton);
        }
    }

    @Override
    public View getView(int itemIndex, View listItem, ViewGroup viewGroup) {
        final ListRow row;
        if (listItem == null) {
            row = new ListRow(context, itemIndex);
            listItem = row;
        } else {
            row = ((ListRow) listItem);
        }


        final int i = itemIndex;
        Image image = imageArrayList.get(i);

        if (image.isDefault()) {
            Picasso.with(context)
                    .load("file:///android_asset/" + image.getThumbnailPath())
                    .into(row.thumbnail);
        } else {
            Picasso.with(context)
                    .load(new File(image.getThumbnailPath()))
                    .into(row.thumbnail);
        }

        String s = image.getTitle();
        row.title.setText(s);

//        final Dialog editDialogF = row.editDialog;
//        final Button okButtonF = row.okButton;
//        final Button clearButtonF = row.clearButton;
//        final EditText editTextF = row.editText;
        row.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row.editText.setText(((ListActivity) context).imageList.get(i).getTitle());

                row.okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String imagePath = ((ListActivity) context).imageList.get(i).getImagePath();
                        ((ListActivity) context).imageList.get(i).setTitle(row.editText.getText().toString());
                        ((ListActivity) context).updateTitle(imagePath, row.editText.getText().toString());
                        notifyDataSetChanged();
                        row.editDialog.dismiss();
                    }
                });
                row.editDialog.show();

                row.clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        row.editText.setText("");
                    }
                });
            }
        });

        row.deleteButton.setOnClickListener(new View.OnClickListener() {
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
}
