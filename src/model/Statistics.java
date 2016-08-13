package model;

import java.util.Vector;

/**
 * Created by oleh on 15.12.15.
 */
public class Statistics {
    private static Vector<Packet> packetsDelivered = new Vector<>();

    private static int servPackCount = 0;
    private static int infoPackCount = 0;
    private static int messageSent = 0;
    private static int messageReceived = 0;

    public static void addServPackCount () {
        servPackCount++;
    }

    public static void addInfoPackCount () {
        infoPackCount++;
    }

    public static void addMessageSent () {
        messageSent++;
    }

    public static void addMessageReceived () {
        messageReceived++;
    }

    public static int getInfoPackCount() {
        return infoPackCount;
    }

    public static int getServPackCount() {
        return servPackCount;
    }

    public static int getMessageSent() {
        return messageSent;
    }

    public static int getMessageReceived () {
        return messageReceived;
    }

    public static void addDelivered(Packet _packet) {
        packetsDelivered.add(_packet);
    }

    public static Vector<Packet> getDeliveredPackets() {
        return packetsDelivered;
    }

    public static double getAverageTimeDeliver () {
        int sum = 0, n = 0;

        for (Packet p: packetsDelivered) {
            if (p.getTime().getR() >= 0) {
                sum += p.getTime().getR() - p.getTime().getL();
                n++;
            }
        }

        if (n != 0)
            return (double) sum / n;
        else return 0;
    }

    public static void clear() {
        packetsDelivered = new Vector<>();
        servPackCount = 0;
        messageSent = 0;
        messageReceived = 0;
        infoPackCount = 0;
    }

}
