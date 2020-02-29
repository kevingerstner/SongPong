import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

public class MenuButton{
	
	private SongPong game;
	protected volatile boolean isOverButton = false;
	private Rectangle2D buttonArea;
	private String buttonText;
	
	private boolean boxEnabled = false;
	private int xPos;
	private int yPos;
	private int boxWidth;
	private int boxPadding = 5;
	private int boxHeight;
	
	public MenuButton(SongPong game, String buttonText, int x, int y) {
		this.game = game;
		this.xPos = x;
		this.yPos = y;
		boxWidth = 100;
		boxHeight = 30;
		this.buttonText = buttonText;
		buttonArea = new Rectangle2D.Float(xPos, yPos, boxWidth, boxHeight); //x, y, width, height
	}
	
	public void mouseAction() {
	
	}
	
	public void drawButton(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		g.setColor(Color.white);
	
		if (isOverButton)
			g.setColor(Color.DARK_GRAY);
		
		//BOX
		if(boxEnabled) {
			Stroke stroke1 = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0,
					new float[] {3,1}, 0);
			g2.setStroke(stroke1);
			g2.draw(buttonArea);
		}
		
		//TEXT
		g2.drawString(buttonText, xPos+boxPadding, yPos+(boxHeight/2)+boxPadding);
	}

	protected void isHovered(int x, int y) {
		if(game.running) {
			isOverButton = buttonArea.contains(x, y) ? true : false;
		}
	}
}
