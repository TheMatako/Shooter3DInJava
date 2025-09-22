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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class GameState extends AbstractAppState implements ActionListener {
	
	private SimpleApplication app;
	private Map gameMap;
	private Player player;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (SimpleApplication) app;
		this.app.getFlyByCamera().setEnabled(true);
		
		// initialize inputs
		setupInputs();
		
		// Create and load map
		gameMap = new Map(this.app);
		gameMap.loadMap();
		
		// Create player
		player = new Player(this.app, gameMap);
		// Position camera at spawn position
		Vector3f spawnPos = gameMap.getPlayerSpawnPosition();
		this.app.getCamera().setLocation(spawnPos);
	}
	
	
	private void setupInputs() {
		app.getInputManager().addMapping("Return to Menu", new KeyTrigger(KeyInput.KEY_RETURN));
		app.getInputManager().addListener(this, "Return to Menu");
	}
	
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals("Return to Menu") && !isPressed) {
			returnToMenu();
		}
	}
	
	private void returnToMenu() {
		app.getFlyByCamera().setEnabled(false);
		
		setEnabled(false);
		app.getStateManager().detach(this);
		
		MenuState menuState = new MenuState();
		app.getStateManager().attach(menuState);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		app.getInputManager().deleteMapping("Return to Menu");
		app.getInputManager().removeListener(this);
		
		// Unload map
		if (gameMap != null) {
			gameMap.unloadMap();
		}
		
		// Unload player
		if (player != null) {
			player.cleanup();
		}
		
		app.getRootNode().detachAllChildren();
	}
	
	public void update(float tpf) {
		if (player != null) {
			player.update(tpf);
		}
	}
}
