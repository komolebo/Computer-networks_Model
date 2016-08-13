package test;

import model.AlgorithmRunner;
import model.Node;
import model.tools.Pair;

import java.util.*;

/**
 * Created by oleh on 11.12.15.
 */
public class RoutingTableBackup {
    public static Map<Pair<Integer, Integer>, Pair<Vector<Integer>, Integer>> Table = new HashMap<>();

    public RoutingTableBackup() {
        System.out.println("Routing table constructor called");
    }

//    public static boolean routOK (Pair<Integer, Integer> _way, int _nextID) {
//        for (Pair<Integer, Integer> pair: Table.keySet())
//            if (pair.getL() == _way.getL() && pair.getR() == _way.getR())
//                if (Table.get(pair).getL().contains(_nextID))
//                    return true;
//
//        return false;
//    }

    private static void tryFindWay(int _idFrom, int _idTo) {
        Set<Integer> setLeft = new HashSet<>(), setRight = new HashSet<>();

        for (Pair<Integer, Integer> pair: Table.keySet()) {
            if (pair.getR() == _idFrom) setLeft.add(pair.getL());
            if (pair.getL() == _idFrom) setLeft.add(pair.getR());
            if (pair.getR() == _idTo) setRight.add(pair.getL());
            if (pair.getL() == _idTo) setRight.add(pair.getR());

            Set<Integer> intersection = new HashSet<>(setLeft);
            intersection.retainAll(setRight);

            if (!intersection.isEmpty()) {
                int connectorID = intersection.iterator().next();

                Vector<Integer> way = new Vector<>();
                way.add(_idFrom);
                way.add(connectorID);
                way.add(_idTo);

                Table.put(new Pair<>(_idFrom, _idTo), new Pair<>(way, 11));

                return;
            }
        }
    }

    public static void calcTransitive() {
        // For each node: go to all nodes and find what ways we didn't calculate
        for (Node node: AlgorithmRunner.getNodes()) {
            for (Node nodeTo: AlgorithmRunner.getNodes()) {
                // Remove recycling
                if (node.getID() != nodeTo.getID()) {
                    for (Pair fromTo: Table.keySet()) {
                        // Check if transitive connection already exists
                        if (fromTo.getL() == node.getID() && fromTo.getR() == nodeTo.getID() ||
                                fromTo.getL() == nodeTo.getID() && fromTo.getR() == node.getID())
                            break;
                        // If connection doesn't exist - calc transitive way
                        tryFindWay(node.getID(), nodeTo.getID());
                    }
                }
            }
        }
    }

    public static boolean routOK (int _destID, int _nextID, Set<Integer> _whereHaveBeen) {
        if (_whereHaveBeen.contains(_nextID)) return false;

        // if there is direct way
        for (Pair key: Table.keySet())
            if (key.getL() == _destID && key.getR() == _nextID || key.getL() == _nextID && key.getR() == _destID)
                return true;
        return false;
//        return Table.keySet().contains(new Pair<>(_nextID, _destID)) ||
//                Table.keySet().contains(new Pair<>(_destID, _nextID));
    }

    public static void updateTable(Vector<Pair <Pair<Integer, Integer>, Integer>> _wayInfo) {
        Vector<Integer> nodesList = new Vector<>();
        Vector<Integer> weights = new Vector<>();

        for (Pair<Pair<Integer, Integer>, Integer> local: _wayInfo) {
            int i = local.getL().getL();
            int j = local.getL().getR();
            int weight = local.getR();

            if (!nodesList.contains(i)) nodesList.add(i);
            nodesList.add(j);

            weights.add(weight);
        }

        for (int i = 0; i < nodesList.size() - 1; i++)
            for (int j = i + 1; j < nodesList.size(); j++) {
                int sum = 0;
                Vector<Integer> routList = new Vector<>();
                for (int k = i; k <= j; k++) routList.add(nodesList.get(k));
                for (int k = i; k < j; k++) sum += weights.get(k);

                Table.put(new Pair<>(nodesList.get(i), nodesList.get(j)), new Pair<>(routList, sum));
            }
    }
}
