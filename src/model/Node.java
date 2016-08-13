package model;

import java.util.Vector;

/**
 * Created by oleh on 11.12.15.
 */
public class Node extends AbstractObject {
    private Vector<Packet> buffer = new Vector<>();
    private boolean enabled = true;

    public Node(){
        super();
    }

    public Node(int id) {
        super(id);
    }

    public int getID(){
        return super.getID();
    }

    public Vector<Packet> getBuffer(){
        return buffer;
    }

    public void addBuffer(Packet _packet) {
        this.buffer.add(_packet);
    }

    public Packet getPacket(int _i) {
        Packet packet = buffer.get(_i);

        buffer.remove(_i);

        return packet;
    }

    public void setBuffer(Vector<Packet> buffer) {
        this.buffer = buffer;
    }

    public void setEnabled(boolean _enabled) {
        enabled = _enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
