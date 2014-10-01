package edu.ut.whispercom.whispercom;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matt on 9/28/14.
 */
public class PlaySound {
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private final int duration = 1; // seconds
    private final int sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
    private final int numSamples = duration * sampleRate;
    private final double sampleRow[] = new double[numSamples];
    private final double sampleCol[] = new double[numSamples];
    private final double freqOfRowTone; // hz
    private final double freqOfColTone; // hz
    private final int[] rowFreqs = MyActivity.rowFreqs;
    private final int[] colFreqs = MyActivity.colFreqs;

    private final byte generatedRowSnd[] = new byte[2 * numSamples];
    private final byte generatedColSnd[] = new byte[2 * numSamples];

    Handler handler = new Handler();

    PlaySound(String buttonText) {
        Map<String, int[]> toneMap = new HashMap<String, int[]>();
        toneMap.put("1", new int[]{0, 0});
        toneMap.put("2", new int[]{0, 1});
        toneMap.put("3", new int[]{0, 2});
        toneMap.put("A", new int[]{0, 3});
        toneMap.put("4", new int[]{1, 0});
        toneMap.put("5", new int[]{1, 1});
        toneMap.put("6", new int[]{1, 2});
        toneMap.put("B", new int[]{1, 3});
        toneMap.put("7", new int[]{2, 0});
        toneMap.put("8", new int[]{2, 1});
        toneMap.put("9", new int[]{2, 2});
        toneMap.put("C", new int[]{2, 3});
        toneMap.put("*", new int[]{3, 0});
        toneMap.put("0", new int[]{3, 1});
        toneMap.put("#", new int[]{3, 2});
        toneMap.put("D", new int[]{3, 3});

        this.freqOfRowTone = rowFreqs[toneMap.get(buttonText)[0]];
        this.freqOfColTone = colFreqs[toneMap.get(buttonText)[1]];
        create();
    }

    protected void create() {
        // Use a new tread as this can take a while
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                genTone();
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        thread.start();
    }

    void genTone(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sampleRow[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfRowTone));
            sampleCol[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfColTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sampleRow buffer is normalised.
        int idx = 0;
        int ramp = numSamples / 5 ;
        for (int i = 0; i < numSamples; ++i) {
            final double dRowVal = sampleRow[i];
            final double dColVal = sampleCol[i];

            final short valRow;
            final short valCol;
            if (i < ramp) {
                valRow = (short) ((dRowVal * 32767 * i/ramp));
                valCol = (short) ((dColVal * 32767 * i/ramp));
            } else if (i < numSamples - ramp) {
                // scale to maximum amplitude
                valRow = (short) ((dRowVal * 32767));
                valCol = (short) ((dColVal * 32767));
            } else {
                valRow = (short) ((dRowVal * 32767 * (numSamples-i)/ramp ));
                valCol = (short) ((dColVal * 32767 * (numSamples-i)/ramp ));
            }
            // in 16 bit wav PCM, first byte is the low order byte
            generatedRowSnd[idx] = (byte) (valRow & 0x00ff);
            generatedColSnd[idx] = (byte) (valCol & 0x00ff);
            idx++;

            generatedRowSnd[idx] = (byte) ((valRow & 0xff00) >>> 8);
            generatedColSnd[idx] = (byte) ((valCol & 0xff00) >>> 8);
            idx++;

        }
    }

    void playSound(){
        final AudioTrack audioTrackRow = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STREAM);
        audioTrackRow.write(generatedRowSnd, 0, generatedRowSnd.length);

        final AudioTrack audioTrackCol = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STREAM);
        audioTrackCol.write(generatedColSnd, 0, generatedColSnd.length);

        audioTrackRow.play();
        audioTrackCol.play();

        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        audioTrackRow.release();
        audioTrackCol.release();
    }
}