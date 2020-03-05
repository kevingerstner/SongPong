import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

public class GameStats {
	
	private GamePanel gp;
	
	private FontMetrics metrics;

	// used for gathering statistics
	protected long statsInterval = 0L; // in ns
	protected long prevStatsTime;
	protected long totalElapsedTime = 0L;
	protected long totalTime = 0L;
	protected long gameStartTime;
	
	protected double timeSpentInGame = 0; // in seconds
	protected long pauseTime = 0;
	protected long skipTime;
	
	protected long deltaTime = 0;
	
	protected long period; // period between drawing in _nanosecs_
	
	private static long MAX_STATS_INTERVAL = 1000000000L;
	// record stats every 1 second (roughly)
	
	private static int NUM_FPS = 10;
	// number of FPS values stored to get an average

	protected long frameCount = 0;
	protected double fpsStore[];
	protected long statsCount = 0;
	protected double averageFPS = 0.0;

	protected long framesSkipped = 0L;
	protected long totalFramesSkipped = 0L;
	protected double upsStore[];
	protected double averageUPS = 0.0;
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	
	private int statPosX = 0;
	private int statPosY = 0;
	
	private Color statBoxColor = new Color(48/255f,48/255f,48/255f,0.5f);
	private int statBoxH = 40;
	private int statBoxW = 350;
	private int statBoxPadding = 20;
	
	public GameStats(GamePanel gp) {
		this.gp = gp;
		
		// Initialize timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}
	}
	
	/**
	 * Draws a gray box in the top left corner to display statistics
	 * @param g
	 */
	public void drawStatsBox(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		//FONT
		Font font = new Font("SansSerif", Font.BOLD, 14);
		g.setFont(font);
		metrics = g2.getFontMetrics(font);
		
		//Assemble Text
		// Frame count & average FPS and UPS at top left
		String statString = "FPS: " + df.format(averageFPS) + " | " + "UPS: " + df.format(averageUPS);
		
		// Draw Box
		statBoxW = metrics.stringWidth(statString) + (statBoxPadding * 2); // add left and right padding for text
		statBoxH = metrics.getHeight() + (statBoxPadding);
		Rectangle2D statBox = new Rectangle2D.Float(statPosX,statPosY,statBoxW,statBoxH);
		g.setColor(statBoxColor);
		g2.fill(statBox);
		
		// Draw Text
		g.setColor(Color.white);
		g.drawString(statString, statPosX + statBoxPadding, statPosY + statBoxPadding);  // was (10,55)
		
		// Time used and boxes used at bottom left
		g.setColor(Color.black);
		g.drawString("Time Spent: " + df.format(timeSpentInGame) + " secs", 10, gp.pHeight - 15);
	}
	
	/**
	 * storeStats():
	 * 
	 * - the summed periods for all the iterations in this
	 * interval (period is the amount of time a single frame iteration should
	 * take), the actual elapsed time in this interval, the error between these
	 * two numbers;
	 * 
	 * - the total frame count, which is the total number of calls to run();
	 * 
	 * - the frames skipped in this interval, the total number of frames
	 * skipped. A frame skip is a game update without a corresponding render;
	 * 
	 * - the FPS (frames/sec) and UPS (updates/sec) for this interval, the
	 * average FPS & UPS over the last NUM_FPSs intervals.
	 * 
	 * The data is collected every MAX_STATS_INTERVAL (1 sec).
	 */
	protected void storeStats(){
		
		frameCount++;
		statsInterval += period;
		
		long timeNow = System.nanoTime();
		if(!gp.isPaused)
			updateTime(timeNow);

		if (statsInterval >= MAX_STATS_INTERVAL) { // record stats every

			long realElapsedTime = timeNow - prevStatsTime; // time since last
			// stats collection
			totalElapsedTime += realElapsedTime;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * 1_000_000_000L);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}
			/*
			 * System.out.println(timedf.format( (double)
			 * statsInterval/1000000000L) + " " + timedf.format((double)
			 * realElapsedTime/1000000000L) + "s " + df.format(timingError) +
			 * "% " + frameCount + "c " + framesSkipped + "/" +
			 * totalFramesSkipped + " skip; " + df.format(actualFPS) + " " +
			 * df.format(averageFPS) + " afps; " + df.format(actualUPS) + " " +
			 * df.format(averageUPS) + " aups" );
			 */
			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	}
	
	protected void skipForwardTime(double sec) {
		skipTime += (long)(sec * 1_000_000_000);
		updateTime(System.nanoTime()); // force update
	}
	
	protected void skipBackwardTime(double sec) {
		skipTime -= (long)(sec * 1_000_000_000);
		updateTime(System.nanoTime());
	}
	
	protected void updateTime(long timeNow) {
		timeSpentInGame = ((double)(timeNow - gameStartTime + skipTime) / 1_000_000_000); // ns --> seconds
	}
	
	protected void pauseTime() {
		pauseTime = System.nanoTime();
	}
	
	protected void resumeTime() {
		// In time, you will know what it's like to lose. 
		// To feel so desperately that you're right. Yet to fail all the same. 
		// Dread it. Run from it. Destiny still arrives.
		long resumeTime = System.nanoTime();
		gameStartTime += resumeTime - pauseTime;
	}
	
	protected void printStats() {
		System.out.println("+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
		System.out.println("Frame Count/Loss: " + frameCount + " / "
				+ totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
	} // end of printStats()
	
	public double getTimeElapsed() {
		return timeSpentInGame;
	}
}
