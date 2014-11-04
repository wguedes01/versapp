package com.versapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.versapp.chat.ChatManager;
import com.versapp.confessions.ConfessionsFragment;


public class DashboardActivity extends FragmentActivity {

    private static final int CONFESSIONS_PAGE = 1;

    private ViewPager pager;
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Tracker tracker = ((MainApplication) getApplicationContext()).getTracker();
        tracker.setScreenName("DashboardActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

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

                        // Get tracker.
                        Tracker t = ((MainApplication) getApplication()).getTracker();
                        // Build and send an Event.
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Swipes")
                                .setAction("Swipe to Thoughts")
                                .build());
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

        }

        // Sync chats
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                ChatManager.getInstance().syncLocalChatDB(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ChatManager.CHAT_SYNCED_ACTION));
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
