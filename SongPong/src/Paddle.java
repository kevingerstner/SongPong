import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Paddle {
	
	private final int PADDLE_WIDTH = 175;
	private final int PADDLE_RADIUS = PADDLE_WIDTH / 2;
	private final int PADDLE_HEIGHT = 30;
	
	private int screenX;
	private int screenY;
	
	private int rightBound;
	private int leftBound = 0;
	
	private int paddleY;
	private int paddleX;

	public Paddle(SongMap song) {
		screenX = song.screenW;
		screenY = song.screenH;
		paddleY = screenY - (screenY / 10); // This is a good height for the paddle
		paddleX = (screenX / 2) - (PADDLE_RADIUS); // Spawn paddle in the middle of the screen
		
		rightBound = screenX - PADDLE_WIDTH;
	}
	
	public void drawPaddle(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.red);
		g2.fillRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
	}
	
	public void resetPaddle() {
		paddleX = (screenX / 2) - (PADDLE_RADIUS); // Spawn paddle in the middle of the screen
	}
	
	public void movePaddle(int x) {
		paddleX = x - (PADDLE_RADIUS); // Shift so cursor is in the middle
		if(paddleX < leftBound) {paddleX = leftBound;} // Clamp left
		if(paddleX > rightBound) {paddleX = rightBound;} // Clamp right
	}
	
	public boolean checkCatchBall(int x, int y, int size) {
		int xLeftBound = paddleX - size;
		int xRightBound = paddleX + PADDLE_WIDTH;
		int yTopBound = paddleY;
		int yBottomBound = paddleY + PADDLE_HEIGHT;
		
		boolean xin = (x > xLeftBound) && (x < xRightBound);
		boolean yin = (y > yTopBound) && (y < yBottomBound);
		return xin && yin;
	}
	
	public int getPaddleX() {
		return paddleX;
	}
	
	public int getPaddleY() {
		return paddleY;
	}
	
	public int getPaddleTopY() {
		
		return (paddleY - (PADDLE_HEIGHT / 2));
	}
}
