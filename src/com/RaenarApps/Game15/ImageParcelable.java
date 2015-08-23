package com.RaenarApps.Game15;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Raenar on 23.07.2015.
 */
public class ImageParcelable implements Parcelable {
    private String title;
    private String imagePath;
    private String thumbnailPath;
    private boolean isDefault;

    public static final Creator<ImageParcelable> CREATOR = new Creator<ImageParcelable>() {
        @Override
        public ImageParcelable createFromParcel(Parcel parcel) {
            return new ImageParcelable(parcel);
        }

        @Override
        public ImageParcelable[] newArray(int i) {
            return new ImageParcelable[0];
        }
    };

    public ImageParcelable(String title, String imagePath, String thumbnailPath, boolean isDefault) {
        this.title = title;
        this.imagePath = imagePath;
        this.thumbnailPath = thumbnailPath;
        this.isDefault = isDefault;
    }

    public ImageParcelable(Parcel parcel) {
        this.title = parcel.readString();
        this.imagePath = parcel.readString();
        this.thumbnailPath = parcel.readString();
        this.isDefault = (parcel.readInt() != 0);
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(imagePath);
        parcel.writeString(thumbnailPath);
        parcel.writeInt(isDefault ? 1 : 0);
    }
}
