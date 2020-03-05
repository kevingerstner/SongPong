package backend;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicPlayer implements Runnable {
	
	SongPong game;
	GameStats gs;
	
	Clip audioClip;
	long clipTime;
	
	protected boolean musicPlaying = false;
	protected boolean musicStarted = false;
	
	private long delayTimeMillis;
	protected double songStartTime;
	
	public MusicPlayer(SongPong game, double delayTime) {
		this.game = game;
		this.gs = game.gs;
		this.delayTimeMillis = (long)(delayTime * 1000);
	}
	
	public void playMusic() {
		audioClip.start();
		songStartTime = gs.getTimeElapsed(); 
		System.out.println("START MUSIC");
	}
	
	public void cueMusic() {
		System.out.println("Cued music...");
		musicStarted = true;
	}
	
	public void skipSeconds(double sec) {
		long currentClipTime = audioClip.getMicrosecondPosition();
		long longSkipTime = (long)(sec * 1_000_000); // convert seconds to microseconds
		audioClip.setMicrosecondPosition(currentClipTime + longSkipTime);
	}
	
	public void loadMusic(String musicLocation) {
		try {
			File musicPath = new File(musicLocation);
			
			if(musicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
				audioClip = AudioSystem.getClip();
				audioClip.open(audioInput);
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
		clipTime = audioClip.getMicrosecondPosition();
		audioClip.stop();
	}
	
	public void resumeMusic() {
		System.out.println("RESUME MUSIC");
		musicPlaying = true;
		audioClip.setMicrosecondPosition(clipTime);
		audioClip.start();
	}

	@Override
	public void run() {
		if(!musicStarted) {
			try {
				Thread.sleep(delayTimeMillis);
			} catch (InterruptedException e) {
				System.err.println("Music sleep failed.");
			}
			musicStarted = true;
			playMusic();
		}
	}
}
