package view.elements;

import java.awt.*;

/**
 * Created by oleh on 11.12.15.
 */
public class GChannel {
    private int ID, thickness, weight;
    private Color color;
    private GNode node1, node2;
    private boolean duplex = true;

    public GChannel(int _ID, GNode _node1, GNode _node2, int _weight, boolean _duplex){
        color = Settings.CHANNEL_COLOR;
        thickness = Settings.CHANNEL_THICKNESS;

        node1 = _node1;
        node2 = _node2;
        ID = _ID;
        weight = _weight;
        duplex = _duplex;
    }

    public int getID() {
        return ID;
    }

    public void select(boolean _select) {
        if (_select)
            color = Settings.CHANNEL_COLOR_SELECT;
        else color = Settings.CHANNEL_COLOR;
    }

    public Dimension getPosition1(){
        return node1.getPosition();
    }

    public Dimension getPosition2(){
        return node2.getPosition();
    }

    public int getThickness() {
        return thickness;
    }

    public Color getColor() {
        return color;
    }

    public int getNode1ID(){
        return node1.getID();
    }

    public int getNode2ID(){
        return node2.getID();
    }

    public int getWeight() {
        return weight;
    }

    public boolean isDuplex() {
        return duplex;
    }
}
