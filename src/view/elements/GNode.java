package view.elements;

import java.awt.*;

/**
 * Created by oleh on 11.12.15.
 */
public class GNode {
    private final int ID;
    private Color color;

    private Dimension position;
    private int r;
    private boolean enabled = true; // enabled = 1, enabled - 0
    private boolean selected = false;

    public GNode(int _ID, int _x, int _y) {
        ID = _ID;
        color = Settings.NODE_COLOR;
        r = Settings.NODE_RADIUS;

        position = new Dimension(_x, _y);
    }

    public void enable(boolean _enabled){
        enabled = _enabled;
        selected = false;

        if (enabled)
            color = Settings.NODE_COLOR;
        else
            color = Settings.NODE_COLOR_DISABLE;
    }

    public void select(boolean _selected){
        selected = _selected;

        if (selected)
            color = Settings.NODE_COLOR_SELECT;
        else if (!enabled)
            color = Settings.NODE_COLOR_DISABLE;
        else color = Settings.NODE_COLOR;
    }

    public void setPosition(Dimension position) {
        this.position = position;
    }

    public boolean isEnabled(){return enabled; }

    public Dimension getPosition(){
        return position;
    }

    public Color getColor() {
        return color;
    }

    public int getR() {
        return r;
    }

    public int getID() {
        return ID;
    }
}
