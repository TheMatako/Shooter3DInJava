package com.mygame.world;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class Map {
	
	private SimpleApplication app;
	private Node mapNode;
	
	// Parameters of the map
	private float mapSize = 50f; // Size of the ground = 50x50 units
	private int gridLines = 25;
	
	public Map(SimpleApplication app) {
		this.app = app;
		this.mapNode = new Node("Map Node");
	}
	
	/* 
	 * Load and creates all elements of the map
	 */
	public void loadMap() {
		createGround();
		// Attach the map node to the rootNode
		app.getRootNode().attachChild(mapNode);
	}
	
	/*
	 * Create the white ground of the map
	 */
	private void createGround() {
		// Create a quad for the ground
		Quad groundQuad = new Quad(mapSize, mapSize);
		Geometry groundGeometry = new Geometry("Ground", groundQuad);
		Material groundMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		groundMaterial.setColor("Color", ColorRGBA.White);
		groundGeometry.setMaterial(groundMaterial);
		
		// Position the ground : centered in (0,0,0) and horizontal
		// The quad is created in the XY plane, so we rotate it so it is horizontal
		groundGeometry.rotate(-90f * (float)Math.PI / 180f, 0, 0);
		groundGeometry.setLocalTranslation(-mapSize/2, 0, mapSize/2);
		
		// Attach ground to map node
		mapNode.attachChild(groundGeometry);
	}

	
	/* 
	 * Delete all map elements
	 */
	public void unloadMap() {
		if (mapNode.getParent() != null) {
			app.getRootNode().detachChild(mapNode);
		}
		mapNode.detachAllChildren();
	}
	
	/*
	 * Return map main node
	 */
	public Node getMapNode() {
		return mapNode;
	}
	
	/*
	 * Return map size
	 */
	public float getMapSize() {
		return mapSize;
	}
	
	/* 
	 * Return a default spawn position for the player
	 */
	public Vector3f getPlayerSpawnPosition() {
		return new Vector3f(0, 2f, 0);
	}
	
	/* 
	 * Verify if a position is in the map
	 */
	public boolean isPositionValid(Vector3f position, float playerWidth) {
		return position.x >= (playerWidth - mapSize) / 2 && position.x <= (mapSize - playerWidth) / 2 && 
				position.z >= (playerWidth - mapSize) / 2 && position.z <= (mapSize - playerWidth) / 2;
	}
	
	/* 
	 * Return ground height (for the collisions)
	 */
	public float getGroundHeight() {
		return 0f;
	}
}
