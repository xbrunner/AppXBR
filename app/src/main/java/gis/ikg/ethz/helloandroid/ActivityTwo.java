package gis.ikg.ethz.helloandroid;

import android.content.Intent;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class ActivityTwo extends AppCompatActivity {

    private static ActivityTwo instance2 = null;
    private Button backButton;
    public TextView infoBox;
    public TextView temperature;
    public int currentScore = 0;
    //public String currentTreasureName = null;
    public double currentTemperature = 20.0;
    private SensorManager SensorManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);


        //Button
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myBackButtonAction();
            }
        });

        Intent intent = getIntent();
        String currentTreasureName = "";
        currentTreasureName = intent.getStringExtra("currentTreasureName");
        //Show Info Box
        infoBox = (TextView)findViewById(R.id.info);
        infoBox.setText("You choosed : \n " + currentTreasureName);

    }

    //Setup Button "GO!"
    private void myBackButtonAction() {

            // Opening new intent
            Intent backIntent = new Intent(this, MainActivity.class);
            //myFirstIntent.putExtra("key1","Back to activity one");
            //myFirstIntent.putExtra("key2", 2019);
            startActivity(backIntent);

        }

}
