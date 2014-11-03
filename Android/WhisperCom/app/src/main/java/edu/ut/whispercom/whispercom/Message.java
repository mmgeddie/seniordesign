package edu.ut.whispercom.whispercom;

/**
 * Created by matt on 11/3/14.
 */
public class Message {
    public String data;
    public int direction;
    public Message (String data, int direction) {
        this.data = data;
        this.direction = direction;
    }
}
