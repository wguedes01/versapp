package com.versapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by william on 23/09/14.
 */
public class ImageManager {

    private final static int DEFAULT_HEIGHT = 120;
    private final static int DEFAULT_WIDTH = 120;
    private final static int DEFAULT_IMAGE_QUALITY = 1;
    private int targetH;
    private int targetW;
    private ImageView imageView;

    public ImageManager(ImageView i, int h, int w) {
        this.setImageView(i);
        this.setTargetHeight(h);
        this.setTargetWidth(w);
    }

    public ImageManager(ImageView i) {
        this.setImageView(i);
        this.setTargetHeight(i.getHeight());
        this.setTargetWidth(i.getWidth());
    }

    public ImageManager() {
        this.setTargetHeight(DEFAULT_HEIGHT);
        this.setTargetWidth(DEFAULT_WIDTH);
    }

    // Checks if the device can handle a picture intent
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public float getRotationDegrees(String imgPath) {
        float degreeRotation = 0;
        try {
            ExifInterface exif = new ExifInterface(imgPath);
            degreeRotation = this.exifOrientationToDegrees(Integer.valueOf(exif.getAttribute(ExifInterface.TAG_ORIENTATION)));
            Log.d("MESSAGE_IMAGE", "" + degreeRotation);
        } catch (IOException e) {
            Log.d("MESSAGE_IMAGE", "Failed to get ExifInterface");
            e.printStackTrace();
        }
        return degreeRotation;
    }

    public Bitmap getScaledBitmapImage(String path) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        float degreeRotation = getRotationDegrees(path);

        BitmapFactory.decodeFile(path, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bMap = BitmapFactory.decodeFile(path, bmOptions);
        if (degreeRotation != 0) {
            bMap = rotateImage(bMap, degreeRotation);
        }
        return bMap;
    }

    public Bitmap getUnscaledBitmapImage(String path) {
        float degreeRotation = getRotationDegrees(path);
        Bitmap bMap = BitmapFactory.decodeFile(path);
        if (degreeRotation != 0) {
            bMap = rotateImage(bMap, degreeRotation);
        }
        return bMap;
    }

    public void setScaledImageBitmap(String path) {
        if (this.imageView != null) {
            this.imageView.setImageBitmap(getScaledBitmapImage(path));
        }
    }

    public String getSelectedImagePath(Intent data, Context c) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = c.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    public Bitmap getCapturedImage(Intent data) {
        Bundle extras = data.getExtras();
        return (Bitmap) extras.get("data");
    }

    public void setCapturedImageBitmap(Intent data) {
        this.imageView.setImageBitmap(getCapturedImage(data));
    }

    public String compressBitmapToString(Bitmap bMap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bMap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("PROFILE_PIC", "" + quality);
        Log.d("PROFILE_PIC", "" + encoded.length());
        return (encoded.length() > 1000 && quality > 1) ? compressBitmapToString(bMap, (quality > 10) ? quality - 10 : quality - 1) : encoded;
    }

    public String convertBitmapToString(Bitmap bMap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap convertStringToBitmap(String encodedBitmap) {
        try {
            byte[] encodeByte = Base64.decode(encodedBitmap, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Bitmap autoCropBitmap(Bitmap bMap, int x, int y, int width, int height) {
        return Bitmap.createBitmap(bMap, x, y, width, height);
    }

    public Bitmap rotateImage(Bitmap bMap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
    }

    public Bitmap rotateImage(Bitmap bMap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
    }

    /** Get rotation in degrees */
    private float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    // -----------------------
    // Getters/Setters
    // -----------------------
    public int getTargetHeight() {
        return targetH;
    }

    public void setTargetHeight(int targetH) {
        this.targetH = (targetH > 0) ? targetH : DEFAULT_HEIGHT;
    }

    public int getTargetWidth() {
        return targetW;
    }

    public void setTargetWidth(int targetW) {
        this.targetW = (targetW > 0) ? targetW : DEFAULT_WIDTH;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

}
