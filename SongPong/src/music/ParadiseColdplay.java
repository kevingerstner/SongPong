package music;

import backend.SongMap;
import backend.SongPong;

public class ParadiseColdplay extends SongMap{
	
	private String musicPath;

	public ParadiseColdplay(SongPong game, String songName, String musicPath, int delay) {
		super(game, songName, musicPath, delay);
	}
	
}
