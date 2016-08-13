package view.elements;

import model.Modes.Pack;
import java.awt.*;

/**
 * Created by oleh on 11.12.15.
 */
public class GPacket {
    private Dimension position;
    private Color color;

    public GPacket(int _x, int _y, Pack _serv){
        if (_serv == Pack.THERE_AND_BACK_SAVED || _serv == Pack.THERE_AND_BACK_ROUT)
            color = Settings.PACKET_COLOR_SERV_TWO_DIR;
        else if (_serv == Pack.DG || _serv == Pack.LC)
            color = Settings.PACKET_COLOR_INFO;
        else if (_serv == Pack.SUPERVISOR)
            color = new Color(0xFF00D5);
        else if (_serv == Pack.SERV_SAVED)
            color = new Color(0x00FF3C);
        else color = Settings.PACKET_COLOR_SERV_ONE_DIR;

        position = new Dimension(_x, _y);
    }

    public void setPosition(int _x, int _y) {
        position.setSize(_x, _y);
    }

    public Dimension getPosition(){
        return position;
    }

    public Color getColor() {
        return color;
    }
}
