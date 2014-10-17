package com.versapp.chat.conversation;

/**
 * Created by william on 16/10/14.
 */
public class ChatMessage {

    public Message message;
    public boolean animate = false;

    public ChatMessage(Message message) {
        this.message = message;
    }

    public ChatMessage(Message message, boolean animate) {
        this.message = message;
        this.animate = animate;
    }

}
