//package gis.ikg.ethz.helloandroid;
//
//
//public class SensorActivity extends ActivityTwo implements SensorEventListener {
//    private SensorManager sensorManager;
//    private Sensor pressure;
//
//    @Override
//    public final void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//        // Get an instance of the sensor service, and use that to get an instance of
//        // a particular sensor.
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//    }
//
//    @Override
//    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Do something here if sensor accuracy changes.
//    }
//
//    @Override
//    public final void onSensorChanged(SensorEvent event) {
//        float millibarsOfPressure = event.values[0];
//        // Do something with this sensor data.
//    }
//
//    @Override
//    protected void onResume() {
//        // Register a listener for the sensor.
//        super.onResume();
//        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    @Override
//    protected void onPause() {
//        // Be sure to unregister the sensor when the activity pauses.
//        super.onPause();
//        sensorManager.unregisterListener(this);
//    }
//}