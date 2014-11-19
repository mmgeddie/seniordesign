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
    private boolean isListening = false;

    private final int sampleRate = 44100;
//    private final int sampleRate = 8000;

    private double frequency[];

    private AudioProcessor goertzelAudioProcessor;

    private AudioDispatcher dispatcher = null;

	public Listner(MessagingActivity activity){
        frequency = new double[MessagingActivity.colFreqs.length+MessagingActivity.rowFreqs.length];
        for (int i = 0; i< MessagingActivity.rowFreqs.length; i++) {
            frequency[i] = MessagingActivity.rowFreqs[i];
        }
        for (int i = 0; i< MessagingActivity.colFreqs.length; i++) {
            frequency[MessagingActivity.rowFreqs.length + i] = MessagingActivity.colFreqs[i];
        }

        goertzelAudioProcessor = new GoertzelImpl(sampleRate, frequency, new DetectedFreqHandler(activity));

		process();
	}

	/**
	 * Process a DTMF character: generate sound and decode the sound.
	 */
	public void process(){
        isListening = true;
        try {
            final int minBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT);
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, minBufferSize, 0);
            dispatcher.addAudioProcessor(goertzelAudioProcessor);
            new Thread(dispatcher).start();
        } catch (Exception e) {
            if (dispatcher != null)
                dispatcher.stop();
            e.printStackTrace();
        }

	}

    public void stopProcessing() {
        if (isListening && dispatcher != null) {
            isListening = false;
            dispatcher.stop();
        }
    }
}
