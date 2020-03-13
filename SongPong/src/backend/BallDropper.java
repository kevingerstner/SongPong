package backend;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import gamecomponents.BallBouncing;
import gamecomponents.BallSimple;
import gamecomponents.Paddle;

public class BallDropper implements Runnable{

	// REFERENCES
	private SongMap song;
	private Paddle paddle;
	private DecimalFormat df = new DecimalFormat("0.###"); // 3 dp
	
	// CONSTANTS
	final static int NUM_COL = 16;
	protected final static double GRAVITY_C = 300; // the ball accerates 150 pixels per second
	
	// SCREEN INFO
	protected int screenW;
	protected int screenH;
	private int screenPadding;
	private int effScreenW;
	private int[] ballCols;
	
	// DEBUG
	protected boolean showBallColumns = true;
	protected boolean showBallNum = false;
	
	// BALLS
	private int ballCounter = 0;
	private int dropIndex = 0;
	private int ballSize;
	private ArrayList<Ball> ballList = new ArrayList<Ball>(); // list of balls to drop
	private ArrayList<Ball> activeBallList = new ArrayList<Ball>(); //stores all balls currently spawned
	private ArrayList<Ball> finishedBallList = new ArrayList<Ball>(); //stores all missed balls

	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public BallDropper(SongMap g) {
		song = g;
		paddle = song.paddle;
		
		screenW = song.screenW;
		screenH = song.screenH;
		
		calcColumns();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RUN
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	SPAWN
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public BallBouncing createBounceBall(ArrayList<Double> hitTimes, int column) {
		// ATTRIBUTES
		int startPosX = ballCols[column] - (ballSize / 2);
		int[] pos = {startPosX, song.ballSpawnY};
		
		BallBouncing bounceBall = new BallBouncing(song, hitTimes, pos, ballCounter+1);
		
		ballList.add(bounceBall);
		ballCounter++;
		return bounceBall;
	}
	
	public BallSimple createSimpleBall(ArrayList<Double> hitTimes, int column) {
		// ATTRIBUTES
		int startPosX = ballCols[column] - (ballSize / 2);
		int[] pos = {startPosX, song.ballSpawnY};
		
		BallSimple simpBall = new BallSimple(song, hitTimes, pos, ballCounter+1);
		ballList.add(simpBall);
		ballCounter++;
		return simpBall;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	GAME CONTROLS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void stopBalls() {
		for(Ball ball : activeBallList) {
			ball.stopMoving();
		}
	}
	
	public void rewindBalls() {
		double currentTime = song.getTime();
		System.out.println("yahhhh it's rewind time " + df.format(currentTime));
		
		// look through all balls that have already been dropped or skipped
		for(int i = dropIndex-1; i > 0; i--) {
			Ball ball = ballList.get(i);
			
			// If ball is already spawned
			if(ball.getSpawnTime() < currentTime) {
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
		double currentTime = song.getTime();
		//System.out.println("CHECK DROP @ t = " + currentTime);
		
		if(dropIndex < ballList.size()) {
			Ball b = ballList.get(dropIndex);
			
			if(b.getSpawnTime() <= song.getTime()) {
				
				// This second condition prevents a ball from spawning if its time has been skipped
				if(checkRemove(b, currentTime)) {}
				else {
					System.out.println("BALL " + b.ballNum + "/ SPAWN @ t=" + df.format(b.getSpawnTime()) + "s / TIME: " + currentTime);
					activeBallList.add(b); //add to the list of spawned balls
	
					b.falling = true;
					dropIndex++;
				}
			}
		}
	}
	
	private boolean checkRemove(Ball b, double currentTime) {
		if(b.getSpawnTime() + 0.25 < song.getTime()) {
			System.out.println("Removed ball "+ b.ballNum + " with drop time " + df.format(b.getSpawnTime()));
			finishedBallList.add(b);
			dropIndex++;
			return true;
		}
		else
			return false;
	}
	
	public void clearFinishedBalls() {
		finishedBallList.clear();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void updateBalls() {
		for(Ball ball : activeBallList) {
			ball.moveBall();
			
			if(ball.checkIfFinished()) { 
				finishedBallList.add(ball);
				//debugCatchTime(ball);
			}
			if(ball.checkMissed()) { finishedBallList.add(ball);}
		}
		
		// Destroy any balls that are caught or missed
		for(Ball rmBall : finishedBallList) {
			activeBallList.remove(rmBall);
		}
	}
	
	public void debugCatchTime(Ball ball) {
		double actualCatchTime = ball.getCatchTime();
		//double shouldBeCaught = ball.getSpawnTime() + dropTime;
		System.out.println("--------------------------------");
		//System.out.println("Expected Catch time: " + df.format(shouldBeCaught));
		System.out.println("Actual Catch time: " + df.format(actualCatchTime));
		//System.out.println("CATCH DT: " + df.format(actualCatchTime - (shouldBeCaught)));
		System.out.println("--------------------------------");
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
	
	public void toggleShowBallNum() {showBallNum = showBallNum ? false : true;}
	
	public void toggleBallColumns() {
		if(showBallColumns) showBallColumns = false;
		else showBallColumns = true;
	}
	
	public int[] getColumns() {
		return ballCols;
	}
}
