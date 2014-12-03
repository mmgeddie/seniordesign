package edu.ut.whispercom.whispercom;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by matt on 10/15/14.
 */
public class EncodeDecode {
    public static final int START = 16;
    public static final int REPEAT = 17;
    public static final int STOP = 19;

    private static ArrayList<String> recievedMessageList = new ArrayList<String>();
    private static TreeSet<Integer> errors = new TreeSet<Integer>();
    private static int lastSequence = -1;
    private static ReceiveMessage receiveMessage;
    private static boolean waitingForRepeat = false;
    private static boolean lastMessageComplete = false;

    public static int[] encode(String s, int sequence) {
        List<Integer> output = new ArrayList<Integer>();
        output.add(START);
        int a = sequence >>> 4;
        int lastChunk = a;
        // sequence number
        output.add(a);
        int b = sequence & 15;
        if (lastChunk == b) {
            output.add(REPEAT);
        }
        lastChunk = b;
        output.add(b);
        System.out.println("``` seq:"+sequence+" a:"+a+" b:"+b);

        // calculate checksum & add to transmission
        int checksum = CRC8.calc((sequence+s).getBytes(), (sequence+s).length()) & 0xFF;
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
    public static String decode(MessagingActivity activity, List<Integer> in, boolean finalReceipt) {
        System.out.println("{{{{{ "+in.toString());
        if (receiveMessage == null || lastMessageComplete) {
            receiveMessage = new ReceiveMessage(activity);
            lastSequence = -1;
            recievedMessageList = new ArrayList<String>();
            System.out.println("new message here3");
            lastMessageComplete = false;
        }
        StringBuilder sb = new StringBuilder();
        for (Iterator<Integer> iterator = in.iterator(); iterator.hasNext();) {
            Integer i = iterator.next();
            if (i == REPEAT) {
                iterator.remove();
            }
        }
        if (in.size() > 4) {
            // get sequence
            int a = in.get(0);
            int b = in.get(1);
            int sequence = a << 4;
            sequence = sequence + b;

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
//            if (finalReceipt && ((sb.length() != length) || (checksum != (CRC8.calc(sb.toString().getBytes(), sb.length()) & 0xFF)))) {
//            if (checksum != (CRC8.calc(sb.toString().getBytes(), sb.length()) & 0xFF)) {
//                    activity.playMessage(new int[]{18});
//                return "Error, requesting retransmission";
//            }
            boolean validChecksum = true;
            if (checksum != (CRC8.calc((sequence+sb.toString()).getBytes(), (sequence+sb.toString()).length()) & 0xFF)) {
                validChecksum = false;
            }
            if (finalReceipt) {
                if (validChecksum){
                    if (waitingForRepeat) {
                        if (sequence < recievedMessageList.size()) {
                            recievedMessageList.set(sequence, sb.toString());
                            errors.remove(sequence);
                        } else {
                            System.out.println("somethingWrongWaitingForRepeat");
                            waitingForRepeat = false;
                            recievedMessageList = new ArrayList<String>();
                            errors = new TreeSet<Integer>();
                        }
                        checkForErrors(activity);
                    } else {
                        if (sequence < lastSequence) {
                            StringBuilder totalMessage = new StringBuilder();
                            for (int i = 0; i < recievedMessageList.size() ; i++) {
                                totalMessage.append(recievedMessageList.get(i));
                            }
                            receiveMessage.data = totalMessage.toString();
                            activity.runOnUiThread(receiveMessage);
                            recievedMessageList = new ArrayList<String>();
                            receiveMessage = new ReceiveMessage(activity);
                            System.out.println("new message here1");
                            lastSequence = -1;
                        }
                        for (int i = 1; i < (sequence - lastSequence); i++) {
                            recievedMessageList.add("----");
                            errors.add(lastSequence + i);
                        }
                        recievedMessageList.add(sb.toString());
                        lastSequence = sequence;
                    }
                    StringBuilder totalMessage = new StringBuilder();
                    for (int i = 0; i < recievedMessageList.size() ; i++) {
                        totalMessage.append(recievedMessageList.get(i));
                    }
                    receiveMessage.data = totalMessage.toString();
                    activity.runOnUiThread(receiveMessage);
                    if (errors.size() == 0 && totalMessage.length() > 0 && totalMessage.charAt(totalMessage.length()-1) == 4) {
                        lastMessageComplete = true;
                    }
                } else if (!waitingForRepeat) {
                    errors.add(lastSequence + 1);
                }
            } else if (!waitingForRepeat) {
                StringBuilder totalMessage = new StringBuilder();
                for (int i = 0; i < recievedMessageList.size() ; i++) {
                    totalMessage.append(recievedMessageList.get(i));
                }
                receiveMessage.data = totalMessage.toString() + sb.toString();
                activity.runOnUiThread(receiveMessage);
            }
            System.out.println("~~~~ seq:"+sequence+" - valid:"+validChecksum+" |||"+sb.toString());
            return sb.toString();
        }
        return "";
    }

    public static void checkForErrors (MessagingActivity activity) {
        if (recievedMessageList.size() > 0) {
            String lastPacket = recievedMessageList.get(recievedMessageList.size()-1);
            if (lastPacket.charAt(lastPacket.length()-1) != 4) {
                errors.add(lastSequence+1);
            }
        }
        if (errors.size() == 1) {
            waitingForRepeat = true;
            ArrayList<Integer> repeatMessage = new ArrayList<Integer>();
            repeatMessage.add(18);
            repeatMessage.add(START);
            int firstError = errors.pollFirst();
            System.out.println("waitingForRepeat seq:"+firstError);
            int a = firstError >>> 4;
            int lastChunk = a;
            // sequence number
            repeatMessage.add(a);
            int b = firstError & 15;
            if (lastChunk == b) {
                repeatMessage.add(REPEAT);
            }
            lastChunk = b;
            repeatMessage.add(b);
            repeatMessage.add(STOP);
            int[] out = new int[repeatMessage.size()];
            for (int i = 0; i < repeatMessage.size(); i++) {
                out[i] = repeatMessage.get(i);
            }
            activity.playMessage(out, false);
        } else {
            System.out.println("notWaitingForRepeat");
            waitingForRepeat = false;
        }
    }

    public static void repeatSequence(MessagingActivity activity, List<Integer> in) {
        for (Iterator<Integer> iterator = in.iterator(); iterator.hasNext();) {
            Integer i = iterator.next();
            if (i == REPEAT) {
                iterator.remove();
            }
        }
        int a = in.get(0);
        int b = in.get(1);
        int sequence = a << 4;
        sequence = sequence + b;
        activity.playMessage(activity.lastMessage.get(sequence), false);
    }
}
