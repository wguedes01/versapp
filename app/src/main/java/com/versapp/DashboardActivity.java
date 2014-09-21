package com.versapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.versapp.confessions.ConfessionsFragment;


public class DashboardActivity extends FragmentActivity {



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
                    case 1:
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



        System.out.println(getFilesDir());

    }
}
