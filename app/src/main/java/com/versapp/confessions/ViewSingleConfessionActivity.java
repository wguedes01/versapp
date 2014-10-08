package com.versapp.confessions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.versapp.GCSManager;
import com.versapp.Logger;
import com.versapp.R;

public class ViewSingleConfessionActivity extends Activity {

    public static final String CONFESSION_ID_INTENT_EXTRA = "CONFESSION_ID_INTENT_EXTRA";

    private Confession confession;

    private View confessionHolder;
    private ImageView backgroundImage;
    private TextView bodyText;
    private TextView favoritesCount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_confession);

        final long confessionId = getIntent().getLongExtra(CONFESSION_ID_INTENT_EXTRA, -1);

        Log.d(Logger.CONNECTION_DEBUG, "Got id here: " + confessionId);

        confessionHolder = findViewById(R.id.activity_view_single_confession_confession_holder);
        backgroundImage = (ImageView) findViewById(R.id.activity_view_single_confession_background_image);
        bodyText = (TextView) findViewById(R.id.activity_view_single_confession_body);
        favoritesCount = (TextView) findViewById(R.id.activity_view_single_confession_favorites_count);
        progressBar = (ProgressBar) findViewById(R.id.activity_view_single_confession_progress_bar);

        new AsyncTask<Void, Void, Confession>(){

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Confession doInBackground(Void... params) {
                return ConfessionManager.getInstance().getConfessionFromServer(getParent(), confessionId);
            }

            @Override
            protected void onPostExecute(final Confession confession) {

                if (confession != null) {

                    if (confession.getBody().length() > 140) {
                        bodyText.setTextSize(25);
                    } else {
                        bodyText.setTextSize(30);
                    }
                    bodyText.setText(confession.getBody());

                    favoritesCount.setText(String.valueOf(confession.getNumFavorites()));
                    findViewById(R.id.activity_view_single_confession_favorite_count_holder).setVisibility(View.VISIBLE);
                    bodyText.setText(confession.getBody());

                    if (confession.getImageUrl().startsWith("#")){
                        backgroundImage.setBackgroundColor(Color.parseColor(confession.getImageUrl()));
                        progressBar.setVisibility(View.GONE);
                    } else {

                        new AsyncTask<Void, Void, Bitmap>() {

                            @Override
                            protected Bitmap doInBackground(Void... params) {
                                return GCSManager.getInstance(getApplicationContext()).downloadImage(confession.getImageUrl(), backgroundImage.getWidth(), backgroundImage.getHeight());
                            }

                            @Override
                            protected void onPostExecute(Bitmap bitmap) {

                                progressBar.setVisibility(View.GONE);

                                if (bitmap != null) {
                                    ConfessionImageCache.setBitmapOnView(getApplicationContext(), backgroundImage, bitmap);
                                }

                                super.onPostExecute(bitmap);
                            }
                        }.execute();

                    }

                }

                super.onPostExecute(confession);
            }

        }.execute();



        adjustLayout(confessionHolder);

    }

    private void adjustLayout(View view){
        Display display = getWindowManager().getDefaultDisplay();

        int width = 0;
        int height = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

        RelativeLayout.LayoutParams paramsList = (RelativeLayout.LayoutParams) view.getLayoutParams();
        paramsList.height = width;
        view.setLayoutParams(paramsList);
    }



}
