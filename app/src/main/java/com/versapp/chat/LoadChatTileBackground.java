package com.versapp.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.versapp.GCSManager;
import com.versapp.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by william on 27/09/14.
 */
public class LoadChatTileBackground extends AsyncTask<Void, Void, Bitmap> {

    private Context context;
    private LruCache<String, Bitmap> cache;
    private ImageView backgroundView;
    private String imageUrl;
    private ProgressBar progressBar;

    public LoadChatTileBackground(Context context, LruCache<String, Bitmap> cache, String imageUrl, ImageView backgroundView, ProgressBar progressBar) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.backgroundView = backgroundView;
        this.progressBar = progressBar;
        this.cache = cache;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);

        super.onPreExecute();
    }



    @Override
    protected Bitmap doInBackground(Void... params) {

        if (cache.get(imageUrl) != null) {
            Log.d(Logger.CHAT_DEBUG, "Got image from cache. Url: " + imageUrl);
            return cache.get(imageUrl);
        } else {
            Log.d(Logger.CHAT_DEBUG, "Downloaded image for tile: " + imageUrl);
            Bitmap image = GCSManager.getInstance(context).downloadImage(imageUrl, backgroundView.getWidth(), backgroundView.getHeight());
            cache.put(imageUrl, image);
            return image;
        }

            // save bitmap on device.
            //saveOnDevice(image);

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        progressBar.setVisibility(View.GONE);

        if (bitmap != null){
            ColorFilter filter = new ColorFilter();
            backgroundView.setColorFilter(Color.rgb(130, 130, 130), android.graphics.PorterDuff.Mode.MULTIPLY);
            backgroundView.setImageBitmap(bitmap);
        } else {

        }

        super.onPostExecute(bitmap);
    }

    private void saveOnDevice(Bitmap image){
        String root = Environment.getExternalStorageDirectory().toString();
        try {
            File file = new File(root + "/versapp_images");
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
