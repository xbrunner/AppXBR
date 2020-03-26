package gis.ikg.ethz.helloandroid;

public class Treasures {
    private String TreasureName;
    private double Longitude;
    private double Latitude;
    private int MaxCoins;
    private boolean found = true;

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
}
