import java.util.ArrayList;

/**
 * Created by Matt on 4/17/2017.
 */

public class QuadTree {
    private Node front;
    private double ullat;
    private double ullon;
    private double lrlat;
    private double lrlon;
    private int depth;
    private ArrayList<ArrayList<Node>> displayarray;

    public QuadTree() {
        this.front = new Node(0, 0, 37.892195547244356, -122.2998046875,
                37.82280243352756, -122.2119140625);
        this.depth = 0;
        this.displayarray = new ArrayList<>();
    }

    public double getullat() {
        return this.ullat;
    }

    public double getullon() {
        return this.ullon;
    }

    public double getlrlat() {
        return this.lrlat;
    }

    public double getlrlon() {
        return this.lrlon;
    }

    public void raster(int depth, double ullon, double ullat, double lrlon, double lrlat) {
        this.depth = depth;
        rasterHelper(this.front, ullon, ullat, lrlon, lrlat);
    }

    public void rasterHelper(Node a, double ullon, double ullat, double lrlon, double lrlat) {
        /**
         * Split current node into four sections
         * check each of them to see if they contain the space
         * Repeat until depth is gone
         * Add any new nodes that are in box to QuadTree
         */
        Node[] splitNode = split(a);
        for (int x = 0; x < 4; x++) {
            if (checkIfContains(splitNode[x], ullon, ullat, lrlon, lrlat)) {
                //if (x == 0) {
                    //a.northwest = splitNode[x];
                //}
                //if (x == 1) {
                    //a.northeast = splitNode[x];
                //}
                //if (x == 2) {
                    //a.southwest = splitNode[x];
                //}
                //if (x == 3) {
                    //a.southeast = splitNode[x];
                //}
                if (splitNode[x].depth != this.depth) {
                    rasterHelper(splitNode[x], ullon, ullat, lrlon, lrlat);
                }
                else if (splitNode[x].depth == this.depth) {
                    addToDisplay(splitNode[x]);
                    if (splitNode[x].ullat >= ullat) {
                        this.ullat = splitNode[x].ullat;
                    }
                    if (splitNode[x].ullon <= ullon) {
                        this.ullon = splitNode[x].ullon;
                    }
                    if (splitNode[x].lrlat <= lrlat) {
                        this.lrlat = splitNode[x].lrlat;
                    }
                    if (splitNode[x].lrlon >= lrlon) {
                        this.lrlon = splitNode[x].lrlon;
                    }
                }
            }
        }
    }

    public Node[] split(Node a) {
        Node[] splitNode = new Node[4];
        double centerlat = (a.ullat + a.lrlat) / 2;
        double centerlon = (a.ullon + a.lrlon) / 2;
        splitNode[0] = new Node(a.depth + 1, a.imgNumber * 10 + 1, a.ullat, a.ullon, centerlat, centerlon);
        splitNode[1] = new Node(a.depth + 1, a.imgNumber * 10 + 2, a.ullat, centerlon, centerlat, a.lrlon);
        splitNode[2] = new Node(a.depth + 1, a.imgNumber * 10 + 3,
                centerlat, a.ullon, a.lrlat, centerlon);
        splitNode[3] = new Node(a.depth + 1, a.imgNumber * 10 + 4,
                centerlat, centerlon, a.lrlat, a.lrlon);
        return splitNode;
    }

    public boolean checkIfContains(Node a, double ullon, double ullat, double lrlon, double lrlat) {
        if (a.lrlon < ullon) {
            return false;
        }
        if (a.ullon > lrlon) {
            return false;
        }
        if (a.lrlat > ullat) {
            return false;
        }
        if (a.ullat < lrlat) {
            return false;
        }
        return true;
    }

    public void addToDisplay(Node a) {
        int x = 0;
        int y = 0;
        if (!this.displayarray.isEmpty()) {
            int counter = this.displayarray.size();
            while (x < counter) {
                if (a.ullat == this.displayarray.get(x).get(0).ullat) {
                    this.displayarray.get(x).add(a);
                    y++;
                }
                x++;
            }
        }
        if (y != 1) {
            ArrayList<Node> temp = new ArrayList<>();
            temp.add(a);
            this.displayarray.add(temp);
        }
    }

    public String[][] displayString() {
        String[][] temp = new String[this.displayarray.size()][this.displayarray.get(0).size()];
        int f = this.displayarray.size();
        int g = this.displayarray.get(0).size();
        for (int i = 0; i < f; i++) {
            for (int j = 0; j < g; j++) {
                temp[i][j] = this.displayarray.get(i).remove(0).img;
            }
        }
        return temp;
    }

}
