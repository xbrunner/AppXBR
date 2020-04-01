package gis.ikg.ethz.helloandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The MainActivity is a menu where you can see your score, select a treasure to search and switch to the ActivityTwo
 *
 *  -> This is nos possible to get 2 times the same treasure
 *  -> The treasures (Name, Location, Coins) are given in treasures.csv
 *  -> The MainActivity read this .csv-file and put it in a Spinner for choose an object
 *  -> When a treasure is found, the new score is calculate in the MainActivity
 *
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button myButton;
    private Spinner spinner = null;
    public TextView coins;
    private int score = 0;
    private int currentScore = 0;
    public Treasures selectedTreasure = null;
    private Map<String, Treasures> treasures = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Call read csv function
        readTreasuresData();

        // Coins score
        coins = (TextView)findViewById(R.id.coins);

        // Spinner
        final List<String> list = new ArrayList<String>();

        // Insert values from map (csv) to spinner
        for(Map.Entry<String, Treasures> entry : this.treasures.entrySet()) {
            list.add(entry.getValue().getTreasureName());
        }
        spinner = findViewById(R.id.spinner1);

        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp1);

        // Button
        myButton = (Button)findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButtonAction();
            }
        });

        // Get score from ActivityTwo if the treasure is found
        Intent intentBack = getIntent();
        currentScore = intentBack.getIntExtra("currentTreasureMaxCoins", 0);
        score += currentScore;
    }

    // Action "GO!" Button
    private void myButtonAction() {
        // Get Item from Spinner
        String treasureName = spinner.getSelectedItem().toString();
        selectedTreasure = this.treasures.get(treasureName);

        // Intent to Activity two if object is not already found
        if(!this.selectedTreasure.isFound()) {
            // Opening new intent
            Intent Intent = new Intent(this, ActivityTwo.class);
            Intent.putExtra("currentTreasureName", selectedTreasure.getTreasureName());
            Intent.putExtra("currentLongitude", selectedTreasure.getLongitude());
            Intent.putExtra("currentLatitude", selectedTreasure.getLatitude());
            Intent.putExtra("currentTreasureMaxCoins", selectedTreasure.getMaxCoins());
            Intent.putExtra("currentTreasureIsFound", selectedTreasure.isFound());
            Intent.putExtra("currentScore", score);
            startActivityForResult(Intent, 0);
        } else {
            // Toast to show if the object is already found
            Toast.makeText(getApplicationContext(), "Treasure already founded!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // Get Result from ActivityTwo and update score
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                int scoreToAdd = data.getIntExtra("scoreToAdd", 0);
                String treasureName = data.getStringExtra("currentTreasureName");
                this.treasures.get(treasureName).setFound();
                score += scoreToAdd;
                this.updateScore();
            }
        }
    }

    // Method to update score
    public void updateScore() {
        coins.setText("You have: \n " + String.valueOf(score) + " COINS");
    }

    // Read csv:
    private void readTreasuresData() {
        InputStream is = getResources().openRawResource(R.raw.treasures);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try {
            while((line = reader.readLine()) != null) {
                // Split by ','
                String[] tokens = line.split(";");
                // Read the data
                Treasures TreasuresRead = new Treasures();
                TreasuresRead.setTreasureName(tokens[0]);
                TreasuresRead.setLongitude(Double.parseDouble(tokens[1]));
                TreasuresRead.setLatitude(Double.parseDouble(tokens[2]));
                TreasuresRead.setMaxCoins(Integer.parseInt(tokens[3]));
                treasures.put(TreasuresRead.getTreasureName(), TreasuresRead);
            }
        } catch(IOException e) {
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
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    protected void onStart() {
        this.updateScore();
        super.onStart();
    }
}
