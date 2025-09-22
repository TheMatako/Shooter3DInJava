package com.mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class GameState extends AbstractAppState implements ActionListener {
	
	private SimpleApplication app;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (SimpleApplication) app;
		this.app.getFlyByCamera().setEnabled(true);
		
		// initialize inputs
		setupInputs();
		
		// initialize game
		createTestCube();
	}
	
	public void createTestCube() {
        Box boxMesh = new Box(1f, 1f, 1f);
        Geometry boxGeometry = new Geometry("Test Cube", boxMesh);
        
        Material boxMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        boxMaterial.setColor("Color", ColorRGBA.Blue);
        
        boxGeometry.setMaterial(boxMaterial);
        app.getRootNode().attachChild(boxGeometry);
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
		
		app.getRootNode().detachAllChildren();
	}
}
