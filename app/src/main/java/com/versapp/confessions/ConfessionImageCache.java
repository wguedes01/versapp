package com.versapp.confessions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.versapp.GCSManager;
import com.versapp.R;

/**
 * Created by william on 22/09/14.
 */
public class ConfessionImageCache {

    private Activity activity;
    private GCSManager gcsManager;

    public ConfessionImageCache(Activity activity) {
        this.activity = activity;
        this.gcsManager = GCSManager.getInstance(activity);
    }

    private LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(3);

    public void addToCache(String key, Bitmap bitmap){
        if (cache.get(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public boolean isCached(String key){
        return cache.get(key) != null;
    }

    public static void setBitmapOnView(Context context, ImageView view, Bitmap image) {

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.image_fade_in);
        view.startAnimation(animation);
        animation.start();

        if (isAboveWhiteThreshold(image)) {
            ColorFilter filter = new ColorFilter();
            view.setColorFilter(Color.rgb(130, 130, 130), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            view.setColorFilter(null);
        }

        view.setImageBitmap(image);
    }

    private static boolean isAboveWhiteThreshold(Bitmap image) {

        int border = (int) (image.getWidth() * 0.2);

        int width = (int) (image.getWidth());
        int height = (int) (image.getHeight());
        int numPixels = (width - (2 * border)) * (height - (2 * border));

        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;

        for (int r = border; r < width - border; r++) {
            for (int c = border; c < height - border; c++) {
                int pixel = image.getPixel(r, c);
                totalRed += Color.red(pixel);
                totalGreen += Color.green(pixel);
                totalBlue += Color.blue(pixel);
            }
        }

        System.out.println(String.format("%s,%s,%s", totalRed / numPixels, totalGreen / numPixels, totalBlue / numPixels));

        return ((totalBlue / numPixels) > 115 && (totalRed / numPixels) > 115 && (totalGreen / numPixels) > 115) || (((totalRed / numPixels) + (totalGreen / numPixels) + (totalBlue / numPixels)) > 230);
    }

    public Bitmap getCachedImage(String imageUrl) {
        return cache.get(imageUrl);
    }

    public void cacheImage(final String imageUrl, final int width, final int height) {

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return GCSManager.getInstance(activity).downloadImage(imageUrl, width, height);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                addToCache(imageUrl, bitmap);
                super.onPostExecute(bitmap);
            }
        }.execute();

    }
}
