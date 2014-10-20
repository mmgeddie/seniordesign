package edu.ut.whispercom.whispercom;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ActionBarActivity {

    final static int[] rowFreqs = {18000, 18200, 18400, 18600, 18800};
    final static int[] colFreqs = {19000, 19250, 19500, 19750};

    List<String> transmitLog = new ArrayList<String>();
    List<Integer> receiveLog = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
//        Listner listner = new Listner(this);

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
        int[] out = EncodeDecode.encode("Hello");
//        String buttonText = ((Button) v).getText().toString();
//        updateTransmitLog(buttonText);
//        new PlaySound(buttonText);

//        for (int i : out) {
//            try {
//                new PlaySound(i);
//                Thread.sleep(250);
//            } catch(InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
//        }
    }

    public void updateTransmitLog(String buttonText) {
        transmitLog.add(buttonText);
        TextView tv =  (TextView)findViewById(R.id.textViewTransmitLog);
        tv.setText(transmitLog.toString());
    }
}
