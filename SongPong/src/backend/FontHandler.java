package backend;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.HashMap;

public class FontHandler {
	
	public FontHandler() {
		loadFont("src/font/ARCADE.TTF", "arcade-li");
		loadFont("src/font/ARCADE_N.TTF", "arcade");
	}
	
	private HashMap<String, Font> fontList = new HashMap<String, Font>();
	
	private static final Font SERIF_FONT = new Font("serif", Font.PLAIN, 24);
	
	public Font loadFont(String fontPath, String name) {
		Font font = null;
		if (fontPath == null) {
			System.err.println("Error loading font. Default applied.");
	        return SERIF_FONT;
	    }
	    try {
	        File fontFile = new File(fontPath);
	        font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
	        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(font);
	    } catch (Exception ex) {
	    	System.err.println("ERROR LOADING FONT FROM FILE");
	        font = SERIF_FONT;
	    }
	    fontList.put(name, font);
	    return font;
	}
	
	public Font getFont(String name) {
		Font font = fontList.get(name);
		if(font == null) {
			return SERIF_FONT;
		}
		else {
			return font;
		}
	}
}
