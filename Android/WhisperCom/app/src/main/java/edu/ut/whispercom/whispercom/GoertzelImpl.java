package edu.ut.whispercom.whispercom;


import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.DTMF;
import be.tarsos.dsp.pitch.Goertzel.FrequenciesDetectedHandler;

/**
 * Contains an implementation of the GoertzelImpl algorithm. It can be used to
 * detect if one or more predefined frequencies are present in a signal. E.g. to
 * do DTMF decoding.
 * 
 * @author Joren Six
 */
public class GoertzelImpl implements AudioProcessor {

	/**
	 * If the power in dB is higher than this threshold, the frequency is
	 * present in the signal.
	 */
	private static final double POWER_THRESHOLD = 10;// in dB (37)

	/**
	 * A list of frequencies to detect.
	 */
	private final double[] frequenciesToDetect;
	/**
	 * Cached cosine calculations for each frequency to detect.
	 */
	private final double[] precalculatedCosines;
	/**
	 * Cached wnk calculations for each frequency to detect.
	 */
	private final double[] precalculatedWnk;
	/**
	 * A calculated power for each frequency to detect. This array is reused for
	 * performance reasons.
	 */
	private final double[] calculatedPowers;

	private final FrequenciesDetectedHandler handler;

	public GoertzelImpl(final float audioSampleRate, double[] frequencies, FrequenciesDetectedHandler frequenciesDetectedHandler) {

		frequenciesToDetect = frequencies;
		precalculatedCosines = new double[frequencies.length];
		precalculatedWnk = new double[frequencies.length];
		this.handler = frequenciesDetectedHandler;

		calculatedPowers = new double[frequencies.length];

		for (int i = 0; i < frequenciesToDetect.length; i++) {
			precalculatedCosines[i] = 2 * Math.cos(2 * Math.PI
					* frequenciesToDetect[i] / audioSampleRate);
			precalculatedWnk[i] = Math.exp(-2 * Math.PI
					* frequenciesToDetect[i] / audioSampleRate);
		}
	}

	public boolean processFull(float[] audioFloatBuffer, byte[] audioByteBuffer) {
		double skn0, skn1, skn2;
		int numberOfDetectedFrequencies = 0;
		for (int j = 0; j < frequenciesToDetect.length; j++) {
			skn0 = skn1 = skn2 = 0;
			for (int i = 0; i < audioFloatBuffer.length; i++) {
				skn2 = skn1;
				skn1 = skn0;
				skn0 = precalculatedCosines[j] * skn1 - skn2
						+ audioFloatBuffer[i];
			}
			double wnk = precalculatedWnk[j];
			calculatedPowers[j] = 20 * Math.log10(Math.abs(skn0 - wnk * skn1));
			if (calculatedPowers[j] > POWER_THRESHOLD) {
				numberOfDetectedFrequencies++;
			}
		}

		if (numberOfDetectedFrequencies > 0) {
			double[] frequencies = new double[numberOfDetectedFrequencies];
			double[] powers = new double[numberOfDetectedFrequencies];
			int index = 0;
			for (int j = 0; j < frequenciesToDetect.length; j++) {
				if (calculatedPowers[j] > POWER_THRESHOLD) {
					frequencies[index] = frequenciesToDetect[j];
					powers[index] = calculatedPowers[j];
					index++;
				}
			}
			handler.handleDetectedFrequencies(frequencies, powers,
                    frequenciesToDetect, calculatedPowers.clone());
		}

		return true;
	}

    @Override
    public boolean process(AudioEvent audioEvent) {
        processFull(audioEvent.getFloatBuffer(), audioEvent.getByteBuffer());
        return true;
    }

	@Override
	public void processingFinished() {
	}

}
