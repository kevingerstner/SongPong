package gamecomponents;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import backend.ImageHandler;
import backend.SongMap;
import backend.SongPong;

public class Paddle {
	
	private ImageHandler ih;
	
	private final int PADDLE_WIDTH = 175;
	private final int PADDLE_RADIUS = PADDLE_WIDTH / 2;
	private final int PADDLE_HEIGHT = 30;
	
	private int screenX;
	private int screenY;
	
	private int rightBound;
	private int leftBound = 0;
	
	private int[] position = new int[2];
	
	// IMAGE
	private BufferedImage paddleSprite;

	public Paddle(SongMap song) {
		ih = song.imgHandler;
		screenX = song.screenW;
		screenY = song.screenH;
		position[1] = screenY - (screenY / 10);// This is a good height for the paddle
		position[0] = (screenX / 2) - (PADDLE_RADIUS);// Spawn paddle in the middle of the screen
		
		rightBound = screenX - PADDLE_WIDTH;
		
		paddleSprite = ih.loadImage("src/images/paddle.png");
	}
	
	public void drawPaddle(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.red);
		ih.drawImage(g2, paddleSprite, position);
	}
	
	public void resetPaddle() {
		position[0] = (screenX / 2) - (PADDLE_RADIUS); // Spawn paddle in the middle of the screen
	}
	
	public void movePaddle(int x) {
		position[0] = x - (PADDLE_RADIUS); // Shift so cursor is in the middle
		if(position[0] < leftBound) {position[0] = leftBound;} // Clamp left
		if(position[0] > rightBound) {position[0] = rightBound;} // Clamp right
	}
	
	public boolean checkCatchBall(int x, int y, int size) {
		int xLeftBound = position[0] - (size / 2);
		int xRightBound = position[0] + (size / 2) + PADDLE_WIDTH;
		int yTopBound = position[1];
		int yBottomBound = position[1] + PADDLE_HEIGHT;
		
		boolean xin = (x > xLeftBound) && (x < xRightBound);
		boolean yin = (y > yTopBound) && (y < yBottomBound);
		return xin && yin;
	}
	
	public int getPaddleX() {
		return position[0];
	}
	
	public int getPaddleY() {
		return position[1];
	}
	
	public int getPaddleTopY() {
		return (position[1] - (PADDLE_HEIGHT / 2));
	}
}
