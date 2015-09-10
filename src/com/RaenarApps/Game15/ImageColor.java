package com.RaenarApps.Game15;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.*;

/**
 * Created by Raenar on 27.07.2015.
 */
public class ImageColor {
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

        return Integer.toHexString(rgb[0]) + " " + Integer.toHexString(rgb[1]) + " " + Integer.toHexString(rgb[2]);

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


    public String returnColour(Context context) {
        String s = dominantColor.replaceAll("\\s", "");
        if (s.length() > 6) {
//            Toast.makeText(context, "Length > 6", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "PRE OP: " + dominantColor, Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "POST OP: " + s, Toast.LENGTH_SHORT).show();
        }
        if (s.length() == 6) {
//            Toast.makeText(context, "Length = 6", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "S: "+s, Toast.LENGTH_SHORT).show();
            return "#"+s;
        }
//        Toast.makeText(context, "Length < 6", Toast.LENGTH_SHORT).show();
        return null;
    }
}

//    public String returnColour(Context context) {
//
//        if (dominantColor.length() > 6) {
////            Toast.makeText(context, "Length > 6", Toast.LENGTH_SHORT).show();
////            Toast.makeText(context, "PRE OP: " + dominantColor, Toast.LENGTH_SHORT).show();
//            String s = dominantColor.replaceAll("\\s", "");
////            Toast.makeText(context, "POST OP: " + s, Toast.LENGTH_SHORT).show();
//            return s;
//        } else {
//            if (dominantColor.length() < 6) {
////                Toast.makeText(context, "Length < 6", Toast.LENGTH_SHORT).show();
//                return null;
//            } else {
////                Toast.makeText(context, "Length = 6", Toast.LENGTH_SHORT).show();
//                return dominantColor;
//            }
////            return "#ffffff0c";
//        }
//    }
//}
