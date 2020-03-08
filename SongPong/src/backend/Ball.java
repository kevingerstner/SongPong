package backend;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import gamecomponents.Paddle;

public abstract class Ball {

	// CONSTANTS
	protected final static double BALL_SPEED = 200;
	private double worldScale;
	private DecimalFormat df = new DecimalFormat("0.###");  // 3 dp
	
	// REFERENCES
	protected SongPong game;
	protected SongMap song;
	protected Paddle paddle;
	protected BallDropper bd;
	protected GameStats gs;
	protected ImageHandler ih;
	
	// ATTRIBUTES
	public int ballNum;
	protected int ballSize; // Diameter
	protected Color myColor = Color.red;
	
	// IMAGE
	protected BufferedImage ballSprite;
	
	// POSITION
	protected int startPosX;
	protected int startPosY;
	protected int[] startPosition = new int[2];
	protected int[] position = new int[2];
	
	// VELOCITY
	protected double[] velocity = new double[2];
	
	// ACCELERATION
	protected double[] acceleration = new double[2];
	
	// TIME
	protected ArrayList<Double> spawnTimes;
	protected boolean firstUpdate = true;
	protected double totalTime = 0;
	protected double lastTime;
	protected double catchTime;
	
	// STATE
	protected boolean falling = false;
	protected boolean missed = false;
	protected int timesCaught = 0;
	protected int numBouncesLeft;
	protected boolean doneBouncing = false;
	protected boolean readyToDelete = false;
	
	// OPTIONS
	private boolean showBallNum = true;
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	DEFAULT CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public Ball(SongMap song, ArrayList<Double> spawnTimes, int[] pos, int num) {
		this.song = song;
		paddle = song.paddle;
		this.spawnTimes = spawnTimes;
		this.bd = song.bd;
		this.gs = song.gs;
		ih = song.imgHandler;
		this.game = song.game;
		worldScale = song.worldScale;
		
		// ATTRIBUTES
		ballNum = num;
		numBouncesLeft = spawnTimes.size();
		ballSprite = ih.loadImage("src/images/ball_red.png");
		ballSize = (int)(ballSprite.getWidth() * worldScale);
		
		// POSITION
		startPosition[0] = pos[0];
		startPosition[1] = pos[1];
		position[0] = startPosition[0];
		position[1] = startPosition[1];
		
		// PHYSICS
		initPhysics();
	}
	
	/**
	 * This constructor is used to create a test ball to gather info
	 * @param song
	 */
	public Ball(SongMap song) {
		this.song = song;
		this.game = song.game;
		ih = song.imgHandler;
		
		initPhysics();
		ballSprite = ih.loadImage("src/images/ball_red.png");
		ballSize = (int)(ballSprite.getWidth() * worldScale);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	PHYSICS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	private void initPhysics() {
		velocity[0] = 0;
		velocity[1] = BALL_SPEED;
		
		acceleration[0] = 0;
		acceleration[1] = BallDropper.GRAVITY_C;
	}
	
	public double calcDropTime(double deltaY) {
		double determinant = BALL_SPEED * BALL_SPEED + (2.0 * acceleration[1] * deltaY);
		double time = (-BALL_SPEED + Math.sqrt(determinant)) / acceleration[1];
		System.out.println("+++++++++++++++++++++++++++++++++++");
		System.out.println("Expected Ball Drop Time: " + df.format(time) + " sec.");
		System.out.println("+++++++++++++++++++++++++++++++++++");
		return time;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	INITIATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void moveBallToSpawnLoc() {
		position[0] = startPosition[0];
		position[1] = startPosition[1];
		stopMoving();
		
		resetState();
		myColor = Color.green;
	}
	
	private void resetState() {
		falling = true;
		timesCaught = 0;
		doneBouncing = false;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	synchronized public void moveBall() {
		// Ensures velocity starts at 0
		if(firstUpdate) {
			lastTime = gs.getTimeElapsed();
			firstUpdate = false;
		}
		
		double startTime = gs.getTimeElapsed();
		double elapsedTime = startTime - lastTime;
		double timeStep = elapsedTime; // in seconds
		
		// ---- UPDATE ------------------------
		velocity[0] += (acceleration[0] * timeStep);
		velocity[1] += (acceleration[1] * timeStep);
		
		position[1] += (velocity[1] * timeStep); // update position
		
		// ---- FALL ------------------------
		if(falling && checkCollide()) {
			catchTime = gs.getTimeElapsed();
			System.out.println("CAUGHT BALL " + ballNum + " @ t = "+ df.format(catchTime));
			position[1] -= 5; // move off of paddle
			timesCaught++;
			song.addPoint();
			numBouncesLeft--;
			
			if(numBouncesLeft > 0) {
				//System.out.println("ball " + ballNum + " has " + numBouncesLeft + " bounces left.");
				velocity[1] = -BALL_SPEED;
				handleCollide();
			}
			else {
				doneBouncing = true;
				velocity[1] = -BALL_SPEED;
				handleCollide();
			}
			
		}
		
		// ---- FINISH CONDITIONS -------------
		
		if(checkMissed()) {
			missed = true;
			handleFinish();
		}
		
		// ---- END -------------------------
		lastTime = startTime;
		totalTime += timeStep;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	STATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	protected boolean checkIfFinished() {
		return readyToDelete;
	}
	
	protected boolean checkMissed() {
		return (int)position[1]-ballSize > bd.screenH;
	}
	
	synchronized public void stopMoving() {
		firstUpdate = true;
	}
	
	public boolean checkCollide() {
		return paddle.checkCatchBall((int)position[0], (int)position[1], ballSize);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	protected void drawBall(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
	
		int[] adjPos = {position[0] - (ballSize / 2), position[1] - ballSize};
		ih.drawImage(g2, ballSprite, adjPos);
		
		if(showBallNum)
			displayBallNum(g2);
	}
	
	private void displayBallNum(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.drawString("" + ballNum, (int)(position[0] - 5), (int)(position[1] - (ballSize / 2) + 5 ));
	}
		
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	GETTERS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public double getSpawnTime() { return spawnTimes.get(0); }
	
	public double getCatchTime() { return catchTime; }
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	SETTERS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void skipSeconds(double sec) {
		for(int t = 0; t < spawnTimes.size(); t++) {
			spawnTimes.set(t, spawnTimes.get(t) - sec);
		}
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	STUFF
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	private boolean first = true;
	
	private void debugMovement() {
		System.out.println("---------------------------------");
		System.out.println("TIME: " + totalTime);
		//System.out.println("FORCE: " + force[1]);
		System.out.println("ACCELERATION: " + acceleration[1]);
		System.out.println("VELOCITY: " + velocity[1]);
		System.out.println("POSITION: " + position[1]);
		System.out.println("---------------------------------");
		
		if(position[1] > 1050 && first) {
			System.out.println("Actual Time to drop:" + df.format(totalTime));
			System.out.println("+++++++++++++++++++++++++++++++++++++");
			first = false;
		}
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	ABSTRACT CLASSES
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	protected abstract void handleCollide();
	
	protected abstract void handleFinish();
	
	protected abstract void animate();
	
}
