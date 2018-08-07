import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    private int numvertices;
    private int numedges;
    private HashMap<Long, GNode> vertices;
    private ArrayList<Edge> edges;
    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        this.vertices = new HashMap<>();
        this.edges = new ArrayList<>();
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        ArrayList<Long> temp = new ArrayList<>();
        for (Long key: this.vertices.keySet()) {
            if (this.vertices.get(key).getAdj() == 0) {
                temp.add(key);
            }
        }
        while (!temp.isEmpty()) {
            removeNode(temp.remove(0));
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> temp = new ArrayList<>();
        for (Long key: this.vertices.keySet()) {
            temp.add(this.vertices.get(key).getid());
        }
        return temp;
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        return vertices.get(v).getAdjacent();
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        double lonv = lon(v);
        double latv = lat(v);
        double lonw = lon(w);
        double latw = lat(w);
        double distance = Math.pow((((lonv - lonw) * (lonv - lonw))
                + ((latv - latw) * (latv - latw))), .5);
        return distance;
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        GNode w = new GNode(0, lat, lon);
        long closest = w.getid();
        this.vertices.put(w.getid(), w);
        double distance = 1000000;
        double check;
        for (Long key: this.vertices.keySet()) {
            if (key != 0) {
                check = distance(key, 0);
                if (check < distance) {
                    distance = check;
                    closest = key;
                }
            }
        }
        return closest;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        return this.vertices.get(v).getLon();
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return this.vertices.get(v).getLat();
    }

    public void addNode(long id, double lat, double lon) {
        GNode temp = new GNode(id, lat, lon);
        this.vertices.put(id, temp);
        this.numvertices++;
    }

    public void addEdge(long id1, long id2) {
        Edge newEdge = new Edge(id1, id2);
        this.vertices.get(id1).addAdjacent(id2);
        this.vertices.get(id2).addAdjacent(id1);
        edges.add(newEdge);
    }

    public void addEdges(ArrayList<Long> edgesToAdd) {
        for (int i = 0; i < edgesToAdd.size() - 1; i++) {
            addEdge(edgesToAdd.get(i), edgesToAdd.get(i + 1));
        }
    }

    public void removeNode(long v) {
        vertices.remove(v);
        this.numvertices--;
    }

    public HashMap<Long, GNode> getVertices() {
        return this.vertices;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }
}
