package controller;

import model.Channel;
import model.Node;
import view.FrameMain;
import view.elements.Settings;

import java.awt.*;

/**
 * Created by oleh on 29.12.15.
 */
public class ControllerSettings {
// Algorithm settings
    public static int getInfoPackageSize() {
        return Settings.INFO_PACKAGE_SIZE;
    }

    public static void setInfoPackageSize(int _value) {
        Settings.INFO_PACKAGE_SIZE = _value;
    }

    public static int getServPackageSize() {
        return Settings.SERV_PACKAGE_SIZE;
    }

    public static void setServPackageSize(int _value) {
        Settings.SERV_PACKAGE_SIZE = _value;
    }

// Graphical settings
    public static int getNodeRadius() {
        return Settings.NODE_RADIUS;
    }

    public static void setNodeRadius(int _value) {
        Settings.NODE_RADIUS = _value;
        FrameMain.controllerShow.updateUI();
    }

    public static int getPacketRadius () {
        return Settings.PACKET_RADIUS;
    }

    public static void setPacketRadius(int _value) {
        Settings.PACKET_RADIUS = _value;
        FrameMain.controllerShow.updateUI();
    }

    // Colors
    public static Color getColor (Object obj) {
        if (obj instanceof Node)
            return Settings.NODE_COLOR;
        else if (obj instanceof Channel)
            return Settings.CHANNEL_COLOR;
        return null;
    }
}
