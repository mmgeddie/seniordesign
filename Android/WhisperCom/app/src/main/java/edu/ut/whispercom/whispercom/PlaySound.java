package edu.ut.whispercom.whispercom;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

/**
 * Created by matt on 9/28/14.
 */
public class PlaySound {
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private final static int duration = 1; // seconds
    private final static int sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
    private final static int numSamples = (duration * sampleRate)/4;
    private final static double sampleRow[] = new double[numSamples];
    private final static double sampleCol[] = new double[numSamples];
    private static double freqOfRowTone; // hz
    private static double freqOfColTone; // hz
    private final static int[] rowFreqs = MyActivity.rowFreqs;
    private final static int[] colFreqs = MyActivity.colFreqs;
    private final static AudioTrack audioTrackRow = new AudioTrack(AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, numSamples,
            AudioTrack.MODE_STREAM);


    static byte[] genTone(){
        final byte generatedSnd[] = new byte[2 * numSamples];
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sampleRow[i] = (Math.sin(2 * Math.PI * i / (sampleRate/freqOfRowTone)) + Math.sin(2 * Math.PI * i / (sampleRate/freqOfColTone))) /2;
            sampleCol[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfColTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sampleRow buffer is normalised.
        int idx = 0;
        int ramp = numSamples / 5 ;
        for (int i = 0; i < numSamples; ++i) {
            final double dRowVal = sampleRow[i];
            final double dColVal = sampleCol[i];

            final short val;
            if (i < ramp) {
                val = (short) ((dRowVal * 32767 * i/ramp));
            } else if (i < numSamples - ramp) {
                // scale to maximum amplitude
                val = (short) ((dRowVal * 32767));
            } else {
                val = (short) ((dRowVal * 32767 * (numSamples-i)/ramp ));
            }
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx] = (byte) (val & 0x00ff);
            idx++;

            generatedSnd[idx] = (byte) ((val & 0xff00) >>> 8);
            idx++;

        }
        return generatedSnd;
    }

    public static void playSound(int index) {
        freqOfRowTone = rowFreqs[index / colFreqs.length];
        freqOfColTone = colFreqs[index % colFreqs.length];
        writeSound(genTone());
        writeSound(new byte[numSamples]);
    }

    private static void writeSound(byte generatedSnd[]){

        audioTrackRow.write(generatedSnd, 0, generatedSnd.length);

        audioTrackRow.play();
    }
}