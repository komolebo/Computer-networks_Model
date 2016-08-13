package view;

import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

/**
 * Created by oleh on 11.12.15.
 */
public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new FrameMain(840);
                } catch (IOException | ParseException e) {e.printStackTrace();}
            }
        });
    }
}
