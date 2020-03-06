package gamecomponents;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import backend.Ball;
import backend.SongMap;

public class BallSimple extends Ball{
	
	// ATTRIBUTES
	
	private double bounceTime = 1;
	private double bounceSpeedMod = 1;
	private double bounceAcc = (BALL_SPEED / bounceTime);
	
	private BufferedImage[] explodeFrames;
	private int activeFrame = 0;
	
	// Used for Test Ball
	public BallSimple(SongMap song) {
		super(song);
	}

	public BallSimple(SongMap song, ArrayList<Double> spawnTimes, int[] pos, Color c, int num) {
		super(song, spawnTimes, pos, num);
		explodeFrames = ih.loadStripImageArray("src/images/ball_red_strip.png", 6);
	}
	
	protected void handleCollide() {
		acceleration[1] = bounceAcc;
		velocity[1] *= bounceSpeedMod;
		myColor = Color.green;
		doneBouncing = true;
	}
		
	synchronized public void drawBall(Graphics g) {
		// MOTION
		super.drawBall(g);
	}

	@Override
	protected void handleFinish() {
		
	}
	
	protected void animate() {
		if(doneBouncing) {
			ballSprite = explodeFrames[activeFrame];
			if(activeFrame < explodeFrames.length-1) {
				activeFrame++;
			} else {
				readyToDelete = true;
				activeFrame = 0;
			}
		}
	}
	
}
