package gis.ikg.ethz.helloandroid;

import android.content.Intent;
import android.net.Uri;
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

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button myButton;
    public TextView coins;
    public int Score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call read csv function
        readTreasuresData();

        //Coins score
        coins = (TextView)findViewById(R.id.coins);
        coins.setText("You have: \n " + String.valueOf(Score) + " Coins");

        //Spinner
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myButtonAction();
            }
        });

    }

    private void myButtonAction() {
        // TODO implement your button response here
        TextView box = (TextView) findViewById(R.id.textbox);
        box.setText(getResources().getString(R.string.respond));

        //Change score
        Score = Score + 10;
        coins.setText("You have: \n " + String.valueOf(Score) + " Coins");

        // Codes for opening new intent
     /* Intent myFirstIntent = new Intent(this, ActivityTwo.class);
        myFirstIntent.putExtra("key1","ActivityTwo has been launched");
        myFirstIntent.putExtra("key2", 2019);
        startActivity(myFirstIntent);
    */
        // Code for opening a web browser
/*
        Uri webpage = Uri.parse("http://www.ethz.ch/");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } */
    }

    //Read csv:
    private List<Treasures> treasures = new ArrayList<>();
    private void readTreasuresData() {
       InputStream is = getResources().openRawResource(R.raw.treasures);
       BufferedReader reader = new BufferedReader(
               new InputStreamReader(is, Charset.forName("UTF-8"))
       );

       String line = "";

           try {
               //Stap over headers
               //reader.readLine();

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
                   treasures.add(TreasuresRead);

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
