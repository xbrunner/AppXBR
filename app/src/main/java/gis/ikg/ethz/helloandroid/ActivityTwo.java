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
    public double currentTemperature = 20.0;
    private SensorManager SensorManager = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        //Intent
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String text_value = extras.getString("key1");
            int number_value = extras.getInt("key2");

            //displayOutput(text_value, number_value);
        }

        //Get objects from MainActivity
        MainActivity act = MainActivity.getInstance();
        Treasures treasures = act.selectedTreasure;
        act.addScore(treasures.getMaxCoins());
        currentScore = act.score;

        //Show Info Box
        infoBox = (TextView)findViewById(R.id.info);
        this.updateInfoBox();

        //Show Temperature
        infoBox = (TextView)findViewById(R.id.temperature);
        this.updateTemperature();


        //Button
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myBackButtonAction();
            }
        });
    }

    //Get Instance to switch between Activities
    public static ActivityTwo getInstanceTwo()
    {
        return ActivityTwo.instance2;
    }


    //Setup Button "GO!"
    private void myBackButtonAction() {

            // Opening new intent
            Intent myFirstIntent = new Intent(this, MainActivity.class);
            myFirstIntent.putExtra("key1","Back to activity one");
            myFirstIntent.putExtra("key2", 2019);
            startActivity(myFirstIntent);

        }

    //Method to update score
    private void updateInfoBox() {
        infoBox.setText("You have: \n " + String.valueOf(currentScore) + " Coins");

    }

    //Method to update score
    private void updateTemperature() {
        infoBox.setText("Temperature: \n " + String.valueOf(currentTemperature));

    }
}
