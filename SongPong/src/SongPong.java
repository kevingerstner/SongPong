// WormChase.java
// Roger Mailler, January 2009, adapted from
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A worm moves around the screen and the user must
 click (press) on its head to complete the game.

 If the user misses the worm's head then a blue box
 will be added to the screen (if the worm's body was
 not clicked upon).

 A worm cannot move over a box, so the added obstacles
 *may* make it easier to catch the worm.

 A worm starts at 0 length and increases to a maximum
 length which it keeps from then on.

 A score is displayed on screen at the end, calculated
 from the number of boxes used and the time taken. Less
 boxes and less time mean a higher score.

 -------------

 Uses full-screen exclusive mode, active rendering,
 and double buffering/page flipping.

 On-screen pause and quit buttons.

 Using Java 3D's timer: J3DTimer.getValue()
 *  nanosecs rather than millisecs for the period

 Average FPS / UPS
 20			50			80			100
 Win 98:         20/20       50/50       81/83       84/100
 Win 2000:       20/20       50/50       60/83       60/100
 Win XP (1):     20/20       50/50       74/83       76/100
 Win XP (2):     20/20       50/50       83/83       85/100

 Located in /WormFSEM

 Updated: 12th Feb 2004
 * added extra sleep to the end of our setDisplayMode();

 * moved createBufferStrategy() call to a separate
 setBufferStrategy() method, with added extras
 ----
 */

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class SongPong extends GamePanel{
	
	private static final long serialVersionUID = -2450477630768116721L;

	private static int DEFAULT_FPS = 100;

	private int score = 0;
	private Font font;
	private FontMetrics metrics;
	
    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
    
    // UI
    private Menu myMenu;
    protected MouseCursor customCursor;
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
		paradise = new ParadiseColdplay(this, "src/Notemap/nm_test.txt", 0);
		activeSong = paradise;
						
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
		if(myMenu.isEnabled) {
			myMenu.displayMenu(g);
		}

	}

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	UPDATE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	@Override
	protected void simpleUpdate() {
		paradise.updateSongStuff();
	}
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	USER INPUT
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	@Override
	protected void mousePress(int x, int y) {
		if(myMenu.isEnabled) {
			myMenu.checkOnMousePress(x, y);
		}
		paradise.handleMousePress(x, y);
	}

	@Override
	protected void mouseMove(int x, int y) {
		if (running) { // stops problems with a rapid move after pressing 'quit'
			if(myMenu.isEnabled) {
				myMenu.checkOnMouseHover(x, y);
			}
			paradise.handleMouseMove(x, y);
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
					paradise.showBallColumns();
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

} // end of WormChase class

