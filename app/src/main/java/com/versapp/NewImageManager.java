package com.versapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by william on 17/10/14.
 */
public class NewImageManager {



    public static Bitmap correctImageRotation(Context context, Uri uri) throws IOException {

        ExifInterface exif = new ExifInterface(uri.getPath());
        float degreeRotation = exifOrientationToDegrees(Integer.valueOf(exif.getAttribute(ExifInterface.TAG_ORIENTATION)));

        Matrix matrix = new Matrix();
        matrix.postRotate(degreeRotation);

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /** Get rotation in degrees */
    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

}
