package backend;
// GameFrame.java
// Roger Mailler, January 2009, adapted from
// 		Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;

import javax.swing.JFrame;

public abstract class GamePanel extends JFrame implements Runnable {
	private static final long serialVersionUID = 1863596360846514344L;
	private static final int NUM_BUFFERS = 2; // used for page flipping

	public GameStats gs;

	private static final int NO_DELAYS_PER_YIELD = 16;

	/*
	 * Number of frames with a delay of 0 ms before the animation thread yields
	 * to other running threads.
	 */
	private static int MAX_FRAME_SKIPS = 5; // was 2;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered

	public int pWidth; // panel dimensions
	public int pHeight;

	private Thread animator; // the thread that performs the animation
	public boolean running = false; // used to stop the animation thread
	public boolean isPaused = false;
	private boolean finishedOff = false;

	// used at game termination
	protected boolean gameOver = false;

	// used for full-screen exclusive mode
	private GraphicsDevice gd;
	private Graphics gScr;
	private BufferStrategy bufferStrategy;
	
/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	CONSTRUCTOR
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	public GamePanel(long period) {
		
		gs = new GameStats(this);
		
		gs.period = period;

		initFullScreen();

		readyForTermination();

		simpleInitialize();

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mousePress(e.getX(), e.getY());
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				mouseMove(e.getX(), e.getY());
			}
		});

		gameStart();

	} // end of GamePanel()

	
	private void initFullScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();

		setUndecorated(true); // no menu bar, borders, etc. or Swing components
		setIgnoreRepaint(true); // turn off all paint events since doing active
		// rendering
		setResizable(false);

		if (!gd.isFullScreenSupported()) {
			System.out.println("Full-screen exclusive mode not supported");
			System.exit(0);
		}
		gd.setFullScreenWindow(this); // switch on full-screen exclusive mode

		// we can now adjust the display modes, if we wish
		showCurrentMode();

		// setDisplayMode(800, 600, 8); // or try 8 bits
		// setDisplayMode(1280, 1024, 32);

		reportCapabilities();

		pWidth = getBounds().width;
		pHeight = getBounds().height;

		setBufferStrategy();
	}

	
	private void reportCapabilities() {
		System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		// Image Capabilities
		ImageCapabilities imageCaps = gc.getImageCapabilities();
		System.out.println("Image Caps. isAccelerated: "
				+ imageCaps.isAccelerated());
		System.out.println("Image Caps. isTrueVolatile: "
				+ imageCaps.isTrueVolatile());

		// Buffer Capabilities
		BufferCapabilities bufferCaps = gc.getBufferCapabilities();
		System.out.println("Buffer Caps. isPageFlipping: "
				+ bufferCaps.isPageFlipping());
		System.out.println("Buffer Caps. Flip Contents: "
				+ getFlipText(bufferCaps.getFlipContents()));
		System.out.println("Buffer Caps. Full-screen Required: "
				+ bufferCaps.isFullScreenRequired());
		System.out.println("Buffer Caps. MultiBuffers: "
				+ bufferCaps.isMultiBufferAvailable());
		System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
	} // end of reportCapabilities()

	//??????????
	private String getFlipText(BufferCapabilities.FlipContents flip) {
		if (flip == null)
			return "false";
		else if (flip == BufferCapabilities.FlipContents.UNDEFINED)
			return "Undefined";
		else if (flip == BufferCapabilities.FlipContents.BACKGROUND)
			return "Background";
		else if (flip == BufferCapabilities.FlipContents.PRIOR)
			return "Prior";
		else
			// if (flip == BufferCapabilities.FlipContents.COPIED)
			return "Copied";
	} // end of getFlipTest()

	//?????????
	private void setBufferStrategy() {
		
		createBufferStrategy(NUM_BUFFERS);
		bufferStrategy = getBufferStrategy(); // store for later
		
	} // end of setBufferStrategy()

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	QUIT
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	private void readyForTermination() {
		addKeyListener(new KeyAdapter() {
			// listen for esc, q, end, ctrl-c on the canvas to
			// allow a convenient exit from the full screen configuration
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_Q)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					running = false;
				}
			}
		});

		// for shutdown tasks
		// a shutdown may not only come from the program
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				running = false;
				finishOff();
			}
		});
	} // end of readyForTermination()



/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	GAME STATES
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	public void resumeGame() {
		isPaused = false;
		gs.resumeTime();
	}

	public void pauseGame() {
		isPaused = true;
		gs.pauseTime();
		pauseActions();
	}

	public void stopGame() {
		running = false;
	}

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	RUNNABLE
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	/**
	 * Starts the animation loop
	 */
	private void gameStart() {
		if (animator == null || !running) {
			System.out.println("Initialized game thread...");
			animator = new Thread(this); //puts this GameFrame in new Thread
			animator.start(); // calls GameFrame's run method
		}
		songStart();
	}
	
	public void run()
	{
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		gs.gameStartTime = System.nanoTime();
		gs.prevStatsTime = gs.gameStartTime;
		beforeTime = gs.gameStartTime;

		running = true;

		/* The frames of the animation are drawn inside the while loop. */
		while (running) {
			
			gameUpdate(); // update
			screenUpdate(); // render

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (gs.period - timeDiff) - overSleepTime;

			// If this process took shorter than a cycle, sleep
			// If this process took longer than a cycle, yield
			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1_000_000L); // nano -> ms
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > gs.period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= gs.period;
				gameUpdate(); // update state but don't render
				skips++;
			}
			gs.framesSkipped += skips;
			gs.storeStats();
		}
		finishOff();
		System.exit(0); // so window disappears
	} // end of run()

	/**
	 * Renders to the backbuffer
	 */
	private void gameRender(Graphics gScr) {
		// clear the background
		gScr.setColor(Color.white);
		gScr.fillRect(0, 0, pWidth, pHeight);

		// call Song Pong's render
		simpleRender(gScr);

		if (gameOver)
			gameOverMessage(gScr);
	}

	private void screenUpdate() {
		// use active rendering
		try {
			gScr = bufferStrategy.getDrawGraphics();
			gameRender(gScr);
			gScr.dispose();
			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			else
				System.out.println("Contents Lost");
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		}
	} // end of screenUpdate()

	/**
	 * Should be update the game state
	 */
	private void gameUpdate() {
		if (!isPaused && !gameOver)
			simpleUpdate();

	}

	

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	TERMINATION / CLEANUP METHODS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	/**
	 * finishOff():
	 * Tasks to do before terminating. Called at end of run() and via the
	 * shutdown hook in readyForTermination().
	 * 
	 * The call at the end of run() is not really necessary, but included for
	 * safety. The flag stops the code being called twice.
	 **/
	private void finishOff(){ 
		// System.out.println("finishOff");
		if (!finishedOff) {
			finishedOff = true;
			gs.printStats();
			restoreScreen();
			System.exit(0);
		}
	}

	/**
	 * restoreScreen():
	 * Switch off full screen mode. This also resets the display mode if it's
	 * been changed.
	 **/
	private void restoreScreen(){
		Window w = gd.getFullScreenWindow();
		if (w != null)
			w.dispose();
		gd.setFullScreenWindow(null);
	}

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	DISPLAY MODE METHODS
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/
	
	/**
	 * setDisplayMode(int width, int height, int bitDepth):
	 * Attempt to set the display mode to the given width, height, and bit depth
	 * @param width
	 * @param height
	 * @param bitDepth
	 */
	private void setDisplayMode(int width, int height, int bitDepth) {
		
		if (!gd.isDisplayChangeSupported()) {
			System.out.println("Display mode changing not supported");
			return;
		}

		if (!isDisplayModeAvailable(width, height, bitDepth)) {
			System.out.println("Display mode (" + width + "," + height + ","
					+ bitDepth + ") not available");
			return;
		}

		DisplayMode dm = new DisplayMode(width, height, bitDepth,
				DisplayMode.REFRESH_RATE_UNKNOWN); // any refresh rate
		try {
			gd.setDisplayMode(dm);
			System.out.println("Display mode set to: (" + width + "," + height
					+ "," + bitDepth + ")");
		} catch (IllegalArgumentException e) {
			System.out.println("Error setting Display mode (" + width + ","
					+ height + "," + bitDepth + ")");
		}

		try { // sleep to give time for the display to be changed
			Thread.sleep(1000); // 1 sec
		} catch (InterruptedException ex) {
		}
	}
	/**
	 * isDisplayModeAvailable(int width, int height, int bitDepth)
	 * Check that a displayMode with this width, height, bit depth is available.
	 * We don't care about the refresh rate, which is probably REFRESH_RATE_UNKNOWN anyway.
	 * @param width
	 * @param height
	 * @param bitDepth
	 **/
	private boolean isDisplayModeAvailable(int width, int height, int bitDepth) {
		
		DisplayMode[] modes = gd.getDisplayModes();
		showModes(modes);

		for (int i = 0; i < modes.length; i++) {
			if (width == modes[i].getWidth() && height == modes[i].getHeight()
					&& bitDepth == modes[i].getBitDepth())
				return true;
		}
		return false;
	}
	/**
	 * showModes(DisplayMode[] modes):
	 * Pretty print the display mode information in modes
	 * @param modes
	 **/
	private void showModes(DisplayMode[] modes){
		System.out.println("Modes");
		for (int i = 0; i < modes.length; i++) {
			System.out.print("(" + modes[i].getWidth() + ","
					+ modes[i].getHeight() + "," + modes[i].getBitDepth() + ","
					+ modes[i].getRefreshRate() + ")  ");
			if ((i + 1) % 4 == 0)
				System.out.println();
		}
		System.out.println();
	}
	/**
	 * 	showCurrentMode():
	 *  Print the display mode details for the graphics device
	 */
	private void showCurrentMode(){
		DisplayMode dm = gd.getDisplayMode();
		System.out.println("Current Display Mode: (" + dm.getWidth() + ","
				+ dm.getHeight() + "," + dm.getBitDepth() + ","
				+ dm.getRefreshRate() + ")  ");
	}

/* =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * 	ABSTRACT GAME METHODS TO IMPLEMENT
 * =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+*/

	/**
	 * Should implement game specific rendering
	 * 
	 * @param g
	 */
	protected abstract void simpleRender(Graphics g);

	/**
	 * Should display a game specific game over message
	 * 
	 * @param g
	 */
	protected abstract void gameOverMessage(Graphics g);

	protected abstract void simpleUpdate();
	
	protected abstract void songStart();
		
	protected abstract void pauseActions();

	/**
	 * This just gets called when a click occurs, no default behavior
	 */
	protected abstract void mousePress(int x, int y);

	/**
	 * This just gets called when a click occurs, no default behavior
	 */
	protected abstract void mouseMove(int x, int y);

	/**
	 * Should be overridden to initialize the game specific components
	 */
	protected abstract void simpleInitialize();

} // end of GamePanel class
