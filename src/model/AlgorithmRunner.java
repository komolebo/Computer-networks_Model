package model;

import controller.ControllerAlgorithm;
import controller.ControllerInfo;
import model.Modes.Pack;
import model.tools.Pair;
import view.FrameMain;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by oleh on 11.12.15.
 */
public class AlgorithmRunner {
    public static int TICK = 0;
    private static int idMain1, idMain2;

    private static Vector<Node> nodes = new Vector<>();
    private static Vector<Channel> channels = new Vector<>();

    public AlgorithmRunner() { }

    /* Main node - node with max number of connected channels */
    public static Pair<Integer, Integer> getMainNodesID(){
        Map<Integer, Integer> map = new HashMap<>();

        for (Channel channel: channels){
            int nodeID1 = channel.getNode1().getID(),
                nodeID2 = channel.getNode2().getID();

            if (map.keySet().contains(nodeID1))
                map.put(nodeID1, map.get(nodeID1) + 1);
            else map.put(nodeID1, 1);

            if (map.keySet().contains(nodeID2))
                map.put(nodeID2, map.get(nodeID2) + 1);
            else map.put(nodeID2, 1);
        }

        int _id1 = (int) map.keySet().toArray()[0];

        for (Integer i: map.keySet())
            if (map.get(i) > map.get(_id1))
                _id1 = i;

        map.remove(_id1);
        int _id2 = (int) map.keySet().toArray()[0];
        for (Integer i: map.keySet())
            if (map.get(i) > map.get(_id2))
                _id2 = i;

        return new Pair<>(_id1, _id2);
    }

    public static void startRouting(){
        // Previous settings
        TICK = 0;
        Pair mainIDs = getMainNodesID();
        idMain1 = (int) mainIDs.getL();
        idMain2 = (int) mainIDs.getR();

        new RoutingTable();
        Traffic.generateScenario();

//        System.out.println("Main nodes: " + idMain1 + " " + idMain2);

        do {
            while (Modes.getRoutingState() == Modes.RoutingState.RUNNING){
                // Firstly cast packets from traffic manager to nodes
                if (Traffic.Schedule.keySet().contains(TICK)) {
                    Vector<Packet> packets = Traffic.Schedule.get(TICK);
                    for (Packet p : packets) {
                        for (Node n : nodes)
                            if (n.getID() == p.getStartAddr()) {
                                if (p instanceof PacketLC)
                                    n.addBuffer(new PacketLC(p.getStartAddr(), p.getFinalAddr(), p.getMessage(), ((PacketLC) p).getFullSize(), ((PacketLC) p).getAmount(), p.getType(), TICK));
                                else if (p instanceof PacketDG)
                                    n.addBuffer(new PacketDG(p.getStartAddr(), p.getFinalAddr(), p.getMessage(), p.getSIZE(), ((PacketDG) p).isLast(), p.getType(), p.getTime().getL()));
                                else  n.addBuffer(new Packet(p.getStartAddr(), p.getFinalAddr(), p.getMessage(), p.getType(), TICK));
                                break;
                            }
                    }
                    Traffic.Schedule.remove(TICK);
                }

                // Move packets to channels
                for (Channel ch: channels) {
                    // If channel is busy then calculate another one
                    if (ch.isDuplex() && ch.getBuffer12() != null && ch.getBuffer21() != null ||
                            !ch.isDuplex() && (ch.getBuffer12() != null || ch.getBuffer21() != null))
                        continue;

                    Node node1 = ch.getNode1();
                    Node node2 = ch.getNode2();
                    if (!node1.isEnabled() || !node2.isEnabled()) continue;

                    // Calculate ch capacity = translate speed
                    final float capacity = ch.getCapacity();

                    // Move packets from node to ch appropriate to ch capacity
                    if (ch.isDuplex()) {
                        Packet packet;

                        // Check channel's state, if free - then configure packet setting and put it in channel
                        if (ch.getBuffer12() == null) {
                            packet = selectPacket(node1, node2);

                            if (packet != null && (isSameNetwork(node1.getID(), node2.getID()) || packet.getType() == Pack.SUPERVISOR || packet.getType() == Pack.SUPERVISOR_FOUND || packet.getType() == Pack.DG || packet.getType() == Pack.SERV_SAVED || packet.getType() == Pack.LC)) {

                                node1.getBuffer().remove(packet);
                                packet.setTicksInChannel(packet.getSIZE(), capacity);
                                if (!packet.isPathFound())
                                    packet.addPassedNode(node1.getID(), node2.getID(), ch.getWeight());
                                ch.addBuffer12(packet);
                            }
                        }

                        if (ch.getBuffer21() == null) {
                            packet = selectPacket(node2, node1);

                            if (packet != null && (isSameNetwork(node1.getID(), node2.getID()) || packet.getType() == Pack.SUPERVISOR || packet.getType() == Pack.SUPERVISOR_FOUND || packet.getType() == Pack.DG || packet.getType() == Pack.SERV_SAVED || packet.getType() == Pack.LC)) {

                                node2.getBuffer().remove(packet);
                                packet.setTicksInChannel(packet.getSIZE(), capacity);
                                if (!packet.isPathFound())
                                    packet.addPassedNode(node2.getID(), node1.getID(), ch.getWeight());
                                ch.addBuffer21(packet);
                            }
                        }
                    } // If half-duplex channel
                    else {
                        // Firstly choose node with bigger buffer
                        Node loaded = node1.getBuffer().size() >= node2.getBuffer().size() ? node1 : node2;
                        Node unloaded = loaded.getID() == node1.getID() ? node2 : node1;

                        Packet packet = selectPacket(loaded, unloaded);

                        if (packet != null && (isSameNetwork(node1.getID(), node2.getID()) || packet.getType() == Pack.SUPERVISOR || packet.getType() == Pack.SUPERVISOR_FOUND || packet.getType() == Pack.DG || packet.getType() == Pack.SERV_SAVED || packet.getType() == Pack.LC)) {
                            loaded.getBuffer().remove(packet);
                            packet.setTicksInChannel(packet.getSIZE(), capacity);
                            if (!packet.isPathFound()) packet.addPassedNode(loaded.getID(), unloaded.getID(), ch.getWeight());

                            if (loaded.getID() == node1.getID())
                                ch.addBuffer12(packet);
                            else ch.addBuffer21(packet);
                        }
                        else {
                            packet = selectPacket(unloaded, loaded);

                            if (packet != null && (isSameNetwork(node1.getID(), node2.getID()) || packet.getType() == Pack.SUPERVISOR || packet.getType() == Pack.SUPERVISOR_FOUND || packet.getType() == Pack.DG || packet.getType() == Pack.SERV_SAVED || packet.getType() == Pack.LC)) {
                                unloaded.getBuffer().remove(packet);
                                packet.setTicksInChannel(packet.getSIZE(), capacity);
                                if (!packet.isPathFound()) packet.addPassedNode(unloaded.getID(), loaded.getID(), ch.getWeight());

                                if (unloaded.getID() == node1.getID())
                                    ch.addBuffer12(packet);
                                else ch.addBuffer21(packet);
                            }
                        }
                    }
                }

                // Transmitting packages in channels
                for (Channel ch: channels) {
                    // If channel is free - no purpose to check it
                    if (ch.getBuffer12() == null && ch.getBuffer21() == null) continue;

                    Node node1 = ch.getNode1();
                    Node node2 = ch.getNode2();

                    Packet packet1 = ch.getBuffer12();
                    Packet packet2 = ch.getBuffer21();

                    if (packet1 != null) {
                        packet1.decTicksInChannel();
                        // The moment when package has reached next node
                        if (packet1.getTicksLeft() == 0) {
                            receivePackages(node2, packet1);
                            ch.addBuffer12(null);
                        }
                    }
                    if (packet2 != null) {
                        packet2.decTicksInChannel();
                        if (packet2.getTicksLeft() == 0) {
                            receivePackages(node1, packet2);
                            ch.addBuffer21(null);
                        }
                    }
                }
                if (TICK % 50 == 0) checkDeadPackages();

                if (Traffic.STAGE_TICKS.contains(TICK)) {
                    Modes.setUserMode(Modes.User.VISIBLE);
                    FrameMain.panelTool.refreshButtonVisibility();
                    Modes.setRoutingState(Modes.RoutingState.PAUSED);
                }

                if (Modes.getUserMode() == Modes.User.VISIBLE) {
                    ControllerAlgorithm.delay();
                    ControllerInfo.refreshPanelProgress();
                }
                TICK++;
            }
            ControllerAlgorithm.deadDelay();
        }while (Modes.getRoutingState() != Modes.RoutingState.STOPPED);

        for (Channel channel: channels) {
            channel.addBuffer12(null);
            channel.addBuffer21(null);
        }
        for (Node node: nodes)
            node.getBuffer().clear();

        RoutingTable.clear();
        Statistics.clear();

        ControllerAlgorithm.delay();
        FrameMain.controllerShow.updateUI();
    }

    private static void checkDeadPackages() {
        for (Node node: nodes)
            for (int i = 0; i < node.getBuffer().size(); i++){
                Packet p = node.getBuffer().get(i);
                if (p.getType() == Pack.THERE_AND_BACK_ROUT || p.getType() == Pack.THERE_AND_BACK_SAVED || p.getType() == Pack.SUPERVISOR) {
                    if (p.getPassedNodesList().size() <= 1) continue;
                    // Collect neighbours id list
                    Vector<Integer> neighbours = new Vector<>();
                    for (Channel c: channels)
                        if (c.getNode1().getID() == node.getID() || c.getNode2().getID() == node.getID())
                            neighbours.add(c.getNode1().getID() == node.getID() ? c.getNode2().getID() : node.getID());

                    boolean noWay = true;
                    for (Integer neighbour: neighbours)
                        if (isSameNetwork(neighbour, p.getPassedNodesList().get(0)))
                            if (!p.getPassedNodesList().contains(neighbour)) {
                                noWay = false;
                                break;
                            }

                    if (noWay) {
//                        System.out.println("Deleting in " + node.getID());
                        node.getBuffer().remove(p);
                        i--;
                    }
                }
            }
    }

    private static void receivePackages(Node node, Packet p) {
        // If package arrived in main.json node - skip (values already taken)
        if ((p.getFinalAddr() == idMain1 && node.getID() == idMain1 || p.getFinalAddr() == idMain2 && node.getID() == idMain2) && p.getType() != Pack.LC && p.getType() != Pack.DG){
            // If supervisor
            if (p.getType() == Pack.SUPERVISOR) {
                RoutingTable.update(p.getPassedNodes());
                Packet newPacket = new Packet(p.getFinalAddr(), p.getStartAddr(), "Back to my main.json", Pack.SUPERVISOR_FOUND, TICK);
                newPacket.setPathFound(true);
                newPacket.setPassedNodes(new Pair<>(p.getPassedNodesList(), p.getWeightPassed()));
                newPacket.remPassedNode(node.getID());
                node.addBuffer(newPacket);
                Statistics.addServPackCount();
            }
//            else
//                System.out.println("Main node received info");
        }
        else if (p.getFinalAddr() == node.getID() && (p.getType() == Pack.SERV_ROUT || p.getType() == Pack.SERV_SAVED))
            System.out.println("One direction message received");
        else if (p instanceof PacketDG) {
            PacketDG pDG = (PacketDG) p;
            switch (pDG.getState()) {
                case INFO:
                    if (pDG.getFinalAddr() == node.getID()) {
                        pDG = new PacketDG(p.getFinalAddr(), p.getStartAddr(), "Back 1", pDG.getSIZE(), pDG.isLast(), Pack.LC, pDG.getTime().getL());
                        pDG.send();
                    }
                    Statistics.addInfoPackCount();
                    node.addBuffer(pDG);
                    break;
                case SERVICE:
                    if (pDG.getFinalAddr() == node.getID()) {
                        if (pDG.isLast()) {
                            Statistics.addMessageSent();
                            Statistics.addMessageReceived();
                        }
                        pDG.end();
                        Statistics.addDelivered(pDG.reached(TICK));
                    }
                    else {
                        node.addBuffer(pDG);
                    }
                    Statistics.addServPackCount();
                    break;
            }
        }
        // If Logic connection mode
        else if (p instanceof PacketLC) {
            PacketLC pLC = (PacketLC) p;
            switch (pLC.getState()) {
                case CONNECTING_ASK:
                    // If final
                    if (pLC.getFinalAddr() == node.getID()) {
                        pLC = new PacketLC(p.getFinalAddr(), p.getStartAddr(), "Back 1", pLC.getFullSize(), pLC.getAmount(), Pack.LC, TICK);
                        pLC.connected();
                        Statistics.addMessageSent();
                    }
                    node.addBuffer(pLC.started(TICK));
                    Statistics.addServPackCount();
                    break;
                case CONNECTING_ANSWER:
                    if (pLC.getFinalAddr() == node.getID()) {
                        pLC = new PacketLC(p.getFinalAddr(), p.getStartAddr(), "Back 2", pLC.getFullSize(), pLC.getAmount(), Pack.LC, pLC.getTime().getL());
                        pLC.connectAnswered();
                        pLC.setAmount();
                    }
                    node.addBuffer(pLC);
                    Statistics.addServPackCount();
                    break;
                case TRANSMITTING_ASK:
                    if (pLC.getFinalAddr() == node.getID()) {
                        pLC = new PacketLC(p.getFinalAddr(), p.getStartAddr(), "Back 3", pLC.getFullSize(), pLC.getAmount(), Pack.LC, pLC.getTime().getL());
                        pLC.transmitted();
                    }
                    node.addBuffer(pLC);
                    Statistics.addInfoPackCount();
                    break;
                case TRANSMITTING_ANSWER:
                    if (pLC.getFinalAddr() == node.getID()) {
                        pLC = new PacketLC(p.getFinalAddr(), p.getStartAddr(), "Back 4", pLC.getFullSize(), pLC.getAmount(), Pack.LC, pLC.getTime().getL());
                        pLC.decAmount();

                        if (pLC.getAmount() <= 0) {
                            pLC.transmitAnswered();
                            Statistics.addMessageReceived();
                            Statistics.addDelivered(pLC.reached(TICK));
                        }
                        else pLC.connectAnswered();
                    }
                    node.addBuffer(pLC);
                    Statistics.addInfoPackCount();
                    break;
                case BREAKING_ASK:
                    if (pLC.getFinalAddr() == node.getID()) {
                        pLC = new PacketLC(p.getFinalAddr(), p.getStartAddr(), "Back 5", Pack.LC, TICK);
                        pLC.broken();
                    }
                    node.addBuffer(pLC);
                    Statistics.addServPackCount();
                    break;
                case BREAKING_ANSWER:
                    if (pLC.getFinalAddr() == node.getID()) {
//                        System.out.println("LC completed");
                    }
                    else node.addBuffer(pLC);
                    break;
            }
        }
        // If service message arrived to final destination
        else if ((p.getType() == Pack.THERE_AND_BACK_SAVED || p.getType() == Pack.THERE_AND_BACK_ROUT) && p.getFinalAddr() == node.getID()) {
            RoutingTable.update(p.getPassedNodes());   // here we update routing table

            // Return to start node
            Packet newPacket = new Packet(node.getID(), isNetworkN1(node.getID()) ? idMain1 : idMain2, "Return to main.json",
                    p.getType() == Pack.THERE_AND_BACK_SAVED ? Pack.SERV_SAVED : Pack.SERV_ROUT, TICK);
            newPacket.setPathFound(true); // Packet will be moving by route from memory
            newPacket.setPassedNodes(new Pair<>(new Vector<>(p.getPassedNodesList()), p.getWeightPassed()));
            newPacket.remPassedNode(node.getID()); // Except the last node - we were there

            node.addBuffer(newPacket);
            Statistics.addServPackCount();
        }
        // If package "too old"
        else if (p.getWeightPassed() > 50 && !p.isPathFound()) {}
//            System.out.println("Deleting packet");
        // If package moves by route from memory - just delete invited node
        else if (p.getType() == Pack.SERV_SAVED || p.getType() == Pack.SERV_ROUT || p.getType() == Pack.SUPERVISOR_FOUND){
            p.remPassedNode(node.getID());
            node.addBuffer(p);
        }
        // If informative message received
//        else if (p.getType() == Pack.DG) {
//            node.addBuffer(p);
//            Statistics.addInfoPackCount();
//        }

        // If service message to translate
        else if (p.getType() == Pack.THERE_AND_BACK_SAVED || p.getType() == Pack.THERE_AND_BACK_ROUT || p.getType() == Pack.SUPERVISOR) {
            for (int i = 0; i < getNodeDegree(node.getID()); i++) {
                Packet newPacket = new Packet(p.getStartAddr(), p.getFinalAddr(), p.getMessage(), p.getType(), TICK);
                newPacket.setPassedNodes(new Pair<>(new Vector<>(p.getPassedNodesList()), p.getWeightPassed()));
                node.addBuffer(newPacket);
                Statistics.addServPackCount();
            }
        }
    }

    private static Packet selectPacket(Node node1, Node node2) {
        for (Packet p: node1.getBuffer()) {
            if (p.getFinalAddr() == node2.getID() ||
                    p.getType() == Pack.SERV_ROUT && RoutingTable.routOK(node1.getID(), p.getFinalAddr(), node1.getID(), node2.getID()) ||
                    (p.getType() == Pack.SERV_SAVED || p.getType() == Pack.SUPERVISOR_FOUND) && p.getPassedNodesList().lastElement() == node2.getID() ||
                    (p.getType() == Pack.THERE_AND_BACK_ROUT || p.getType() == Pack.THERE_AND_BACK_SAVED) && !p.getPassedNodesList().contains(node2.getID()) ||
                    (p.getType() == Pack.DG || p.getType() == Pack.LC) && RoutingTable.isInfoOK(p.getStartAddr(), p.getFinalAddr(), node1.getID(), node2.getID()) ||
                    p.getType() == Pack.SUPERVISOR && !p.isPathFound() && !p.getPassedNodesList().contains(node2.getID()))
                return p;
        }
        return null;
    }

    public static Vector<Node> getNodes() {
        return nodes;
    }

    public static void setNodes(Vector<Node> _nodes) {
        nodes = _nodes;
    }

    public static int addNode() {
        Node node = new Node();
        nodes.add(node);

        return node.getID();
    }

    public static void deleteNode(int id){
        Node node = getNodeByID(id);

        if (node != null) nodes.remove(node);
    }

    private static Node getNodeByID(int id) {
        Node node = null;

        for (Node n: nodes)
            if (n.getID() == id)
                node = n;
        return node;
    }

    public static int getNodeDegree(int _id) {
        int number = 0;

        for (Channel ch: channels)
            if (ch.getNode1().getID() == _id || ch.getNode2().getID() == _id)
                number++;

        return number - 1;
    }

    public static int getNodeIndexByID(int _id) {
        for (Node node: nodes)
            if (node.getID() == _id)
                return nodes.indexOf(node);

        return -1;
    }

    public static Vector<Integer> getNodesList() {
        Vector<Integer> list = new Vector<>();

        for (Node n: nodes)
            list.add(n.getID());

        return list;
    }


    public static Vector<Channel> getChannels() {
        return channels;
    }

    public static void setChannels(Vector<Channel> _channels) {
        channels = _channels;
    }

    public static int addChannel(int weight, int id1, int id2, boolean type) {
        channels.add(new Channel(weight, getNodeByID(id1), getNodeByID(id2), type));

        return channels.get(channels.size() - 1).getID();
    }

    public static void deleteChannel(int _id) {
        for (int i = 0; i < channels.size(); i++)
            if (channels.get(i).getID() == _id){
                channels.remove(channels.get(i));
                return;
            }
    }


    public static boolean isNetworkN1(int _id1) {
        return _id1 <= 12;
    }

    public static boolean isSameNetwork(int _id1, int _id2) {
        return _id1 <= 12 && _id2 <= 12 || _id1 > 12 && _id2 > 12;
    }

    public static boolean isMain(int _id) {
        return _id == idMain1 || _id == idMain2;
    }

    public static int getMainID1() {
        return idMain1;
    }

    public static int getMainID2(){
        return idMain2;
    }
}
