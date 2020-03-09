package backend;

public class SongSurfer {
	SongPong game;
	SongMap song;
	BallDropper bd;
	GameStats gs;
	MusicPlayer player;
	
	double skipAmt = 5;
	
	public SongSurfer(SongMap song) {
		this.song = song;
		game = song.game;
		
		bd = song.bd;
		player = song.tuneSpinner;
		gs = song.gs;
	}

	public void skipForward() {
		System.out.println("SKIP FORWARD " + skipAmt + " SEC.");
		game.skipForwardTime(skipAmt);
		player.skipSeconds(skipAmt);
		bd.purgeBalls();
	}
	
	public void skipBackward() {
		System.out.println("SKIP BACKWARDS " + skipAmt + " SEC.");
		game.skipBackwardTime(skipAmt);
		bd.purgeBalls();
		bd.rewindBalls();
		player.skipSeconds(-skipAmt);
	}
}
