package model;

import model.tools.Pair;

import java.util.*;

/**
 * Created by oleh on 11.12.15.
 */
public class RoutingTable {
    public static Map<Pair<Integer, Integer>, Vector<Pair<Vector<Integer>, Integer>>> Table = new HashMap<>();

    public RoutingTable() {
        System.out.println("Routing table constructor called");
    }

    public static void update(Pair<Vector<Integer>, Integer> _info) {
        int from = _info.getL().get(0);
        int to = _info.getL().get(_info.getL().size() - 1);

        Vector<Pair<Vector<Integer>, Integer>> updatedInfo = getPairByID(from, to);

        if (updatedInfo != null)
            updatedInfo.add(new Pair<>(_info.getL(), _info.getR()));
        else {
            Vector<Pair<Vector<Integer>, Integer>> newInfo = new Vector<>();
            newInfo.add(new Pair<>(_info.getL(), _info.getR()));
            Table.put(new Pair<>(from, to), newInfo);
        }
    }

    public static Pair<Vector<Integer>, Integer> getMinWay(int _from, int _to) {
        if (_from == _to) {
            Vector<Integer> v = new Vector<>(1);
            v.add(_from);
            return new Pair<>(v, 0);
        }

        Vector<Pair<Vector<Integer>, Integer>> ways = getPairByID(_from, _to);
        if (ways == null) return null;

        Vector<Integer> bestWay = ways.get(0).getL();
        int bestWeight = ways.get(0).getR();

        for (Pair<Vector<Integer>, Integer> way: ways) {
            if (way.getR() < bestWeight) {
                bestWay = way.getL();
                bestWeight = way.getR();
            }
        }

        return new Pair<>(bestWay, bestWeight);
    }

    public static Pair<Vector<Integer>, Integer> getMinTransitWay (int _from, int _to) {
        if (_from == _to) {
            Vector<Integer> v = new Vector<>(1);
            v.add(_from);
            return new Pair<>(v, 0);
        }

        Vector<Pair<Vector<Integer>, Integer>> ways = getPairByID(_from, _to);
        if (ways == null) return null;

        Vector<Integer> bestWay = ways.get(0).getL();
        int bestNum = ways.get(0).getL().size();
        int bestWeight = ways.get(0).getR();

        for (Pair<Vector<Integer>, Integer> way: ways) {
            if (way.getL().size() < bestNum) {
                bestNum = way.getL().size();
                bestWay = way.getL();
                bestWeight = way.getR();
            }
        }

        return new Pair<>(bestWay, bestWeight);
    }

    public static boolean routOKTransitive (int _idFrom, int _idTo, int _cur, int _next) {
        int idMain1 = AlgorithmRunner.getMainID1();
        int idMain2 = AlgorithmRunner.getMainID2();
        int idMainFrom, idMainTo;

        // _idfrom - from networkX, _idTo - from networkY
        if (AlgorithmRunner.isSameNetwork(_idFrom, idMain1)) {
            idMainFrom = idMain1;
            idMainTo = idMain2;
        }else{
            idMainFrom = idMain2;
            idMainTo = idMain1;
        }

        Pair<Vector<Integer>, Integer> NodeToMain = getMinWay(_idFrom, idMainFrom);
        if (NodeToMain == null || NodeToMain.getL().size() < 1) return false;
        Pair<Vector<Integer>, Integer> MainToMain = getMinWay(idMain1, idMain2);
        if (MainToMain == null || MainToMain.getL().size() < 1) return false;
        Pair<Vector<Integer>, Integer> MainToNode = getMinWay(_idTo, idMainTo);
        if (MainToNode == null || MainToNode.getL().size() < 1) return false;
        if (!NodeToMain.getL().contains(_next) && !MainToMain.getL().contains(_next) && !MainToNode.getL().contains(_next)) return false;

        if (NodeToMain.getL().get(0) != _idFrom) Collections.reverse(NodeToMain.getL());
        if (MainToMain.getL().get(0) != idMainFrom) Collections.reverse(MainToMain.getL());
        if (MainToNode.getL().get(0) != idMainTo) Collections.reverse(MainToNode.getL());

        Vector<Integer> builtWay = new Vector<>();
        List<Integer> MainToMainCopy = new Vector<>(MainToMain.getL());

        // If regional way contains local way from node to main.json
        if (MainToMainCopy.contains(_idFrom)) {
            NodeToMain = new Pair<>(new Vector<Integer>(), 0);
            // 6 12 1 2 5 4 3
            MainToMainCopy = MainToMainCopy.subList(MainToMainCopy.indexOf(_idFrom) + 1, MainToMainCopy.size());
        } else {
            // Check if there is optimization chance    6 1 2 3 4 5    /  6 1 4
            for (int i = 0; i < NodeToMain.getL().size(); i++)
                if (MainToMainCopy.contains(NodeToMain.getL().get(i))) {
                    MainToMainCopy = MainToMainCopy.subList(MainToMainCopy.indexOf(NodeToMain.getL().get(i)), MainToMainCopy.size());
                    NodeToMain = new Pair<>(new Vector<>(NodeToMain.getL().subList(0, i)), 0);
                    break;
                }
        }

        if (MainToMainCopy.contains(_idTo)) {
            MainToNode = new Pair<>(new Vector<Integer>(), 0);
            // 6 12 1 2 5 4 3
            MainToMainCopy = MainToMainCopy.subList(0, MainToMainCopy.indexOf(_idTo) + 1);
        } else {
            for (int i = 0; i < MainToNode.getL().size() - 1; i++) {
                if (MainToMainCopy.contains(MainToNode.getL().get(i))) {
                    MainToMainCopy = MainToMainCopy.subList(0, MainToMainCopy.indexOf(MainToNode.getL().get(i)));
                    MainToNode = new Pair<>(new Vector<>(MainToNode.getL().subList(i, MainToNode.getL().size())), 0);
                }
            }
        }

        builtWay.addAll(NodeToMain.getL());
        builtWay.addAll(MainToMainCopy);
        builtWay.addAll(MainToNode.getL());

        int indexTo = builtWay.indexOf(_idTo);
        int indexCur = builtWay.indexOf(_cur);
        int indexNext = builtWay.indexOf(_next);

        return indexNext >= 0 && Math.abs(indexTo - indexNext) < Math.abs(indexTo - indexCur);
    }

    public static boolean routOK(int _idFrom, int _idTo, int _cur, int _next) {
        Pair<Vector<Integer>, Integer> bestWay = getMinWay(_idFrom, _idTo);

        if (bestWay == null || !bestWay.getL().contains(_next)) return false;


        int dest = bestWay.getL().indexOf(_idTo);
        int next = bestWay.getL().indexOf(_next);
        int cur = bestWay.getL().indexOf(_cur);

        return next >= 0 && Math.abs(dest - next) < Math.abs(dest - cur);
    }

    public static boolean isInfoOK(int _idFrom, int _idTo, int _cur, int _next) {
        if (AlgorithmRunner.isSameNetwork(_idFrom, _idTo))
            return routOK(_idFrom, _idTo, _cur, _next);
        else return routOKTransitive(_idFrom, _idTo, _cur, _next);
    }

    public static void clear() {
        Table = new HashMap<>();
    }

    public static void output() {
        System.out.println("Keyset has " + Table.keySet().size());

        for (Pair<Integer, Integer> i: Table.keySet()) {
            System.out.print(i.getL() + "->" + i.getR() + ":  ");
            for (Pair<Vector<Integer>, Integer> ii: Table.get(i))
                System.out.print(".." + ii.getL() + "with" + ii.getR());
            System.out.println();
        }
    }

    private static Vector<Pair<Vector<Integer>, Integer>> getPairByID(int _from, int _to) {
        for (Pair<Integer, Integer> i: Table.keySet())
            if (i.getL() == _from && i.getR() == _to || i.getL() == _to && i.getR() == _from)
                return Table.get(i);

        return null;
    }
}
