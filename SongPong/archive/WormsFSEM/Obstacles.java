// Obstacles.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
//Updated to new graphics standards by Roger Mailler, January 2009
/* A collection of boxes which the worm cannot move over
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Obstacles {
	private static final int BOX_LENGTH = 12;

	private ArrayList<Rectangle2D> boxes; // arraylist of Rectangle objects
	private WormChase wormChase;

	public Obstacles(WormChase wc) {
		boxes = new ArrayList<Rectangle2D>();
		wormChase = wc;
	}

	synchronized public void add(int x, int y) {
		boxes.add(new Rectangle2D.Double(x, y, BOX_LENGTH, BOX_LENGTH));
		wormChase.setBoxNumber(boxes.size()); // report new number of boxes
	}

	synchronized public boolean hits(Point2D p, int size) {
		Rectangle2D r = new Rectangle2D.Double(p.getX(), p.getY(), size, size);
		Rectangle2D box;
		for (int i = 0; i < boxes.size(); i++) {
			box = boxes.get(i);
			if (box.intersects(r))
				return true;
		}
		return false;
	} // end of intersects()

	synchronized public void draw(Graphics g)
	// draw a series of blue boxes
	{
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.blue);
		for (int i = 0; i < boxes.size(); i++) {
			g2.fill(boxes.get(i));
		}
	} // end of draw()

	synchronized public int getNumObstacles() {
		return boxes.size();
	}

} // end of Obstacles class
