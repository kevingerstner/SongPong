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
	
	private BufferedImage[] explodeFrames;
	private int activeFrame = 0;

	public BallSimple(SongMap song, ArrayList<Double> spawnTimes, int[] pos, int num) {
		super(song, spawnTimes, pos, num);
		ballSprite = ih.loadImage("src/images/Simple Ball/simple_ball.png");
		explodeFrames = ih.loadStripImageArray("src/images/Simple Ball/simple_ball_strip.png", 6);
		setSize(ballSprite.getWidth());
	}
	
	
	protected void handleCollide() {
		velocity[1] = -LAST_BOUNCE_SPEED;
		
	}
		
	synchronized public void drawBall(Graphics g) {
		// MOTION
		super.drawBall(g);
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
	
	// Used for Test Ball
	public BallSimple(SongMap song) {
		super(song);
		ballSprite = ih.loadImage("src/images/ball_red.png");
		setSize(ballSprite.getWidth());
	}
	
}
