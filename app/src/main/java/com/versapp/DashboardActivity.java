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
import com.versapp.vcard.VCard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


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

                /*
                FriendsManager.getInstance(DashboardActivity.this).syncWithServer();

                ArrayList<Friend> friends = new FriendsDAO(getApplicationContext()).getFriends();

                  //      FriendsManager.getInstance(DashboardActivity.this).syncWithServer();

                System.out.println("Friends:");
                for(Friend f : friends){
                    System.out.println("Name: "+f.getName());
                }

    */
            testVCard2();
                return null;
            }


        }.execute();

       // super.onBackPressed();
    }


    private void  testVCard(){

        String username = "will";

        if (!username.contains("@")) {
            username = username + "@" + ConnectionManager.SERVER_IP_ADDRESS;
        }

        VCard vCard = null;

        final String PACKET_ID = "request_vcard";

        username = username.trim();

        String xml = String.format("<iq type='get' to='%s' id='%s'><vCard xmlns='vcard-temp'/></iq>", username, PACKET_ID);

        String serverResponse = ConnectionService.sendCustomXMLPacket(xml, PACKET_ID);

        System.out.println(serverResponse);

        InputStream vCardXml = null;
        try {
            vCardXml = new ByteArrayInputStream(serverResponse.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        /*
        parser = Xml.newPullParser();

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(vCardXml, null);

            vCard = parseXml(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		/*
		 *
		 * if ((vCard.getUsername() == null) ||
		 * (vCard.getUsername().equals("null"))) { return null; }
		 */




    }


    public void testVCard2(){

    }
}
