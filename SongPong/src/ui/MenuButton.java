package ui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import backend.SongPong;

public abstract class MenuButton{
	
	public volatile boolean isOverButton = false;
	protected Rectangle2D buttonArea;
	private String buttonText;
	
	private Color hoverColor;
	private Color color;
	private Font font;
	private FontMetrics metrics;
	
	private boolean boxEnabled = false;
	private int xPos;
	private int yPos;
	private int boxWidth;
	private int padding = 0;
	private int boxHeight;
	
	private String align = "left";
	
	public MenuButton(String buttonText, int x, int y, Color color, Color hoverColor, Font font, String align) {
		this.xPos = x;
		this.yPos = y;
		this.font = font;
		this.color = color;
		this.hoverColor = hoverColor;
		this.buttonText = buttonText;
		this.align = align;
		buttonArea = new Rectangle2D.Float(xPos - (boxWidth / 2), yPos, boxWidth + padding, boxHeight + padding); //x, y, width, height

	}
		
	public void drawButton(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		metrics = g2.getFontMetrics(font);
		
		g.setColor(color);
		
		boxWidth = metrics.stringWidth(buttonText);
		boxHeight = metrics.getHeight();
		
		if (isOverButton)
			g.setColor(hoverColor);
		
		if(align.equals("left")) {
			buttonArea = new Rectangle2D.Float(xPos, yPos, boxWidth + padding, boxHeight + padding); //x, y, width, height
			g2.drawString(buttonText, xPos+padding, yPos + boxHeight);
		} else if(align.equals("center")) {
			buttonArea = new Rectangle2D.Float(xPos - (boxWidth / 2), yPos, boxWidth + padding, boxHeight + padding); //x, y, width, height
			g2.drawString(buttonText, xPos+padding-(boxWidth / 2), yPos + boxHeight);
		}
		
		//BOX
		if(boxEnabled) {
			Stroke stroke1 = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0,
					new float[] {3,1}, 0);
			g2.setStroke(stroke1);
			g2.draw(buttonArea);
		}
		
	}

	public void isHovered(int x, int y) {
		isOverButton = buttonArea.contains(x, y) ? true : false;
	}
	

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	ABSTRACT --> All buttons must implement action
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public abstract void mouseAction();
}
