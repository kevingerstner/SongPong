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

import scenes.MainMenu;
import scenes.ParadiseColdplay;
import scenes.SchoolBillWurtz;
import scenes.Test;
import ui.Menu;

public class SongPong extends GamePanel{
	
	private static final long serialVersionUID = -2450477630768116721L;

	private static int DEFAULT_FPS = 100;

	private int score = 0;
	private Font font;
	private FontMetrics metrics;
	
	public ImageHandler ih;
	public FontHandler fh;
	public double worldScale;
			    
    // UI
    private Menu myMenu;
    public MouseCursor customCursor;
	private Robot mouseMover;
    
    // Scenes
    private Scene activeScene;
    private MainMenu mainMenu;
    private Test testSong;
    private ParadiseColdplay paradise;
    private SchoolBillWurtz school;
    
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
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	INITIALIZE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/	

	@Override
	protected void simpleInitialize() {
		
		worldScale = 4.0;
		
		this.requestFocus();
				
		// keyboard input
		keyboardInput();
		
		try {
			mouseMover = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		
		ih = new ImageHandler(this);
		fh = new FontHandler();
		
		// UI components
		myMenu = new Menu(this);
		customCursor = new MouseCursor(this);

		// set up message font
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);
		
		// scenes
		mainMenu = new MainMenu(this);
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	STATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void startScene(String sceneName) {
		switch(sceneName) {
		case "mainmenu":
			activeScene = mainMenu;
			break;
		case "paradise":
			System.out.println("START SONG: PARADISE");
			paradise = new ParadiseColdplay(this, "src/Notemap/nm_paradise_coldplay.txt", "src/Music/ColdplayParadise.wav");
			activeScene = paradise;
			break;
		case "school":
			System.out.println("START SONG: SCHOOL");
			school = new SchoolBillWurtz(this, "src/notemap/nm_school_bill_wurtz.txt", "src/Music/School_Bill_Wurtz2.wav");
			activeScene = school;
			break;
		case "test":
			System.out.println("START SONG: TEST");
			testSong = new Test(this, "src/notemap/nm_test.txt", null);
			activeScene = testSong;
		default: 
			System.err.println("That scene does not exist.");
		}
		activeScene.initScene();
	}
	
	@Override
	public void pauseActions() {
		activeScene.handleGamePause();
		customCursor.setCursorDefault();
	}
 
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RENDER
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	@Override
	protected void simpleRender(Graphics g) {
	    // Render Scene
	    activeScene.renderScene(g);
		
	    // Background Game Stuff
	    gs.drawStatsBox(g);
	    
		// Render Menu
		if(myMenu.isMenuEnabled()) {
			myMenu.displayMenu(g);
		}

	}

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	@Override
	protected void simpleUpdate() {
		activeScene.updateScene();
	}

	@Override
	protected void simpleAnimate() {
		activeScene.animateScene();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	USER INPUT
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	@Override
	protected void mousePress(int x, int y) {
		double timeClicked = getTimeElapsed();
		if(myMenu.isMenuEnabled()) {
			myMenu.checkOnMousePress(x, y);
		} else {
			activeScene.handleMousePress(x, y, timeClicked);
		}
	}

	@Override
	protected void mouseMove(int x, int y) {
		if (running) { // stops problems with a rapid move after pressing 'quit'
			if(myMenu.isMenuEnabled()) {
				myMenu.checkOnMouseHover(x, y);
			}
			activeScene.handleMouseMove(x, y);
		}
	}
	
	private void keyboardInput() {
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				activeScene.handleKeyboardInput(keyCode);
				if (keyCode == KeyEvent.VK_ESCAPE)
					myMenu.toggleMenu();
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
		if(activeScene instanceof SongMap)
			return (SongMap) activeScene;
		else
			return null;
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

}

