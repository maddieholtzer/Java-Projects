/**
 * Created by Matt on 4/19/2017.
 */
import java.util.ArrayList;

public class GNode {
    private double lat;
    private double lon;
    private long id;
    private ArrayList<Long> adjacent;
    private int adj;
    private boolean marked;
    private double distance;
    private GNode parent;

    public GNode(long id, double lat, double lon){
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.adjacent = new ArrayList<>();
        this.adj = 0;
        this.marked = false;
        this.parent = null;
        this.distance = Integer.MAX_VALUE;
    }

    public double getLat(){
        return this.lat;
    }

    public double getLon(){
        return this.lon;
    }

    public long getid(){
        return this.id;
    }

    public Iterable<Long> getAdjacent(){
        return this.adjacent;
    }

    public ArrayList<Long> getAdjacents(){
        return this.adjacent;
    }

    public void addAdjacent(Long adj){
        this.adjacent.add(adj);
        this.adj++;
    }

    public int getAdj(){
        return this.adj;
    }

    public void markNode(){
        this.marked = true;
    }

    public double getDistance(){
        return this.distance;
    }

    public void resetDistance(double x){
        this.distance = x;
    }

    public void resetParent(GNode par){
        this.parent = par;
    }

    public GNode getParent(){
        return this.parent;
    }

    public boolean isMarked(){
        return this.marked;
    }
}
