package edu.ut.whispercom.whispercom;

import android.app.Activity;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;


import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.Goertzel.FrequenciesDetectedHandler;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.DTMF;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AndroidAudioInputStream;
import be.tarsos.dsp.pitch.DTMF;

/**
 * An example of DTMF ( Dual-tone multi-frequency signaling ) decoding with the GoertzelImpl algorithm.
 * @author Joren Six
 */
public class DTMFListner{
	/**
	 * 
	 */
//	private final int stepSize = 256*4;
    private final int stepSize = 3584*2;

    private final int sampleRate = 44100;
//    private final int sampleRate = 8000;1800

    private final double frequency[] ={18000, 18010, 18500,18750,19000,19250,19500,19750,20000};

    private TextView view;
    private Activity activity;
	
	private final AudioProcessor goertzelAudioProcessor = new GoertzelImpl(sampleRate, stepSize, frequency, new FrequenciesDetectedHandler() {
		@Override
		public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
			System.out.println(Arrays.toString(frequencies));
			if (frequencies.length == 2) {
				int rowIndex = -1;
				int colIndex = -1;
				for (int i = 0; i < 4; i++) {
					if (frequencies[0] == frequency[i] || frequencies[1] == frequency[i])
						rowIndex = i;
				}
				for (int i = 4; i < frequency.length; i++) {
					if (frequencies[0] == frequency[i] || frequencies[1] == frequency[i])
						colIndex = i-4;
				}
				if(rowIndex>=0 && colIndex>=0){
					System.out.println(""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);
				}
			}
		}
	});

    private final AudioProcessor lowAudioProcessor = new GoertzelImpl(sampleRate, stepSize, frequency, new FrequenciesDetectedHandler() {
        @Override
        public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
            System.out.println("All freq: "+Arrays.toString(frequencies));
            UpdateNum updateNum = new UpdateNum(Arrays.toString(frequencies));
            activity.runOnUiThread(updateNum);
            if (frequencies.length >= 2) {
                double f1 = 0;
                double f2 = 0;
                double highestPower = 0;
                for (int i = 0; i < powers.length; i++) {
                    if (powers[i] > highestPower) {
                        f2 = f1;
                        f1 = frequencies[i];
                    }
                }
                System.out.println("["+f1+"] ["+f2+"]");
                int rowIndex = -1;
                int colIndex = -1;
                for (int i = 0; i < 4; i++) {
                    if (f1 == frequency[i] || f2 == frequency[i])
                        rowIndex = i;
                }
                for (int i = 4; i < frequency.length; i++) {
                    if (f1 == frequency[i] || f2 == frequency[i])
                        colIndex = i-4;
                }
                if(rowIndex>=0 && colIndex>=0){
                    System.out.println(""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);

                    String number = ""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex];
                    UpdateNum updateNum = new UpdateNum(number);
                    activity.runOnUiThread(updateNum);
                }
            }
        }
    });

	public DTMFListner(Activity activity){
        this.activity = activity;
        view = (TextView)activity.findViewById(R.id.textView);
        view.setText("testing");
		process();
	}

	/**
	 * Process a DTMF character: generate sound and decode the sound.
	 */
	public void process(){
        AudioDispatcher dispatcher = null;
        try {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, stepSize, 0);
//            dispatcher.addAudioProcessor(goertzelAudioProcessor);
            dispatcher.addAudioProcessor(lowAudioProcessor);
            new Thread(dispatcher).start();
        } catch (Exception e) {
            if (dispatcher != null)
                dispatcher.stop();
            e.printStackTrace();
        }

	}

    private class UpdateNum implements Runnable {
        String num;
        UpdateNum(String num) {
            this.num = num;
        }
        @Override
        public void run() {
            view.setText(num);
        }
    };
}
