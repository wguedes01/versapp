package com.versapp.vcard;

import com.versapp.connection.ConnectionService;

/**
 * Created by william on 21/09/14.
 */
public class VCardManager {

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

}
