package backend;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class MusicPlayer{
	
	private SongMap song;
	
	private boolean soundIsEnabled = false;
	
	Clip musicClip;
	Clip catchClip;
	Clip missClip;
	long clipTime;
	
	protected boolean musicPlaying = false;
		
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	DEFAULT CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public MusicPlayer(SongMap song) {
		this.song = song;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	MUSIC TRACK
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void toggleSound() {
		soundIsEnabled = soundIsEnabled ? false : true;
	}
	
	public void playMusic() {
		if(musicClip != null) {
			musicClip.start();
			System.out.println("Music starts at " + song.getTime());
		}
	}
	
	public void skipSeconds(double sec) {
		if(musicClip != null) {
			long currentClipTime = musicClip.getMicrosecondPosition();
			long longSkipTime = (long)(sec * 1_000_000); // convert seconds to microseconds
			musicClip.setMicrosecondPosition(currentClipTime + longSkipTime);
		}
	}
	
	public void loadMusic(String musicLocation) {
		try {
			File musicPath = new File(musicLocation);
			
			if(musicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
				musicClip = AudioSystem.getClip();
				musicClip.open(audioInput);
			}
			else {
				System.err.println("No file, bitch.");
			}
		}
		catch(Exception ex) 
		{
			System.err.println("Could not load music. RIP.");
		}
	}
	
	public void pauseMusic() {
		if(musicClip != null) {
			System.out.println("PAUSE MUSIC");
			musicPlaying = false;
			clipTime = musicClip.getMicrosecondPosition();
			musicClip.stop();
		}
	}
	
	public void resumeMusic() {
		System.out.println("RESUME MUSIC");
		musicPlaying = true;
		musicClip.setMicrosecondPosition(clipTime);
		musicClip.start();
	}
	
	/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	 * 	CLIP
	 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public synchronized void playCatchSound(String musicLocation) {
		if(soundIsEnabled) {
		  new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run() {
		      try {
		        catchClip = AudioSystem.getClip();
				File musicPath = new File(musicLocation);
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(musicPath);
		        catchClip.open(inputStream);
		        //FloatControl volume = (FloatControl) catchClip.getControl(FloatControl.Type.MASTER_GAIN);
		        //volume.setValue(-1 * 20);
		        catchClip.start(); 
		      } catch (Exception e) {
		        System.err.println(e.getMessage());
		      }
		    }
		  }).start();
		}
		}
	
	public synchronized void playMissSound(String musicLocation) {
		if(soundIsEnabled) {
		  new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run() {
		      try {
		        missClip = AudioSystem.getClip();
				File musicPath = new File(musicLocation);
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(musicPath);
		        missClip.open(inputStream);
		        missClip.start(); 
		      } catch (Exception e) {
		        System.err.println(e.getMessage());
		      }
		    }
		  }).start();
		}
		}
	
}
