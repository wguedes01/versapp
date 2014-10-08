package com.versapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.versapp.confessions.ConfessionsFragment;


public class DashboardActivity extends FragmentActivity {

    private static final int CONFESSIONS_PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ViewPager pager = (ViewPager) findViewById(R.id.main_view_pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

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

        });

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

       // super.onBackPressed();
    }

}
