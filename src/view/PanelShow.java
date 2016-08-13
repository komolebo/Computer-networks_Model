package view;

import controller.ControllerShow;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by oleh on 11.12.15.
 */
class PanelShow extends JPanel{
    public PanelShow(int _W, int _H) throws IOException, ParseException {
        add(new ControllerShow(_W, _H));
    }
}
