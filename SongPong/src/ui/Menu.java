package ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import backend.SongMap;
import backend.SongPong;

public class Menu {
	private SongPong game;
	protected boolean isEnabled;
	
	private Color menuBGColor = new Color(130,130,130);
	private Font font;
	private int menuWidth = 480;
	private int menuHeight = 480;
	
	private int paddingLeft = 200;
	
	private ArrayList<MenuButton> buttons = new ArrayList<MenuButton>();
	private MenuButton quitButton;
	
	public Menu(SongPong game) {
		this.game = game;
		createButtons();
		
		isEnabled = false;
		menuWidth = game.pWidth;
		menuHeight = game.pHeight;
	}
	
	private void createButtons() {
		quitButton = new MenuButton(game, "Quit", paddingLeft, 300) {
			public void mouseAction(){
				game.running = false;
			}
		};
		buttons.add(quitButton);
	}
	
	synchronized public void displayMenu(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		//BACKGROUND
		g2.setColor(menuBGColor);
		g2.fillRect(0, 0, menuWidth, menuHeight);
		
		//GLOBAL APPEARANCE
		font = new Font("SansSerif", Font.BOLD, 24);
		g.setFont(font);
		g.setColor(Color.white);
		
		String menu = "Menu";
		g.drawString(menu, paddingLeft, 50);
		
		for(MenuButton butt : buttons) {
			butt.drawButton(g);
		}
	}
	
	public void checkOnMouseHover(int x, int y) {
		for(MenuButton butt : buttons) {
			butt.isHovered(x, y);
		}
	}
	
	public void checkOnMousePress(int x, int y) {
		for(MenuButton butt : buttons) {
			if (butt.isOverButton) {
				butt.mouseAction();
			}
		}
	}
	
	public void toggleMenu() {
		System.out.println("<ACTION> Toggle Menu");
		
		SongMap activeSong = game.getActiveSong();
		
		if(!isEnabled) {
			isEnabled = true;
			game.pauseActions();
			
		}
		else {
			isEnabled = false;
			game.resumeGame();
			game.customCursor.setCursorInvisible();
			activeSong.tuneSpinner.resumeMusic();
		}
	}
	
	public boolean isMenuEnabled() {return isEnabled;}
}