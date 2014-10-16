package com.versapp;

import android.test.suitebuilder.annotation.SmallTest;

import com.versapp.chat.ConfessionChatBuilder;
import com.versapp.chat.GroupChatBuilder;
import com.versapp.chat.OneToOneChatBuilder;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by william on 16/10/14.
 */
public class ChatBuilderTest extends TestCase {

    @SmallTest
    public void testOneToOneChatBuilderToJson(){

        String username = "william";

        OneToOneChatBuilder builder = new OneToOneChatBuilder(username);

        ArrayList<String> participants = new ArrayList<String>();
        participants.add(username);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "121");
            jsonObject.put("participants", new JSONArray(participants));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assertEquals(builder.toJson().toString(), jsonObject.toString());

        builder.setType("thought");
        assertNotSame(builder.toJson().toString(), jsonObject.toString());
    }

    @SmallTest
    public void testConfessionChatBuilderToJson(){

        long cid = 7;

        ConfessionChatBuilder builder = new ConfessionChatBuilder(cid);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "thought");
            jsonObject.put("cid", cid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assertEquals(builder.toJson().toString(), jsonObject.toString());

        builder.setType("121");
        assertNotSame(builder.toJson().toString(), jsonObject.toString());
    }

    @SmallTest
    public void testGroupChatBuilderToJson(){

        String groupName = "Cool name";

        ArrayList<String> participant = new ArrayList<String>();
        participant.add("william");
        participant.add("g");

        GroupChatBuilder builder = new GroupChatBuilder(participant, groupName);

        ArrayList<String> participantFinal = new ArrayList<String>();
        participantFinal.add("william");
        participantFinal.add("g");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "group");
            jsonObject.put("participants", new JSONArray(participantFinal));
            jsonObject.put("name", groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assertEquals(builder.toJson().toString(), jsonObject.toString());

        builder.setType("thought");
        assertNotSame(builder.toJson().toString(), jsonObject.toString());
    }

}
