package com.mygame;

import com.mygame.states.MenuState;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;


public class Shooter3D extends SimpleApplication {
	
	public static void main(String[] args) {
		Shooter3D app = new Shooter3D();
		
		AppSettings settings = new AppSettings(true);
		settings.setTitle("Shooter 3D");
		settings.setResolution(1280, 720);
		settings.setFullscreen(false);
		settings.setVSync(true);
		settings.setSamples(4);
		
		app.setSettings(settings);
		app.setShowSettings(false);
		app.start();
	}

	@Override 
	public void simpleInitApp() {
		// Disable flymcam cursor

		flyCam.setEnabled(false);
		setDisplayStatView(false);
		
		MenuState menuState = new MenuState();
		stateManager.attach(menuState);
	}
}