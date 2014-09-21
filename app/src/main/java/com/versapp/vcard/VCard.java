package com.versapp.vcard;

/**
 * Created by william on 21/09/14.
 */
public class VCard {

    public static final String NICKNAME_TAG_ITEM = "NICKNAME";
    public static final String FULL_NAME_TAG_ITEM = "FN";
    public static final String FIRST_NAME_TAG_ITEM = "GIVEN";
    public static final String LAST_NAME_TAG_ITEM = "FAMILY";
    public static final String USERNAME_TAG_ITEM = "USERNAME";

    private String nickname;
    private String firstName;
    private String lastName;

    public VCard(String firstName, String lastName) {
        this.nickname = firstName;
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }

    public VCard() {

    }

    public String getNickName() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        if (firstName == null) {
            return "";
        }
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if (lastName == null) {
            return "";
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public String toString() {

        StringBuilder xmlProperties = new StringBuilder();

        xmlProperties.append(String.format("<%s>%s</%s>", FULL_NAME_TAG_ITEM, String.format("%s %s", this.firstName, this.lastName), FULL_NAME_TAG_ITEM));

        xmlProperties.append("<N>");
        xmlProperties.append(String.format("<%s>%s</%s>", FIRST_NAME_TAG_ITEM, this.getFirstName(), FIRST_NAME_TAG_ITEM));
        xmlProperties.append(String.format("<%s>%s</%s>", LAST_NAME_TAG_ITEM, this.getLastName(), LAST_NAME_TAG_ITEM));
        xmlProperties.append("</N>");
        xmlProperties.append(String.format("<%s>%s</%s>", NICKNAME_TAG_ITEM, this.nickname, NICKNAME_TAG_ITEM));

        return xmlProperties.toString();
    }

    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

}
