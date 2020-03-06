package backend;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import gamecomponents.Paddle;

public abstract class SongMap{
	
	// REFERENCES
	protected SongPong game;
	protected GameStats gs;
	protected Thread musicThread;
	public MusicPlayer tuneSpinner;
	public ImageHandler imgHandler;
	public ScoreKeeper sk;
	
	// TOOLS
	protected ClickTimer ct;
	protected SongSurfer ss;
	
	// GAME COMPONENTS
	protected Paddle paddle;
	protected BallDropper bd;
	
	// WORLD INFO
	public int screenW;
	public int screenH;
	public double worldScale = 4.0;
	
	protected double delayTimeSec;
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public SongMap(SongPong game, String notemapPath, double delaySec) {
		this.game = game;
		this.gs = game.gs;
		this.delayTimeSec = delaySec;
		imgHandler = new ImageHandler(this);
		
		screenW = game.pWidth;
		screenH = game.pHeight;
		
		// Game components
		paddle = new Paddle(this);
		bd = new BallDropper(this);
				
		// Music components
		tuneSpinner = new MusicPlayer(game, delayTimeSec);
		tuneSpinner.loadMusic("src/Music/ColdplayParadise.wav");
		readNoteMap(notemapPath);
		
		// Tools
		ct = new ClickTimer(this);
		ss = new SongSurfer(this);
		sk = new ScoreKeeper(this);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	INITIALIZE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void startSong() {
		musicThread = new Thread(tuneSpinner);
		musicThread.start();
		tuneSpinner.cueMusic();

		System.out.println("Initialized music thread...");
		
		paddle.resetPaddle();
		game.moveMouseToPos(paddle.getPaddleX(), paddle.getPaddleY());
		game.customCursor.setCursorInvisible();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void updateSongStuff() {
		bd.checkDrop();
		bd.updateBalls();
	}
	
	public void animateStuff() {
		bd.animateBalls();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CLASS COMMUNICATOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	public void addPoint() {
		sk.addPoint();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void renderSongStuff(Graphics g) {
		if(bd.showBallColumns)
	    	bd.displayColumns(g);
		
		// Game Elements
		bd.renderBalls(g);
		paddle.drawPaddle(g);
		
		sk.displayScore(g);
	}
	
	public void showBallColumns() {
		bd.toggleBallColumns();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	USER INPUT
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void handleMouseMove(int x, int y) {
		paddle.movePaddle(x);
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
			    String[] data = line.split("\t");
			    
			    // if line is blank or incomplete, skip it
			    if(data.length < 3) {
			    	System.err.println("LINE SKIPPED");
			    	break;
			    }
			    
			    // COLUMN
			    int column = Integer.valueOf(data[1]);
			    
			    // TIMES
			    ArrayList<Double> times = new ArrayList<Double>();
			    for(int i = 2; i < data.length; i++) {
			    	times.add(Double.valueOf(data[i]));
			    }
			    
			    // BALL TYPE
			    String type = data[0];
			    switch(type) {
			    case "s":
			    	bd.spawnSimpleBall(times, column);
			    	break;
			    case "b":
			    	bd.spawnBounceBall(times, column);
			    	break;
			    default:
			    	System.err.println("BALL TYPE DOES NOT EXIST");
			    }
			}
			scanner.close();
		} catch (IOException e) {
			System.err.println("Could not load note map.");
		}
		
	}
}
