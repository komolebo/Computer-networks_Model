package model;

import model.Modes.Pack;
import model.tools.Pair;
import view.elements.Settings;

import java.util.Vector;

/**
 * Created by oleh on 11.12.15.
 */
public class Packet extends AbstractObject {
    private final int startAddr, finalAddr;
    private final Pack type;
    private final String message;
    private final int SIZE; //

    private Pair<Integer, Integer> time;
    private int weightPassed = 0;
    private boolean pathFound = false;
    private int ticksInChannel;

    private Vector<Integer> passedNodesList = new Vector<>();

    public Packet(int _startAddr, int _finalAddr, String _message, Pack _type, int _tick){
        super();

//        SIZE = ThreadLocalRandom.current().nextInt(Settings.MAX_PACKAGE_SIZE / 10, Settings.MAX_PACKAGE_SIZE);
        SIZE = Settings.SERV_PACKAGE_SIZE;

        startAddr = _startAddr;
        finalAddr = _finalAddr;
        message = _message;
        type = _type;

        time = new Pair<>(_tick, -1);
    }

    public Packet(int _startAddr, int _finalAddr, String _message, int _size, Pack _type, int _tick){
        super();

        if (_size <= Settings.SERV_PACKAGE_MIN_SIZE) _size = Settings.SERV_PACKAGE_MIN_SIZE;
        SIZE = _size;

        startAddr = _startAddr;
        finalAddr = _finalAddr;
        message = _message;
        type = _type;

        time = new Pair<>(_tick, -1);
    }

    public Packet(int _startAddr, int _finalAddr, String _message, int _size, Pack _type, Pair<Integer, Integer> _time){
        super();

        if (_size <= Settings.SERV_PACKAGE_MIN_SIZE) _size = Settings.SERV_PACKAGE_MIN_SIZE;
        SIZE = _size;

        startAddr = _startAddr;
        finalAddr = _finalAddr;
        message = _message;
        type = _type;

        time = _time;
    }

    public int getID(){
        return super.getID();
    }

    public String getMessage() {
        return message;
    }

    public void setTicksInChannel(int _size, float _capacity) {
        ticksInChannel = (int) (_size / _capacity);
    }

    public void decTicksInChannel() {
        ticksInChannel--;
    }

    public int getTicksLeft() {
        return ticksInChannel;
    }

    public int getStartAddr() {
        return startAddr;
    }

    public int getFinalAddr() {
        return finalAddr;
    }

    public int getSIZE() {
        return SIZE;
    }

    public void setPassedNodes(Pair<Vector<Integer>, Integer> _passedNodes) {
        passedNodesList = new Vector<>(_passedNodes.getL());
        weightPassed = _passedNodes.getR();
    }

    public void remPassedNode (int _id) {
        passedNodesList.removeElement(_id);
    }

    public void addPassedNode(int _id1, int _id2, int _weight) {
        if (!passedNodesList.contains(_id1)) passedNodesList.add(_id1);
        passedNodesList.add(_id2);
        weightPassed += _weight;
    }

    public Pair<Vector<Integer>, Integer> getPassedNodes() {
        return new Pair<>(new Vector<>(passedNodesList), weightPassed);
    }

    public Vector<Integer> getPassedNodesList() {
        return passedNodesList;
    }

    public int getWeightPassed() {
        return weightPassed;
    }

    public void setPathFound(boolean pathFound) {
        this.pathFound = pathFound;
    }

    public boolean isPathFound() {
        return pathFound;
    }

    public Pack getType() {
        return type;
    }

    public Pair<Integer, Integer> getTime() {
        return time;
    }

    public Packet reached (int _tick) {
        time.setR(_tick);
        return this;
    }

    public Packet started (int _tick) {
        time.setL(_tick);
        return this;
    }
}
