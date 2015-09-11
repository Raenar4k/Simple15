package com.RaenarApps.Game15;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.util.*;

/**
 * Created by Raenar on 27.07.2015.
 */
public class ImageColor {
    public static final String TAG = "ImageColor";
    String dominantColor;


    public ImageColor(Bitmap image) throws Exception {

        int height = image.getHeight();
        int width = image.getWidth();

        Map m = new HashMap();

        for (int i = 0; i < width; i++) {

            for (int j = 0; j < height; j++) {

                int rgb = image.getPixel(i, j);
                int[] rgbArr = getRGBArr(rgb);

                if (!isGray(rgbArr)) {

                    Integer counter = (Integer) m.get(rgb);
                    if (counter == null)
                        counter = 0;
                    counter++;
                    m.put(rgb, counter);

                }
            }
        }
        dominantColor = getMostCommonColor(m);

    }


    public static String getMostCommonColor(Map map) {

        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {

                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());

            }

        });

        Map.Entry me = (Map.Entry) list.get(list.size() - 1);
        int[] rgb = getRGBArr((Integer) me.getKey());

        StringBuilder hexRGB = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            String hexColor = Integer.toHexString(rgb[i]);
            if (hexColor.length() < 2) {
                hexColor = "0" + hexColor;
            }
            hexRGB.append(hexColor);
        }
        Log.v(TAG, "red= " + Integer.toHexString(rgb[0]));
        Log.v(TAG, "green= " + Integer.toHexString(rgb[1]));
        Log.v(TAG, "blue= " + Integer.toHexString(rgb[2]));
        Log.v(TAG, "Color str =  "+ hexRGB);

        return hexRGB.toString();

    }


    public static int[] getRGBArr(int pixel) {

        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red, green, blue};

    }

    public static boolean isGray(int[] rgbArr) {

        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];

        int tolerance = 10;

        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance) {

                return false;

            }

        return true;
    }


    public String returnColour() {
        if (dominantColor.length() == 6) {
            return "#"+dominantColor;
        }
        return null;
    }
}

