package backend;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import gamecomponents.Paddle;

public abstract class Ball {

	// CONSTANTS
	protected final static double BALL_SPEED = 200;
	final static int BALL_SIZE = 50; // Diameter
	private DecimalFormat df = new DecimalFormat("0.###");  // 3 dp
	
	protected Paddle paddle;
	protected BallDropper bd;
	protected GameStats gs;
	
	// ATTRIBUTES
	Ellipse2D ball_shape;
	public int ballNum;
	Color myColor = Color.red;
	private boolean showBallNum = true;
	
	// POSITION
	protected int startPosX;
	protected int startPosY;
	protected float[] startPosition = new float[2];
	protected float[] position = new float[2];
	
	// VELOCITY
	protected double[] velocity = new double[2];
	
	// ACCELERATION
	protected double[] acceleration = new double[2];
	
	// TIME
	protected ArrayList<Double> spawnTimes;
	protected boolean firstUpdate = true;
	protected double totalTime = 0;
	protected double lastTime;
	
	// STATE
	protected boolean isFalling = false;
	protected boolean isCaught = false;
	protected boolean isBouncing = false;
	protected boolean doneBouncing = false;
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	DEFAULT CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public Ball() {
		initPhysics();
	}
	
	public Ball(SongMap song, ArrayList<Double> spawnTimes, int[] pos, int num) {
		paddle = song.paddle;
		this.bd = song.bd;
		this.gs = song.gs;
		ballNum = num;
		
		// POSITION
		startPosition[0] = pos[0];
		startPosition[1] = pos[1];
		position[0] = startPosition[0];
		position[1] = startPosition[1];
		
		initPhysics();
		
		// ATTRIBUTES
		ball_shape = new Ellipse2D.Float(startPosX, startPosY, BALL_SIZE, BALL_SIZE);
		
		// TIME
		this.spawnTimes = spawnTimes;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	PHYSICS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	private void initPhysics() {
		
		// v0 = speed
		velocity[0] = 0;
		velocity[1] = BALL_SPEED;
		
		acceleration[0] = 0;
		acceleration[1] = 150;
	}
	
	public double calcDropTime(double deltaY) {
		double determinant = BALL_SPEED * BALL_SPEED + (2 * acceleration[1] * deltaY);
		double time = (-BALL_SPEED + Math.sqrt(determinant)) / acceleration[1];
		System.out.println("++++++++++++++++++++++++++++++");
		System.out.println("Expected Ball Drop Time: " + df.format(time) + " sec.");
		System.out.println("++++++++++++++++++++++++++++++");
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
		isFalling = true;
		isBouncing = false;
		isCaught = false;
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
		
		// ---- FALL ------------------------
		if(isFalling) {
			position[1] += (velocity[1] * timeStep); // update position
			checkCollide();
		}
		
		// ---- BOUNCE ----------------------
		if(isBouncing) {
			position[1] += (velocity[1] * timeStep); // update position
			handleBounce();
		}
		
		// ---- END -------------------------
		lastTime = startTime;
		totalTime += timeStep;
	}
	
	protected boolean checkMissed() {
		return (int)position[1]-BALL_SIZE > bd.screenH;
	}
	
	public boolean checkDoneBouncing() {
		return (isCaught && !isBouncing);
	}
	
	synchronized public void stopMoving() {
		firstUpdate = true;
	}
	
	public boolean checkCollide() {
		//DETECT
		boolean collided = paddle.checkCatchBall((int)position[0], (int)position[1], BALL_SIZE);
		
		//ACTION
		if(collided) {
			//System.out.println("CAUGHT @ " + gs.getTimeElapsed());
			isFalling = false;
			isCaught = true;
			isBouncing = true;
			myColor = Color.blue;
						
			velocity[1] = -BALL_SPEED;
			
			handleCollide();
		
		}
		return collided;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	protected void drawBall(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(myColor);
		
		// X :
		// Y : shifted size down so that y-position is at the bottom of the ball (good for paddle collision)
		g2.fillOval((int)position[0], (int)position[1]-BALL_SIZE, BALL_SIZE, BALL_SIZE);
		if(showBallNum)
			displayBallNum(g2);
	}
	
	private void displayBallNum(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.drawString("" + ballNum, (int)position[0]+(BALL_SIZE/2)-5, (int)position[1]-(BALL_SIZE/2)+5);
	}
		
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	GETTERS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public double getSpawnTime() { return spawnTimes.get(0); }
	
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
	
	protected abstract void handleBounce();
	
}
