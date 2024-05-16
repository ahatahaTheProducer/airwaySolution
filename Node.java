import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ArrayList;
public class Node implements Comparable<Node>{
    String name = "";
    String airfield = "";
    double latitude = 0.0;
    double longitude = 0.0;
    double parkingCost = 0.0;
    Double cost;
    long time;
    double lastBeforePark = Double.MAX_VALUE;
    
    long startingTime;
    
    ArrayList<String> path = new ArrayList<>();
    public Node(String name, String airfield, double latitude, double longitude, double parkingCost){
        this.name = name;
        this.airfield = airfield;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingCost = parkingCost;
        this.cost = Double.MAX_VALUE;
    }
    public String toString(){ 
        return name + " " + airfield + " " + latitude + " " + longitude + " " + parkingCost;
    }
    @Override
    public int compareTo(Node other) {
        return Double.compare(cost, other.cost);
    }
}
