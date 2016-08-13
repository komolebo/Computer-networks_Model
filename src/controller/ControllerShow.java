package controller;

import model.AlgorithmRunner;
import org.json.simple.parser.ParseException;
import view.elements.GChannel;
import view.elements.GNode;
import view.elements.GPacket;
import view.elements.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by oleh on 11.12.15.
 */
public class ControllerShow extends JPanel{
    private static Vector<GNode> nodes = new Vector<>();
    private static Vector<GChannel> channels = new Vector<>();
    private static Vector<GPacket> packets = new Vector<>();

    private static final int SIDE_LENGTH = 10;

    private static GNode current;

    public ControllerShow(int W, int H) throws IOException, ParseException {
        setPreferredSize(new Dimension(W, H));
        setBackground(Settings.PANEL_SHOW_COLOR);

        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());

        setVisible(true);
    }

    private void drawChannel(Graphics2D g1, String weight, boolean _duplex, int x1, int y1, int x2, int y2) {
        final int ARR_SIZE = 5;
        Graphics2D g = (Graphics2D) g1.create();

        x1 += 5; x2 += 5; y1 += 5; y2 += 5;
        g.setColor(Settings.CHANNEL_LABEL_COLOR);
        g.drawString(weight, (x1 + x2) / 2 + 7, (y1 + y2) / 2 + 7);

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        g.setColor(Settings.CHANNEL_COLOR);
        g.drawLine(0, 0, len, 0);

        if (_duplex) {
            len = (int) (0.33 * len);
            g.fillPolygon(new int[]{len, len - ARR_SIZE, len - ARR_SIZE},
                    new int[]{0, ARR_SIZE, -ARR_SIZE}, 3);
            len *= 2;
            g.fillPolygon(new int[]{len, len + ARR_SIZE, len + ARR_SIZE},
                    new int[]{0, ARR_SIZE, -ARR_SIZE}, 3);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.PLAIN, 10));

// Draw channels
        for (GChannel ch: channels){
            drawChannel(g2, String.valueOf(ch.getWeight()), ch.isDuplex(), ch.getPosition1().width, ch.getPosition1().height,
                    ch.getPosition2().width, ch.getPosition2().height);
        }

// Draw nodes
        for (GNode node : nodes) {
            g2.setColor(node.getColor());
            Ellipse2D ellipse = new Ellipse2D.Float(node.getPosition().width, node.getPosition().height,
                    node.getR(), node.getR());
            g2.draw(ellipse);

            g2.setColor(node.getColor());
            g2.fill(ellipse);

            g2.setFont(new Font("Plain", Font.PLAIN, 10));
            g2.drawString("Node " + node.getID(), node.getPosition().width - 5, node.getPosition().height - 3);
        }

// Draw packets
        final int r = Settings.PACKET_RADIUS;
        for (GPacket packet: packets){
            Ellipse2D ellipse = new Ellipse2D.Double(packet.getPosition().width, packet.getPosition().height, r, r);
            g2.setColor(packet.getColor());
            g2.fill(ellipse);
        }
    }

    public static GNode find(Point2D p){
        for (GNode node: nodes){
            final int x0 = node.getPosition().width;
            final int y0 = node.getPosition().height;

            if (Math.abs(x0 - p.getX()) <= node.getR() &&
                    Math.abs(y0 - p.getY()) <= node.getR())
                return node;
        }

        return null;
    }

    public void remove(GNode _node){
        if (_node == null) return;
        if (_node == current) current = null;

        nodes.remove(_node);
        AlgorithmRunner.deleteNode(_node.getID());

        removeChannel(_node);

        repaint();
    }

    public void removeChannel (GNode _node) {
        for (int i = 0; i < channels.size(); i++)
            if (channels.get(i).getNode1ID() == _node.getID() || channels.get(i).getNode2ID() == _node.getID()){
                AlgorithmRunner.deleteChannel(channels.get(i).getID());
                channels.remove(channels.get(i));
            }

    }

    public void add(Point2D p){
        double x = p.getX();
        double y = p.getY();

        current = new GNode(AlgorithmRunner.addNode(), (int)x, (int)y);
        nodes.add(current);
        repaint();
    }

    public void select(GNode _node){
        System.out.println("Selec5");
        for (GNode node: nodes){
            if (node.equals(_node))
                node.select(true);
            else node.select(false);
        }
        ControllerInfo.selectID(_node.getID());
        repaint();
    }

    public void deselect(){
        for (GNode node: nodes)
            node.select(false);

        ControllerInfo.deselectID();
        repaint();
    }

    public void disable(GNode _node){
        System.out.println("Disable");
        for (GNode node: nodes){
            if (node.getID() == _node.getID()){
                node.enable(!node.isEnabled());
                ControllerAlgorithm.setNodeEnabled(node.getID(), node.isEnabled());
            }
        }
        repaint();
    }

    private class MouseHandler extends MouseAdapter{
        public void mousePressed(MouseEvent e){
            // Double click outside the ellipse - new ellipse
            current = find(e.getPoint());
            if (current != null && e.getClickCount() == 2 && SwingUtilities.isRightMouseButton(e)) remove(current);
            else if (current != null && e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) disable(current);
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            current = find(e.getPoint());
            if (current != null && e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) select(current);
            else if (current == null && e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) add(e.getPoint());
            else if (current == null) deselect();
        }
    }

    private class MouseMotionHandler implements MouseMotionListener{
        @Override
        public void mouseDragged(MouseEvent e) {
            if (current != null){
                int x = e.getX();
                int y = e.getY();

                current.setPosition(new Dimension(x - SIDE_LENGTH / 2, y - SIDE_LENGTH / 2));
                repaint();
            }
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            if (find(e.getPoint()) == null)
                setCursor(Cursor.getDefaultCursor());
            else setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    public void addChannel(int _node1ID, int _node2ID, int _weight, boolean _duplex){
        GNode node1 = null, node2 = null;

        for (GNode n: nodes)
            if (n.getID() == _node1ID)
                node1 = n;

        for (GNode n: nodes)
            if (n.getID() == _node2ID)
                node2 = n;

        if (node1 != null && node2 != null) {
            int newID = AlgorithmRunner.addChannel(10, _node1ID, _node2ID, _duplex);

            GChannel channel = new GChannel(newID, node1, node2, _weight, _duplex);
            channels.add(channel);
        }


        repaint();
    }

    public void deleteChannel(int _node1ID, int _node2ID){
        GChannel channel = null;

        for (GChannel chann: channels)
            if (chann.getNode1ID() == _node1ID && chann.getNode2ID() == _node2ID)
                channel = chann;

        if (channel != null)
            channels.remove(channel);

        repaint();
    }

    public void setNodes(Vector<GNode> _nodes) {
        nodes = _nodes;
    }

    public void setChannels(Vector<GChannel> _channels) {
        channels = _channels;
    }

    public void setPackets(Vector<GPacket> _packets) {
        packets = _packets;
    }

    public GNode getNodeByID (int id) {
        for (GNode node: nodes)
            if (node.getID() == id)
                return node;
        return null;
    }

    public Vector<GPacket> getPackets() {
        return packets;
    }
}
