package com.versapp.chat.conversation;

/**
 * Created by william on 29/09/14.
 */
public class Message {

    public static final String IMAGE_URL_PROPERTY = "image_url";

    private boolean isMine;
    private String thread;
    private String timestmap;
    private org.jivesoftware.smack.packet.Message smackMessage;

    public Message(String thread, String body, String imageUrl, String timestamp, boolean isMine) {
        this.smackMessage = new org.jivesoftware.smack.packet.Message();
        this.smackMessage.setType(org.jivesoftware.smack.packet.Message.Type.chat);
        this.smackMessage.setBody(body);
        this.smackMessage.setProperty(IMAGE_URL_PROPERTY, (imageUrl == null ? "" : imageUrl));
        this.isMine = isMine;
        this.thread = thread;
        this.timestmap = timestamp;
    }

    public org.jivesoftware.smack.packet.Message getSmackMessage(){
        return smackMessage;
    }

    public String getBody(){
        return smackMessage.getBody();
    }

    public String getFrom(){
        return smackMessage.getFrom();
    }

    public String getImageUrl(){
        return smackMessage.getProperty(IMAGE_URL_PROPERTY).toString();
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getTimestmap() {
        return timestmap;
    }

    public void setTimestmap(String timestmap) {
        this.timestmap = timestmap;
    }
}
