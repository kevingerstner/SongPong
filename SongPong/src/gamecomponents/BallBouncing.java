package gamecomponents;
import java.awt.Color;
import java.util.ArrayList;

import backend.Ball;
import backend.SongMap;

public class BallBouncing extends Ball{
	
	private double bounceHitTime;
	private double bounceAcc = (BALL_SPEED / (bounceHitTime/2));
	
	public BallBouncing(SongMap song, ArrayList<Double> hitTimes, int[] pos, Color c, int num) {
		super(song, hitTimes, pos, num);
		//bounceHitTime = hitTimes.get(0);
	}

	@Override
	protected void handleCollide() {
		acceleration[1] = bounceAcc;
	}

	@Override
	protected void handleBounce() {
		// TODO Auto-generated method stub
		
	}
	
}
