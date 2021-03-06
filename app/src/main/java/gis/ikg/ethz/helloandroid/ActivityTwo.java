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
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity determine if the Treasure is found and return the score to MainActivity
 *
 *  -> This activity measures position, speed & acceleration of the user and also user environment temperature & magnetic field
 *  -> This activity calculates the orientation(acceleration, magnetic field) and AverageSpeed(speed, time)
 *  -> If Dist(User,Treasure) < 10 meter, the treasure is considered as found
 *  -> The score depends on the average speed and temperature like follows: Score = Treasure Score + Temperature(if positive) + AverageSpeed
 *
 * !!!Warning: The game is optimized for a normal speed (KML Playback speed: 1x)!!!
 */

public class ActivityTwo extends AppCompatActivity implements SensorEventListener {
    private Button backButton;
    private Integer currentScore = 0;
    private Integer calculateScore = 0;
    private Integer negativeTemperature = 0;

    private TextView infoBox;
    private TextView distanceBox;
    private TextView temperatureBox;
    private TextView speedBox;
    private TextView avgSpeedBox;
    private ImageView arrow;

    private double currentDistance = 100;
    private double currentSpeed = 0; // km/h
    private double currentTime = 1;  // s
    private double currentAvgSpeed = 0; // km/h
    private double currentTemperature = 0; // °C

    private float[] mGravity;     // accelerometer
    private float[] mGeomagnetic; // magnetometer
    private float currentAzimut; // degrees
    private float treasureAzimut; // degrees

    private Location treasureLocation = new Location("treasureLocation");
    private LocationManager locationManager;
    private LocationListener locationListener;
    private SensorManager sensorManager;
    private Sensor sensorTemperature;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;

    private String treasureName;
    private Double treasureLongitude;
    private Double treasureLatitude;
    private Integer treasureMaxCoins;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        // Set id for Text- and ImageViews
        infoBox = (TextView)findViewById(R.id.infoBox);
        distanceBox = (TextView)findViewById(R.id.distanceBox);
        temperatureBox = (TextView)findViewById(R.id.temperatureBox);
        speedBox = (TextView)findViewById(R.id.speedBox);
        avgSpeedBox = (TextView)findViewById(R.id.avgSpeedBox);
        arrow = (ImageView)findViewById(R.id.arrow);

        // Get variables from MainActivity using Intent
        Intent intent = getIntent();
        treasureName = intent.getStringExtra("currentTreasureName");
        treasureLongitude = intent.getDoubleExtra("currentLongitude", 0);
        treasureLatitude = intent.getDoubleExtra("currentLatitude", 0);
        treasureMaxCoins = intent.getIntExtra("currentTreasureMaxCoins", 0);
        currentScore = intent.getIntExtra("currentScore", 0);

        infoBox.setText(" " + treasureName + "\n You have : " + Integer.toString(currentScore) + " COINS");

        // Set location of treasure
        treasureLocation.setLongitude(treasureLongitude);
        treasureLocation.setLatitude(treasureLatitude);

        // Set Back Button
        backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBackButtonAction();
            }
        });

        // Location measurement
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location currentLocation) {
                // Calculate time
                Chronometer simpleChronometer = (Chronometer)findViewById(R.id.chronometer); // initiate a chronometer
                simpleChronometer.start();                                                   // start a chronometer
                currentTime = (double)((SystemClock.elapsedRealtime() - simpleChronometer.getBase()) / 1000);

                // Calculate distance
                currentDistance = currentLocation.distanceTo(treasureLocation);
                distanceBox.setText("Distance to treasure: " + Math.round(currentDistance) + " meters");

                // Calculate current speed
                currentSpeed = currentLocation.getSpeed();
                speedBox.setText("Your speed: " + Math.round(currentSpeed) + " km/h");

                // Calculate average speed
                currentAvgSpeed += (currentSpeed);

                // Calculate Azimut Position treasure
                treasureAzimut = currentLocation.bearingTo(treasureLocation);

                if(currentTime > 0) {
                    avgSpeedBox.setText("Your average speed: " + Math.round(currentAvgSpeed / currentTime) + "km/h");
                } else {
                    avgSpeedBox.setText("Your average speed: 0 km/h");
                }

                // If treasure found go to MainActivity
                if(currentDistance < 10) {
                    Toast.makeText(getApplicationContext(), "NICE! \n You found it", Toast.LENGTH_SHORT).show();
                    if (currentTemperature > 0) {
                        //Calculate score with temperature and avg speed - Case Temperature positive
                        calculateScore = Math.round(treasureMaxCoins + Math.round(currentTemperature + (currentAvgSpeed / currentTime)));
                        findTreasure();
                    } else {
                        //Calculate score with temperature and avg speed - Case Temperature negative
                        calculateScore = Math.round(treasureMaxCoins + Math.round(negativeTemperature + (currentAvgSpeed / currentTime)));
                        findTreasure();
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Check permissions for location
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET },
                    10);
            return;
        }

        // Calculate location every 0.5 second
        locationManager.requestLocationUpdates("gps", 500, 0, locationListener);
        Integer score = intent.getIntExtra("score", 0);

        // Configure sensors
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void findTreasure() {
        locationManager.removeUpdates(locationListener);

        // Opening new intent
        Intent backIntent = new Intent();
        backIntent.putExtra("currentTreasureName", treasureName);
        backIntent.putExtra("scoreToAdd", calculateScore);
        setResult(RESULT_OK, backIntent);
        finish();
    }

    // Action "BACK" Button
    private void myBackButtonAction() {
        locationManager.removeUpdates(locationListener);

        // Opening new intent
        Intent backIntent = new Intent();
        setResult(RESULT_CANCELED, backIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) { case 10: }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Temperature
        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            currentTemperature = event.values[0];
            temperatureBox.setText("Temperature: " + Math.round(currentTemperature) + "°C");
        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if(mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if(SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                currentAzimut = orientation[0];
            }
            RotateAnimation rotateAnimation = new RotateAnimation(treasureAzimut - currentAzimut, treasureAzimut - currentAzimut,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(100);
            rotateAnimation.setFillAfter(true);
            arrow.setAnimation(rotateAnimation);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
