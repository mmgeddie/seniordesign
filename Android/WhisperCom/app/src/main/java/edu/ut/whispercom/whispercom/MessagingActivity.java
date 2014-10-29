package edu.ut.whispercom.whispercom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MessagingActivity extends ActionBarActivity {
    private Button sendButton;
    private Button okButton;
    private EditText messageBodyField;
    private EditText unField;
    private String messageBody;
    private int recipientId;
    private String username;
    private boolean transmitReceive = false;
    private Listner listner;
    MessageAdapter messageAdapter;
    ListView messagesList;
    List<Integer> receiveLog = new ArrayList<Integer>();
    Date lastRecieved  = new Date(Long.MIN_VALUE + 1);
    Date lastSent = new Date(Long.MIN_VALUE);
    int[] lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_dialog);
        final Dialog dialog = new Dialog(this);

        setContentView(R.layout.messaging);
        recipientId = 0;
        username = "Anonymous";
        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
        listner = new Listner(this);

        createUNDialog();

    }

    private Context getContext() {
        return this;
    }

    protected void createUNDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogview = inflater.inflate(R.layout.name_dialog, null);
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Set Username")
                .setView(dialogview)
                .setPositiveButton("Enter", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.setOnShowListener(new DialogInterface.OnShowListener(){
            public void onShow(DialogInterface dialog){
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        unField = (EditText) d.findViewById(R.id.username);
                        username = unField.getText().toString();
                        if (username.isEmpty()==false) {
                            d.dismiss();
                        }
                    }
                });
            }
        });
        d.show();
    }

    protected void sendMsg() {
        messageBodyField = (EditText) findViewById(R.id.messageBodyField);
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        //Here is where you will actually send the message throught Sinch
        Toast.makeText(this, "Sending message! recipientId: " + recipientId
                + " Message: " + messageBody, Toast.LENGTH_LONG).show();
        messageBodyField.setText("");
        messageBody = username + ": " + messageBody;

        int[] out = EncodeDecode.encode(messageBody);
//        messageAdapter.addMessage(Arrays.toString(out) + " = " + messageBody, MessageAdapter.DIRECTION_OUTGOING);
        messageAdapter.addMessage(messageBody, MessageAdapter.DIRECTION_OUTGOING);

        playMessage(out);
        lastMessage = out;
        lastSent = new Date();

    }

    protected void receiveMsg(String message){
        messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
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
        if (id == R.id.change_un) {
            createUNDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTransmitReceive(boolean transmitReceive) {
        this.transmitReceive = transmitReceive;
        if (transmitReceive) {
            sendButton.setEnabled(false);
        } else {
            sendButton.setEnabled(true);
        }
    }

    public void playMessage(int[] message) {
        runOnUiThread(new Runnable() {
            public void run() {
                setTransmitReceive(true);
            }
        });
        listner.stopProcessing();
        class MessagePlayer implements Runnable {
            int[] message;
            MessagePlayer(int[] message) { this.message = message; }
            public void run() {
                for (int i : message) {
                    PlaySound.playSound(i);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        setTransmitReceive(false);
                        listner.process();
                    }
                });
            }
        }
        Thread t = new Thread(new MessagePlayer(message));
        t.start();
    }


}
