package com.versapp.confessions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.versapp.GCSManager;
import com.versapp.R;
import com.versapp.util.ImageManager;

import java.io.IOException;

public class ComposeConfessionActivity extends FragmentActivity {

    ProgressDialog progressDialog;

    Bitmap backgroundImage;
    ImageView backgroundPictureImageView;
    ImageButton pictureBtn;
    ImageButton composeBtn;
    EditText bodyEdit;
    ViewPager pager;

    RelativeLayout frame;

    private static String selectedBackgroundColor;
    private static final String[] backgroundColors = {"#008CF4","#3DD8F5","#6913C1","#8D51CB","#FFA319","#F7E200","#6FC10B","#9EDE4F","#F23637","#FF4662" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_confession);

        progressDialog = new ProgressDialog(ComposeConfessionActivity.this);
        progressDialog.setTitle("Creating Thought...");
        progressDialog.setMessage("You'll see it shortly!");

        backgroundPictureImageView = (ImageView) findViewById(R.id.compose_confession_background_image);
        pictureBtn = (ImageButton) findViewById(R.id.compose_confession_attach_pic_btn);
        composeBtn = (ImageButton) findViewById(R.id.compose_confession_btn);
        bodyEdit = (EditText) findViewById(R.id.compose_confession_body_edit);
        pager = (ViewPager) findViewById(R.id.compose_confession_color_view_pager);

        pager.setAdapter(new ColorPagerAdapter(getSupportFragmentManager()));


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

        frame = (RelativeLayout) findViewById(R.id.compose_confession_frame);

        RelativeLayout.LayoutParams paramsList = (RelativeLayout.LayoutParams) frame.getLayoutParams();
        paramsList.height = width;
        frame.setLayoutParams(paramsList);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 140) {
                    Toast.makeText(getApplicationContext(),  "Oops.. you've reached the character limit", Toast.LENGTH_SHORT).show();
                }
            }
        };

        bodyEdit.addTextChangedListener(watcher);
        bodyEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    return true;
                }

                return false;
            }
        });


        bodyEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus){
                    RelativeLayout.LayoutParams paramsList = (RelativeLayout.LayoutParams) frame.getLayoutParams();
                    paramsList.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    frame.setLayoutParams(paramsList);
                }

            }
        });

    }

    private class ColorPagerAdapter extends FragmentPagerAdapter {

        private final int COLOR_COUNT = backgroundColors.length;

        public ColorPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            selectedBackgroundColor = backgroundColors[i];
            return ComposeConfessionColorFragment.newInstance(backgroundColors[i]);

        }

        @Override
        public int getCount() {
            return COLOR_COUNT;
        }
    }

    public void composeConfession(View view) {

        final String body = bodyEdit.getText().toString();

        new AsyncTask<Void, Void, Confession>(){

            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected Confession doInBackground(Void... params) {

                if (backgroundImage != null) {

                    String url = null;

                    try {
                        url = GCSManager.getInstance(ComposeConfessionActivity.this).upload(backgroundImage);

                        if (url != null) {

                            return ConfessionManager.getInstance().createConfession(body, url);

                        } else {        Display display = getWindowManager().getDefaultDisplay();

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
                            return null;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    return ConfessionManager.getInstance().createConfession(body,  selectedBackgroundColor);

                }

                return null;
            }

            @Override
            protected void onPostExecute(Confession confession) {

                progressDialog.cancel();

                if (confession != null) {
                    ConfessionsFragment.addConfession(confession);

                    finish();
                } else {
                    Toast.makeText(ComposeConfessionActivity.this, "Failed to create thought. Please try again.", Toast.LENGTH_LONG).show();
                }

                super.onPostExecute(confession);
            }
        }.execute();


    }

    public void attachImage(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Background Options");
        builder.setItems(new CharSequence[] { "Select Image from Camera", "Take a Picture" }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    browsePictures();
                } else if (which == 1) {
                    takePicture();
                }
            }
        });
        builder.show();

    }

    private static final int LOAD_IMAGE_REQUEST_CODE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private void browsePictures(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST_CODE);
    }

    public void takePicture(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void browsePictures() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST_CODE);
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOAD_IMAGE_REQUEST_CODE && data != null) {
            handlePictureSelectedIntent(data);
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            handlePictureTakenIntent(data);
        }
    }

    public void handlePictureTakenIntent(final Intent intent) {

        new AsyncTask<Void, Void, Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... params) {

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


                ImageManager imageManager = new ImageManager();
                String selectedImagePath = imageManager.getSelectedImagePath(intent, getApplicationContext());
                imageManager.setTargetHeight(width);
                imageManager.setTargetWidth(width);

                return cropBitmap(imageManager.getScaledBitmapImage(selectedImagePath));

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {

                setMessagePicture(bitmap);

                super.onPostExecute(bitmap);
            }
        }.execute();

    }

    public void handlePictureSelectedIntent(final Intent intent) {


        new AsyncTask<Void, Void, Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... params) {

                ImageManager imageManager = new ImageManager();
                String selectedImagePath = imageManager.getSelectedImagePath(intent, getApplicationContext());
                imageManager.setTargetHeight(500);
                imageManager.setTargetWidth(500);

                return cropBitmap(imageManager.getScaledBitmapImage(selectedImagePath));

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {

                setMessagePicture(bitmap);

                super.onPostExecute(bitmap);
            }

        }.execute();

    }

    protected Bitmap cropBitmap(Bitmap image) {
        if (image.getWidth() >= image.getHeight()) {
            return Bitmap.createBitmap(image, (int) 0, 0, (int) image.getWidth(), image.getHeight());
        } else {
            double height = image.getWidth();
            double inset = 0.5 * (image.getHeight() - height);
            return Bitmap.createBitmap(image, 0, (int) inset, image.getWidth(), (int) height);
        }
    }

    protected void setMessagePicture(final Bitmap scaledImage) {

     /*
        int highlightColor = getApplicationContext().getResources().getColor(R.color.);
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(highlightColor, PorterDuff.Mode.SRC_ATOP);
        backgroundPictureImageView.setColorFilter(colorFilter);
*/
        backgroundImage = scaledImage;
        backgroundPictureImageView.setImageBitmap(scaledImage);



    }




}


