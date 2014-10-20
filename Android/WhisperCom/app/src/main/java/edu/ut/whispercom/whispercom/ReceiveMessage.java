package edu.ut.whispercom.whispercom;

import android.widget.TextView;

/**
 * Created by matt on 9/28/14.
 */
public class ReceiveMessage implements Runnable {
    MessagingActivity activity;
    String body;
    ReceiveMessage(MessagingActivity activity, String body) {
        this.activity = activity;
        this.body = body;
    }
    @Override
    public void run() {
        activity.receiveMsg(body);
    }
}
