package model;

import view.elements.Settings;

/**
 * Created by oleh on 25.12.15.
 */
public class PacketLC extends Packet {
    public enum State {CONNECTING_ASK, CONNECTING_ANSWER, TRANSMITTING_ASK, TRANSMITTING_ANSWER, BREAKING_ASK, BREAKING_ANSWER}
    private State state = State.CONNECTING_ASK;
    private int amount = -1;
    private int fullSize;

    public State getState() {
        return state;
    }


    public void setAmount() {
        amount = fullSize / Settings.INFO_PACKAGE_SIZE + (fullSize % Settings.INFO_PACKAGE_SIZE == 0 ? 0 : 1);
    }
    @Override
    public int getSIZE() {
        if (amount == -1)
            return Settings.SERV_PACKAGE_SIZE;
        else if (amount == 1)
            return (fullSize % Settings.INFO_PACKAGE_SIZE == 0) ? Settings.INFO_PACKAGE_SIZE : fullSize % Settings.INFO_PACKAGE_SIZE;
        else return Settings.INFO_PACKAGE_SIZE;
    }

    public int getFullSize() {
        return fullSize;
    }

    public int getAmount() {
        return amount;
    }

    public void decAmount () {
        amount--;
    }

    public PacketLC(int _startAddr, int _finalAddr, String _message, Modes.Pack _type, int _tick) {
        super(_startAddr, _finalAddr, _message, _type, _tick);
    }

    public PacketLC(int _startAddr, int _finalAddr, String _message, int _size, int _amount, Modes.Pack _type, int _tick) {
        super(_startAddr, _finalAddr, _message, _size, _type, _tick);
        fullSize = _size;
        amount = _amount;
    }

    public void connected () {
        state = State.CONNECTING_ANSWER;
    }

    public void connectAnswered () {state = State.TRANSMITTING_ASK; }

    public void transmitted () {
        state = State.TRANSMITTING_ANSWER;
    }

    public void transmitAnswered () {state = State.BREAKING_ASK;}

    public void broken () {state = State.BREAKING_ANSWER;}
}
