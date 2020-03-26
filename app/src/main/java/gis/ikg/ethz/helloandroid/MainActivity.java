package gis.ikg.ethz.helloandroid;

import android.content.Intent;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static MainActivity instance = null;
    private Button myButton;
    private Spinner spinner = null;
    public TextView coins;
    public int score = 20;
    //public int scoreActivity2 = 0;
    public Treasures selectedTreasure = null;
    private Map<String, Treasures> treasures = new ArrayMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call read csv function
        readTreasuresData();

        //Coins score
        coins = (TextView)findViewById(R.id.coins);
        this.updateScore();

        //Spinner
        final List<String> list = new ArrayList<String>();

        //Insert values from map (csv) to spinner
        for (Map.Entry<String, Treasures> entry : this.treasures.entrySet()) {
            list.add(entry.getValue().getTreasureName());
        }
        //Log.d("bla", "bla"+ treasures.get(2));
        spinner = findViewById(R.id.spinner1);

        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp1);

        //Get objects from Activity2
        //ActivityTwo act = ActivityTwo.getInstanceTwo();
        //scoreActivity2 = act.currentScore;



        //Button
        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myButtonAction();
            }
        });

    }

//    //Get Instance to switch between Activities
//    public static MainActivity getInstance()
//    {
//        return MainActivity.instance;
//    }

    //Setup Button "GO!"
    private void myButtonAction() {

        //Get Item from Spinner
        String treasureName = spinner.getSelectedItem().toString();
        selectedTreasure = this.treasures.get(treasureName);

        //Intent to Activity two if object is not already founded
        if(!this.selectedTreasure.isFound()) {
            // Opening new intent
            Intent Intent = new Intent(this, ActivityTwo.class);
            Intent.putExtra("currentTreasureName", selectedTreasure.getTreasureName());
            startActivity(Intent);

        } else {
            Toast.makeText(getApplicationContext(), "Treasure already founded!", Toast.LENGTH_SHORT).show(); //Toast to show if the object is already found
        }
    }

    //Score
    public void addScore(int scoreAdd) {
        this.score += scoreAdd;
        this.updateScore();
    }
    //Method to update score
    private void updateScore() {
        coins.setText("You have: \n " + String.valueOf(score) + " Coins");
    }

    //Read csv:
    private void readTreasuresData() {
       InputStream is = getResources().openRawResource(R.raw.treasures);
       BufferedReader reader = new BufferedReader(
               new InputStreamReader(is, Charset.forName("UTF-8"))
       );
       String line = "";
           try {
               while ( (line = reader.readLine()) != null) {
                   //Log.d("Blabla", "Blabla" +line);

                    //Split by ','
                   String[] tokens = line.split(";");
                   //Read the data
                   Treasures TreasuresRead = new Treasures();
                   TreasuresRead.setTreasureName(tokens[0]);
                   //Log.d("MyActivity", tokens[1]);
                   TreasuresRead.setLongitude(Double.parseDouble(tokens[1]));
                   TreasuresRead.setLatitude(Double.parseDouble(tokens[2]));
                   TreasuresRead.setMaxCoins(Integer.parseInt(tokens[3]));
                   treasures.put(TreasuresRead.getTreasureName(), TreasuresRead);
                   Log.d("MyActivity", "Just created: " + tokens[0] + ","+ tokens[1] + ","+ tokens[2] + "," + tokens[3]);
               }
           } catch (IOException e) {
               Log.wtf("MyActivity", "Error reading data file on line" + line, e);
               e.printStackTrace();
            }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
