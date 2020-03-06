package gamecomponents;
import java.awt.Color;
import java.util.ArrayList;

import backend.Ball;
import backend.SongMap;

public class BallBouncing extends Ball{
	
	private double bounceTime = 2;
	private double bounceAcc = (BALL_SPEED / (bounceTime/2));
	
	public BallBouncing(SongMap song, ArrayList<Double> hitTimes, int[] pos, Color c, int num) {
		super(song, hitTimes, pos, num);
	}

	@Override
	protected void handleCollide() {
		myColor = Color.blue;
		acceleration[1] = bounceAcc;
	}

	@Override
	protected void handleFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void animate() {
		// TODO Auto-generated method stub
		
	}
	
}
