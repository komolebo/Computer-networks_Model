package controller;

import model.FileManager;
import model.Modes;
import org.json.simple.parser.ParseException;
import view.FrameData;
import view.FrameMain;
import view.FrameSettings;
import view.FrameTraffic;

import java.io.IOException;

/**
 * Created by oleh on 25.12.15.
 */
public class ControllerTool {
    public static void deleteChannel(int _from, int _to) {
        FrameMain.controllerShow.deleteChannel(_from, _to);
        FrameMain.controllerShow.deleteChannel(_to, _from);
    }

    public static void addChannel(int _from, int _to, int _weight, boolean _duplex) {
        FrameMain.controllerShow.addChannel(_from, _to, _weight, _duplex);
    }

    public static void dump(String _name) {
        try {
            FileManager.dump(_name);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void read(String _name) {
        try {
            FileManager.read(_name);
            FrameMain.controllerShow.updateUI();
        } catch (IOException | ParseException e) { e.printStackTrace(); }

    }

    public static void run () {
        if (Modes.getRoutingState() == Modes.RoutingState.PAUSED)
            Modes.setRoutingState(Modes.RoutingState.RUNNING);
        else if (Modes.getRoutingState() != Modes.RoutingState.RUNNING) {
            Modes.setRoutingState(Modes.RoutingState.RUNNING);

            ControllerAlgorithm myRunnable = new ControllerAlgorithm();
            Thread t = new Thread(myRunnable);
            t.start();
        }
    }

    public static void pause () {
        if (Modes.getRoutingState() != Modes.RoutingState.STOPPED)
            Modes.setRoutingState(Modes.RoutingState.PAUSED);
    }

    public static void stop () {
        Modes.setRoutingState(Modes.RoutingState.STOPPED);
    }

    public static void openData () {
        new FrameData(500, 500);
    }

    public static void openSettings () {
        new FrameSettings();
    }

    public static void openTraffic () {
        new FrameTraffic();
    }

    public static void setUserMode(String _text) {
        if (_text.equals("Visible  "))
            Modes.setUserMode(Modes.User.INVISIBLE);
        else  Modes.setUserMode(Modes.User.VISIBLE);

        ControllerInfo.refreshPanelProgress();
    }

    public static boolean isVisibleToUser() {
        return Modes.getUserMode() == Modes.User.VISIBLE;
    }
}
