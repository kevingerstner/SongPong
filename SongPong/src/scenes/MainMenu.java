package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import backend.ImageHandler;
import backend.Scene;
import backend.SongPong;
import ui.MenuButton;

public class MainMenu extends Scene{
	
	private SongPong game;
	private ImageHandler ih;
	
	private int width;
	private int height;
	
	private Font font;
	private float fontSize = 36f;
	private Color buttonColor = new Color(157, 28, 217);
	private Color hoverColor = new Color(61, 13, 84);
	private Color quitColor = new Color(235, 232, 52);
	
	private ArrayList<MenuButton> buttons = new ArrayList<MenuButton>();
	
	
	// GRAPHICS
	private BufferedImage songPongLogo;
	private int[] logoPosition = new int[2];
	
	public MainMenu(SongPong game) {
		this.game = game;
		this.ih = game.ih;
		
		width = game.pWidth;
		height = game.pHeight;
		
		// FONT
		font = game.fh.getFont("arcade").deriveFont(fontSize);
		
		// LOGO
		songPongLogo = ImageHandler.loadImage("src/images/SongPongLogo-01.png");
		logoPosition[0] = (width / 2) - (songPongLogo.getWidth() / 2);
		logoPosition[1] = 20;
		
		// BUTTONS
		MenuButton paradiseButton = new MenuButton("START PARADISE", (width / 2), 500, buttonColor, hoverColor, font, "center") {
			@Override
			public void mouseAction(){
				game.startScene("paradise");
			}
		};
		
		MenuButton schoolButton = new MenuButton("START SCHOOL", (width / 2), 575, buttonColor, hoverColor, font, "center") {
			@Override
			public void mouseAction(){
				game.startScene("school");
			}
		};
		
		MenuButton testButton = new MenuButton("TEST", (width / 2), 650, buttonColor, hoverColor, font, "center") {
			@Override
			public void mouseAction(){
				game.startScene("test");
			}
		};
		
		MenuButton quitButton = new MenuButton("QUIT TO DESKTOP", (width / 2), 850, buttonColor, quitColor, font, "center") {
			@Override
			public void mouseAction(){
				game.running = false;
			}
		};
		
		buttons.add(paradiseButton);
		buttons.add(schoolButton);
		buttons.add(testButton);
		buttons.add(quitButton);
	}

	@Override
	public void renderScene(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, width, height);
		
		g2.drawImage(songPongLogo, logoPosition[0], logoPosition[1], null);
		
		g2.setColor(buttonColor);
		g2.setFont(font);
		
		for(MenuButton button : buttons) {
			button.drawButton(g2);
		}
	}

	@Override
	public void updateScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initScene() {
		game.sceneRunning = false;
		game.resetSceneTime();
	}

	@Override
	public void handleMousePress(int x, int y, double timeClicked) {
		for(MenuButton button : buttons) {
			if(button.isOverButton)
				button.mouseAction();
		}
	}

	@Override
	public void handleMouseMove(int x, int y) {
		for(MenuButton button : buttons) {
			button.isHovered(x, y);
		}
	}

	@Override
	public void handleKeyboardInput(int keyCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleGamePause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleGameResume() {
		
	}

	@Override
	public void animateScene() {
		// TODO Auto-generated method stub
		
	}
	
}
