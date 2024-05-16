import java.util.ArrayList;

public class AirField {
    String fieldCode  = "";
    ArrayList<long[]> weather = new ArrayList<long[]>();
    
    public AirField(String fieldCode){
        this.fieldCode = fieldCode;
    }
    public void addWeather(long[] weather){
        
        long time = weather[0];
        long[] tmpweather = {time, weather[1]};
        this.weather.add(tmpweather);
    }
    public static double weatherDecoder(int weatherCode){
        //we are going to take the code and turn it to its binary representation
        //we are going to use a long array to store the binary representation
        String binaryRepresentation = String.format("%" + 5 + "s", Integer.toBinaryString(weatherCode)).replace(' ', '0');
        int bw = Integer.parseInt(String.valueOf(binaryRepresentation.charAt(0)));
        int br = Integer.parseInt(String.valueOf(binaryRepresentation.charAt(1)));
        int bs = Integer.parseInt(String.valueOf(binaryRepresentation.charAt(2)));
        int bh = Integer.parseInt(String.valueOf(binaryRepresentation.charAt(3)));
        int bb = Integer.parseInt(String.valueOf(binaryRepresentation.charAt(4)));
        
        //we are going to calculate the weather multiplier value
        double weatherMultiplier = (bw * 1.05 + (1 - bw)) * (br * 1.05 + (1 - br)) * (bs * 1.10 + (1 - bs)) * (bh * 1.15 + (1 - bh)) * (bb * 1.20 + (1 - bb));
        return weatherMultiplier;
    }
}
