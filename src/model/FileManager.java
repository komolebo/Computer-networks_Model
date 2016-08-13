package model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import view.FrameMain;
import view.elements.GChannel;
import view.elements.GNode;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by oleh on 13.12.15.
 */
public class FileManager {
    public static void dump(String name) throws IOException {
        JSONObject obj = new JSONObject();

        JSONObject jsonNodes = new JSONObject();
        for (Node node: AlgorithmRunner.getNodes()) {
            JSONObject jsonNode = new JSONObject();

            jsonNode.put("x", FrameMain.controllerShow.getNodeByID(node.getID()).getPosition().width);
            jsonNode.put("y", FrameMain.controllerShow.getNodeByID(node.getID()).getPosition().height);

            jsonNodes.put(node.getID(), jsonNode);
        }

        JSONObject jsonChannels = new JSONObject();
        for (Channel channel: AlgorithmRunner.getChannels()) {
            JSONObject jsonChannel = new JSONObject();

            jsonChannel.put("weight", channel.getWeight());
            jsonChannel.put("id1", channel.getNode1().getID());
            jsonChannel.put("id2", channel.getNode2().getID());
            jsonChannel.put("type", channel.isDuplex());

            jsonChannels.put(channel.getID(), jsonChannel);
        }

        obj.put("nodes", jsonNodes);
        obj.put("channels", jsonChannels);

        FileWriter file = new FileWriter("files/" + name + ".json");
        file.write(obj.toJSONString());
        file.flush();
        file.close();
    }

    public static void read(String name) throws IOException, ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader("files/" + name));

        Vector<Node> nodes = new Vector<>();
        Vector<GNode> gNodes = new Vector<>();
        Vector<Channel> channels = new Vector<>();
        Vector<GChannel> gChannels = new Vector<>();


        JSONObject jsonNodes = (JSONObject) obj.get("nodes");
        for (Object i: jsonNodes.keySet()){
            Integer id = Integer.valueOf(i.toString());

            nodes.add(new Node(id));

            Integer x = Integer.valueOf(String.valueOf(((JSONObject) jsonNodes.get(i)).get("x")));
            Integer y = Integer.valueOf(String.valueOf(((JSONObject) jsonNodes.get(i)).get("y")));
            gNodes.add(new GNode(id, x, y));
        }

        JSONObject jsonChannels = (JSONObject) obj.get("channels");
        for(Object i: jsonChannels.keySet()){
            Integer id = Integer.valueOf(i.toString());

            Integer weight = Integer.valueOf(String.valueOf(((JSONObject) jsonChannels.get(i)).get("weight")));
            Integer id1 = Integer.valueOf(String.valueOf(((JSONObject) jsonChannels.get(i)).get("id1")));
            Integer id2 = Integer.valueOf(String.valueOf(((JSONObject) jsonChannels.get(i)).get("id2")));
            boolean type = Boolean.valueOf(String.valueOf(((JSONObject) jsonChannels.get(i)).get("type")));

            Node node1 = getNodeByID(nodes, id1);
            Node node2 = getNodeByID(nodes, id2);
            channels.add(new Channel(id, weight, node1, node2, type));

            GNode gNode1 = getGNodeByID(gNodes, id1);
            GNode gNode2 = getGNodeByID(gNodes, id2);
            gChannels.add(new GChannel(id, gNode1, gNode2, weight, type));
        }


        AlgorithmRunner.setNodes(nodes);
        AlgorithmRunner.setChannels(channels);
        FrameMain.controllerShow.setNodes(gNodes);
        FrameMain.controllerShow.setChannels(gChannels);

        FrameMain.controllerShow.updateUI();
    }

    private static Node getNodeByID(Vector<Node> nodes, int id) {
        for (Node node: nodes)
            if (node.getID() == id)
                return node;
        return null;
    }

    private static GNode getGNodeByID(Vector<GNode> nodes, int id) {
        for (GNode node: nodes)
            if (node.getID() == id)
                return node;
        return null;
    }
}

