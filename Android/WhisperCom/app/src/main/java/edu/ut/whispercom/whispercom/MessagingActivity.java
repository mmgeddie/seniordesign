package edu.ut.whispercom.whispercom;

import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MessagingActivity extends ActionBarActivity {
    private Button sendButton;
    private EditText messageBodyField;
    private String messageBody;
    private int recipientId;
    MessageAdapter messageAdapter;
    ListView messagesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);
        recipientId=0;
        messagesList= (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
    }

    protected void sendMsg(){
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
        messageAdapter.addMessage(messageBody, MessageAdapter.DIRECTION_OUTGOING);
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
}
