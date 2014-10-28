package com.versapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.versapp.confessions.ConfessionImageCache;

/**
 * Created by william on 22/09/14.
 */
public class DownloadImageAT extends AsyncTask<Void, Void, Bitmap> {

    String key;
    ImageView imageView;
    Context context;
    ConfessionImageCache cache;
    ProgressBar progressBar;

    int width;
    int height;

    public DownloadImageAT(Context context, String key, ImageView imageView, ConfessionImageCache cache, ProgressBar progressBar, int width, int height) {
        this.context = context;
        this.key = key;
        this.imageView = imageView;
        this.cache = cache;
        this.progressBar = progressBar;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap image = GCSManager.getInstance(context).downloadImage(key, width, height);

        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        progressBar.setVisibility(View.GONE);

        if (!isCancelled()) {

            if (imageView != null) {

                if (bitmap != null) {

                    ConfessionImageCache.setBitmapOnView(context, imageView, bitmap);
                    cache.addToCache(key, bitmap);
                } else {
                    imageView.setImageBitmap(null);
                    // set normal image.
                }
            }

        }

    }




}
