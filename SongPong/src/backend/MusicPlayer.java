package backend;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicPlayer implements Runnable {
	
	SongPong game;
	GameStats gs;
	
	Clip musicClip;
	Clip catchClip;
	Clip missClip;
	long clipTime;
	
	protected boolean musicPlaying = false;
	protected boolean musicStarted = false;
	
	private long delayTimeMillis;
	protected double songStartTime;
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	DEFAULT CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public MusicPlayer(SongPong game, double delayTime) {
		this.game = game;
		this.gs = game.gs;
		this.delayTimeMillis = (long)(delayTime * 1000);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	MUSIC TRACK
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void playMusic() {
		musicClip.start();
		songStartTime = gs.getTimeElapsed(); 
		System.out.println("START MUSIC @ time = " + songStartTime);
	}
	
	public void cueMusic() {
		System.out.println("Cued music...");
		musicStarted = true;
	}
	
	public void skipSeconds(double sec) {
		long currentClipTime = musicClip.getMicrosecondPosition();
		long longSkipTime = (long)(sec * 1_000_000); // convert seconds to microseconds
		musicClip.setMicrosecondPosition(currentClipTime + longSkipTime);
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
			ex.printStackTrace();
			System.err.println("Could not load music. RIP.");
		}
	}
	
	public void pauseMusic() {
		System.out.println("PAUSE MUSIC");
		musicPlaying = false;
		clipTime = musicClip.getMicrosecondPosition();
		musicClip.stop();
	}
	
	public void resumeMusic() {
		System.out.println("RESUME MUSIC");
		musicPlaying = true;
		musicClip.setMicrosecondPosition(clipTime);
		musicClip.start();
	}

	@Override
	public void run() {
		// Wait until signal is recieved
		while(!musicStarted) {
			Thread.yield();
		}
		try {
			Thread.sleep(delayTimeMillis);
		} catch (InterruptedException e) {
			System.err.println("Music sleep failed.");
		}
		playMusic();
	}
	
	/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	 * 	CLIP
	 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public synchronized void playCatchSound(String musicLocation) {
		  new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run() {
		      try {
		        catchClip = AudioSystem.getClip();
				File musicPath = new File(musicLocation);
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(musicPath);
		        catchClip.open(inputStream);
		        catchClip.start(); 
		      } catch (Exception e) {
		        System.err.println(e.getMessage());
		      }
		    }
		  }).start();
		}
	
	public synchronized void playMissSound(String musicLocation) {
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
