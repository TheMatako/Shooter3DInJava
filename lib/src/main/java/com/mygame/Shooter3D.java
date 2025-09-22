package com.mygame;

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
		createTestCube();
	}
	
	public void createTestCube() {
		Box boxMesh = new Box(1f, 1f, 1f);
		
		Geometry boxGeometry = new Geometry("Test Cube", boxMesh);
		
		Material boxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		boxMaterial.setColor("Color",  ColorRGBA.Blue);
		
		boxGeometry.setMaterial(boxMaterial);
		
		rootNode.attachChild(boxGeometry);
	}
}