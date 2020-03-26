package gis.ikg.ethz.helloandroid;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class ActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String text_value = extras.getString("key1");
            int number_value = extras.getInt("key2");

            displayOutput(text_value, number_value);
        }


        MainActivity act = MainActivity.getInstance();
        act.addScore(100);
        Treasures treasure = act.selectedTreasure;
    }

    private void displayOutput(String text, int number) {
        // TODO change the two TextViews to contain text and number
        TextView box = (TextView) findViewById(R.id.textView);
        box.setText(text);

        box = (TextView) findViewById(R.id.textView2);
        box.setText( Integer.toString(number));
    }
}
