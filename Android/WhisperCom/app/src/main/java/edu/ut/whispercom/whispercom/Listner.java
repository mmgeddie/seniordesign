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
public class Listner {
	/**
	 * 
	 */
//	private final int stepSize = 256*4;
    private final int stepSize = 3584;

    private final int sampleRate = 44100;
//    private final int sampleRate = 8000;

//    private final double frequency[] ={770, 1336, 18000, 18010, 18500,18750,19000,19250,19500,19750,20000};
    private double frequency[];

    private AudioProcessor goertzelAudioProcessor;

	public Listner(MyActivity activity){
        frequency = new double[MyActivity.colFreqs.length+MyActivity.rowFreqs.length];
        for (int i = 0; i< MyActivity.rowFreqs.length; i++) {
            frequency[i] = MyActivity.rowFreqs[i];
        }
        for (int i = 0; i< MyActivity.colFreqs.length; i++) {
            frequency[MyActivity.rowFreqs.length + i] = MyActivity.colFreqs[i];
        }

        goertzelAudioProcessor = new GoertzelImpl(sampleRate, stepSize, frequency, new DetectedFreqHandler(activity));

		process();
	}

	/**
	 * Process a DTMF character: generate sound and decode the sound.
	 */
	public void process(){
        AudioDispatcher dispatcher = null;
        try {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, stepSize, 0);
            dispatcher.addAudioProcessor(goertzelAudioProcessor);
            new Thread(dispatcher).start();
        } catch (Exception e) {
            if (dispatcher != null)
                dispatcher.stop();
            e.printStackTrace();
        }

	}
}
