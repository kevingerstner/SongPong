package backend;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

public class GameStats {
	
	private GamePanel gp;
	
	private FontMetrics metrics;
	
	protected long deltaTime = 0;
	
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	
	private int statPosX = 0;
	private int statPosY = 0;
	
	private Color statBoxColor = new Color(48/255f,48/255f,48/255f,0.5f);
	private int statBoxH = 40;
	private int statBoxW = 350;
	private int statBoxPadding = 20;
	
	public GameStats(GamePanel gp) {
		this.gp = gp;
	}
	
	/**
	 * Draws a gray box in the top left corner to display statistics
	 * @param g
	 */
	public void drawStatsBox(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		//FONT
		Font font = new Font("SansSerif", Font.BOLD, 14);
		g.setFont(font);
		metrics = g2.getFontMetrics(font);
		
		//Assemble Text
		// Frame count & average FPS and UPS at top left
		String statString = "FPS: " + df.format(gp.averageFPS) + " | " + "UPS: " + df.format(gp.averageUPS);
		
		// Draw Box
		statBoxW = metrics.stringWidth(statString) + (statBoxPadding * 2); // add left and right padding for text
		statBoxH = metrics.getHeight() + (statBoxPadding);
		Rectangle2D statBox = new Rectangle2D.Float(statPosX,statPosY,statBoxW,statBoxH);
		g.setColor(statBoxColor);
		g2.fill(statBox);
		
		// Draw Text
		g.setColor(Color.white);
		g.drawString(statString, statPosX + statBoxPadding, statPosY + statBoxPadding);  // was (10,55)
		
		// Time used and boxes used at bottom left
		g.setColor(Color.white);
		g.drawString("Time Spent: " + df.format(gp.timeInScene) + " secs", 10, gp.pHeight - 15);
	}
	
}
