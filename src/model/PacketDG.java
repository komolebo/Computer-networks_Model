package model;

import view.elements.Settings;

/**
 * Created by oleh on 28.12.15.
 */
public class PacketDG extends Packet {
    public boolean isLast() {
        return isLast;
    }

    public enum State {INFO, SERVICE, ENDED}

    private State state = State.INFO;
    private boolean isLast = false;

    public PacketDG(int _startAddr, int _finalAddr, String _message, int _size, boolean _isLast, Modes.Pack _type, int _tick) {
        super(_startAddr, _finalAddr, _message, _size, _type, _tick);
        isLast = _isLast;
    }

    public State getState() {
        return state;
    }

    public void send() {
        state = State.SERVICE;
    }

    public void end() {
        state = State.ENDED;
    }

    @Override
    public int getSIZE() {
        if (state == State.SERVICE)
            return Settings.SERV_PACKAGE_SIZE;
        return super.getSIZE();
    }
}