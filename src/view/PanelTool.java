package view;

import controller.ControllerTool;
import view.elements.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by oleh on 13.12.15.
 */
public class PanelTool extends JPanel implements ChangeListener {
    private JTextField TextFrom, TextTo;
    private JRadioButton rbtnDuplex;
    private JButton buttonVis;

    public PanelTool(int H) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Settings.PANEL_TOOL_COLOR);
        setPreferredSize(new Dimension(getWidth(), H));

/*  Add / Delete channel setting */
        JPanel contentPane = new JPanel();
        contentPane.setBackground(Settings.PANEL_TOOL_COLOR);
        contentPane.setOpaque(true);
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        TextFrom = Settings.createTextField(10, true);
        TextTo = Settings.createTextField(10, true);
        JButton btnDelete = Settings.createButton("Delete");
        final JButton btnAdd = Settings.createButton("Add");
        rbtnDuplex = Settings.createRadioButton("");
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!TextFrom.getText().equals("") && !TextTo.getText().equals("")) {
                    Integer _from = Integer.valueOf(TextFrom.getText());
                    Integer _to = Integer.valueOf(TextTo.getText());
                    ControllerTool.deleteChannel(_from, _to);
                }
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!TextFrom.getText().equals("") && !TextTo.getText().equals("")) {
                    int weight = Integer.parseInt(JOptionPane.showInputDialog("Channel initData",
                            "Please, input here channel weight"));
                    boolean duplex = rbtnDuplex.isSelected();

                    ControllerTool.addChannel(Integer.valueOf(TextFrom.getText()),
                            Integer.valueOf(TextTo.getText()), weight, duplex);
                }
            }
        });

        c.insets = new Insets(3, 3, 3, 3);
        contentPane.add(Settings.createLabel("Duplex channel"), c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        contentPane.add(rbtnDuplex, c);

        c.gridwidth = GridBagConstraints.RELATIVE;
        contentPane.add(Settings.createLabel("from: "), c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        contentPane.add(TextFrom, c);

        c.gridwidth = GridBagConstraints.RELATIVE;
        contentPane.add(Settings.createLabel("to: "), c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        contentPane.add(TextTo, c);

        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(0, 0, 0, 0);
        btnDelete.setSize(new Dimension(2, 2));
        contentPane.add(btnDelete, c);
        contentPane.add(btnAdd, c);

        add(contentPane);
/* Add / Delete channel setting ends */

        JPanel panelTopology = new JPanel();
        panelTopology.setLayout(new GridBagLayout());
        panelTopology.setBackground(Settings.PANEL_TOOL_COLOR);

        JButton buttonSaveTopology = Settings.createButton("Save topology");
        JButton buttonLoadTopology = Settings.createButton("Load topology");
        final JTextField textField = Settings.createTextField(22, false);

        JSlider delayer = createSlider();
        delayer.addChangeListener(this);

        buttonSaveTopology.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.dump(textField.getText());
            }
        });

        buttonLoadTopology.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.read(textField.getText());
            }
        });

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 3;

        panelTopology.add(textField, c);

        c.weightx = 1;
        c.gridwidth = GridBagConstraints.RELATIVE;
        panelTopology.add(buttonSaveTopology, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        panelTopology.add(buttonLoadTopology, c);
        panelTopology.add(delayer, c);
        add(panelTopology);

/* Algorithm control */
        JButton buttonRun = Settings.createButton("Start");
        JButton buttonPause = Settings.createButton("Pause");
        JButton buttonStop = Settings.createButton("Stop");
        buttonRun.setIcon(new ImageIcon("res/ico/main-run.png"));
        buttonStop.setIcon(new ImageIcon("res/ico/main-stop.png"));
        buttonPause.setIcon(new ImageIcon("res/ico/main-pause.png"));
        buttonRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.run();
            }
        });
        buttonPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.pause();
            }
        });
        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.stop();
            }
        });

        JPanel panelControl = new JPanel();
        panelControl.setBackground(Settings.PANEL_TOOL_COLOR);
        panelControl.setLayout(new GridBagLayout());

        c.gridwidth = GridBagConstraints.REMAINDER;
        panelControl.add(buttonRun, c);
        panelControl.add(buttonPause, c);
        panelControl.add(buttonStop, c);

        add(panelControl);

/* Panel for windows, graphs */
        final JButton buttonData = Settings.createButton("Data");
        buttonData.setIcon(new ImageIcon("res/ico/main-data.png"));
        buttonData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.openData();
            }
        });

        final JButton buttonSetting = Settings.createButton("Setting");
        buttonSetting.setIcon(new ImageIcon("res/ico/main-setting.png"));
        buttonSetting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.openSettings();
            }
        });

        final JButton buttonTraffic = Settings.createButton("Traffic");
        buttonTraffic.setIcon(new ImageIcon("res/ico/main-traffic.png"));
        buttonTraffic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ControllerTool.openTraffic();
            }
        });

        JPanel panelConf = new JPanel();
        panelConf.setLayout(new GridBagLayout());
        panelConf.setBackground(Settings.PANEL_TOOL_COLOR);

        panelConf.add(buttonData, c);
        panelConf.add(buttonSetting, c);
        panelConf.add(buttonTraffic, c);

        add(panelConf);

/* Visibility button */
        buttonVis = Settings.createButton("Visible  ");
//        buttonVis.setIcon(new ImageIcon("res/santa.gif"));
        buttonVis.setIcon(new ImageIcon("res/ico/main-invisibility.png"));
        buttonVis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControllerTool.setUserMode(buttonVis.getText());
                refreshButtonVisibility();
            }
        });
        add(buttonVis);
    }

    public void refreshButtonVisibility () {
        if (ControllerTool.isVisibleToUser())
            buttonVis.setText("Visible  ");
        else buttonVis.setText("Invisible");
    }

    private static JSlider createSlider () {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, Settings.MIN_DELAY, Settings.MAX_DELAY, Settings.MIN_DELAY);

        slider.setBackground(Settings.SLIDER_BACKGROUND);

        return slider;
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        JSlider source = (JSlider) changeEvent.getSource();
        if (!source.getValueIsAdjusting())
            Settings.DELAY = source.getValue();
    }
}