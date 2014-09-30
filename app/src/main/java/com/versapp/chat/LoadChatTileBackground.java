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

import com.versapp.GCSManager;
import com.versapp.Logger;
import com.versapp.confessions.ConfessionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by william on 27/09/14.
 */
public class LoadChatTileBackground extends AsyncTask<Void, Void, Void> {

    private Context context;
    private LruCache<String, Bitmap> cache;
    private ImageView backgroundView;
    private ConfessionChat chat;
    private View progressBar;

    private Bitmap bitmap;
    private String colorBackground;

    public LoadChatTileBackground(Context context, ConfessionChat chat, LruCache<String, Bitmap> cache, ImageView backgroundView, View progressBar) {
        this.context = context;
        this.chat = chat;
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
    protected Void doInBackground(Void... params) {

        // Ensure variables don't carry any value before this method is executed.
        bitmap = null;
        colorBackground = null;

        // Get confession
        if (chat.getConfession() == null){
           chat.setConfession(ConfessionManager.getInstance().getConfessionFromServer(context, chat.getCid()));
        }



        if (chat.getConfession() != null){

            if (chat.getConfession().getImageUrl().startsWith("#")){
                // color
                colorBackground = chat.getConfession().getImageUrl();
            } else {
                if (cache.get(chat.getConfession().getImageUrl()) != null) {
                    Log.d(Logger.CHAT_DEBUG, "Got image from cache. Url: " + chat.getConfession().getImageUrl());
                    bitmap = cache.get(chat.getConfession().getImageUrl());
                } else {
                    Log.d(Logger.CHAT_DEBUG, "Downloaded image for tile: " + chat.getConfession().getImageUrl());
                    Bitmap image = GCSManager.getInstance(context).downloadImage(chat.getConfession().getImageUrl(), backgroundView.getWidth(), backgroundView.getHeight());
                    cache.put(chat.getConfession().getImageUrl(), image);
                    bitmap = image;
                }
            }

        } else {

        }

            // save bitmap on device.
            //saveOnDevice(image);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        progressBar.setVisibility(View.GONE);

        if (!isCancelled()){

            if (backgroundView != null) {
                if (bitmap != null){
                    ColorFilter filter = new ColorFilter();
                    backgroundView.setColorFilter(Color.rgb(130, 130, 130), android.graphics.PorterDuff.Mode.MULTIPLY);
                    backgroundView.setImageBitmap(bitmap);
                } else if(colorBackground != null) {
                    backgroundView.setBackgroundColor(Color.parseColor(colorBackground));
                } else {

                }
            }

        }


        super.onPostExecute(aVoid);
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
