import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import be.hogent.tarsos.dsp.pitch.Goertzel.FrequenciesDetectedHandler;
import be.hogent.tarsos.dsp.AudioDispatcher;
import be.hogent.tarsos.dsp.AudioProcessor;
import be.hogent.tarsos.dsp.BlockingAudioPlayer;
import be.hogent.tarsos.dsp.pitch.DTMF;
import be.hogent.tarsos.dsp.util.AudioFloatConverter;

/**
 * An example of DTMF ( Dual-tone multi-frequency signaling ) decoding with the GoertzelImpl algorithm.
 * @author Joren Six
 */
public class DTMFListner extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1143769091770146361L;
	
	private final int stepSize = 256*8;
	
	private final AudioProcessor goertzelAudioProcessor = new GoertzelImpl(44100, stepSize,DTMF.DTMF_FREQUENCIES, new FrequenciesDetectedHandler() {
		@Override
		public void handleDetectedFrequencies(final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
//			System.out.println(Arrays.toString(frequencies));
			 if (frequencies.length > 2) {
	                double fc = 0;
	                double fr = 0;
	                double highestPowerCol = 0;
	                double highestPowerRow = 0;
	                for (int i = 0; i < powers.length; i++) {
	                    if (frequencies[i] < 1000 && powers[i] > highestPowerRow) {
	                    	fr = frequencies[i];
	                    } else if (frequencies[i] > 1000 && powers[i] > highestPowerCol) {
	                    	fc = frequencies[i];
	                    }
	                }
	                System.out.println("["+fc+"] ["+fr+"]");
	                int rowIndex = -1;
	                int colIndex = -1;
	                for (int i = 0; i < 4; i++) {
	                    if (fr == DTMF.DTMF_FREQUENCIES[i])
	                        rowIndex = i;
	                }
	                for (int i = 4; i < DTMF.DTMF_FREQUENCIES.length; i++) {
	                    if (fc == DTMF.DTMF_FREQUENCIES[i])
	                        colIndex = i-4;
	                }
	                if(rowIndex>=0 && colIndex>=0){
						detectedChar.setText(""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);
	                    System.out.println(""+DTMF.DTMF_CHARACTERS[rowIndex][colIndex]);
	                }
	            }
//			for (int i = 0; i < allPowers.length; i++) {
//				powerBars[i].setValue((int) allPowers[i]);
//			}
		}
	});
	
	private final JProgressBar[] powerBars;
	private final JLabel detectedChar = new JLabel(" ");
	
	public DTMFListner(){
		this.getContentPane().setLayout(new BorderLayout(5,3));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel detectionPanel = new JPanel(new GridLayout(DTMF.DTMF_FREQUENCIES.length,2,5,3));
		powerBars = new JProgressBar[DTMF.DTMF_FREQUENCIES.length];
		for(int i= 0 ; i < DTMF.DTMF_FREQUENCIES.length ; i++){
			detectionPanel.add(new JLabel(DTMF.DTMF_FREQUENCIES[i] + "Hz"));
			powerBars[i] = new JProgressBar(-30,50);
			detectionPanel.add(powerBars[i]);
			powerBars[i].setValue(-30);
		}
		detectionPanel.setBorder(new TitledBorder("Detected Powers"));
		
		JPanel labelPanel = new JPanel(new BorderLayout());
		labelPanel.add(detectionPanel,BorderLayout.NORTH);
		
		detectedChar.setBorder(new TitledBorder("Detected character"));
		detectedChar.setHorizontalAlignment(JLabel.CENTER);
		
		Font f = new Font("Police", Font.PLAIN, 20);
		detectedChar.setFont(f);
		
		labelPanel.add(detectedChar,BorderLayout.CENTER);
		
		this.add(labelPanel,BorderLayout.NORTH);
		try {
			process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String...strings){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					//ignore failure to set default look en feel;
				}
				JFrame frame = new DTMFListner();
				frame.pack();
				frame.setSize(200,290);
				frame.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
	}
	
	/**
	 * Process a DTMF character: generate sound and decode the sound.
	 * @param character The character.
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public void process() throws UnsupportedAudioFileException, LineUnavailableException{
		Mic mic = new Mic();
		mic.startRecording();
		
		final AudioInputStream inputStream2 = mic.getIS();
		final AudioDispatcher dispatcher2 = new AudioDispatcher(inputStream2, stepSize, 0);
		dispatcher2.addAudioProcessor(goertzelAudioProcessor);
		new Thread(dispatcher2).start();
		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
////		new Thread(dispatcher).start();
//		
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	mic.stopRecording();
//        System.out.println("Stop Recording");
//        
//        
//		AudioInputStream inputStream3;
//		try {
//			inputStream3 = AudioSystem.getAudioInputStream(mic.getFile());
////			inputStream3 = AudioSystem.getAudioInputStream(new File("/Users/matt/Downloads/DTMF_dialing.wav"));
//			final AudioDispatcher dispatcher3 = new AudioDispatcher(inputStream3, stepSize, 0);
//			dispatcher3.addAudioProcessor(goertzelAudioProcessor);
//			dispatcher3.addAudioProcessor(new BlockingAudioPlayer(format, stepSize, 0));
//			new Thread(dispatcher3).start();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
