import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class MouseCursor {
	
	SongPong game;
	Paddle paddle;
	BufferedImage blank = null;
	Toolkit tk = Toolkit.getDefaultToolkit();
	JFrame frame;
	
	Cursor c;
	Cursor c1;
	
	public MouseCursor(SongPong game) {
		this.game = game;
		this.frame = game;
	
		try {
			blank = ImageIO.read(new File("src/Images/UI/blank.png"));
		} catch(Exception e) {
			
		}
		
		c1 = tk.createCustomCursor(blank, new Point(frame.getX(), frame.getY()), "img");
		c = Cursor.getDefaultCursor();
	}
	
	public void setCursorInvisible() {
		System.out.println("Invisible cursor.");
		frame.setCursor(c1);
	}
	
	public void setCursorDefault() {
		System.out.println("Default cursor.");
		frame.setCursor(c);
	}
}
