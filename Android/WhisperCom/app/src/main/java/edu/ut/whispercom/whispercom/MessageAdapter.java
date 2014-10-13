package edu.ut.whispercom.whispercom;;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tbren_000 on 10/8/2014.
 */
public class MessageAdapter extends BaseAdapter {
    private List<Pair<String, Integer>> messages;
    private LayoutInflater layoutInflater;
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;

    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<String, Integer>>();
    }

    //Gets called every time you update the view with an
    //incoming or outgoing message
    public void addMessage(String message, int direction) {
        messages.add(new Pair(message, direction));
        notifyDataSetChanged();
    }

    //Returns how many messages are in the list
    @Override
    public int getCount() {
        return messages.size();
    }
    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //Tells your app how many possible layouts there are
    //In our case, right and left messages are our only 2 options
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    //This returns either DIRECTION_INCOMING or DIRECTION_OUTGOING
    @Override
    public int getItemViewType(int i) {
        return messages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);

        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_left;
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.message_right;
            }
            convertView = layoutInflater.inflate(res, viewGroup, false);
        }
        String message = messages.get(i).first;

        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(message);

        return convertView;
    }
}