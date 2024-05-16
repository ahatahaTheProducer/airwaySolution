import java.util.HashMap;
public class Airport implements Comparable<Airport>{
    String name = "";
    String airfield = "";
    double latitude = 0.0;
    double longitude = 0.0;
    double parkingCost = 0.0;
    Double cost;
    
    
    public Airport(String name, String airfield, double latitude, double longitude, double parkingCost){
        this.name = name;
        this.airfield = airfield;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingCost = parkingCost;
        this.cost = 14700000000.0;
    }
    public String toString(){
        return name + " " + airfield + " " + latitude + " " + longitude + " " + parkingCost;
    }
    @Override
    public int compareTo(Airport other) {
        return Double.compare(this.cost, other.cost);
    }
}
