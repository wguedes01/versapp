package com.versapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.versapp.confessions.ConfessionsFragment;

import java.io.File;


public class DashboardActivity extends FragmentActivity {

    private static final int CONFESSIONS_PAGE = 1;

    private ViewPager pager;
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pager = (ViewPager) findViewById(R.id.main_view_pager);
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            final static int FRAGMENT_COUNT = 2;

            @Override
            public Fragment getItem(int i) {
                switch (i){
                    case CONFESSIONS_PAGE:
                        return new ConfessionsFragment();
                    default:
                        return new MainFragment();
                }
            }

            @Override
            public int getCount() {
                return FRAGMENT_COUNT;
            }

        };
        pager.setAdapter(adapter);

        // If tutorial not complete, show swipe to thoughts.
        if (!TutorialManager.getInstance(this).isConfessionTutorialCompleted()){

            final View swipeToConfessionsLabel = findViewById(R.id.swipe_left_to_confessions_tutorial_text_view);
            swipeToConfessionsLabel.setVisibility(View.VISIBLE);

            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i2) {

                }

                @Override
                public void onPageSelected(int i) {

                    if (i == CONFESSIONS_PAGE){ // confessions page.
                        swipeToConfessionsLabel.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

        }


    }

    @Override
    public void onBackPressed() {
        //pager.setCurrentItem(0);
        //super.onBackPressed();


        //Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        //new Crop(source)


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            //resultView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
