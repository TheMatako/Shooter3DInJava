package com.mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class MenuState extends AbstractAppState implements ActionListener  {
	
	private SimpleApplication app;
	private Node guiNode;
	private BitmapText titleText;
	private BitmapText startButtonText;
	private Geometry startButton;
	private BitmapFont guiFont;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (SimpleApplication) app;
		this.guiNode = this.app.getGuiNode();
		
		initializeMenu();
		setupInputs();
	}
	
	public void initializeMenu() {
		// Load default font
		guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		
		// Create title
		titleText = new BitmapText(guiFont);
		titleText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		titleText.setText("Shooter 3D");
		titleText.setColor(ColorRGBA.White);
		// Center
		titleText.setLocalTranslation(
			(app.getCamera().getWidth() - titleText.getLineWidth()) / 2,
			app.getCamera().getHeight() - 100,
			0
		);
		guiNode.attachChild(titleText);
		
		// Create start button (background)
		Quad buttonQuad = new Quad(200, 50);
		startButton = new Geometry("Start Button", buttonQuad);
		
		Material buttonMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		buttonMat.setColor("Color", ColorRGBA.Green);
		startButton.setMaterial(buttonMat);
		startButton.setLocalTranslation(
			(app.getCamera().getWidth() - 200) / 2,
			(app.getCamera().getHeight() - 50) / 2,
			0
		);
		guiNode.attachChild(startButton);
		
		// Create text
		startButtonText = new BitmapText(guiFont);
		startButtonText.setSize(guiFont.getCharSet().getRenderedSize());
		startButtonText.setText("Start Game");
		startButtonText.setColor(ColorRGBA.White);
		// Center text in the button
		startButtonText.setLocalTranslation(
			(app.getCamera().getWidth() - startButtonText.getLineWidth()) / 2,
			(app.getCamera().getHeight() + startButtonText.getLineHeight()) / 2,
			1
		);
		guiNode.attachChild(startButtonText);
	}
	
	private void setupInputs() {
		app.getInputManager().addMapping("Start Game", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		app.getInputManager().addListener(this, "Start Game");
	}
	
	@Override 
	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals("Start Game") && !isPressed) {
			// Verify if click is on button
			Vector2f click3d = app.getInputManager().getCursorPosition();
			
			// Button's position
			float buttonX = (app.getCamera().getWidth() - 200) / 2;
			float buttonY = (app.getCamera().getHeight() - 50) / 2;
			
			if (click3d.x >= buttonX && click3d.x <= buttonX + 200 && click3d.y >= buttonY && click3d.y <= buttonY + 50) {
				startGame();
			}
		}
	}
	
	private void startGame() {
		// Disable menu
		setEnabled(false);
		// Create and activate game state
		GameState gameState = new GameState();
		app.getStateManager().attach(gameState);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		// Clean elements from GUI
		guiNode.detachChild(titleText);
		guiNode.detachChild(startButton);
		guiNode.detachChild(startButtonText);
		
		// Clean inputs
		app.getInputManager().deleteMapping("Start Game");
		app.getInputManager().removeListener(this);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if (enabled) {
			guiNode.attachChild(titleText);
			guiNode.attachChild(startButton);
			guiNode.attachChild(startButtonText);
		} else {
			guiNode.detachChild(titleText);
			guiNode.detachChild(startButton);
			guiNode.detachChild(startButtonText);
		}
	}
}
