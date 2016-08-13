package view;

import controller.ControllerSettings;
import view.elements.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by oleh on 20.12.15.
 */
public class FrameSettings extends JFrame {
    public FrameSettings() {
        setTitle("Settings");
        setIconImage(new ImageIcon("res/ico/frame-settings.png").getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(new PanelSettings());

        setLocation(1000, 500);
        setVisible(true);
        pack();
    }

    private class PanelSettings extends JPanel {
        private final String[] names = new String[]{"Node color", "Package color", "Channel color"};
        private GridBagConstraints c = new GridBagConstraints();
        private JButton buttonApply, buttonColorChooser;
        private JTextField txtInfoPackageSize = Settings.createTextField(10, true);
        private JComboBox comboComponents = Settings.createComboBox(names);

        public PanelSettings() {
            setLayout(new GridBagLayout());
            setBackground(Settings.FRAME_SETTING_COLOR);

            buttonApply = Settings.createButton("Apply");
            buttonApply.setForeground(new Color(0x007019));
            buttonApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    refreshValues();
                }
            });

            buttonColorChooser = Settings.createButton("Select color");
            buttonColorChooser.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    Color initialBackground = buttonColorChooser.getBackground();
                    Color colSelected = JColorChooser.showDialog(null,
                            "JColorChooser Sample", initialBackground);
                    if (colSelected != null)
                        buttonColorChooser.setForeground(colSelected);
                }
            });

            addElements();
            refreshValues();
        }

        private void refreshValues() {
            String item = txtInfoPackageSize.getText();

            if (item.length() == 0)
                txtInfoPackageSize.setText(String.valueOf(ControllerSettings.getInfoPackageSize()));
            else ControllerSettings.setInfoPackageSize(Integer.parseInt(item));

            if (comboComponents.getSelectedItem() != null)
                switch (String.valueOf(comboComponents.getSelectedItem())) {
                    case "Node color":
                        Settings.NODE_COLOR = buttonColorChooser.getForeground();
                        break;
                    case "Channel color":
                        Settings.CHANNEL_COLOR = buttonColorChooser.getForeground();
                        break;
                    case "Packet color":
                        Settings.PACKET_COLOR_SERV_TWO_DIR = buttonColorChooser.getForeground();
                        break;
                }
            FrameMain.controllerShow.updateUI();
        }

        private void addElements() {
            removeAll();

            comboComponents.add(Settings.createLabel("Node color"));

            c.insets = new Insets(5, 5, 5, 5);
            c.gridwidth = GridBagConstraints.REMAINDER;
            add(Settings.createLabelHeader("Algorithm setting"), c);

            c.gridwidth = GridBagConstraints.RELATIVE;
            add(Settings.createLabel("Max package size"), c);
            c.gridwidth = GridBagConstraints.REMAINDER;
            add(txtInfoPackageSize, c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            add(Settings.createLabelHeader("Graphics"), c);

            c.gridwidth = GridBagConstraints.REMAINDER;

            c.gridwidth = GridBagConstraints.RELATIVE;
            add(comboComponents, c);
            c.gridwidth = GridBagConstraints.REMAINDER;
            add(buttonColorChooser, c);

            add(buttonApply, c);

            pack();
            updateUI();
        }
    }
}

class FrameSettingsRunner {
    public static void main(String[] args) {
        new FrameSettings();
    }
}