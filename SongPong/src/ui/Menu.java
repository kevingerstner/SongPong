package ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import backend.Scene;
import backend.SongMap;
import backend.SongPong;

public class Menu {
	private SongPong game;
	protected boolean isEnabled;
	
	private Color menuBGColor = Color.black;
	private Font font;
	private float fontSize = 36f;
	private int menuWidth = 480;
	private int menuHeight = 480;
	
	private int paddingLeft = 200;
	
	private ArrayList<MenuButton> buttons = new ArrayList<MenuButton>();
	private MenuButton quitButton;
	private MenuButton menuButton;
	
	private boolean switchedScenes = false;
	
	public Menu(SongPong game) {
		this.game = game;
		
		font = game.fh.getFont("arcade").deriveFont(fontSize);

		createButtons();
		
		isEnabled = false;
		menuWidth = game.pWidth;
		menuHeight = game.pHeight;
	}
	
	private void createButtons() {
		menuButton = new MenuButton("Back to Menu", paddingLeft, 300, Color.white, Color.red, font, "left") {
			@Override
			public void mouseAction() {
				switchedScenes = true;
				toggleMenu();
				game.startScene("mainmenu");
			}
		};
		quitButton = new MenuButton("Quit", paddingLeft, 400, Color.white, Color.red, font, "left") {
			@Override
			public void mouseAction() {
				game.running = false;
			}
		};
		buttons.add(menuButton);
		buttons.add(quitButton);
	}
	
	synchronized public void displayMenu(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		//BACKGROUND
		g2.setColor(menuBGColor);
		g2.fillRect(0, 0, menuWidth, menuHeight);
		
		//GLOBAL APPEARANCE
		g.setFont(font);
		g.setColor(Color.white);
		
		String menu = "Menu";
		g.drawString(menu, paddingLeft, 100);
		
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
		
		if(!isEnabled) {
			System.out.println("<MENU> PAUSE GAME");
			isEnabled = true;
			game.pauseGame();
			
		}
		else {
			System.out.println("<MENU> RESUME GAME");
			isEnabled = false;
			game.resumeGame();
			if(!switchedScenes) {
				game.customCursor.setCursorInvisible();
				game.getActiveSong().handleGameResume();
			}
		}
	}
	
	public boolean isMenuEnabled() {return isEnabled;}
}