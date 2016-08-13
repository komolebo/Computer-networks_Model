package view;

import model.AlgorithmRunner;
import model.Modes;
import model.Node;
import model.RoutingTable;
import model.tools.Pair;
import view.elements.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

/**
 * Created by oleh on 13.12.15.
 */
public class PanelInfo extends JPanel {
    public static PanelShowProgress panelProgress;
    public static PanelShowData panelData;

    public PanelInfo(int W, int H)
    {
        setBackground(Settings.INFO_PANEL_COLOR);
        setPreferredSize(new Dimension(W, H));
        setLayout(new BorderLayout());

        panelProgress = new PanelShowProgress();
        panelData = new PanelShowData();
        add(panelProgress, BorderLayout.SOUTH);
        add(panelData, BorderLayout.NORTH);

        updateUI();

        setVisible(true);
    }

    public void refreshTact() {
        remove(panelProgress);
        panelProgress = new PanelShowProgress();
        add(panelProgress, BorderLayout.SOUTH);
        updateUI();
    }

    public void selectIDInfo(int _id) {
        panelData.setSelected(_id);
        panelData.refreshTable();
    }

    private class PanelShowData extends JPanel {
        private final Object[] Columns = new String[]{"To", "Way", "Len"};
        private JTable table;
        private JScrollPane scrollPane;
        private GridBagConstraints c;
        private int curID = -1;

        public PanelShowData() {
            setBackground(Settings.INFO_PANEL_COLOR);
            setPreferredSize(new Dimension(200, 550));
            setLayout(new GridBagLayout());

            c = new GridBagConstraints();

            DefaultTableModel model = new DefaultTableModel(new Integer[][]{}, Columns){
                @Override
                public Class getColumnClass(int column) {
                    switch (column) {
                        case 0:
                            return Integer.class;
                        case 1:
                            return Integer.class;
                        case 2:
                            return Integer.class;
                        default:
                            return String.class;
                    }
                }
            };
            table = Settings.createTable(model);
            table.getColumnModel().getColumn(0).setMaxWidth(35);
            table.getColumnModel().getColumn(2).setMaxWidth(40);
            table.setFont(new Font("PLAIN", Font.PLAIN, 10));

            scrollPane = Settings.createScroolPane(table);
            scrollPane.setMinimumSize(new Dimension(190, 380));

            new java.util.Timer().schedule(new TimerTask() {
                public void run() {
                    if (Modes.getRoutingState() == Modes.RoutingState.RUNNING)
                        addElements();
                }
            }, 0, Settings.FREQUENCY_REFRESH_DATA);
        }

        public void setSelected(int _id) {
            curID = _id;
        }

        public void addElements() {
            removeAll();

            if (curID < 0) return;

            Node node = AlgorithmRunner.getNodes().get(AlgorithmRunner.getNodeIndexByID(curID));
            if (node == null) return;

            c.insets = new Insets(2, 2, 10, 2);
            c.gridwidth = GridBagConstraints.REMAINDER;

            add(Settings.createLabelHeader("Object info:"), c);
            add(Settings.createLabel("ID: " + curID), c);
            add(Settings.createLabel("State: " + (node.isEnabled() ? "enabled" : "disabled")), c);
            add(Settings.createLabel("Buffer load: " + node.getBuffer().size()), c);

            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(25, 5, 5, 2);

            add(Settings.createLabelHeader("Routing table: "), c);
            c.insets = new Insets(5, 5, 5, 5);
            add(scrollPane, c);

            refreshTable();

            updateUI();
        }

        public void refreshTable() {
            if (curID < 0) return;

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            Vector<Node> nodes = AlgorithmRunner.getNodes();

            for (Node node : nodes)
                if (node.getID() != curID) {
                    Pair<Vector<Integer>, Integer> ways = RoutingTable.getMinWay(curID, node.getID());
                    if (ways != null)
                        model.addRow(new Object[]{node.getID(), ways.getL().toString(), ways.getR()});
                }
            table.updateUI();
        }
    }

    private class PanelShowProgress extends JPanel {
        private GridBagConstraints c;

        public PanelShowProgress() {
            setBackground(Settings.INFO_PANEL_COLOR);
            setLayout(new GridBagLayout());

            c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new Insets(5, 5, 5, 5);

            JButton button = Settings.createButton("");
            if (Modes.getUserMode() == Modes.User.INVISIBLE)
                button.setIcon(new ImageIcon("res/ico/main-invisibility.png"));
            else if (Modes.getRoutingState().equals(Modes.RoutingState.PAUSED))
                button.setIcon(new ImageIcon("res/ico/main-pause.png"));
            else if (Modes.getRoutingState().equals(Modes.RoutingState.STOPPED))
                button.setIcon(new ImageIcon("res/ico/main-stop.png"));
            else button.setIcon(new ImageIcon("res/ico/main-run.png"));

            add(button, c);
            add(Settings.createLabelHeader("TICK: " + AlgorithmRunner.TICK), c);

            updateUI();
        }
    }
}