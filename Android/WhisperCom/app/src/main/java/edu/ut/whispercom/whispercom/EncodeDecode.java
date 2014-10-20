package edu.ut.whispercom.whispercom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by matt on 10/15/14.
 */
public class EncodeDecode {
    public static int[] encode(String s) {
        List<Integer> output = new ArrayList<Integer>();
        output.add(16);
        int a = s.length() >>> 4;
        int lastChunk = a;
        output.add(a);
        int b = s.length() & 15;
        if (lastChunk == b) {
            output.add(17);
        }
        lastChunk = b;
        output.add(b);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            a = c >>> 4;
            if (a == lastChunk) {
                output.add(17);
            }
            lastChunk = a;
            output.add(a);
            b = c & 15;
            if (b == lastChunk) {
                output.add(17);
            }
            lastChunk = b;
            output.add(b);
        }
        output.add(19);
        int[] out = new int[output.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = output.get(i);
        }
        System.out.println("Output array:" + Arrays.toString(out));
        return out;
    }
    public static String decode(List<Integer> in) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Integer> iterator = in.iterator(); iterator.hasNext();) {
            Integer i = iterator.next();
            if (i == 17) {
                iterator.remove();
            }
        }
        for (int i = 2; i < in.size() - 1 ; i = i+2) {
            int a = in.get(i);
            int b = in.get(i+1);
            int c = a << 4;
            c = c + b;
            sb.append((char)c);
        }
        return sb.toString();
    }
}
