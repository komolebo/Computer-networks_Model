package view;

import model.*;
import model.Modes.Pack;
import view.elements.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by oleh on 22.12.15.
 */
public class FrameTraffic extends JFrame{
    public FrameTraffic() {
        setTitle("Traffic");
        setIconImage(new ImageIcon("res/ico/frame-traffic.png").getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(300, 400));
        setLocation(1000, 0);

        add(new PanelTraffic());

        pack();
        setVisible(true);
    }

    private class PanelTraffic extends JPanel{
        JTable table;
        DefaultTableModel model;

        public PanelTraffic() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Settings.PANEL_TRAFFIC_COLOR);
            setSize(new Dimension(300, 300));

            model = new DefaultTableModel(new Object[][]{}, new String[]{"Start address", "Final address", "Size", "tick", "type", "Status"}){
                @Override
                public Class getColumnClass(int column) {
                    switch (column) {
                        case 0:
                            return Integer.class;
                        case 1:
                            return Integer.class;
                        case 2:
                            return Integer.class;
                        case 3:
                            return Integer.class;
                        case 4:
                        default:
                            return String.class;
                    }
                }
            };

            refresh();
        }

        public void refresh() {
            removeAll();

            table = new JTable(model);
//            table = Settings.createTable(model);
//            table.setSelectionForeground(new Color(0x2F3118));
//            table.setSelectionBackground(new Color(0x180131));
            table.setAutoCreateRowSorter(true);

            initTrafficTable();

            JScrollPane scrollPane = new JScrollPane(table);

            add(scrollPane);
            add(new PanelButtons());
            add(new PanelPackGenerator());

            updateUI();
        }

        private void initTrafficTable () {
            for (Integer t: Traffic.Schedule.keySet())
                for (Packet p: Traffic.Schedule.get(t))
                    if (p instanceof PacketLC)
                        addRow(p.getStartAddr(), p.getFinalAddr(), ((PacketLC) p).getFullSize(), p.getType(), t);
                    else if (p instanceof PacketDG)
                        addRow(p.getStartAddr(), p.getFinalAddr(), p.getSIZE(), p.getType(), t);
                    else addRow(p.getStartAddr(), p.getFinalAddr(), p.getSIZE(), p.getType(), t);
        }

        private void addrow() {
            model.addRow(new Object[]{0, 0, 0, AlgorithmRunner.TICK + 1, "Datagram", "new"});
        }

        private void addRow(int _from, int _to, int _size, Pack _type, Integer _t) {
            model.addRow(new Object[]{_from, _to, _size, _t, _type, "updated"});
        }

        private void addRow(int _from, int _to, int _size, Pack _type, Integer _t, String _status) {
            model.addRow(new Object[]{_from, _to, _size, _t, _type, _status});
        }

        private void delRow(int _index) {
            model.removeRow(_index);
        }

        private void update() {
            // Read update to Algorithm.Traffic
            for (int i = 0; i < table.getRowCount(); i++) {
                if (!String.valueOf(table.getValueAt(i, 5)).equals("new")) continue;

                int start = (int) table.getValueAt(i, 0);
                int dest = (int) table.getValueAt(i, 1);
                int size = (int) table.getValueAt(i, 2);
                int tick = (int) table.getValueAt(i, 3);
                Pack type = String.valueOf(table.getValueAt(i, 4)).contains("D") ? Pack.DG : Pack.LC;
                int amount = size / Settings.INFO_PACKAGE_SIZE + (size % Settings.INFO_PACKAGE_SIZE == 0 ? 0 : 1);

//                table.setValueAt("confirmed", i, 5);

                if (type == Pack.DG) {
                    for (int k = 0; k < amount - 1; k++)
                        Traffic.addPacket(tick, new PacketDG(start, dest, "", Settings.INFO_PACKAGE_SIZE, false, type, tick));
                    Traffic.addPacket(tick, new PacketDG(start, dest, "", (size % Settings.INFO_PACKAGE_SIZE == 0 ? Settings.INFO_PACKAGE_SIZE : size % Settings.INFO_PACKAGE_SIZE), true, type, tick));
                }
                else Traffic.addPacket(tick, new PacketLC(start, dest, "", size, 0, type, tick));
            }

            // Previous clear
            for (int i = 0; i < table.getRowCount(); i++){
                delRow(i);
                i--;
            }

            initTrafficTable();

            // Clear late packets
            for (int i = 0; i < table.getRowCount(); i++)
                if (table.getValueAt(i, 3) != "" && Integer.valueOf(String.valueOf(table.getValueAt(i, 3))) < AlgorithmRunner.TICK) {
                    delRow(i);
                    i--;
                }
        }

        private class PanelButtons extends JPanel{
            public PanelButtons() {
                setBackground(Settings.PANEL_TRAFFIC_COLOR);
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

                JButton buttonAdd = Settings.createButton("Add row");
                JButton buttonDelete = Settings.createButton("Delete row");
                JButton buttonUpdate = Settings.createButton("Update");
                buttonAdd.setIcon(new ImageIcon("res/ico/traffic-add.png"));
                buttonDelete.setIcon(new ImageIcon("res/ico/traffic-delete.png"));
                buttonUpdate.setIcon(new ImageIcon("res/ico/traffic-update.png"));
                buttonAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addrow();
                        updateUI();
                    }
                });
                buttonDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (table.getRowCount() > 0) {
                            if (!table.getValueAt(table.getSelectedRow(), 4).equals("updated")) {
                                delRow(table.getSelectedRow());
                                updateUI();
                            }
                        }
                    }
                });
                buttonUpdate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        PanelTraffic.this.update();
                        updateUI();
                    }
                });

                add(buttonAdd);
                add(buttonDelete);
                add(buttonUpdate);
            }
        }

        private class PanelPackGenerator extends JPanel{
            private GridBagConstraints c = new GridBagConstraints();
            private JRadioButton radioButton;

            public PanelPackGenerator() {
                setLayout(new GridBagLayout());
                setBackground(Settings.PANEL_TRAFFIC_COLOR);

                final JTextField textAverageSize = Settings.createTextField(10, true);
                final JTextField textPackageNumber = Settings.createTextField(10, true);
                JButton buttonGenerate = Settings.createButton("Generate");
                radioButton = Settings.createRadioButton("Datagram");

                buttonGenerate.setIcon(new ImageIcon("res/ico/traffic-generate.png"));
                buttonGenerate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!textAverageSize.getText().equals("") && !textPackageNumber.getText().equals(""))
                            generate(Integer.valueOf(textAverageSize.getText()), Integer.valueOf(textPackageNumber.getText()), radioButton.isSelected());
                    }
                });

                c.insets = new Insets(2, 2, 2, 2);
                add(Settings.createLabel("Average pack size"), c);
                c.gridwidth = GridBagConstraints.REMAINDER;
                add(textAverageSize, c);

                c.gridwidth = GridBagConstraints.RELATIVE;
                add(Settings.createLabel("Packages number"), c);
                c.gridwidth = GridBagConstraints.REMAINDER;
                add(textPackageNumber, c);
                add(radioButton, c);

                add(buttonGenerate, c);
            }

            private void generate(int _averSize, int _number, boolean _datagram) {
                Vector<Integer> idList = AlgorithmRunner.getNodesList();
                if (idList.size() == 0) return;

                Pack type = (_datagram ? Pack.DG : Pack.LC);

                for (int i = 0; i < _number; i++) {
                    int randFrom = ThreadLocalRandom.current().nextInt(0, idList.size() - 1);
                    int randTo = ThreadLocalRandom.current().nextInt(0, idList.size() - 1);

                    if (AlgorithmRunner.isSameNetwork(randFrom, randTo) && randFrom != randTo)
                        addRow(idList.get(randFrom), idList.get(randTo), _averSize, type, AlgorithmRunner.TICK + 1, "new");
                    else i--;
                }
            }
        }
    }
}