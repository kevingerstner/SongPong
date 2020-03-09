package backend;

import java.awt.Graphics;

public abstract class Scene {
	
	public abstract void initScene();
	public abstract void renderScene(Graphics g);
	public abstract void updateScene();
	public abstract void animateScene();
	
	public abstract void handleMousePress(int x, int y, double timeClicked);
	public abstract void handleMouseMove(int x, int y);
	public abstract void handleKeyboardInput(int keyCode);
	
	public abstract void handleGamePause();
	public abstract void handleGameResume();
	
}
