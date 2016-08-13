package model;

import model.tools.Pair;
import model.Modes.Pack;

import java.util.*;

public class Traffic {
    public static Map<Integer, Vector<Packet>> Schedule = new HashMap<Integer, Vector<Packet>>();
    public static final int MAX_NEIGHBOUR = 2500;
    public static Set<Integer> STAGE_TICKS = new HashSet<>();
    private static int idMain1 = -1;
    private static int idMain2 = -1;

    public Traffic() {  //101275
        System.out.println("Traffic constructor called");

        Pair<Integer, Integer> mainNodes = AlgorithmRunner.getMainNodesID();
        idMain1 = mainNodes.getL();
        idMain2 = mainNodes.getR();
    }

    public static void generateScenario() {
        int t = 0;

        t = mainToNodeAndBack(t);
        STAGE_TICKS.add(t);
        t = nodeToNodeAndMain(t);
        STAGE_TICKS.add(t);

        clear();
        mainToMain(t);
        STAGE_TICKS.add(t);
        STAGE_TICKS.add(400000);

//
//            // 3. Main node sends to casual node route info
//            t = (MAX_NEIGHBOUR + 1) * nodes.size() / 2 + nodes.size() * nodes.size() * MAX_NEIGHBOUR;
//            for (Node node: nodes) {
//                if (node.getID() != idMain1 && node.getID() != idMain2) {
//                    if (isSameNetwork(node.getID(), idMain1))
//                        v.add(new Packet(idMain1, node.getID(), "", Pack.SERV_ROUT));
//                    else v.add(new Packet(idMain2, node.getID(), "", Pack.SERV_ROUT));
//                }
//            }
//            Schedule.put(t, v);
//
//            // 4. Info messages come in

//            t = 100000;
//            v = new Vector<>();
//
//            for (Node n : nodes) {
//                for (int i = 0; i < 10; i++) {
//                    if (n.getID() != idMain1 && n.getID() != idMain2) {
//                        int index;
//                        if (isNetworkN1(n.getID()))
//                            index = getNodeIndexByID(ThreadLocalRandom.current().nextInt(1, 12));
//                        else index = getNodeIndexByID(ThreadLocalRandom.current().nextInt(13, 24));
//
//                        int idTo = nodes.get(index).getID();
//                        int idFrom = n.getID();
//
//
//                        if (idTo != idFrom)
//                            v.add(new Packet(idFrom, idTo, "", Pack.DG));
//                    }
//                }
//            }
//            Schedule.put(t, v);
    }

    public static void clear() {
        for (Node node : AlgorithmRunner.getNodes())
            node.getBuffer().clear();
    }

    // 1. Firstly generate signals main.json->node and node->main.json
    private static int mainToNodeAndBack(int t) {
        Vector<Packet> v;
        int ind1 = 0, ind2 = 0;
        for (Node n : AlgorithmRunner.getNodes()) {
            v = new Vector<>();

            if (n.getID() != idMain1 && n.getID() != idMain2)
                if (AlgorithmRunner.isNetworkN1(n.getID())) {
                    int tick = t + ind1 * MAX_NEIGHBOUR + 2;
                    for (int j = 0; j < AlgorithmRunner.getNodeDegree(AlgorithmRunner.isNetworkN1(n.getID()) ? idMain1 : idMain2) + 1; j++)
                        v.add(new Packet(idMain1, n.getID(), "Form way to centre1", Pack.THERE_AND_BACK_SAVED, tick));
                    ind1++;

                    Schedule.put(tick, v);
                } else {
                    int tick = t + ind2 * MAX_NEIGHBOUR;
                    for (int j = 0; j < AlgorithmRunner.getNodeDegree(AlgorithmRunner.isNetworkN1(n.getID()) ? idMain1 : idMain2) + 1; j++)
                        v.add(new Packet(idMain2, n.getID(), "Form way to centre2", Pack.THERE_AND_BACK_SAVED, tick));
                    ind2++;

                    Schedule.put(tick, v);
                }
        }
        return (MAX_NEIGHBOUR + 1) * AlgorithmRunner.getNodes().size() / 2 + 2 * MAX_NEIGHBOUR;
    }

    // 2. From each node send info to every node and back to main.json
    private static int nodeToNodeAndMain(int t) {
        final int t0 = t;

        // Simultaneously from both networks
        for (Node nIN : AlgorithmRunner.getNodes())
            for (Node nOUT : AlgorithmRunner.getNodes()) {
                if (!AlgorithmRunner.isMain(nIN.getID()) && !AlgorithmRunner.isMain(nOUT.getID()) && AlgorithmRunner.isNetworkN1(nIN.getID()) && AlgorithmRunner.isNetworkN1(nOUT.getID()) && nIN.getID() != nOUT.getID()) {
                    for (int i = 0; i < AlgorithmRunner.getNodeDegree(nIN.getID()) + 1; i++)
                        addPacket(t, new Packet(nIN.getID(), nOUT.getID(), "Node search for node", Pack.THERE_AND_BACK_ROUT, t));
                    t += MAX_NEIGHBOUR;
                }
            }
        t = t0;
        for (Node nIN : AlgorithmRunner.getNodes())
            for (Node nOUT : AlgorithmRunner.getNodes()) {
                if (!AlgorithmRunner.isMain(nIN.getID()) && !AlgorithmRunner.isMain(nOUT.getID()) && !AlgorithmRunner.isNetworkN1(nIN.getID()) && !AlgorithmRunner.isNetworkN1(nOUT.getID()) && nIN.getID() != nOUT.getID()) {
                    for (int i = 0; i < AlgorithmRunner.getNodeDegree(nIN.getID()) + 1; i++)
                        addPacket(t, new Packet(nIN.getID(), nOUT.getID(), "Node search for node", Pack.THERE_AND_BACK_ROUT, t));
                    t += MAX_NEIGHBOUR;
                }
            }
        return t + 2 * MAX_NEIGHBOUR;
    }

    // N. Adopting regional networks
    private static int mainToMain(int _t) {
        for (int i = 0; i < AlgorithmRunner.getNodeDegree(idMain1) + 1; i++)
            addPacket(_t, new Packet(idMain1, idMain2, "Regional synchronize", Pack.SUPERVISOR, _t));
        return _t + 2 * MAX_NEIGHBOUR;
    }

    public static void addPacket(Integer _t, Packet _packet) {
        Vector<Packet> v = Schedule.get(_t);
        if (v == null) v = new Vector<>();

        // Check if there is node ID
        v.add(_packet);

        Schedule.put(_t, v);
    }
}