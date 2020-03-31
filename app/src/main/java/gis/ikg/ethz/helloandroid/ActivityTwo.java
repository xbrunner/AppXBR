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
import android.media.Image;
import android.net.Uri;
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
    private ImageView arrow;

    private double currentDistance = 100;
    private double currentSpeed = 0; // km/h
    private double currentTime = 1;  // s
    private double currentAvgSpeed = 0;
    private double currentTemperature = 20;

    private float[] mGravity;     // accelerometer
    private float[] mGeomagnetic; // magnetometer
    private float currentAzimut;
    private float treasureAzimut;

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

        // Set id for boxes
        infoBox = (TextView)findViewById(R.id.infoBox);
        distanceBox = (TextView)findViewById(R.id.distanceBox);
        temperatureBox = (TextView)findViewById(R.id.temperatureBox);
        speedBox = (TextView)findViewById(R.id.speedBox);
        avgSpeedBox = (TextView)findViewById(R.id.avgSpeedBox);
        arrow = (ImageView)findViewById(R.id.arrow);

        // get variables from Intent
        Intent intent = getIntent();
        treasureName = intent.getStringExtra("currentTreasureName");
        treasureLongitude = intent.getDoubleExtra("currentLongitude", 0);
        treasureLatitude = intent.getDoubleExtra("currentLatitude", 0);
        treasureMaxCoins = intent.getIntExtra("currentTreasureMaxCoins", 0);

        infoBox.setText(" " + treasureName + "\n You have : " + Integer.toString(currentScore) + " COINS");

        // Set location of treasure
        treasureLocation.setLongitude(treasureLongitude);
        treasureLocation.setLatitude(treasureLatitude);

        // Button
        backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBackButtonAction();
            }
        });

        // Location
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

                // Opening new intent
                if(currentDistance < 4) {
                    Toast.makeText(getApplicationContext(), "NICE! \n You found it", Toast.LENGTH_SHORT).show();
                    findTreasure();
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

        // Calculate location every 1 second
        locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
        Integer score = intent.getIntExtra("score", 0);

        // Update score
        currentScore = score + treasureMaxCoins;

        // Sensor

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
        backIntent.putExtra("scoreToAdd", 65);
        setResult(RESULT_OK, backIntent);
        // startActivity(backIntent);

        finish();
    }

    // Setup Button "GO!"
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
