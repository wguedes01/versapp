package com.versapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by william on 03/10/14.
 */
public class TutorialManager {

    private static final String SHARED_PREFERENCES_TUTORIAL = "SHARED_PREFERENCES_TUTORIAL";
    private static final String TUTORIAL_COMPLETED = "TUTORIAL_COMPLETED";
    private static final String CONFESSION_TUTORIAL_COMPLETED = "CONFESSION_TUTORIAL_COMPLETED";
    private static final String TUTORIAL_CREATE_FIRST_CONFESSION_CHAT = "TUTORIAL_CREATE_FIRST_CONFESSION_CHAT";

    private static final String TUTORIAL_IS_CHAT_EXPLAINED = "TUTORIAL_IS_CHAT_EXPLAINED";



    private static TutorialManager instance;
    private Context context;
    private SharedPreferences prefs;

    private TutorialManager(Context context) {
        this.context = context.getApplicationContext();
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

    public void setConfessionTutorialCompleted(){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(CONFESSION_TUTORIAL_COMPLETED, true);
        edit.commit();
    }

    public boolean isConfessionTutorialCompleted(){
            return prefs.getBoolean(CONFESSION_TUTORIAL_COMPLETED, false);
    }

    public void setCreateFirstConfessionChatTutorialCompleted(){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(TUTORIAL_CREATE_FIRST_CONFESSION_CHAT, true);
        edit.commit();
    }

    public boolean isCreateFirstConfessionChatTutorialCompleted() {
        return prefs.getBoolean(TUTORIAL_CREATE_FIRST_CONFESSION_CHAT, false);
    }

    public void setChatExplained(){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(TUTORIAL_IS_CHAT_EXPLAINED, true);
        edit.commit();
    }

    public boolean isChatExplained() {
        return prefs.getBoolean(TUTORIAL_IS_CHAT_EXPLAINED, false);
    }


}
