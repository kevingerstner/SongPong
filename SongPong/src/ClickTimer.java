import java.text.DecimalFormat;

public class ClickTimer {
	
	GameStats gs;
	BallDropper bd;
	
	private double songDelay;
	
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	
	int[] cols;
	
	public ClickTimer(SongMap game) {
		gs = game.gs;
		bd = game.bd;
		songDelay = game.delayTimeSec;
		cols = bd.getColumns();
	}
	public void recordClickTime(int clickX) {
		int lowDist = 1000;
		int closestCol = 0;
		for(int w = 0; w < cols.length; w++) {
			if(clickX - cols[w] < lowDist) {
				closestCol = w;
				lowDist = clickX - cols[w];
			}
		}
		
		System.out.println("Click @" + df.format(gs.getTimeElapsed() - songDelay) + ", " + closestCol);
	}
}
