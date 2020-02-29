import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class BallSimple extends Ball{
	
	// ATTRIBUTES
	Color ballColor;
	
	private double bounceTime = 1;
	private double bounceSpeedMod = 1;
	private double bounceAcc = (BALL_SPEED / bounceTime);


	public BallSimple(SongMap song, ArrayList<Double> spawnTimes, int[] pos, Color c, int num) {
		
		super(song, spawnTimes, pos, num);
		
		// ATTRIBUTES
		ballColor = c;
	}
	
	protected void handleCollide() {
		acceleration[1] = bounceAcc;
		velocity[1] *= bounceSpeedMod;
	}
		
	synchronized public void drawBall(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		// ATTRIBUTES
		g2.setColor(ballColor);
		
		// MOTION
		super.drawBall(g);
	}

	@Override
	protected void handleBounce() {
		//System.out.println("BOUNCING");
		if(Math.abs(velocity[1]) <= 20) {isBouncing = false;}
		
	}
	
	
	
}
