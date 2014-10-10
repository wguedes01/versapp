package com.versapp.confessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

/**
 * Created by william on 20/09/14.
 */
public class ConfessionTutorial {

    private static final String CONFESISON_TUTORIAL_COMPLETED = "CONFESSION_TUTORIAL_COMPLETED";

    private Context context;
    private View parent;
    private View swipeUpOrDownLabel;

    public ConfessionTutorial(Context context, View parent) {
        this.context = context;
        this.parent = parent;
    }

    public void complete() {

        setCompleted();
        this.swipeUpOrDownLabel.setVisibility(View.GONE);

    }

    private void setCompleted() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();

        edit.putBoolean(CONFESISON_TUTORIAL_COMPLETED, true);
        edit.commit();
    }

    public static boolean isCompleted(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(CONFESISON_TUTORIAL_COMPLETED, false);
    }

}
