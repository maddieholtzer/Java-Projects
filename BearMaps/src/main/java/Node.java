/**
 * Created by Matt on 4/17/2017.
 */
public class Node {
    int depth;
    int imgNumber;
    String img;
    Node southeast;
    Node southwest;
    Node northeast;
    Node northwest;
    double ullat;
    double ullon;
    double lrlat;
    double lrlon;

    public Node(int depth, int img, double ullat, double ullon, double lrlat, double lrlon) {
        this.depth = depth;
        this.img = "img/" + img + ".png";
        this.imgNumber = img;
        this.ullat = ullat;
        this.ullon = ullon;
        this.lrlat = lrlat;
        this.lrlon = lrlon;
        this.southeast = null;
        this.southwest = null;
        this.northeast = null;
        this.northwest = null;
    }
}

