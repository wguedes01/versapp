package com.versapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by william on 03/10/14.
 */
public class TutorialManager {

    private static final String SHARED_PREFERENCES_TUTORIAL = "SHARED_PREFERENCES_TUTORIAL";
    private static final String TUTORIAL_COMPLETED = "TUTORIAL_COMPLETED";

    private static TutorialManager instance;
    private Context context;
    private SharedPreferences prefs;

    private TutorialManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, Context.MODE_PRIVATE);
    }

    public static TutorialManager getInstance(Context context) {

        if (instance == null) {
            instance = new TutorialManager(context);
        }

        return instance;
    }

    public boolean isTutorialComplete(){
       return prefs.getBoolean(TUTORIAL_COMPLETED, false);
    }

    public void setCompleted(){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(TUTORIAL_COMPLETED, true);
        edit.commit();
    }
}
