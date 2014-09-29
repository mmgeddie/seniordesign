package edu.ut.whispercom.whispercom;

import android.widget.TextView;

/**
 * Created by matt on 9/28/14.
 */
public class UpdateTextView implements Runnable {
    TextView textView;
    String updateText;
    UpdateTextView(String updateText, TextView textView) {
        this.textView = textView;
        this.updateText = updateText;
    }
    @Override
    public void run() {
        textView.setText(updateText);
    }
}
