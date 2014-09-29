package edu.ut.whispercom.whispercom;

import android.app.Activity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.tarsos.dsp.pitch.DTMF;
import be.tarsos.dsp.pitch.Goertzel;

/**
 * Created by matt on 9/28/14.
 */
public class DetectedFreqHandler implements Goertzel.FrequenciesDetectedHandler {
    private MyActivity activity;
    private TextView textViewReceiveAllMonitor;
    private TextView textViewReceiveFilterLog;

    public DetectedFreqHandler(MyActivity activity) {
        this.activity = activity;
        textViewReceiveAllMonitor = (TextView)activity.findViewById(R.id.textViewReceiveAllMonitor);
        textViewReceiveFilterLog = (TextView)activity.findViewById(R.id.textViewReceiveFilterLog);
    }

//    @Override
//    public void handleDetectedFrequencies2(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
//        System.out.println("All freq: "+ Arrays.toString(frequencies));
//        UpdateTextView updateAll = new UpdateTextView(Arrays.toString(frequencies), textViewReceiveAllMonitor);
//        activity.runOnUiThread(updateAll);
//        if (frequencies.length >= 2) {
//            double f1 = 0;
//            double f2 = 0;
//            double highestPower = 0;
//            for (int i = 0; i < powers.length; i++) {
//                if (powers[i] > highestPower) {
//                    f2 = f1;
//                    f1 = frequencies[i];
//                }
//            }
//            System.out.println("["+f1+"] ["+f2+"]");
//            int rowIndex = -1;
//            int colIndex = -1;
//            for (int i = 0; i < 4; i++) {
//                if (f1 == allFrequencies[i] || f2 == allFrequencies[i])
//                    rowIndex = i;
//            }
//            for (int i = 4; i < allFrequencies.length; i++) {
//                if (f1 == allFrequencies[i] || f2 == allFrequencies[i])
//                    colIndex = i-4;
//            }
//            if(rowIndex>=0 && colIndex>=0){
//                System.out.println(""+ DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);
//
//                String number = ""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex];
//                UpdateTextView updateFilter = new UpdateTextView(number, textViewReceiveFilterLog);
//                activity.runOnUiThread(updateFilter);
//            }
//        }
//    }

    @Override
    public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
			System.out.println(Arrays.toString(frequencies));
            UpdateTextView updateAll = new UpdateTextView(Arrays.toString(frequencies), textViewReceiveAllMonitor);
            activity.runOnUiThread(updateAll);
			if (frequencies.length == 2) {
				int rowIndex = -1;
				int colIndex = -1;
				for (int i = 0; i < 4; i++) {
					if (frequencies[0] == allFrequencies[i] || frequencies[1] == allFrequencies[i])
						rowIndex = i;
				}
				for (int i = 4; i < allFrequencies.length; i++) {
					if (frequencies[0] == allFrequencies[i] || frequencies[1] == allFrequencies[i])
						colIndex = i-4;
				}
				if(rowIndex>=0 && colIndex>=0){
                    String number = ""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex];
					System.out.println(number);
                    activity.receiveLog.add(DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);
                    String filteredLog = activity.receiveLog.toString();
                    if (filteredLog.length() > 84) {
                        filteredLog = filteredLog.substring(filteredLog.length()-84, filteredLog.length());
                    }
                    UpdateTextView updateFiltered = new UpdateTextView(filteredLog, textViewReceiveFilterLog);
                    activity.runOnUiThread(updateFiltered);
				}
			}
		}

}
