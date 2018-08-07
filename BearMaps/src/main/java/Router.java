//import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
//import java.util.HashMap;
//import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest, 
     * where the longs are node IDs.
     */

    static class GNodeComparator implements Comparator<GNode> {
        public int compare(GNode g1, GNode g2) {
            return (int) (g1.getDistance() - g2.getDistance());
        }
    }

    public static LinkedList<Long> shortestPath(GraphDB g, double stlon,
                                                double stlat, double destlon, double destlat) {
        ArrayList<Long> marked = new ArrayList<>();
        ArrayList<Long> affected = new ArrayList<>();
        Comparator<GNode> comparator = new GNodeComparator();
        LinkedList<Long> thePath = new LinkedList<>();
        Long startID = g.closest(stlon, stlat);
        Long finishID = g.closest(destlon, destlat);
        GNode currentNode = g.getVertices().get(startID);
        PriorityQueue<GNode> priority = new PriorityQueue<>(1, comparator);
        currentNode.resetDistance(0);
        while (currentNode.getid() != finishID) {
            if (!marked.contains(currentNode.getid())) {
                for (int i = 0; i < currentNode.getAdj(); i++) {
                    GNode tempnode = g.getVertices().get(currentNode.getAdjacents().get(i));
                    if (!marked.contains(tempnode.getid())) {
                        double distancefromend = g.distance(finishID, tempnode.getid());
                        double alt = currentNode.getDistance()
                                + g.distance(currentNode.getid(), tempnode.getid());
                        double alt2 = alt + distancefromend;
                        if (alt2 < tempnode.getDistance()) {
                            tempnode.resetDistance(alt2);
                            tempnode.resetParent(currentNode);
                            affected.add(tempnode.getid());
                        }
                        priority.add(tempnode);
                    }
                }
            }
            marked.add(currentNode.getid());
            currentNode = priority.remove();
            affected.add(currentNode.getid());
        }
        while (currentNode.getid() != startID) {
            thePath.addFirst(currentNode.getid());
            currentNode = currentNode.getParent();
        }
        thePath.addFirst(currentNode.getid());
        for (int i = 0; i < affected.size(); i++) {
            g.getVertices().get(affected.get(i)).resetDistance(Integer.MAX_VALUE);
            g.getVertices().get(affected.get(i)).resetParent(null);
        }
        return thePath;
    }
}
