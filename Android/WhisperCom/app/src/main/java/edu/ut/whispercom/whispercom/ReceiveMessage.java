package edu.ut.whispercom.whispercom;

import java.util.Date;

/**
 * Created by matt on 9/28/14.
 */
public class ReceiveMessage implements Runnable {
    MessagingActivity activity;
    String data = "";
    Message message;
    ReceiveMessage(MessagingActivity activity) {
        this.activity = activity;
    }
    @Override
    public void run() {
        if (message == null) {
            message = activity.receiveMsg();
        }
        // DEBUG: used for debugging to test bandwidth
        //Date now = new Date();
        //message.data = data + " Time: " + now.getTime();
        message.data = data;
        activity.messageAdapter.notifyDataSetChanged();
    }
}
