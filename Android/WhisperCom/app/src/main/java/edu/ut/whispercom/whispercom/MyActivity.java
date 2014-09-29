package edu.ut.whispercom.whispercom;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ActionBarActivity {

//    final static int[] rowFreqs = {697, 770, 852, 941};
//    final static int[] colFreqs = {1209, 1336, 1477, 1633};

    final static int[] rowFreqs = {18000, 18250, 18500, 18750};
    final static int[] colFreqs = {19000, 19250, 19500, 19750};
    List<String> transmitLog = new ArrayList<String>();
    List<Character> receiveLog = new ArrayList<Character>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Listner listner = new Listner(this);

//        new AudioDispatcher(audioStream, 3584, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void playTone(View v) {
        String buttonText = ((Button) v).getText().toString();
        updateTransmitLog(buttonText);
        new PlaySound(buttonText);
    }

    public void updateTransmitLog(String buttonText) {
        transmitLog.add(buttonText);
        TextView tv =  (TextView)findViewById(R.id.textViewTransmitLog);
        tv.setText(transmitLog.toString());
    }
}
