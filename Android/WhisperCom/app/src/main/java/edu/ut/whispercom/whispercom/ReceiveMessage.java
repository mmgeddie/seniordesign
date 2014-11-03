package edu.ut.whispercom.whispercom;

/**
 * Created by matt on 9/28/14.
 */
public class ReceiveMessage implements Runnable {
    MessagingActivity activity;
    String data;
    Message message;
    ReceiveMessage(MessagingActivity activity) {
        this.activity = activity;
    }
    @Override
    public void run() {
        if (message == null) {
            message = activity.receiveMsg();
        }
        message.data = data;
        activity.messageAdapter.notifyDataSetChanged();
    }
}
