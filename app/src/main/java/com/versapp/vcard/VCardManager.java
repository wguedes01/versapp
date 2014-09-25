package com.versapp.vcard;

import android.util.Xml;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by william on 21/09/14.
 */
public class VCardManager {


    public static final String NICKNAME_TAG_ITEM = "NICKNAME";
    public static final String FULL_NAME_TAG_ITEM = "FN";
    public static final String FIRST_NAME_TAG_ITEM = "GIVEN";
    public static final String LAST_NAME_TAG_ITEM = "FAMILY";
    public static final String USERNAME_TAG_ITEM = "USERNAME";

    public static void createVCard(VCard vCard) {
        sendVCardUpdatePacket(vCard);
    }

    private static String sendVCardUpdatePacket(VCard vCard) {
        final String PACKET_ID = "vcard_update_packet";

        StringBuilder xmlPacket = new StringBuilder();
        xmlPacket.append(String.format("<iq type='set' id='%s'>", PACKET_ID));
        xmlPacket.append("<vCard xmlns='vcard-temp'>");

        xmlPacket.append(vCard.toString());

        xmlPacket.append("</vCard>");
        xmlPacket.append("</iq>");

        String response = ConnectionService.sendCustomXMLPacket(xmlPacket.toString(), PACKET_ID);

        // Sends a packet to all contacts saying vCard has been updated.
        String packetId2 = "";

        return response;

    }

    public static VCard getVCard(String username) {

        XmlPullParser parser;

        // just in case a bug happens on the code that allows a non-friend to
        // invite person to group.
        if (username == null) {
            return null;
        }

        if (!username.contains("@")) {
            username = username + "@" + ConnectionManager.SERVER_IP_ADDRESS;
        }

        VCard vCard = null;

        String serverResponse = requestVCardPacket(username);

        if (serverResponse == null) {
            return null;
        }

        InputStream vCardXml = null;
        try {
            vCardXml = new ByteArrayInputStream(serverResponse.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        parser = Xml.newPullParser();

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(vCardXml, null);

            vCard = parseXml(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		/*
		 *
		 * if ((vCard.getUsername() == null) ||
		 * (vCard.getUsername().equals("null"))) { return null; }
		 */

        return vCard;


    }

    public static String requestVCardPacket(String username) {
        final String PACKET_ID = "request_vcard";

        username = username.trim();

        String xml = String.format("<iq type='get' to='%s' id='%s'><vCard xmlns='vcard-temp'/></iq>", username, PACKET_ID);

        return ConnectionService.sendCustomXMLPacket(xml, PACKET_ID);
    }

    public static VCard parseXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();

        VCard vCard = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    vCard = new VCard();
                    break;

                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName == "vCard") {

                    } else if (vCard != null) {
                        if (tagName.equals(FIRST_NAME_TAG_ITEM)) {
                            vCard.setFirstName(parser.nextText());
                        } else if (tagName.equals(LAST_NAME_TAG_ITEM)) {
                            vCard.setLastName(parser.nextText());
                        } else if (tagName.equals(NICKNAME_TAG_ITEM)) {
                            vCard.setNickname(parser.nextText());
                        }

                    }
                    break;
                case XmlPullParser.END_DOCUMENT:
                    return vCard;
            }
            eventType = parser.next();
        }

        return vCard;

    }

}
