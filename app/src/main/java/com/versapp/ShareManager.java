package com.versapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

/**
 * Created by william on 15/10/14.
 */
public class ShareManager {

    private static final String SHARE_FILE = "SHARE_FILE";
    private static final String TWITTER_PACKAGE = "com.twitter.android";

    public static boolean isTwitterInstalled(Context context){

        PackageManager pm = context.getPackageManager();

        try{
            pm.getPackageInfo(TWITTER_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e){
            return false;
        }

    }

    public static void shareOnTwitter(Context context){

        String message = "I've just joined #Versapp and I'm about to start sharing my thoughts!";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        share.setPackage(TWITTER_PACKAGE);

        context.startActivity(Intent.createChooser(share, "Share").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        setSharedOnTwitter(context);
    }

    private static void setSharedOnTwitter(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(TWITTER_PACKAGE, true);
        edit.commit();
    }

    public static boolean isSharedOnTwitter(Context context){
        return context.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE).getBoolean(TWITTER_PACKAGE, false);
    }


}
