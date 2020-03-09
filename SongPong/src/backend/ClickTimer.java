package backend;
import java.text.DecimalFormat;

public class ClickTimer {
	
	GameStats gs;
	BallDropper bd;
	SongMap song;
	
	private double gameLag = 0.25; //this value comes from observation and is not based in truth
	
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	
	int[] cols;
	
	public ClickTimer(SongMap song) {
		this.song = song;
		gs = song.gs;
		bd = song.bd;
		cols = bd.getColumns();
	}
	public void recordClickTime(int clickX, double timeClicked) {
		int lowDist = 1000;
		int closestCol = 0;
		for(int w = 0; w < cols.length; w++) {
			if(clickX - cols[w] < lowDist) {
				closestCol = w;
				lowDist = clickX - cols[w];
			}
		}
		
		System.out.println("Click @" + df.format(timeClicked - gameLag) + ", " + closestCol);
	}
}
