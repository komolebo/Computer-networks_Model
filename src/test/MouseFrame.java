package test;

import javax.swing.*;

/**
 * Created by oleh on 12.12.15.
 */
public class MouseFrame extends JFrame {
    public MouseFrame(){
        add(new MouseComponent());
        pack();
        setVisible(true);
    }
}

