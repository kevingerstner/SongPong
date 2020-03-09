package scenes;

import backend.SongMap;
import backend.SongPong;

public class Test extends SongMap{
	
	private String musicPath;

	public Test(SongPong game, String songName, String musicPath) {
		super(game, songName, musicPath);
	}
	
}
