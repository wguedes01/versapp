
package com.versapp;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.robotium.solo.Solo;

/**
 * Created by william on 16/10/14.
 */
public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;

    public LoginTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @SmallTest
    public void testLoginValidCredentials(){

        loginAction(solo, "will", "111111");

        assertTrue(solo.searchText("Messages"));

        assertTrue(solo.searchText("Login"));
    }

    @SmallTest
    public void testLoginInvalidCredentials(){
        solo.clickOnText("Login");

        loginAction(solo, "will", "1234");

        assertTrue(solo.waitForText("Invalid username or password."));
        assertTrue(solo.searchText("Cancel"));

        solo.clickOnButton("Cancel");
        assertTrue(solo.searchText("Login"));
    }

    public static void loginAction(Solo s, String username, String password){
        s.clickOnText("Login");

        s.enterText(0, username);
        s.enterText(1, password);
        s.clickOnButton(3);
    }

    public static void logoutAction(Solo s){

        s.clickOnText("Profile");
        s.clickOnText("Logout");
        s.clickOnButton(1);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        logoutAction(solo);
    }
}