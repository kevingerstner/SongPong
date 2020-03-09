
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

public abstract class SongMap extends Scene{
	
	// REFERENCES
	protected SongPong game;
	protected GameStats gs;
	protected Thread musicThread;
	public MusicPlayer tuneSpinner;
	public ImageHandler ih;
	public FontHandler fh;
	public ScoreKeeper sk;
	private String notemapPath;
	
	// TOOLS
	protected ClickTimer ct;
	protected SongSurfer ss;
	
	// OPTIONS
	protected boolean debugMode = false;
	
	// STATE
	protected boolean musicStarted = false;
	
	// GAME COMPONENTS
	protected Paddle paddle;
	protected BallDropper bd;
	
	// CONSTANTS
	public int paddleY;
	public int ballSpawnY;
	
	// WORLD INFO
	public int screenW;
	public int screenH;
	public double worldScale;
	
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public SongMap(SongPong game, String notemapPath, String musicPath) {
		this.game = game;
		this.gs = game.gs;
		this.ih = game.ih;
		this.fh = game.fh;
		this.notemapPath = notemapPath;
		this.worldScale = game.worldScale;
		
		screenW = game.pWidth;
		screenH = game.pHeight;
		paddleY = screenH - (screenH / 10);
		ballSpawnY = -100;
		
		// Game components
		paddle = new Paddle(this);
		bd = new BallDropper(this);
				
		// Music components
		tuneSpinner = new MusicPlayer(this);
		tuneSpinner.loadMusic(musicPath);
		
		// Notemap
		readNoteMap(notemapPath);
		
		// Tools
		ct = new ClickTimer(this);
		ss = new SongSurfer(this);
		sk = new ScoreKeeper(this);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	INITIALIZE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void initScene() {
		paddle.resetPaddle();
		game.moveMouseToPos(paddle.getPaddleX(), paddle.getPaddleY());
		game.customCursor.setCursorInvisible();
    	if(debugMode) {
    		bd.toggleShowBallNum();
    	}
		
		tuneSpinner.playMusic();
		musicStarted = true;
		game.setSceneStartTime();
		game.sceneRunning = true;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void updateScene() {
		if(!game.isPaused && game.sceneRunning) {
			bd.checkDrop();
			bd.updateBalls();
		}
	}
	
	public void animateScene() {
		bd.animateBalls();
	}
	
	public void handleGamePause() {
		tuneSpinner.pauseMusic();
		bd.stopBalls();
	}
	
	public void handleGameResume() {
		tuneSpinner.resumeMusic();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CLASS COMMUNICATOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	public double getTime() {
		return game.getSceneTime();
	}
	
	public void addPoint() {
		sk.addPoint();
	}
	
	public void playCatchSFX() {
		tuneSpinner.playCatchSound("src/music/ballCatch5.wav");
	}
	
	public void playMissSFX() {
		tuneSpinner.playMissSound("src/music/ballMiss.wav");
	}
	
	public void invertPaddle() {
		if(!paddle.invertPaddle)
			paddle.invertPaddle = true;
		else
			paddle.invertPaddle = false;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void renderScene(Graphics g) {
		if(debugMode) {
	    	bd.displayColumns(g);
		}
		
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
	
	public void handleMousePress(int x, int y, double timeClicked) {
		ct.recordClickTime(x, getTime());
	}
	
	public void handleKeyboardInput(int keyCode) {
		if(keyCode == KeyEvent.VK_PERIOD) {
			ss.skipForward();
		} else if( keyCode == KeyEvent.VK_COMMA) {
			ss.skipBackward();
		} else if( keyCode == KeyEvent.VK_0) {
			debugMode = debugMode ? false : true;
			bd.toggleShowBallNum();
		} else if( keyCode == KeyEvent.VK_1) {
			tuneSpinner.toggleSound();
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
			    if(data.length >= 3) {
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
				    	bd.createSimpleBall(times, column);
				    	break;
				    case "b":
				    	bd.createBounceBall(times, column);
				    	break;
				    default:
				    	System.err.println("BALL TYPE DOES NOT EXIST");
				    }
			    }
			    
			}
			scanner.close();
		} catch (IOException e) {
			System.err.println("Could not load note map.");
		}
		
	}
}
