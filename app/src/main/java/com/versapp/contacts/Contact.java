package com.versapp.contacts;

/**
 * Created by william on 25/09/14.
 */
public class Contact {

    String[] phone;
    String[] email;

    public Contact(String[] phone, String[] email) {
        this.phone = phone;
        this.email = email;
    }

    public String[] getPhone() {
        return phone;
    }

    public void setPhone(String[] phone) {
        this.phone = phone;
    }

    public String[] getEmail() {
        return email;
    }

    public void setEmail(String[] email) {
        this.email = email;
    }
}
