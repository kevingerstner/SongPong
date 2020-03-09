package gamecomponents;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import backend.Ball;
import backend.SongMap;

public class BallBouncing extends Ball{
		
	private int bounceNum = 0;
	private BufferedImage[] explodeFrames;
	private int activeFrame = 0;
	
	public BallBouncing(SongMap song, ArrayList<Double> hitTimes, int[] pos, int num) {
		super(song, hitTimes, pos, num);
		ballSprite = ih.loadImage("src/images/Bounce Ball/bounce_ball.png");
		explodeFrames = ih.loadStripImageArray("src/images/Bounce Ball/bounce_ball_strip.png", 6);
		setSize(ballSprite.getWidth());
	}

	@Override
	protected void handleCollide() {
		if(++bounceNum < spawnTimes.size()) {
			double hitTime = spawnTimes.get(bounceNum) + dropTime;
			double currTime = song.getTime();
			double deltaT = hitTime - currTime;
			deltaT = deltaT / 2; // we calculate the time it takes for the ball to get the peak.
			
			double initVelocity = -acceleration[1] * deltaT;
			velocity[1] = initVelocity;
			
			//debugBounce(deltaT, initVelocity);
		}
		else {
			velocity[1] = -LAST_BOUNCE_SPEED;
		}
	}

	@Override
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
	
	private void debugBounce(double deltaT, double initVelocity) {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("DT: " + deltaT);
		System.out.println("V0: " + initVelocity);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

	}
	
}
