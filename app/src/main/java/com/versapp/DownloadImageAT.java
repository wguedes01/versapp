package com.versapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by william on 22/09/14.
 */
public class DownloadImageAT extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;
    Context context;

    public DownloadImageAT(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap image = GCSManager.getInstance(context).downloadImage(params[0], imageView.getWidth(), imageView.getHeight());

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (!isCancelled()) {

            if (imageView != null) {
                if (bitmap != null) {
                   // Animation animation = AnimationUtils.loadAnimation(context, R.anim.image_fade_in);
                  //  imageView.startAnimation(animation);
                    Bitmap newBitmap = getDegreeFontColor(bitmap, imageView);
                    imageView.setImageBitmap(newBitmap);
                    // imageView.setBackgroundColor((getDegreeFontColor(bitmap,
                    // imageView)));
                    // imageView.setBackgroundColor(getDegreeFontColor(bitmap,
                    // imageView));
                } else {
                    imageView.setImageBitmap(null);
                    // set normal image.
                }
            }

        }

    }

    private Bitmap getDegreeFontColor(Bitmap image, ImageView imageView) {

        int width = (int) (image.getWidth() * 0.3);
        int height = (int) (image.getHeight() * 0.3);
        int numPixels = width * height;

        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;

        for (int r = 0; r < width; r++) {
            for (int c = image.getHeight() - height; c < image.getHeight(); c++) {
                int pixel = image.getPixel(r, c);
                totalRed += Color.red(pixel);
                totalGreen += Color.green(pixel);
                totalBlue += Color.blue(pixel);
            }
        }

        Bitmap mutable = image.copy(image.getConfig(), true);
		/*
		 * int color = Color.rgb(totalRed / numPixels, totalGreen / numPixels,
		 * totalBlue / numPixels); for (int i = 0; i < width; i++) { for (int a
		 * = image.getHeight() - height; a < image.getHeight(); a++) {
		 * mutable.setPixel(i, a, color);
		 *
		 * }
		 *
		 * }
		 */

        // Log.d(Logger.CONFESSIONS_DEBUG, "COLOR: " + color);

        return mutable;

    }


}
