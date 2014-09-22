package com.versapp.confessions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.versapp.GCSManager;

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

    public void loadBitmap(final ImageView imageView, final String key) {

            if (cache.get(key) != null) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setBitmapOnView(imageView, cache.get(key));
                    }
                });

            } else {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                        if (gcsManager == null) {
                            gcsManager = GCSManager.getInstance(activity);
                        }

                        if (gcsManager != null) {

                            final Bitmap image = gcsManager.downloadImage(key, imageView.getWidth(), imageView.getHeight());

                            addToCache(key, image);

                            imageView.setTag(key);

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setBitmapOnView(imageView, image);
                                    // imageView.setImageBitmap(image);
                                }
                            });

                        }

                    }
                }).start();

        }

    }

    private void setBitmapOnView(ImageView view, Bitmap image) {

        if (isAboveWhiteThreshold(image)) {
            ColorFilter filter = new ColorFilter();
            view.setColorFilter(Color.rgb(130, 130, 130), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            view.setColorFilter(null);
        }

        view.setImageBitmap(image);
    }

    private boolean isAboveWhiteThreshold(Bitmap image) {

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

        return ((totalBlue / numPixels) > 115 && (totalRed / numPixels) > 115 && (totalGreen / numPixels) > 115);
    }

}
