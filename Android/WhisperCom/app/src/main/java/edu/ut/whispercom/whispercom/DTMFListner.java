package edu.ut.whispercom.whispercom;

import android.media.AudioRecord;

import java.util.Arrays;


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
	private static final long serialVersionUID = -1143769091770146361L;
	
	private final int stepSize = 256*4;
	
	private final AudioProcessor goertzelAudioProcessor = new GoertzelImpl(44100, stepSize, DTMF.DTMF_FREQUENCIES, new FrequenciesDetectedHandler() {
		@Override
		public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
			System.out.println(Arrays.toString(frequencies));
			if (frequencies.length == 2) {
				int rowIndex = -1;
				int colIndex = -1;
				for (int i = 0; i < 4; i++) {
					if (frequencies[0] == DTMF.DTMF_FREQUENCIES[i] || frequencies[1] == DTMF.DTMF_FREQUENCIES[i])
						rowIndex = i;
				}
				for (int i = 4; i < DTMF.DTMF_FREQUENCIES.length; i++) {
					if (frequencies[0] == DTMF.DTMF_FREQUENCIES[i] || frequencies[1] == DTMF.DTMF_FREQUENCIES[i])
						colIndex = i-4;
				}
				if(rowIndex>=0 && colIndex>=0){
//					detectedChar.setText(""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);
					System.out.println(""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);
//					for (int i = 0; i < allPowers.length; i++) {
//						powerBars[i].setValue((int) allPowers[i]);
//					}
				}
			}
		}
	});

    private final AudioProcessor lowAudioProcessor = new GoertzelImpl(8000, stepSize, DTMF.DTMF_FREQUENCIES, new FrequenciesDetectedHandler() {
        @Override
        public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
            System.out.println(Arrays.toString(frequencies));
        }
    });

	public DTMFListner(){
		process();
	}

	public static void main(String...strings){
		new DTMFListner();
	}
	
	/**
	 * Process a DTMF character: generate sound and decode the sound.
	 */
	public void process(){
        AudioDispatcher dispatcher = null;
        try {
//            Emulator only supports 8000 sample rate
//            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, stepSize, 0);
//            dispatcher.addAudioProcessor(goertzelAudioProcessor);
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(8000, stepSize, 0);
            dispatcher.addAudioProcessor(lowAudioProcessor);
            new Thread(dispatcher).start();
        } catch (Exception e) {
            if (dispatcher != null)
                dispatcher.stop();
            e.printStackTrace();
        }

	}
}
