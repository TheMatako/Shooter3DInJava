package com.mygame;

import com.mygame.states.MenuState;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


public class Shooter3D extends SimpleApplication {
	
	public static void main(String[] args) {
		Shooter3D app = new Shooter3D();
		
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