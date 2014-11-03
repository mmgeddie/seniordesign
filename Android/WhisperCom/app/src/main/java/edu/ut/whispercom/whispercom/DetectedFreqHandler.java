package edu.ut.whispercom.whispercom;

import android.app.Activity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import be.tarsos.dsp.pitch.DTMF;
import be.tarsos.dsp.pitch.Goertzel;

/**
 * Created by matt on 9/28/14.
 */
public class DetectedFreqHandler implements Goertzel.FrequenciesDetectedHandler {
    private MessagingActivity activity;
    private int lastVal = -1;
    private boolean betweenStartStop = false;
    ReceiveMessage receiveMessage = new ReceiveMessage(activity);

    private static long lastMs = java.lang.System.currentTimeMillis();


    public DetectedFreqHandler(MessagingActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
			System.out.println(Arrays.toString(frequencies));
            System.out.println(Arrays.toString(powers));
            if (frequencies.length >= 2) {
                int high1index = 0;
                double high1power = 0;
                int high2index = 0;
                double high2power = 0;
                int high3index = 0;
                double high3power = 0;
                for (int i = 0; i < frequencies.length; i++) {
                    if (powers[i]>high1power) {
                        high3index = high2index;
                        high3power = high2power;
                        high2index = high1index;
                        high2power = high1power;
                        high1index = i;
                        high1power = powers[i];
                    } else if (powers[i]>high2power) {
                        high3index = high2index;
                        high3power = high2power;
                        high2index = i;
                        high2power = powers[i];
                    } else if (powers[i]>high3power) {
                        high3index = i;
                        high3power = powers[i];
                    }
                }
                System.out.println("1:"+frequencies[high1index]+":"+high1power+" "+"2:"+frequencies[high2index]+":"+high2power+" "+"3:"+frequencies[high3index]+":"+high3power);
			    if (high1power > high3power + 10 && high2power > high3power + 10) {
                    int rowIndex = -1;
                    int colIndex = -1;
                    for (int i = 0; i < MyActivity.rowFreqs.length; i++) {
                        if (frequencies[high1index] == allFrequencies[i] || frequencies[high2index] == allFrequencies[i])
                            rowIndex = i;
                    }
                    for (int i = MyActivity.rowFreqs.length; i < allFrequencies.length; i++) {
                        if (frequencies[high1index] == allFrequencies[i] || frequencies[high2index] == allFrequencies[i])
                            colIndex = i-(MyActivity.rowFreqs.length);
                    }
                    if(rowIndex>=0 && colIndex>=0){
                        int val = colIndex + (rowIndex * MyActivity.colFreqs.length);
                        System.out.println("Detected index: "+ val);
                        if (val == 16) { // Start tone
                            activity.lastRecieved = new Date();
                            activity.receiveLog = new ArrayList<Integer>();
                            betweenStartStop = true;
                            receiveMessage = new ReceiveMessage(activity);
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    activity.setTransmitReceive(true);
                                }
                            });
                        } else if (val == 19) { // Stop tone
                            if (betweenStartStop) {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        activity.setTransmitReceive(false);
                                    }
                                });
                                betweenStartStop = false;
//                                ReceiveMessage updateFiltered = new ReceiveMessage(activity, Arrays.toString(activity.receiveLog.toArray()) + " = " + EncodeDecode.decode(activity.receiveLog));
                                receiveMessage.data = EncodeDecode.decode(activity, activity.receiveLog, true);
                                activity.runOnUiThread(receiveMessage);
                                activity.receiveLog = new ArrayList<Integer>();
                            } else {
                                System.out.println("Received not between startStop:" + Arrays.toString(activity.receiveLog.toArray()));
                            }
                        } else if (val == 18) {
                            if (activity.lastRecieved.compareTo(activity.lastSent) < 0) {
                                activity.playMessage(activity.lastMessage);
                            }
                        }
                        else {
                            if (val != lastVal) {
                                activity.receiveLog.add(val);
                                receiveMessage.data = EncodeDecode.decode(activity, activity.receiveLog, false);
                                activity.runOnUiThread(receiveMessage);
                                long currMs = java.lang.System.currentTimeMillis();
                                System.out.println("Time since last val: " + (currMs - lastMs));
                                lastMs = currMs;
                            }
                            lastVal = val;
                        }
                    }
                }
			}
		}

}
