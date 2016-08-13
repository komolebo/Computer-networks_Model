package view;

import model.Packet;
import model.PacketLC;
import model.Statistics;
import view.elements.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by oleh on 13.12.15.
 */
public class FrameData extends JFrame {
    private static boolean RUN_TIMER;
    public FrameData(int W, int H) {
        setTitle("Statistics");
        setIconImage(new ImageIcon("res/ico/frame-main.json.png").getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(W, H);

        RUN_TIMER = true;

        add(new PanelData());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                RUN_TIMER = false;
            }
        });

//        pack();
        setLocation(1000, 500);
        setVisible(true);
    }

    private class PanelData extends JPanel {
        private GridBagConstraints c = new GridBagConstraints();
        private JScrollPane scrollPane;
        private DefaultTableModel model;
        private String[] Columns = new String[]{"Tick sent", "Tick received", "From", "To", "Size"};

        public PanelData() {
            setLayout(new GridBagLayout());
            setBackground(Settings.FRAME_DATA_COLOR);

            model = new DefaultTableModel(new Integer[][]{}, Columns){
                @Override
                public Class getColumnClass(int column) {
                    switch (column) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            return Integer.class;
                        default:
                            return String.class;
                    }
                }
            };

            JTable table = Settings.createTable(model);
            table.setAutoCreateRowSorter(true);
            scrollPane = Settings.createScroolPane(table);


            new Timer().schedule(new TimerTask() {
                public void run() {
                    if (RUN_TIMER)
                        refresh();
                    else cancel();
                }
            }, 0, Settings.FREQUENCY_REFRESH_DATA);
        }

        private void readModel () {
            model.setRowCount(0);

            for (Packet p: Statistics.getDeliveredPackets())
                if (p instanceof PacketLC)
                    model.addRow(new Object[]{p.getTime().getL(), p.getTime().getR(), p.getStartAddr(), p.getFinalAddr(), ((PacketLC) p).getFullSize()});
                else model.addRow(new Object[]{p.getTime().getL(), p.getTime().getR(), p.getStartAddr(), p.getFinalAddr(), p.getSIZE()});
        }

        public void refresh() {
            removeAll();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 5, 5, 5);

//            readModel();

            add(scrollPane, c);
            add(Settings.createLabelHeader("Service packages: " + String.valueOf(Statistics.getServPackCount() - 101275)), c);
            add(Settings.createLabelHeader("Info packages: " + Statistics.getInfoPackCount()), c);
            add(Settings.createLabelHeader("Messages created: " + Statistics.getMessageSent()), c);
            add(Settings.createLabelHeader("Messages received: " + Statistics.getMessageReceived()), c);
            add(Settings.createLabelHeader("Average time delivering: " + Statistics.getAverageTimeDeliver()), c);
            pack();
            updateUI();
        }
    }
}