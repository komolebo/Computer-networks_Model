package controller;

import model.*;
import view.FrameMain;
import view.elements.GPacket;
import view.elements.Settings;

import java.util.Vector;

/**
 * Created by oleh on 14.12.15.
 */
public class ControllerAlgorithm implements Runnable{
    public static void startAlgorithm() {
        AlgorithmRunner.startRouting();
    }

    public static void syncMtoVPackets() {
        Vector<GPacket> gPackets = new Vector<>();

        for (Channel channel: AlgorithmRunner.getChannels()) {
            if (channel.getBuffer12() != null)
                gPackets.add(drawPackOnChannel(channel, channel.getBuffer12(), true));
            if (channel.getBuffer21() != null)
                gPackets.add(drawPackOnChannel(channel, channel.getBuffer21(), false));
        }

        for (Node node: AlgorithmRunner.getNodes())
            for(Packet packet: node.getBuffer())
                gPackets.add(getPackFromNode(node, packet));

        FrameMain.controllerShow.setPackets(gPackets);
    }

    private static GPacket drawPackOnChannel(Channel channel, Packet packet, boolean from12) {
        int id1 = channel.getNode1().getID();
        int id2 = channel.getNode2().getID();

        int x1 = FrameMain.controllerShow.getNodeByID(id1).getPosition().width;
        int y1 = FrameMain.controllerShow.getNodeByID(id1).getPosition().height;
        int x2 = FrameMain.controllerShow.getNodeByID(id2).getPosition().width;
        int y2 = FrameMain.controllerShow.getNodeByID(id2).getPosition().height;

        int lenX = x2 - x1;
        int lenY = y2 - y1;

        double times = (double) packet.getTicksLeft() / ((float)packet.getSIZE() / channel.getCapacity());

        if (from12) times = 1 - times;

        if (times >= 1 || times < 0)
            System.out.println("Time too big");

        return new GPacket((int)(x1 + lenX * times), (int)(y1 + lenY * times), packet.getType());
    }

    private static GPacket getPackFromNode(Node node, Packet packet) {
        int id = node.getID();

        int x = FrameMain.controllerShow.getNodeByID(id).getPosition().width;
        int y = FrameMain.controllerShow.getNodeByID(id).getPosition().height;

        return new GPacket(x, y, packet.getType());
    }

    public static void delay() {
        syncMtoVPackets();
        FrameMain.controllerShow.updateUI();

        try {
            Thread.sleep(Settings.DELAY);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void deadDelay() {
        try {
            Thread.sleep(Settings.DELAY);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void setNodeEnabled(int _id, boolean _enabled) {
        int index = AlgorithmRunner.getNodeIndexByID(_id);
        AlgorithmRunner.getNodes().get(index).setEnabled(_enabled);
    }

    @Override
    public void run() {
        startAlgorithm();
    }
}
