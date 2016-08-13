package view.elements;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by oleh on 11.12.15.
 */
public class Settings {
    public static int FREQUENCY_REFRESH_DATA = 1000;
    public static int DELAY = 15, MIN_DELAY = 10, MAX_DELAY = 200;
    public static int MAX_PACKAGE_SIZE = 230;
    public static int INFO_PACKAGE_SIZE = 300;
    public static int SERV_PACKAGE_SIZE = 256;
    public static final int SERV_PACKAGE_MIN_SIZE = 184; // 8*23

    /* Setting for GNode class*/
    public static Color NODE_COLOR = new Color(0x1770AA);
    public static Color NODE_COLOR_DISABLE = new Color(0xA6B2BC);
    public static Color NODE_COLOR_SELECT = new Color(0x9E0011);
    public static int NODE_RADIUS = 20;

    /* Setting for GChannel class*/
    public static Color CHANNEL_COLOR = new Color(0x006A71);
    public static Color CHANNEL_COLOR_SELECT = new Color(0x440007);
    public static Color CHANNEL_LABEL_COLOR = new Color(0x95A4C0);
    public static int CHANNEL_THICKNESS = 2;

    /* Setting for GPacket class*/
    public static Color PACKET_COLOR_SERV_TWO_DIR = new Color(0xFF001C);
    public static Color PACKET_COLOR_SERV_ONE_DIR = new Color(0x00AF2D);
    public static Color PACKET_COLOR_INFO = new Color(0xB8BC60);
    public static int PACKET_RADIUS = 8;

    /* Setting for nodes buttons */
    public static Color NODES_BUTTON_BACK_COLOR = new Color(0x0E2125);
    public static Color NODES_BUTTON_COLOR = new Color(0x1770AA);

    /* Setting for general color */
    public static Color PANEL_TOOL_COLOR = new Color(0x0F0F16);
    public static Color PANEL_SHOW_COLOR = new Color(0x0D0D22);
    public static Color FRAME_DATA_COLOR = PANEL_TOOL_COLOR; //new Color(0x0F2B2F);
    public static Color FRAME_SETTING_COLOR = PANEL_TOOL_COLOR;
    public static Color PANEL_TRAFFIC_COLOR = PANEL_TOOL_COLOR; //new Color(0x150123);
    public static Color INFO_PANEL_COLOR = new Color(14, 33, 37);


    /* Components view */
    public static Color LABEL_FORE_COLOR = new Color(0x00888F);
    public static Color LABEL_BACK_COLOR = PANEL_TOOL_COLOR;
    public static Color LABEL_HEADER_FORE_COLOR = new Color(0x6E8784);
    public static Color LABEL_HEADER_BACK_COLOR = PANEL_TOOL_COLOR;
    public static Color BUTTON_FORE_COLOR = new Color(0x6E8784);
    public static final Color TEXTFIELD_FORE_COLOR = new Color(0x95A4C0);
    public static final Color TEXTFIELD_BACK_COLOR = new Color(0x2C3339);
    public static final Color TEXTFIELD_BORDER_COLOR = new Color(0x727B91);
    public static final Color TEXTFIELD_CARET_COLOR = new Color(0x00888F);
    public static final Color SLIDER_BACKGROUND = PANEL_TOOL_COLOR;
    public static final Color TABLE_FORE_COLOR = new Color(0x6E8784);
    public static final Color TABLE_BACK_COLOR = PANEL_TOOL_COLOR.darker(); //new Color(12);
    public static final Color TABLE_HEAD_FORE_COLOR = new Color(0x95A4C0);
    public static final Color TABLE_HEAD_BACK_COLOR = PANEL_TOOL_COLOR;
    public static final Color TABLE_GRID_COLOR = new Color(0x2A332F);

    public static JButton createButton(String name) {
        JButton button = new JButton(name);

        button.setForeground(BUTTON_FORE_COLOR);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));

        return button;
    }

    public static JLabel createLabel(String name) {
        JLabel label = new JLabel(name);

        label.setFont(new Font("Arial", Font.ROMAN_BASELINE, 12));
        label.setForeground(LABEL_FORE_COLOR);
        label.setBackground(LABEL_BACK_COLOR);

        return label;
    }

    public static JLabel createLabelHeader(String name) {
        JLabel label = new JLabel(name);

        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(LABEL_HEADER_FORE_COLOR);
        label.setBackground(LABEL_HEADER_BACK_COLOR);

        return label;
    }

    public static JTextField createTextField(int n, boolean decimal) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);

        JFormattedTextField textField;
        if (decimal)
            textField = new JFormattedTextField(decimalFormat);
        else textField = new JFormattedTextField();

        textField.setColumns(n);

        textField.setFont(new Font("Arial", Font.TRUETYPE_FONT, 12));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setForeground(TEXTFIELD_FORE_COLOR);
        textField.setBackground(TEXTFIELD_BACK_COLOR);
        textField.setBorder(new LineBorder(TEXTFIELD_BORDER_COLOR, 1, true));
        textField.setCaretColor(TEXTFIELD_CARET_COLOR);

        return textField;
    }

    public static JComboBox<String> createComboBox(String[] _values) {
        JComboBox<String> comboBox = new JComboBox<>(_values);

        comboBox.setForeground(Settings.TEXTFIELD_FORE_COLOR);
        comboBox.setBackground(Settings.PANEL_TOOL_COLOR);
        comboBox.setMinimumSize(new Dimension(100, 20));

        return comboBox;
    }

    public static JRadioButton createRadioButton (String name) {
        JRadioButton radioButton = new JRadioButton(name);

        radioButton.setForeground(LABEL_FORE_COLOR);
        radioButton.setBackground(LABEL_BACK_COLOR);
        radioButton.setSize(new Dimension(1, 1));

        return radioButton;
    }

    public static JScrollPane createTableInScroolPane(Object[] col, Object[][] dat) {
        JTable table = new JTable(dat,col);

        table.setBackground(Settings.TABLE_BACK_COLOR);
        table.setForeground(Settings.TABLE_FORE_COLOR);
        table.getTableHeader().setBackground(Settings.TABLE_HEAD_BACK_COLOR);
        table.getTableHeader().setForeground(Settings.TABLE_HEAD_FORE_COLOR);
        table.setGridColor(Settings.TABLE_GRID_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(0x3C444B)));

        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(Settings.PANEL_TOOL_COLOR);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x3C444B)));
        scrollPane.getVerticalScrollBar().setBackground(Settings.PANEL_TOOL_COLOR.brighter());

        ScrollBarStyle scrollUI = new ScrollBarStyle();
        scrollPane.getVerticalScrollBar().setUI(scrollUI.scrollUI);
        return scrollPane;
    }

    public static JScrollPane createScroolPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(Settings.PANEL_TOOL_COLOR);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x3C444B)));
        scrollPane.getVerticalScrollBar().setBackground(Settings.PANEL_TOOL_COLOR.brighter());

        ScrollBarStyle scrollUI = new ScrollBarStyle();
        scrollPane.getVerticalScrollBar().setUI(scrollUI.scrollUI);
        return scrollPane;
    }

    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);

        table.setAutoCreateRowSorter(true);
        table.setBackground(Settings.TABLE_BACK_COLOR);
        table.setForeground(Settings.TABLE_FORE_COLOR);
        table.getTableHeader().setBackground(Settings.TABLE_HEAD_BACK_COLOR);
        table.getTableHeader().setForeground(Settings.TABLE_HEAD_FORE_COLOR);
        table.setGridColor(Settings.TABLE_GRID_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(0x3C444B)));
        return table;
    }
}
