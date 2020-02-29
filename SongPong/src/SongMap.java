import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class SongMap{
	
	protected SongPong game;
	
	protected GameStats gs;
	protected Thread musicThread;
	protected MusicPlayer tuneSpinner;
	
	protected ClickTimer ct;
	protected SongSurfer ss;
	
	protected Paddle myPaddle;
	protected BallDropper bd;
	
	protected int screenW;
	protected int screenH;
	
	protected double delayTimeSec;
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public SongMap(SongPong game, String notemapPath, double delaySec) {
		this.game = game;
		this.delayTimeSec = delaySec;
		
		gs = game.gs;
		screenW = game.pWidth;
		screenH = game.pHeight;
		
		// Game components
		myPaddle = new Paddle(this);
		bd = new BallDropper(this);
				
		// Music components
		tuneSpinner = new MusicPlayer(game, delayTimeSec);
		tuneSpinner.loadMusic("src/Music/ColdplayParadise.wav");
		readNoteMap(notemapPath);
		musicThread = new Thread(tuneSpinner);
		musicThread.start();
		System.out.println("Initialized music thread...");
		
		// Tools
		ct = new ClickTimer(this);
		ss = new SongSurfer(this);
		
		startSong();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	INITIALIZE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void startSong() {
		game.customCursor.setCursorInvisible();
		game.customCursor.moveMouseToStartPos();
		tuneSpinner.cueMusic();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void updateSongStuff() {
		bd.checkDrop();
		bd.updateBalls();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void renderSongStuff(Graphics g) {
		if(bd.showBallColumns)
	    	bd.displayColumns(g);
		
		// Game Elements
		bd.renderBalls(g);
		myPaddle.drawPaddle(g);
	}
	
	public void showBallColumns() {
		bd.toggleBallColumns();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	USER INPUT
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void handleMouseMove(int x, int y) {
		myPaddle.movePaddle(x);
	}
	
	public void handleMousePress(int x, int y) {
		ct.recordClickTime(x);
	}
	
	public void handleKeyboardInput(int keyCode) {
		if(keyCode == KeyEvent.VK_PERIOD) {
			ss.skipForward();
		} else if( keyCode == KeyEvent.VK_COMMA) {
			ss.skipBackward();
		}
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	DATA
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/	
	
	protected void readNoteMap(String noteMapPath) {
		File notePath = new File(noteMapPath);
		Scanner scanner;
		try {
			scanner = new Scanner(notePath);
			while(scanner.hasNextLine()){
			    String line = scanner.nextLine();
			    String[] data = line.split(", ");
			    String type = data[0];
			    int column = Integer.valueOf(data[1]);
			    ArrayList<Double> times = new ArrayList<Double>();
			    for(int i = 2; i < data.length; i++) {
			    	times.add(Double.valueOf(data[i]));
			    }
			    switch(type) {
			    case "s":
			    	bd.spawnSimpleBall(times, column);
			    	break;
			    }
			}
			scanner.close();
		} catch (IOException e) {
			System.err.println("Could not load note map.");
		}
		
	}
}