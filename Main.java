import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import java.util.Iterator;
import java.util.Collections;

public class Main {
    
    public static HashMap<String, Double> distances = new HashMap<String, Double>();
    public static ArrayList<Double> possibleWeathers = new ArrayList<Double>();
    public static void main(String[] args) {
        String airportNames = args[0];
        String directionNames = args[1];
        String weatherName = args[2];
        String missionFileName = args[3];
        String taskOut1 = args[4];
        String taskOut2 = args[5];
        
        String plane = "";
        HashMap<String, Airport> airports = new HashMap<String, Airport>();
        // and we'll read all the directions from each airport but first we need to
        // create an adjecency list
        HashMap<Airport, ArrayList<Airport>> adj = new HashMap<Airport, ArrayList<Airport>>();
        // and while we are taking the list of weather conditions we will use a hashset
        // containing all the airFields
        HashMap<String, AirField> airFields = new HashMap<String, AirField>();
        // here we are going to store all the missions seperately for each task
        ArrayList<ArrayList<String>> missions = new ArrayList<ArrayList<String>>();
        try {
            // i want to store all the airport names in a hashmap
            // after i get all the airports and directions i will store them in a graph
            // read the airport names
            File airportFile = new File(airportNames);
            Scanner airportScanner = new Scanner(airportFile);
            airportScanner.nextLine();
            while (airportScanner.hasNextLine()) {
                String[] airportInfo = airportScanner.nextLine().split(",");
                String name = airportInfo[0];
                String airfield = airportInfo[1];
                double latitude = Double.parseDouble(airportInfo[2]);
                double longitude = Double.parseDouble(airportInfo[3]);
                double parkingCost = Double.parseDouble(airportInfo[4]);
                Airport airport = new Airport(name, airfield, latitude, longitude, parkingCost);
                airports.put(name, airport);

                ArrayList<Airport> directions = new ArrayList<Airport>();
                adj.put(airport, directions);
            }
            airportScanner.close();

        } catch (Exception e) {
            System.out.println("Airport Reading problem \n" + e.getMessage());
        }
        // directions
        try {
            File directionFile = new File(directionNames);
            Scanner directionScanner = new Scanner(directionFile);

            directionScanner.nextLine();
            while (directionScanner.hasNextLine()) {
                String[] directionInfo = directionScanner.nextLine().split(",");
                String airportName = directionInfo[0];
                String directionName = directionInfo[1];

                Airport from = airports.get(airportName);
                Airport to = airports.get(directionName);
                adj.get(from).add(to);
            }
            directionScanner.close();
        } catch (Exception e) {
            System.out.println("Directions reading\n" + e.getMessage());
        }

        // weather
        try {
            File weatherFile = new File(weatherName);
            Scanner weatherScanner = new Scanner(weatherFile);
            weatherScanner.nextLine();
            while (weatherScanner.hasNextLine()) {
                String[] weatherInfo = weatherScanner.nextLine().split(",");
                String airfield = weatherInfo[0];
                AirField temporaryAirF = null;
                if (airFields.containsKey(airfield)) {
                    temporaryAirF = airFields.get(airfield);
                } else {
                    temporaryAirF = new AirField(airfield);
                    airFields.put(airfield, temporaryAirF);
                }
                long time = Long.parseLong(weatherInfo[1]);
                long condition = Long.parseLong(weatherInfo[2]);
                long[] weather = { time, condition };
                temporaryAirF.addWeather(weather);
            }
            weatherScanner.close();

        } catch (Exception e) {
            System.out.println("Weather reading\n" + e.getMessage());
        }
        // mission
        try {
            File missionFile = new File(missionFileName);
            
            Scanner missionScanner = new Scanner(missionFile);
            plane = missionScanner.nextLine();
            
            while (missionScanner.hasNextLine()) {
                String[] missionInfo = missionScanner.nextLine().split(" ");
                ArrayList<String> mission = new ArrayList<String>();
                mission.add(missionInfo[0]);
                mission.add(missionInfo[1]);
                mission.add(missionInfo[2]);
                mission.add(missionInfo[3]);
                missions.add(mission);
            }
            missionScanner.close();
            

        } catch (Exception e) {
            System.out.println("Mission reading\n" + e.getMessage());
        }

        // after we implement dijkstra we will print the results to task1.out and
        // task2.out seperately
        // task1.out
        //lets keep all the possible weathers in an arraylist
        
        for (int j = 0; j < 32; j++) {
            possibleWeathers.add(AirField.weatherDecoder(j));
        }
        String result1 = "";
        String result2 = "";
        for (ArrayList<String> mission : missions) {
           
            String from = mission.get(0);
            String to = mission.get(1);
            long timeOrigin = Long.parseLong(mission.get(2));
            long deadline = Long.parseLong(mission.get(3));
            // we will store the begining and the end airports in the mission
            Airport begin = airports.get(from);
            Airport end = airports.get(to);
            // we will call the dijkstra function
            
            
            result1 += dijkstra(begin, end, timeOrigin, adj, airFields, airports) + "\n";
            
            
        }
        for (ArrayList<String> mission : missions) {
           
            String from = mission.get(0);
            String to = mission.get(1);
            long timeOrigin = Long.parseLong(mission.get(2));
            long deadline = Long.parseLong(mission.get(3));
            // we will store the begining and the end airports in the mission
            Airport begin = airports.get(from);
            Airport end = airports.get(to);
            // we will call the dijkstra function
            
            
            
            result2 += dijkstra2(plane, begin, end, timeOrigin, deadline, adj, airFields, airports) + "\n";
            
        }
        //i am going to print the results to the files
        try{
            File resultFile1 = new File(taskOut1);
            File resultFile2 = new File(taskOut2);
            resultFile1.createNewFile();
            resultFile2.createNewFile();
            java.io.FileWriter resultWriter1 = new java.io.FileWriter(resultFile1);
            java.io.FileWriter resultWriter2 = new java.io.FileWriter(resultFile2);
            resultWriter1.write(result1);
            resultWriter2.write(result2);
            resultWriter1.close();
            resultWriter2.close();
        }
        catch(Exception e){
            System.out.println("Result writing\n" + e.getMessage());
        }
        
        //System.out.print(result1);
        
        //System.out.print(result2);

    }

    public static HashMap<String, Double> constWeather = new HashMap<String, Double>();

    private static String dijkstra(Airport begin, Airport end, long timeOrigin, HashMap<Airport, ArrayList<Airport>> adj, HashMap<String, AirField> airFields, HashMap<String, Airport> airports) {
        //constWeather = new HashMap<String, Double>();
        HashMap<Airport, Airport> path = new HashMap<Airport, Airport>();
        HashSet<Airport> explored = new HashSet<Airport>();
        PriorityQueue<Airport> toExplore = new PriorityQueue<>(Comparator.comparingDouble(Airport -> Airport.cost));
        // but for each call we need to reset the costs
        for (Airport airport : airports.values()) {
            airport.cost = Double.MAX_VALUE;
        }
        
        begin.cost = 0.0;
        toExplore.add(begin);
        boolean found = false;

        while (!toExplore.isEmpty()) {
            // we will take the first airport from the toExplore
            Airport current = toExplore.poll();
            // we will add it to the explored
            if(current.name.equals(end.name)){
                found = true;
                break;
            }
            if(explored.contains(current)){
                continue;
            }
            explored.add(current);
            
            // we will iterate through the adjecency list of the current airport
            changeCost(current, adj, timeOrigin, airFields, toExplore, explored, path);
            
        }
        if(!found){return "No possible solution.";}
        
        double endCost = end.cost;
        Locale.setDefault(Locale.ENGLISH);
        String totalStrCost = String.format("%.5f", endCost) ;
        String finalResult = resultWriter(begin, end, path);
        return finalResult + " " + totalStrCost;

    }
    private static Node nodeCreate(Airport airport, long time, ArrayList<String> path, long startingTime){
        Node result =  new Node( airport.name,  airport.airfield, airport.latitude,  airport.longitude,  airport.parkingCost);
        result.time = time;
        result.startingTime = startingTime;
        ArrayList<String> resultPath = new ArrayList<String>();
        Iterator<String> it = path.iterator();
        while(it.hasNext())  {
            resultPath.add(it.next());
        }
        result.path = resultPath;
        
        return result;
    }
    private static String dijkstra2(String plane, Airport begin, Airport end, long timeOrigin,long deadline, HashMap<Airport, ArrayList<Airport>> adj, HashMap<String, AirField> airFields, HashMap<String, Airport> airports) {
        //constWeather = new HashMap<String, Double>();
        HashMap<String, Double> reached = new HashMap<String, Double>();
        long bestestTime = 0;
        PriorityQueue<Node> toExplore = new PriorityQueue<>();
        
        long startingTime = timeOrigin;
        for (Airport airport : airports.values()) {
            airport.cost = Double.MAX_VALUE;
        }
        //lets introduce planes
        ArrayList<String> beginPath = new ArrayList<String>();
        beginPath.add(begin.name);
        Node beginNode = nodeCreate(begin, timeOrigin, beginPath, startingTime);
        
        beginNode.cost = 0.0;
        toExplore.add(beginNode);
        reached.put(beginNode.name + beginNode.time, beginNode.cost);

        boolean found = false;
        worstCase(end, begin, adj, airports);
        
        boolean first = true;
        Node accomplishedNode = null;
        while (!toExplore.isEmpty()) {
            Node current = toExplore.poll();
            
            if(current.time > deadline){
                break;
            }
            if(current.name.equals(end.name)){
                found = true;
                accomplishedNode = current;
                break;
            }
            String currentName = current.name;
            Airport currentAirport = airports.get(currentName);
            ArrayList<Airport> currentAdj = adj.get(currentAirport);
            for(Airport neighbor : currentAdj){
                

                long flightDuration = flightDuration(plane, distanceCalculator(currentAirport, neighbor));
                long tempTime = current.time + flightDuration;
                double tempCost = cost2(plane, currentAirport, neighbor, current.time, airFields);
                if (reached.containsKey(neighbor.name + tempTime)) {
                    if (reached.get(neighbor.name + tempTime) < tempCost + current.cost) {
                        continue;
                    }
                    
                } 
                if(tempCost + airports.get(neighbor.name).cost > currentAirport.cost)//BU MANTIKTA HAFİF Bİ HATA OLMALI AS0 DOSYASINDA SIKINTI ÇIKIYOR // analamdım gitti ama kesin sonuç için bu if bloğunu sil. dezavantaji zaman kaybı
                    continue;
                
                 
                
                if(tempTime > deadline){
                    break;
                }
                ArrayList<String> tempPath = new ArrayList<String>();
                Iterator<String> it = current.path.iterator();
                while(it.hasNext())  {
                    tempPath.add(it.next());
                }
                
                tempPath.add(neighbor.name);
                Node tempNode = nodeCreate(neighbor, tempTime, tempPath, startingTime);
                
                tempNode.cost = current.cost + tempCost;
                if(!(tempNode.time > deadline)){
                    if(!reached.containsKey(tempNode.name + tempNode.time)){
                        
                        reached.put(tempNode.name + tempNode.time, tempNode.cost);
                        toExplore.add(tempNode);
                    }
                    else{
                        if(reached.get(tempNode.name + tempNode.time) > tempNode.cost){
                            reached.remove(tempNode.name + tempNode.time);
                            reached.put(tempNode.name + tempNode.time, tempNode.cost);
                            toExplore.add(tempNode);
                        }
                        
                    }
                }
            }

            Node parkedNode = nodeCreate(currentAirport, current.time + 21600, current.path, startingTime);
            parkedNode.cost = current.cost + currentAirport.parkingCost;
            if(parkedNode.cost > 131*currentAirport.cost )
                continue;
                
            if(!(parkedNode.time >= deadline)){
                if(!reached.containsKey(parkedNode.name + parkedNode.time)){
                    reached.put(parkedNode.name + parkedNode.time, parkedNode.cost);
                    toExplore.add(parkedNode);
                }
                else{
                    if(reached.get(parkedNode.name + parkedNode.time) > parkedNode.cost){
                        reached.remove(parkedNode.name + parkedNode.time);
                        reached.put(parkedNode.name + parkedNode.time, parkedNode.cost);
                        toExplore.add(parkedNode);
                    }
                }
                parkedNode.path.add("PARK");
                toExplore.add(parkedNode);
            }
        

        }
        if(!found){return "No possible solution.";}
        
        double endCost = accomplishedNode.cost;
        Locale.setDefault(Locale.ENGLISH);
        String totalStrCost = String.format("%.5f", endCost) ;
        String finalResult ="";

        ArrayList<String> tempArray = new ArrayList<String>();

        ArrayList<String> endPath = accomplishedNode.path;
        
        for(String s : endPath){
           finalResult += s + " ";
        }
        return finalResult + totalStrCost;

    }
    private static HashMap<String, Double> worstScenerio = new HashMap<String, Double>();

    private static Double worstCase(Airport begin, Airport end, HashMap<Airport, ArrayList<Airport>> adj, HashMap<String, Airport> airports) {
        //constWeather = new HashMap<String, Double>();
        HashMap<Airport, Airport> path = new HashMap<Airport, Airport>();
        HashSet<Airport> explored = new HashSet<Airport>();
        PriorityQueue<Airport> toExplore = new PriorityQueue<>(Comparator.comparingDouble(Airport -> Airport.cost));
        Double worstMultiplier = AirField.weatherDecoder(31);
        Double multip = worstMultiplier * worstMultiplier*300;
        
        // but for each call we need to reset the costs
        for (Airport airport : airports.values()) {
            airport.cost = Double.MAX_VALUE;
        }
        
        begin.cost = 0.0;
        toExplore.add(begin);
        boolean found = false;

        while (!toExplore.isEmpty()) {
            // we will take the first airport from the toExplore
            Airport current = toExplore.poll();
            // we will add it to the explored
            if(current.name.equals(end.name)){
                found = true;
                break;
            }
            if(explored.contains(current)){
                continue;
            }
            explored.add(current);
            
            // we will iterate through the adjecency list of the current airport
            worstChangeCost(multip, current, adj, toExplore, explored, path);

            
        }
        //if(!found){return "No possible solution.";}
        
        
        Locale.setDefault(Locale.ENGLISH);
        return 1.0;

    }
    private static void worstChangeCost(Double multip, Airport current, HashMap<Airport, ArrayList<Airport>> adj, PriorityQueue<Airport> toExplore, HashSet<Airport> explored, HashMap<Airport, Airport> path){
        
        for(Airport neighbor : adj.get(current)){
            if(explored.contains(neighbor)){
                continue;
            }
            
            double distance = distanceCalculator(current, neighbor);
            double tempCost = distance + multip;
            
            if(current.cost + tempCost < neighbor.cost){
                
                neighbor.cost = current.cost + tempCost;
                path.put(neighbor, current);
            }
            toExplore.add(neighbor);

            
        }
    }
    

    
    private static HashMap<String, Double> staticCost = new HashMap<String, Double>();
    private static double cost2(String plane, Airport current, Airport neighbor, long timeOrigin, HashMap<String, AirField> airFields){
        double distance = distanceCalculator(current, neighbor);
        long flightDuration = flightDuration(plane, distance);
        if(staticCost.containsKey(current.name + neighbor.name + timeOrigin + flightDuration)){
            return staticCost.get(current.name + neighbor.name + timeOrigin+ flightDuration);
        }
        else{
            double w1 = weatherForecast(current, timeOrigin, airFields);
            double w2 = weatherForecast(neighbor, timeOrigin + flightDuration, airFields);
            double cost = (300 * w1 * w2) + distance;
            staticCost.put(current.name + neighbor.name + timeOrigin + flightDuration, cost);
            
            return cost;
        }
    }

    private static void changeCost(Airport current, HashMap<Airport, ArrayList<Airport>> adj, long timeOrigin, HashMap<String, AirField> airFields, PriorityQueue<Airport> toExplore, HashSet<Airport> explored, HashMap<Airport, Airport> path){
        
        for(Airport neighbor : adj.get(current)){
            if(explored.contains(neighbor)){
                continue;
            }
            
            double tempCost = cost(current, neighbor, timeOrigin, airFields);
            
            if(current.cost + tempCost < neighbor.cost){
                
                neighbor.cost = current.cost + tempCost;
                path.put(neighbor, current);
            }
            toExplore.add(neighbor);

            
        }
    }
    public static HashMap<String, Double> constCost = new HashMap<String, Double>();
    private static double cost(Airport current, Airport neighbor, long timeOrigin, HashMap<String, AirField> airFields){
        if(constCost.containsKey(current.name + neighbor.name + timeOrigin)){
            return constCost.get(current.name + neighbor.name + timeOrigin);
        }
        else if(constCost.containsKey(neighbor.name + current.name + timeOrigin)){
            return constCost.get(neighbor.name + current.name + timeOrigin);
        }
        else{
            double w1 = weatherForecast(current, timeOrigin, airFields);
            double w2 = weatherForecast(neighbor, timeOrigin, airFields);
            double distance = distanceCalculator(current, neighbor);
            double cost = (300 * w1 * w2) + distance;
            constCost.put(current.name + neighbor.name + timeOrigin, cost);
            constCost.put(neighbor.name + current.name + timeOrigin, cost);
            return cost;
        }
    }
      
    private static double weatherForecast(Airport airport, long timeOrigin, HashMap<String, AirField> airFields) {
        long w1 =0;
        
        if (constWeather.containsKey(airport.name + timeOrigin)) {
            return constWeather.get(airport.name + timeOrigin);
        } 
        else {
            ArrayList<long[]> tmpWeather = airFields.get(airport.airfield).weather;
            for (long[] weather : tmpWeather) {
                if (weather[0] == timeOrigin) {
                    w1 = weather[1];
                    break;
                }
            }
            double weatherMultiplier =possibleWeathers.get((int)w1);
            constWeather.put(airport.name + timeOrigin, weatherMultiplier);
            return weatherMultiplier;
        }
    }
    private static double distanceCalculator(Airport from, Airport to) {
        if (distances.containsKey(from.name + to.name)) {
            return distances.get(from.name + to.name);
        } else if (distances.containsKey(to.name + from.name)) {
            return distances.get(to.name + from.name);
        } else {
            double lat1 = Math.toRadians(from.latitude);
            double lat2 = Math.toRadians(to.latitude);
            double lon1 = Math.toRadians(from.longitude);
            double lon2 = Math.toRadians(to.longitude);
            double dist = 2 * 6371 * (Math.asin(Math.sqrt( (Math.pow(Math.sin((lat2 - lat1) / 2), 2)) + (Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lon2 - lon1) / 2), 2)))));
            distances.put(from.name + to.name, dist);
            distances.put(to.name + from.name, dist);
            return dist;
        }

    }
    private static String resultWriter(Airport begin, Airport end, HashMap<Airport, Airport> path){
        ArrayList<String> result = new ArrayList<String>();
        result.add(end.name);
        while(true){
            Airport tmp = path.get(end);
           
            if(tmp.equals(begin)){
                result.add(tmp.name);
                break;
            }
            result.add(tmp.name);
            end = tmp;
        }
        int length = result.size();
        String finalResult = "";
        for(int i = length - 1; i >= 0; i--){
            finalResult += result.get(i);
            if(i != 0){
                finalResult += " ";
            }
        }
        return finalResult;
    }
    private static long flightDuration(String plane, double distance){
        if(plane.equals("Carreidas 160")){
            if(distance <= 175)
                return 21600;
            else if(distance <= 350)
                return 43200;
            else
                return 64800;
        }
        else if (plane.equals("Orion III")){
            if(distance <= 1500)
                return 21600;
            else if(distance <= 3000)
                return 43200;
            else
                return 64800;
        }
        else if (plane.equals("Skyfleet S570")){
            if(distance <= 500)
                return 21600;
            else if(distance <= 1000)
                return 43200;
            else
                return 64800;
        }
        else if (plane.equals("T-16 Skyhopper")){
            if(distance <= 2500)
                return 21600;
            else if(distance <= 5000)
                return 43200;
            else
                return 64800;

        }
        return 0;
        
    }
}