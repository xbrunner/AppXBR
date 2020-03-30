package gis.ikg.ethz.helloandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This activity provides if the user found the Treasure if Dist(User,Treasure) < 5 meter
 * The measure of position, orientation, temperature, speed and average speed are measured
 *  Dist(User,Treasure) < 5 meter has to be calculated
 * The score depends on the average speed and temperature
 */


public class ActivityTwo extends AppCompatActivity implements SensorEventListener {

    private static ActivityTwo instance2 = null;
    private Button backButton;
    private int currentScore = 0;
    private TextView infoBox;
    private TextView distanceBox;
    private TextView temperatureBox;
    private TextView speedBox;
    private TextView avgSpeedBox;
    private double currentDistance = 100;
    private double currentSpeed = 0; //km/h
    private double currentTime = 1; //s
    private double currentAvgSpeed = 0;
    private double currentTemperature = 20;
    private Location treasureLocation = new Location("treasureLocation");
    private LocationManager locationManager;
    private LocationListener locationListener;
    private SensorManager sensorManager;
    private Sensor sensorTemperature;
    private Sensor sensorOrientation;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);


        //Set id for boxes
        infoBox = (TextView)findViewById(R.id.infoBox);
        distanceBox = (TextView)findViewById(R.id.distanceBox);
        temperatureBox = (TextView)findViewById(R.id.temperatureBox);
        speedBox = (TextView)findViewById(R.id.speedBox);
        avgSpeedBox = (TextView)findViewById(R.id.avgSpeedBox);

        //get variables from Intent
        Intent intent = getIntent();
        String treasureName = intent.getStringExtra("currentTreasureName");
        Double treasureLongitude = intent.getDoubleExtra("currentLongitude", 0);
        Double treasureLatitude = intent.getDoubleExtra("currentLatitude", 0);
        Integer treasureMaxCoins = intent.getIntExtra("currentTreasureMaxCoins", 0);
        Boolean currentTreasureIsFound = intent.getBooleanExtra("CurrentTreasureIsFound", false);

        infoBox.setText("You choose :  " + treasureName + "\n You have : " ); //+ Integer.toString(score) +" COINS");

        //Set location of treasure
        treasureLocation.setLongitude(treasureLongitude);
        treasureLocation.setLatitude(treasureLatitude);

        //Button
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myBackButtonAction();
            }
        });

        //Location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location currentLocation) {


                    // Calculate time
                    Chronometer simpleChronometer = (Chronometer) findViewById(R.id.chronometer); // initiate a chronometer
                    simpleChronometer.start(); // start a chronometer
                    currentTime = (double) ((SystemClock.elapsedRealtime() - simpleChronometer.getBase())/1000);

                    // Calculate distance
                    currentDistance = currentLocation.distanceTo(treasureLocation);
                    distanceBox.setText("Distance to treasure: " + Math.round(currentDistance) + " meters");

                    //Calculate current speed
                    currentSpeed = currentLocation.getSpeed();
                    speedBox.setText("Your speed: " + Math.round(currentSpeed) + " km/h");

                    //Calculate average speed
                    currentAvgSpeed += (currentSpeed);


                if (currentTime > 0) {
                     avgSpeedBox.setText("Your average speed: " + Math.round(currentAvgSpeed/currentTime) + "km/h");
                } else avgSpeedBox.setText("Your average speed: 0 km/h" );
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };



        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            },10 );
            return;
        }

        //Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);



        //Calculate location every 1 second
        locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);


        Integer score = intent.getIntExtra("score", 0);

        //Update score
        currentScore = score+treasureMaxCoins;

    }

    //Setup Button "GO!"
    private void myBackButtonAction() {

            // Opening new intent
            Intent backIntent = new Intent(this, MainActivity.class);
            backIntent.putExtra("currentScore", currentScore);
            //myFirstIntent.putExtra("key1","Back to activity one");
            //myFirstIntent.putExtra("key2", 2019);
            startActivity(backIntent);

        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:

        }}
//

//    private int treasureScore = 0;
//    //addScore
//    public int addScore(int currentScore, int treasureScore) {
//
//        Integer newScore = this.currentScore += this.treasureScore;
//        return newScore;
//
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Temperature

        currentTemperature = event.values[0];
        temperatureBox.setText("Temperature: "  + Math.round(currentTemperature));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
