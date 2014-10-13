package com.versapp;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by william on 13/10/14.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity activity;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false); // when testing touch we need this.

        activity = getActivity();
    }

    public void testPreConditions(){
        assertNotNull(activity);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
