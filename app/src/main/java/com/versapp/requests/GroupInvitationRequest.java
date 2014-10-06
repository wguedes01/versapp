package com.versapp.requests;

import com.versapp.chat.GroupChat;

/**
 * Created by william on 06/10/14.
 */
public class GroupInvitationRequest extends Request {

    GroupChat chat;

    protected GroupInvitationRequest(GroupChat chat) {
        super(chat.getName(), "Would you like to join the group?");
        this.chat = chat;
    }

    @Override
    public void accept() {

    }

    @Override
    public void deny() {

    }

    public GroupChat getChat() {
        return chat;
    }

    public void setChat(GroupChat chat) {
        this.chat = chat;
    }
}
