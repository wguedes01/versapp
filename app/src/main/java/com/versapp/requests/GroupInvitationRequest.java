package com.versapp.requests;

import android.content.Context;

import com.versapp.chat.ChatManager;
import com.versapp.chat.GroupChat;

/**
 * Created by william on 06/10/14.
 */
public class GroupInvitationRequest extends Request {

    Context context;
    GroupChat chat;

    protected GroupInvitationRequest(Context context, GroupChat chat) {
        super(chat.getName(), "Would you like to join the group?");
        this.chat = chat;
        this.context = context;
    }

    @Override
    public void accept() {
        ChatManager.getInstance().joinGroup(context, chat);
    }

    @Override
    public void deny() {
        ChatManager.getInstance().leaveChat(chat);
    }

    public GroupChat getChat() {
        return chat;
    }

    public void setChat(GroupChat chat) {
        this.chat = chat;
    }
}
