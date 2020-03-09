package backend;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.InputStream;

public class ScoreKeeper {
	
	private SongMap song;
	
	private FontMetrics metrics;
	
	private int score = 0 ;
	private Color textColor = Color.red;
	private Font font;
	private String scoreText;
	private int[] position = new int[2];
	
	public ScoreKeeper(SongMap song) {
		this.song = song;
		font = song.fh.getFont("arcade-li").deriveFont(48f);
		
		position[0] = song.screenW - 100;
		position[1] = 10;
	}
	
	private static final Font SERIF_FONT = new Font("serif", Font.PLAIN, 24);
	
	public void displayScore(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		//GLOBAL APPEARANCE
		//font = new Font("SansSerif", Font.BOLD, 36);
		metrics = g2.getFontMetrics(font);
		g2.setFont(font);
		g2.setColor(textColor);
		scoreText = ""+score;
		
		g2.drawString(scoreText, position[0] - metrics.stringWidth(scoreText), position[1] + metrics.getHeight());
	}
	
	public void addPoint() {
		score++;
	}
}
