package model;

/**
 * Created by oleh on 11.12.15.
 */
public class Channel extends AbstractObject {
    public final static int MAX_WEIGHT = 23;

    private final int weight;
    private final float capacity;
    private final Node node1, node2;

    private boolean isDuplex = false; // 1 - duplex, 0 - half duplex

    private Packet buffer12;
    private Packet buffer21;

    public Channel(int _weight, Node _node1, Node _node2, boolean _isDuplex){
        super();

        weight = _weight;
        capacity = (float)MAX_WEIGHT / weight;
        node1 = _node1;
        node2 = _node2;
        isDuplex = _isDuplex;
    }

    public Channel(int _id, int _weight, Node _node1, Node _node2, boolean _duplex){
        super(_id);

        weight = _weight;
        capacity = (float)MAX_WEIGHT / weight;
        node1 = _node1;
        node2 = _node2;
        isDuplex = _duplex;
    }

    public int getID(){
        return super.getID();
    }

    public int getWeight() {
        return weight;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public boolean isDuplex() {
        return isDuplex;
    }

    public void addBuffer12 (Packet _packet) {
        buffer12 = _packet;
    }

    public void addBuffer21 (Packet _packet) {
        buffer21 = _packet;
    }

    public Packet getBuffer12() {
        return buffer12;
    }

    public Packet getBuffer21() {
        return buffer21;
    }

    public float getCapacity() {
        return 8 * capacity;
    }
}
