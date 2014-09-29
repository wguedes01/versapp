package com.versapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.versapp.confessions.ConfessionsFragment;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import org.jivesoftware.smack.packet.Message;


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

    @Override
    public void onBackPressed() {

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                Message message = new Message();
                message.setType(Message.Type.chat);
                message.setFrom(ConnectionService.getJid());
                message.setTo("7eb56d21-e512-4fbf-ab16-b14b9b6e5552@"+ ConnectionManager.SERVER_IP_ADDRESS); // with G.
                message.setBody("Hello, World!");
                ConnectionService.getConnection().sendPacket(message);

                return null;
            }
        }.execute();

       // super.onBackPressed();
    }



}
