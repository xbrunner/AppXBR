package gis.ikg.ethz.helloandroid;

public class Treasures {
    private String TreasureName; //from csv
    private double Longitude; //from csv
    private double Latitude; //from csv
    private int MaxCoins; //from csv
    private boolean found = false; //false if not already found

    public String getTreasureName() {
        return TreasureName;
    }

    public void setTreasureName(String treasureName) {
        TreasureName = treasureName;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public int getMaxCoins() {
        return MaxCoins;
    }

    public void setMaxCoins(int maxCoins) {
        MaxCoins = maxCoins;
    }

    public boolean isFound() { return this.found; }

    public void  alreadyFound() { this.found = true; }
}
