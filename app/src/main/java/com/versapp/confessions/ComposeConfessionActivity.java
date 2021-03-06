package com.versapp.confessions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.versapp.GCSManager;
import com.versapp.Logger;
import com.versapp.NewImageManager;
import com.versapp.R;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ComposeConfessionActivity extends FragmentActivity {

    ProgressDialog progressDialog;

    Bitmap backgroundImage;
    ImageView backgroundPictureImageView;
    ImageButton pictureBtn;
    ImageButton composeBtn;
    EditText bodyEdit;
    ViewPager pager;

    RelativeLayout frame;
    RelativeLayout buttonsMenu;

    private Uri pathToPicture;

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
        buttonsMenu = (RelativeLayout) findViewById(R.id.activity_compose_confession_menu_opts);

        ColorPagerAdapter adapter = new ColorPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        selectedBackgroundColor = backgroundColors[0];
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                selectedBackgroundColor = backgroundColors[i];
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

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

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }

                return false;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        //((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(bodyEdit.getWindowToken(), 0);
        super.onStop();
    }

    private class ColorPagerAdapter extends FragmentPagerAdapter {

        private final int COLOR_COUNT = backgroundColors.length;

        public ColorPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return ComposeConfessionColorFragment.newInstance(backgroundColors[i]);

        }

        @Override
        public int getCount() {
            return COLOR_COUNT;
        }
    }

    public void composeConfession(View view) {

        final String body = bodyEdit.getText().toString();

        if (body.length() <= 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ComposeConfessionActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Post a thought without text?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    compose(body);
                }
            }).setNegativeButton("Cancel", null);

            AlertDialog d = builder.create();
            d.show();


        } else {
            compose(body);
        }
    }

    public void compose(final String body){
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
                    Crop.pickImage(ComposeConfessionActivity.this);
                } else if (which == 1) {
                    takePicture();
                }
            }
        });
        builder.show();

    }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public void takePicture() {
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File photoFile = null;
            try {
                photoFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg",storageDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                pathToPicture = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Crop.REQUEST_PICK || requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) && resultCode == RESULT_OK) {

            Log.d(Logger.CHAT_DEBUG, "OK....." + data);

            if (data != null) { // picture selected from camera
                beginCrop(data.getData());
            } else {
                beginCrop(pathToPicture);
            }

        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            try {
                Bitmap image = NewImageManager.correctImageRotation(this, Crop.getOutput(result));
                setMessagePicture(image);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void setMessagePicture(final Bitmap scaledImage) {

        if (ConfessionImageCache.isAboveWhiteThreshold(scaledImage)){
            ConfessionImageCache.applyDarkLayer(backgroundPictureImageView);
        }

        backgroundImage = scaledImage;
        backgroundPictureImageView.setImageBitmap(scaledImage);
    }




}


