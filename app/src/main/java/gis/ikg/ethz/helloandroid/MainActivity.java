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

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static MainActivity instance = null;
    private Button myButton;
    private Spinner spinner = null;
    public TextView coins;
    public TextView item;
    public int Score = 0;
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

        for (Map.Entry<String, Treasures> entry : this.treasures.entrySet()) {
            list.add(entry.getValue().getTreasureName());
        }


        //android.R.layout.simple_list_item_1, list);
        //adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //sp1.setAdapter(adp1);


        spinner = findViewById(R.id.spinner1);

        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp1);

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array., android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);


            //item.setText(GetItem);

            //Log.d("Blabla", "Blabla" + String.valueOf(treasureName));



        //Button
        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myButtonAction();
            }
        });

    }

    public static MainActivity getInstance()
    {
        return MainActivity.instance;
    }

    private void myButtonAction() {

        //Get Item from Spinner
        String treasureName = spinner.getSelectedItem().toString();
        selectedTreasure = this.treasures.get(treasureName);

        // TODO implement your button response here
        TextView box = (TextView) findViewById(R.id.textbox);
        box.setText(getResources().getString(R.string.respond));

        if(!this.selectedTreasure.isFound()) {
            // Codes for opening new intent
            Intent myFirstIntent = new Intent(this, ActivityTwo.class);
            myFirstIntent.putExtra("key1","ActivityTwo has been launched");
            myFirstIntent.putExtra("key2", 2019);
            startActivity(myFirstIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Treasure already found !", Toast.LENGTH_SHORT).show();
        }
    }

    public void addScore(int scoreAdd) {
        this.Score += scoreAdd;
        this.updateScore();
    }

    private void updateScore() {
        coins.setText("You have: \n " + String.valueOf(Score) + " Coins");
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
                   //Log.d("MyActivity", "Just created: " + treasures);
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
