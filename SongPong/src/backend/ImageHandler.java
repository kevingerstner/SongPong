package backend;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHandler {
	
	private SongMap song;
	private double worldScale;
	
	private	AffineTransform at;
	public AffineTransformOp scaleOp;
	
	public ImageHandler(SongMap song) {
		this.song = song;
		worldScale = song.worldScale;
		
		at = AffineTransform.getScaleInstance(worldScale, worldScale);
		scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	}
		
	public BufferedImage loadImage(String name) {
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
	
	public void drawImage(Graphics2D g2, BufferedImage img, int[] pos) {
		g2.drawImage(img, scaleOp, (int)pos[0], (int)pos[1]);
	}
}
