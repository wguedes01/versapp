package com.versapp.connection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.versapp.Logger;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by william on 20/09/14.
 */
public class ConnectionService extends Service {

    private static XMPPTCPConnection connection;
    private static String user;
    private static String jid;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static XMPPTCPConnection getConnection(){
        return connection;
    }

    public static void setConnection(XMPPTCPConnection conn) {
        connection = conn;
    }

    public static String sendCustomXMLPacket(final String xml, final String packetId) {

        // we should change this to throwing exceptions..
        if (!connection.isConnected() || !connection.isAuthenticated()) {
            return "";
        }

        PacketCollector iqPacketCollector = ConnectionService.getConnection().createPacketCollector(new PacketFilter() {

            @Override
            public boolean accept(Packet packet) {
                if (packet.toXML().toString().contains(packetId)) {
                    return true;
                }
                return false;
            }
        });

        Packet packet = new Packet() {
            @Override
            public String toXML() {
                return xml;
            }
        };

        try {
            ConnectionService.getConnection().sendPacket(packet);

            Packet p = iqPacketCollector.nextResult();

            if (p != null){
                String response = p.toXML().toString().replaceAll("\\r\\n|\\r|\\n", " ");
                Logger.log(Logger.EJABBERD_SERVER_REQUESTS_DEBUG, "Sent: " + xml);
                Logger.log(Logger.EJABBERD_SERVER_REQUESTS_DEBUG, "Received: " + response);
                return  response;
            }

            return null;

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String getSessionId() {

        String sessionId = "";

        String packetId = "get_session_id";
        String xml = "<iq type='get' id='" + packetId + "' from='" + ConnectionService.getConnection().getUser() + "' to='"
                + ConnectionManager.SERVER_IP_ADDRESS + "' ><query xmlns='who:iq:session'/></iq>";

        String response = sendCustomXMLPacket(xml, packetId);

        Pattern p = Pattern.compile(">(.*?)<");
        Matcher m = p.matcher(response);

        if (m.find()) {
            sessionId = m.group(1);
        }

        return sessionId;
    }

    public static String sendUnauthenticatedCustomXMLPacket(final String xml, final String packetId, XMPPTCPConnection conn) {

        // we should change this to throwing exceptions..
        if (!conn.isConnected()) {
            return "";
        }

        PacketCollector iqPacketCollector = conn.createPacketCollector(new PacketFilter() {

            @Override
            public boolean accept(Packet packet) {
                if (packet.toXML().toString().contains(packetId)) {
                    return true;
                }
                return false;
            }
        });

        Packet packet = new Packet() {
            @Override
            public String toXML() {
                return xml;
            }
        };

        try {
            conn.sendPacket(packet);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        String response = iqPacketCollector.nextResult().toXML().toString().replaceAll("\\r\\n|\\r|\\n", " ");

        Logger.log(Logger.EJABBERD_SERVER_REQUESTS_DEBUG, "Sent (Unauthenticated): " + xml);
        Logger.log(Logger.EJABBERD_SERVER_REQUESTS_DEBUG, "Received (Unauthenticated): " + response);
        return response;

    }


    public static String getUser() {

        if (user == null){
            user = connection.getUser().split("@")[0];
        }

        return user;
    }

    public static String getJid() {

        if (jid == null){
            jid = connection.getUser().split("/")[0];
        }

        return jid;
    }

}
