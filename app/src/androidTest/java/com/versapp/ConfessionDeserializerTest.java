package com.versapp;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.versapp.confessions.Confession;
import com.versapp.confessions.ConfessionManager;

import java.io.InputStream;

/**
 * Created by william on 13/10/14.
 */
public class ConfessionDeserializerTest extends InstrumentationTestCase {

    ConfessionManager confessionManager;
    InputStream emptyInputStream;
    InputStream valid1inputStream;
    InputStream valid3inputStream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        confessionManager = ConfessionManager.getInstance();
        emptyInputStream = this.getClass().getResourceAsStream("/valid_0_confessions_server_return_sample");
        valid1inputStream =  this.getClass().getResourceAsStream("/valid_1_confessions_server_return_sample");
        valid3inputStream =  this.getClass().getResourceAsStream("/valid_3_confessions_server_return_sample");
    }

    @SmallTest
    public void testPreConditions(){
        assertNotNull(emptyInputStream);
        assertNotNull(valid1inputStream);
        assertNotNull(valid3inputStream);
    }

    @SmallTest
    public void testEmptyStreamDeserialize(){
        Confession[] confessions = confessionManager.getDeserializeConfessionsStreamMethod(emptyInputStream);
        assertEquals(0, confessions.length);
    }

    @SmallTest
    public void testOneConfessionStreamDeserialize(){
        Confession[] confessions = confessionManager.getDeserializeConfessionsStreamMethod(valid1inputStream);
        assertEquals(1, confessions.length);
    }

    @SmallTest
    public void testThreeConfessionsStreamDeserialize(){
        Confession[] confessions = confessionManager.getDeserializeConfessionsStreamMethod(valid3inputStream);
        assertEquals(3, confessions.length);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
