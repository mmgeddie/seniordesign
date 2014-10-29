package edu.ut.whispercom.whispercom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by matt on 10/15/14.
 */
public class EncodeDecode {
    public static final int START = 16;
    public static final int REPEAT = 17;
    public static final int STOP = 19;

    public static int[] encode(String s) {
        List<Integer> output = new ArrayList<Integer>();
        output.add(START);
        int a = s.length() >>> 4;
        int lastChunk = a;
        // length of string
        output.add(a);
        int b = s.length() & 15;
        if (lastChunk == b) {
            output.add(REPEAT);
        }
        lastChunk = b;
        output.add(b);

        // calculate checksum & add to transmission
        int checksum = CRC8.calc(s.getBytes(), s.length()) & 0xFF;
        // this is a better way to convert byte to int
        //int checksum = sum & 0xFF;
        a = checksum >>> 4;
        //a = checksum >>> 4;
        if (a == lastChunk) {
            output.add(REPEAT);
        }
        lastChunk = a;
        output.add(a);
        b = checksum & 15;
        if (b == lastChunk) {
            output.add(REPEAT);
        }
        lastChunk = b;
        output.add(b);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            a = c >>> 4;
            if (a == lastChunk) {
                output.add(REPEAT);
            }
            lastChunk = a;
            output.add(a);
            b = c & 15;
            if (b == lastChunk) {
                output.add(REPEAT);
            }
            lastChunk = b;
            output.add(b);
        }
        output.add(STOP);
        int[] out = new int[output.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = output.get(i);
        }
        System.out.println("Output array:" + Arrays.toString(out));
        return out;
    }
    public static String decode(MessagingActivity activity, List<Integer> in) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Integer> iterator = in.iterator(); iterator.hasNext();) {
            Integer i = iterator.next();
            if (i == REPEAT) {
                iterator.remove();
            }
        }
        // get length
        int a = in.get(0);
        int b = in.get(1);
        int length = a << 4;
        length = length + b;

        // get checksum
        a = in.get(2);
        b = in.get(3);
        int checksum = a << 4;
        checksum = checksum + b;

        // get message
        for (int i = 4; i < in.size() - 1 ; i = i+2) {
            a = in.get(i);
            b = in.get(i+1);
            int c = a << 4;
            c = c + b;
            sb.append((char)c);
        }
        if ((sb.length() != length) || (checksum != (CRC8.calc(sb.toString().getBytes(), sb.length()) & 0xFF))) {
            activity.playMessage(new int[]{18});
            return "Error, requesting retransmission";
        }
        return sb.toString();
    }
}
