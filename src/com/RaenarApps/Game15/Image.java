package com.RaenarApps.Game15;

import java.io.Serializable;

/**
 * Created by Raenar on 23.07.2015.
 */
public class Image implements Serializable{
    private String title;
    private String imagePath;
    private String thumbnailPath;
    private boolean isDefault;

    public static final String TITLE = "title";
    public static final String IMAGE_PATH = "ImagePath";
    public static final String THUMBNAIL_PATH = "ThumbnailPath";
    public static final String IS_DEFAULT = "isDefault";
    public static final String IS_PROCESSED = "isProcessed";


    public Image(String title, String imagePath, String thumbnailPath, boolean isDefault) {
        this.title = title;
        this.imagePath = imagePath;
        this.thumbnailPath = thumbnailPath;
        this.isDefault = isDefault;
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
}
