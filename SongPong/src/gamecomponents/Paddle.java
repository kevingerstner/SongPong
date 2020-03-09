package gamecomponents;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;

import backend.ImageHandler;
import backend.SongMap;
import backend.SongPong;

public class Paddle {
	
	private ImageHandler ih;
	
	private int width;
	private int radius;
	private int height;
	
	private int screenX;
	private int screenY;
	private double worldScale;
	
	private int rightBound;
	private int leftBound = 0;
	
	private int[] position = new int[2];
	
	public boolean invertPaddle = false;
	
	// IMAGE
	private BufferedImage paddleSprite;

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public Paddle(SongMap song) {
		ih = song.imgHandler;
		worldScale = song.worldScale;
		
		screenX = song.screenW;
		screenY = song.screenH;
		
		paddleSprite = ih.loadImage("src/images/paddle.png");
		height = (int) (paddleSprite.getHeight() * worldScale);
		width = (int) (paddleSprite.getWidth() * worldScale);
		radius = width / 2;
		
		position[1] = screenY - (screenY / 10);// This is a good height for the paddle
		position[0] = (screenX / 2) - (radius);// Spawn paddle in the middle of the screen
		rightBound = screenX - width;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void drawPaddle(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		if(invertPaddle) {
			short[] data = new short[256];
			for (short i = 0; i < 256; i++) {
			    data[i] = (short) (255 - i);
			}
	
			BufferedImage dstImage = null;
			LookupTable lookupTable = new ShortLookupTable(0, data);
			LookupOp op = new LookupOp(lookupTable, null);
			dstImage = op.filter(paddleSprite, null);
			
			ih.drawImage(g2, dstImage, position);
		}
		else {
			ih.drawImage(g2, paddleSprite, position);
		}
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void resetPaddle() {
		position[0] = (screenX / 2) - (radius); // Spawn paddle in the middle of the screen
	}
	
	public void movePaddle(int x) {
		position[0] = x - (radius); // Shift so cursor is in the middle
		if(position[0] < leftBound) {position[0] = leftBound;} // Clamp left
		if(position[0] > rightBound) {position[0] = rightBound;} // Clamp right
	}
	
	public boolean checkCatchBall(int x, int y, int size) {
		int xLeftBound = position[0] - (size / 2);
		int xRightBound = position[0] + (size / 2) + width;
		int yTopBound = position[1];
		int yBottomBound = position[1] + height;
		
		boolean xin = (x > xLeftBound) && (x < xRightBound);
		boolean yin = (y > yTopBound) && (y < yBottomBound);
		return xin && yin;
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	GETTERS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public int getPaddleX() {
		return position[0];
	}
	
	public int getPaddleY() {
		return position[1];
	}
	
	public int getPaddleTopY() {
		return (position[1] - (height / 2));
	}
}
