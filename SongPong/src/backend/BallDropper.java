package backend;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import gamecomponents.BallBouncing;
import gamecomponents.BallSimple;
import gamecomponents.Paddle;

public class BallDropper {

	// REFERENCES
	private SongMap song;
	private GameStats stats;
	private Paddle paddle;
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	
	// CONSTANTS
	final static int NUM_COL = 16;
	protected final static float GRAVITY_C = 150f;
	private int START_POS_Y; //NOTE: START_POS_Y = 0 means that the top of the ball is at y = 0
	
	// SCREEN INFO
	protected int screenW;
	protected int screenH;
	private int screenPadding;
	private int effScreenW;
	private int[] ballCols;
	protected boolean showBallColumns = true;
	
	// BALLS
	private int ballCounter = 0;
	private int dropIndex = 0;
	private int ballSize;
	private ArrayList<Ball> ballList = new ArrayList<Ball>(); // list of balls to drop
	private ArrayList<Ball> activeBallList = new ArrayList<Ball>(); //stores all balls currently spawned
	private ArrayList<Ball> finishedBallList = new ArrayList<Ball>(); //stores all missed balls
	
	// TIME
	protected double dropTime;
	protected double delayTime;
	protected double lagShift = 0.25; // Don't know where this lag is coming from
	
	// POSITION
	private double deltaY;

	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public BallDropper(SongMap g) {
		song = g;
		stats = song.gs;
		paddle = song.paddle;
		delayTime = song.delayTimeSec;
		
		screenW = song.screenW;
		screenH = song.screenH;
		
		calcColumns();
		
		calcDropTime();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	SPAWN
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public BallBouncing spawnBounceBall(ArrayList<Double> hitTimes, int column) {
		// ATTRIBUTES
		int startPosX = ballCols[column] - (ballSize / 2);
		int[] pos = {startPosX, START_POS_Y};
		Color c = Color.red;
		
		// TIME
		// hitTime: desired time, in seconds, the ball should hit
		// delayTime: time to wait before dropping
		// dropTime: time it takes for the ball to fall
		for(int t = 0; t < hitTimes.size(); t++) {
			hitTimes.set(t, hitTimes.get(t) - dropTime);
		}
		
		BallBouncing bounceBall = new BallBouncing(song, hitTimes, pos, c, ballCounter+1);
		ballList.add(bounceBall);
		ballCounter++;
		return bounceBall;
	}
	
	public BallSimple spawnSimpleBall(ArrayList<Double> hitTimes, int column) {
		// ATTRIBUTES
		int startPosX = ballCols[column] - (ballSize / 2);
		int[] pos = {startPosX, START_POS_Y};
		Color c = Color.red;
		
		// TIME
		// hitTime: desired time, in seconds, the ball should hit
		// delayTime: time to wait before dropping
		// dropTime: time it takes for the ball to fall
		for(int t = 0; t < hitTimes.size(); t++) {
			hitTimes.set(t, hitTimes.get(t) - dropTime);
		}
		
		BallSimple simpBall = new BallSimple(song, hitTimes, pos, c, ballCounter+1);
		ballList.add(simpBall);
		ballCounter++;
		return simpBall;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	GAME CONTROLS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void purgeBalls() {	
		for(Ball b : activeBallList) {
			finishedBallList.add(b);
		}
	}
	
	public void stopBalls() {
		for(Ball ball : activeBallList) {
			ball.stopMoving();
		}
	}
	
	public void removeBall(Ball b) {
		try{
			ballList.remove(b);
		} catch(Exception e) {
			System.err.println("Could not remove the ball.");
		}
	}
	
	public void rewindBalls() {
		double currentTime = stats.getTimeElapsed();
		System.out.println("yahhhh it's rewind time " + df.format(currentTime - delayTime));
		delayTime = 0;
		
		for(int i = dropIndex-1; i > 0; i--) {
			Ball ball = ballList.get(i);
			
			if(ball.getSpawnTime() > currentTime - delayTime) {
				System.out.println("Rewind ball " + ball.ballNum + " with time: " + ball.getSpawnTime());
				finishedBallList.remove(ball);
				ball.moveBallToSpawnLoc();
				dropIndex--;
			}
		}
		System.out.println("NEW DROP INDEX: " + dropIndex);
		activeBallList.clear();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	DROPPER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void checkDrop() {
		double currentTime = stats.getTimeElapsed();
		//System.out.println("CHECK DROP @ t = " + currentTime);
		
		if(dropIndex < ballList.size()) {
			Ball b = ballList.get(dropIndex);
			
			if(b.getSpawnTime() <= currentTime - delayTime) {
				
				// This second condition prevents a ball from spawning if its time has been skipped
				if(checkRemove(b, currentTime)) {}
				else {
					//System.out.println("BALL " + b.ballNum + "/ SPAWN @ t=" + df.format(b.getSpawnTime()) + "s / TIME: " + df.format(currentTime));
					
					activeBallList.add(b); //add to the list of spawned balls
	
					b.falling = true;
					dropIndex++;
				}
			}
		}
	}
	
	private boolean checkRemove(Ball b, double currentTime) {
		if(b.getSpawnTime() + 0.25 < currentTime - delayTime) {
			System.out.println("Removed ball with drop time " + df.format(b.getSpawnTime()));
			finishedBallList.add(b);
			dropIndex++;
			return true;
		}
		else
			return false;
	}
	
	public void calcDropTime() {
		BallSimple testBall = new BallSimple(song);
		ballSize = testBall.ballSize;
		START_POS_Y = -2 * ballSize; 	// determine what height to spawn ball from
		
		deltaY = paddle.getPaddleTopY() - START_POS_Y; // how far to drop
		dropTime = testBall.calcDropTime(deltaY); // how long to drop
		removeBall(testBall);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void updateBalls() {
		for(Ball ball : activeBallList) {
			ball.moveBall();
			
			if(ball.checkIfFinished()) { finishedBallList.add(ball);}
			if(ball.checkMissed()) { finishedBallList.add(ball);}
		}
		
		// Destroy any balls that are caught or missed
		for(Ball rmBall : finishedBallList) {
			activeBallList.remove(rmBall);
		}
	}
	
	public double calcDropTime(int y1, int y2) {
		return (y2 - y1) / Ball.BALL_SPEED;
	}
	
	public double getDropTime() {
		return dropTime;
	}
	
	public void animateBalls() {
		for(Ball ball : activeBallList) {
			ball.animate();
		}
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void renderBalls(Graphics g) {
		for(Ball ball : activeBallList) {
			ball.drawBall(g);
		}
	}
	
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	COLUMNS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void calcColumns() {
		
		screenPadding = (int)(screenW * 0.1); // padding is 10% of screen width on each side (20% total)
		effScreenW = screenW - 2 * screenPadding; // the screen width minus the padding
		//System.out.println("ScreenW: " + screenW + "screenPadding: " + screenPadding + " EffScreenWidth: " + effScreenW);
		
		ballCols = new int[NUM_COL+1]; // need n+1 lines to make n columns
		int colStep = effScreenW / NUM_COL; // amount of x to move per column
		
		for(int i = 0; i < NUM_COL+1; i++) {
			ballCols[i] = colStep * i + screenPadding;
			//System.out.println("Column " + i + " is located at x = " + ballCols[i]);
		}
	}
	
	public void displayColumns(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		for(int i = 0; i < NUM_COL+1; i++) {
			g.setColor(Color.RED);
			g2.drawLine(ballCols[i], 0, ballCols[i], screenH);
		}
	}
	
	public void toggleBallColumns() {
		if(showBallColumns) showBallColumns = false;
		else showBallColumns = true;
	}
	
	public int[] getColumns() {
		return ballCols;
	}
}
