package backend;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import music.ParadiseColdplay;
import ui.Menu;

public class SongPong extends GamePanel{
	
	private static final long serialVersionUID = -2450477630768116721L;

	private static int DEFAULT_FPS = 100;

	private int score = 0;
	private Font font;
	private FontMetrics metrics;
	
    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
    
    // UI
    private Menu myMenu;
    public MouseCursor customCursor;
	private Robot mouseMover;
    
    // GAME
    private SongMap activeSong;
    private ParadiseColdplay paradise;
    
    // STATE
    protected boolean gamePaused = false;
    
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	public SongPong(long period) {
		super(period);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	MAIN
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);

		long period = (long) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");
		new SongPong(period * 1000000L); // ms --> nanosecs
	} // end of main()
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	INITIALIZE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/	

	@Override
	protected void simpleInitialize() {
		
		this.requestFocus();
		
		// keyboard input
		checkForMenu();
		
		try {
			mouseMover = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		
		// UI components
		myMenu = new Menu(this);
		customCursor = new MouseCursor(this);

		// set up message font
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);
		
		// song
		paradise = new ParadiseColdplay(this, "src/Notemap/nm_paradise_coldplay.txt", 4);
		activeSong = paradise;
						
	}
	
	@Override
	protected void songStart() {
		activeSong.startSong();
	}
 
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	@Override
	protected void simpleRender(Graphics g) {
	    // Background Game Stuff
	    gs.drawStatsBox(g);
	    
	    // Music Components
	    paradise.renderSongStuff(g);
		
		// UI Components
		if(myMenu.isMenuEnabled()) {
			myMenu.displayMenu(g);
		}

	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	IMAGE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public BufferedImage loadImage(String name) {
		try {
			BufferedImage img = ImageIO.read(new File(name));
			int transparency = img.getColorModel().getTransparency();
			BufferedImage space = gc.createCompatibleImage(img.getWidth(), img.getHeight(), transparency);
			Graphics2D g2d = space.createGraphics();
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
			return space;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void drawImage(Graphics2D g2, BufferedImage img) {
		
	}
	
	public BufferedImage scaleImage(BufferedImage before) {
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(5.0, 5.0);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		after = scaleOp.filter(before, after);
		return after;
	}

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	@Override
	protected void simpleUpdate() {
		activeSong.updateSongStuff();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	USER INPUT
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	@Override
	protected void mousePress(int x, int y) {
		if(myMenu.isMenuEnabled()) {
			myMenu.checkOnMousePress(x, y);
		}
		activeSong.handleMousePress(x, y);
	}

	@Override
	protected void mouseMove(int x, int y) {
		if (running) { // stops problems with a rapid move after pressing 'quit'
			if(myMenu.isMenuEnabled()) {
				myMenu.checkOnMouseHover(x, y);
			}
			activeSong.handleMouseMove(x, y);
		}
	}
	
	private void checkForMenu() {
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				activeSong.handleKeyboardInput(keyCode);
				if (keyCode == KeyEvent.VK_ESCAPE)
					myMenu.toggleMenu();
				else if (keyCode == KeyEvent.VK_0)
					activeSong.showBallColumns();
			}
		});
	}
	
	protected void moveMouseToPos(int x, int y) {
		mouseMover.mouseMove(x, y);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	GETTERS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public SongMap getActiveSong() {
		return activeSong;
	}
	
	@Override
	protected void gameOverMessage(Graphics g)
	// center the game-over message in the panel
	{
		String msg = "Game Over. Your Score: " + score;
		int x = (pWidth - metrics.stringWidth(msg)) / 2;
		int y = (pHeight - metrics.getHeight()) / 2;
		g.setColor(Color.red);
		g.setFont(font);
		g.drawString(msg, x, y);
	}

	@Override
	public void pauseActions() {
		customCursor.setCursorDefault();
		activeSong.tuneSpinner.pauseMusic();
		activeSong.bd.stopBalls();
	}

} // end of WormChase class

