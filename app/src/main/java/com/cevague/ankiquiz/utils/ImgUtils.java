package com.cevague.ankiquiz.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ImgUtils {

    private static ConcurrentHashMap<String, Bitmap> imageDict;
    private static ConcurrentHashMap<String, Bitmap> scaledImageDict;

    public static void init(){
        imageDict = new ConcurrentHashMap<>();
        scaledImageDict = new ConcurrentHashMap<>();
    }

    public static void preloadImageFromPath(String imagePath) {
        imageDict.put(imagePath, BitmapFactory.decodeFile(imagePath));
    }


    public static Bitmap imageFromPath(String imagePath) {
        if(!imageDict.containsKey(imagePath)){
            imageDict.put(imagePath, BitmapFactory.decodeFile(imagePath));
        }
        return imageDict.get(imagePath);
    }

    private static String scaledPath(String imagePath, int maxWidth, int maxHeight){
        return imagePath + "(" + maxWidth + ":" + maxHeight + ")";
    }

    public static void preloadScaledImageFromPath(String imagePath, int maxWidth, int maxHeight) {
        String finalPath = scaledPath(imagePath, maxWidth, maxHeight);
        imageDict.put(finalPath, scalingImageFromPath(imagePath, maxWidth, maxHeight));
    }

    public static Bitmap scaledImageFromPath(String imagePath, int maxWidth, int maxHeight) {
        String finalPath = scaledPath(imagePath, maxWidth, maxHeight);

        if(!scaledImageDict.containsKey(finalPath)) {
            scaledImageDict.put(finalPath, scalingImageFromPath(imagePath, maxWidth, maxHeight));
        }

        return scaledImageDict.get(finalPath);
    }


    private static Bitmap scalingImageFromPath(String imagePath, int maxWidth, int maxHeight) {
        // Charger les dimensions de l'image sans la décoder pour éviter de charger l'image entière en mémoire
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Décoder l'image pour obtenir ses dimensions sans créer de Bitmap
        BitmapFactory.decodeFile(imagePath, options);

        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;

        // Calculer les facteurs de mise à l'échelle tout en respectant les proportions
        int scaleFactor = 1;
        if (imgWidth > maxWidth || imgHeight > maxHeight) {
            // Calculer le facteur de mise à l'échelle pour réduire l'image
            int widthRatio = Math.round((float) imgWidth / (float) maxWidth);
            int heightRatio = Math.round((float) imgHeight / (float) maxHeight);
            scaleFactor = Math.max(widthRatio, heightRatio);
        }

        // Redimensionner l'image avec le facteur de mise à l'échelle
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;

        // Décoder à nouveau l'image avec les dimensions réduites
        Bitmap scaledBitmap = BitmapFactory.decodeFile(imagePath, options);

        // Retourner l'image redimensionnée
        return scaledBitmap;
    }
}
