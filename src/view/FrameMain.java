package view;

import controller.ControllerShow;
import model.FileManager;
import model.Traffic;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import static java.awt.Toolkit.getDefaultToolkit;

/**
 * Created by oleh on 11.12.15.
 */
public class FrameMain extends JFrame implements ComponentListener{
    private static final int INFO_PANEL_WIDTH = 200;
    private static final int TOOL_PANEL_HEIGHT = 100;

    public static ControllerShow controllerShow;
    public static PanelInfo panelInfo;
    public static PanelTool panelTool;

    public FrameMain(int width) throws IOException, ParseException {
        setTitle("Final project");
        setIconImage(new ImageIcon("res/ico/frame-main.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, (int) getDefaultToolkit().getScreenSize().getHeight());

        addComponentListener(this);
        addPanels(width);

        FileManager.read("main.json");
        new Traffic();
    }

    private void addPanels(int width) throws IOException, ParseException {
        getContentPane().removeAll();
        Container container = getContentPane();

        setLayout(new BorderLayout());

        controllerShow = new ControllerShow(width - INFO_PANEL_WIDTH, getHeight() - TOOL_PANEL_HEIGHT);
        panelInfo = new PanelInfo(INFO_PANEL_WIDTH, getHeight() - TOOL_PANEL_HEIGHT);
        panelTool = new PanelTool(TOOL_PANEL_HEIGHT);
        add(controllerShow, BorderLayout.WEST);
        add(panelInfo, BorderLayout.EAST);
        add(panelTool, BorderLayout.SOUTH);

        setContentPane(container);
        setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        try {
            addPanels(getWidth());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void componentMoved(ComponentEvent componentEvent) { }
    @Override
    public void componentShown(ComponentEvent componentEvent) { }
    @Override
    public void componentHidden(ComponentEvent componentEvent) { }
}
