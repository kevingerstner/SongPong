package backend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHandler {
	
	private SongPong game;
	private double worldScale;
	
	private	AffineTransform at;
	public static AffineTransformOp scaleOp;
	
	public ImageHandler(SongPong game) {
		this.game = game;
		worldScale = game.worldScale;
		
		at = AffineTransform.getScaleInstance(worldScale, worldScale);
		scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	}
		
	public static BufferedImage loadImage(String name) {
		try {
			BufferedImage img = ImageIO.read(new File(name));
			int transparency = img.getColorModel().getTransparency();
			BufferedImage space = new BufferedImage(img.getWidth(), img.getHeight(), transparency);
			Graphics2D g2d = space.createGraphics();
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
			return space;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage[] loadStripImageArray(String fnm, int number) {
		BufferedImage stripIm;
		if ((stripIm = loadImage(fnm)) == null)
			return null;
		
		int imWidth = (int) stripIm.getWidth() / number;
		int height = stripIm.getHeight();
		
		int transparency = stripIm.getColorModel().getTransparency();
		BufferedImage[] strip = new BufferedImage[number];
		Graphics2D stripGC;
		// each BufferedImage from the strip file is stored in strip[]
		for (int i=0; i < number; i++) {
			strip[i] = new BufferedImage(imWidth, height, transparency);
			// create a graphics context
			stripGC = strip[i].createGraphics();
			// copy image
			stripGC.drawImage(stripIm,0,0, imWidth, height, i*imWidth,0,(i*imWidth)+imWidth, height, null);
			stripGC.dispose();
		}
		return strip;
	}
	
	public void drawImageScaled(Graphics2D g2, BufferedImage img, double[] pos) {
		g2.drawImage(img, scaleOp, (int)pos[0], (int)pos[1]);
	}
	
	public void drawImageScaled(Graphics2D g2, BufferedImage img, int[] pos) {
		g2.drawImage(img, scaleOp, (int)pos[0], (int)pos[1]);
	}
}
