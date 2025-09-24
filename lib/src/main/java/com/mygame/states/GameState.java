package com.mygame.states;

import com.mygame.world.Map;
import com.mygame.entities.Player;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;


public class GameState extends AbstractAppState implements ActionListener {
	
	private SimpleApplication app;
	private Map gameMap;
	private Player player;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (SimpleApplication) app;
		
		// initialize inputs
		setupInputs();
		
		// Create and load map
		gameMap = new Map(this.app);
		gameMap.loadMap();
		
		// Create player
		player = new Player(this.app, gameMap);
		Vector3f spawnPos = gameMap.getPlayerSpawnPosition();
		player.setPosition(spawnPos.clone());
		this.app.getCamera().setLocation(spawnPos);
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		// Update player
		if (player != null) {
			player.update(tpf);
		}
	}
	
	private void setupInputs() {
		app.getInputManager().addMapping("Return to Menu", new KeyTrigger(KeyInput.KEY_RETURN));
		app.getInputManager().addMapping("Toogle Debug", new KeyTrigger(KeyInput.KEY_F1));
		app.getInputManager().addListener(this, "Return to Menu", "Toggle Debug");
	}
	
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals("Return to Menu") && !isPressed) {
			returnToMenu();
		} else if (name.equals("Toggle Debug") && !isPressed) {
			gameMap.setDebugMode(!gameMap.getMapObjects().isEmpty());
		}
	}
	
	private void returnToMenu() {
		// Disable this state
		setEnabled(false);
		app.getStateManager().detach(this);
		
		// Reactivate menu
		MenuState menuState = new MenuState();
		app.getStateManager().attach(menuState);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		app.getInputManager().deleteMapping("Return to Menu");
		app.getInputManager().deleteMapping("Toggle Debug");
		app.getInputManager().removeListener(this);
		
		// Unload map
		if (gameMap != null) {
			gameMap.unloadMap();
		}
		
		// Unload player
		if (player != null) {
			player.cleanup();
		}
		
		// Clean scene when we quit game
		app.getRootNode().detachAllChildren();
	}
	

}