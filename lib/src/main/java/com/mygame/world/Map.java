package com.mygame.world;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
	
	private SimpleApplication app;
	private Node mapNode;
	
	// Parameters of the map
	private float mapSize = 60f; // Size of the ground = 60x60 units
	
	// List of objects
	private List<MapObject> mapObjects;
	
	// Debug mode to show collision boxes
	private boolean debugMode = false;
	
	public Map(SimpleApplication app) {
		this.app = app;
		this.mapNode = new Node("Map Node");
		this.mapObjects = new ArrayList<>();
	}
	
	/* 
	 * Load and creates all elements of the map
	 */
	public void loadMap() {
		createGround();
		createPerimeterWalls();
		createHouses();
		createTrees();
		createStructures();
		//createDecorations();
		
		// Attach the map node to the rootNode
		app.getRootNode().attachChild(mapNode);
	}
	
	/*
	 * Create ground with texture grass/stone
	 */
	private void createGround() {
		// main textured ground
		Quad groundQuad = new Quad(mapSize, mapSize);
		Geometry groundGeometry = new Geometry("Ground", groundQuad);
		
		Material groundMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		groundMaterial.setColor("Color", new ColorRGBA(0.3f, 0.6f, 0.2f, 1.0f)); // Green Grass
		groundGeometry.setMaterial(groundMaterial);
		
		// Position the ground : centered in (0,0,0) and horizontal
		// The quad is created in the XY plane, so we rotate it so it is horizontal
		groundGeometry.rotate(-90f * (float)Math.PI / 180f, 0, 0);
		groundGeometry.setLocalTranslation(-mapSize/2, -0.1f, mapSize/2);
		
		// Attach ground to map node
		mapNode.attachChild(groundGeometry);
		
		// Add some areas of sand/stone
		createGroundPatches();
		
		/*
		 * Ref grid (for debug
		 */
		// if (debugMode) {
		//		createGridLines();
		// }
	}
	
	/*
	 * Create differents ground textures
	 */
	private void createGroundPatches() {
		Random random = new Random(42);
		
		// Some stone patches
		for (int i = 0; i < 5; i++) {
			float x = (random.nextFloat() - 0.5f) * (mapSize - 10);
			float z = (random.nextFloat() - 0.5f) * (mapSize - 10);
			float size = 3 + random.nextFloat() * 4;
			
			Quad patchQuad = new Quad(size, size);
			Geometry patchGeometry = new Geometry("Stone Patch", patchQuad);
			
			Material stoneMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
			stoneMaterial.setColor("Color", new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
			patchGeometry.setMaterial(stoneMaterial);
			
			patchGeometry.rotate(-90f * (float)Math.PI / 180f, 0, 0);
			patchGeometry.setLocalTranslation(x- size / 2, -0.05f, z + size / 2);
			
			mapNode.attachChild(patchGeometry);
		}
	}

	/*
	 * Create perimetrical Walls
	 */
	private void createPerimeterWalls() {
		float wallHeight = 4f;
		float wallThickness = 1f;
		
		// North wall
		MapObject northWall = new MapObject(app, MapObject.ObjectType.WALL,
				new Vector3f(0, wallHeight / 2, mapSize / 2),
				new Vector3f(mapSize, wallHeight, wallThickness));
		addMapObject(northWall);
		
		// South wall
		MapObject southWall = new MapObject(app, MapObject.ObjectType.WALL,
				new Vector3f(0, wallHeight / 2, - mapSize / 2),
				new Vector3f(mapSize, wallHeight, wallThickness));
		addMapObject(southWall);

		// South wall
		MapObject eastWall = new MapObject(app, MapObject.ObjectType.WALL,
				new Vector3f(mapSize / 2, wallHeight / 2, 0),
				new Vector3f(wallThickness, wallHeight, mapSize));
		addMapObject(eastWall);

				// South wall
		MapObject westWall = new MapObject(app, MapObject.ObjectType.WALL,
				new Vector3f(- mapSize / 2, wallHeight / 2, 0),
				new Vector3f(wallThickness, wallHeight, mapSize));
		addMapObject(westWall);
	}
	
	/*
	 * Create some scattered houses
	 */
	private void createHouses() {
		// Main house (big)
		MapObject mainHouse = new MapObject(app, MapObject.ObjectType.HOUSE,
				new Vector3f(-15, 2.5f, -15),
				new Vector3f(8, 5, 10));
		addMapObject(mainHouse);
		
		// Small house 1
		MapObject house1 = new MapObject(app, MapObject.ObjectType.HOUSE,
				new Vector3f(20, 2f, 20),
				new Vector3f(5, 4, 6));
		addMapObject(house1);
		
		// Small house 2
		MapObject house2 = new MapObject(app, MapObject.ObjectType.HOUSE,
				new Vector3f(-20, 2f, 15),
				new Vector3f(4, 4, 5));
		addMapObject(house2);
		
		// Warehouse
		MapObject warehouse = new MapObject(app, MapObject.ObjectType.HOUSE,
				new Vector3f(15, 3f, -20),
				new Vector3f(6, 6, 12));
		addMapObject(warehouse);
	}
	
	/*
	 * Create trees naturally scattered
	 */
	private void createTrees() {
		Random random = new Random(123);
		int numberOfTrees = 20;
		
		for (int i = 0; i < numberOfTrees; i ++) {
			// Random position avoiding center and the edges
			float x, z;
			int attempts = 0;
			
			do {
				x = (random.nextFloat() - 0.5f) * (mapSize - 8);
				z = (random.nextFloat() - 0.5f) * (mapSize - 8);
				attempts++;
			} while (isTooCloseToOtherObjects(new Vector3f(x, 0, z), 3f) && attempts < 20);
			
			// Height and size random
			float height = 6f + random.nextFloat() * 4f;
			float width = 0.8f + random.nextFloat() * 0.6f;
			
			MapObject tree = new MapObject(app, MapObject.ObjectType.TREE,
					new Vector3f(x, height / 2, z),
					new Vector3f(width, height, width));
			addMapObject(tree);
		}
	}
	
	/*
	 * Create diverses structures
	 */
	private void createStructures() {
		// Central elevated platform
		MapObject centralPlatform = new MapObject(app, MapObject.ObjectType.PLATFORM,
				new Vector3f(0, 3.5f, 0),
				new Vector3f(10, 1, 10));
		addMapObject(centralPlatform);
		
		// Decorative pillars around the platform
		for (int i = 0; i < 4; i++) {
			float angle = i * 90f;
			float x = (float) Math.cos(Math.toRadians(angle)) * 4.5f;
			float z = (float) Math.sin(Math.toRadians(angle)) * 4.5f;
			
			MapObject pillar = new MapObject(app, MapObject.ObjectType.PILLAR,
					new Vector3f(x, 2f, z),
					new Vector3f(1, 4, 1));
			addMapObject(pillar);
		}
	}
	
	/*
	 * Add some decoratives objects
	 */
	private void createDecorations() {
		Random random = new Random(456);
		
		// Decoratives fences
		for (int i = 0; i < 8; i++) {
			float x = (random.nextFloat() - 0.5f) * (mapSize - 6);
			float z = (random.nextFloat() - 0.5f) * (mapSize - 6);
			
			if (!isTooCloseToOtherObjects(new Vector3f(x, 0, z), 2f)) {
				MapObject fence = new MapObject(app, MapObject.ObjectType.FENCE,
						new Vector3f(x, 1f, z),
						new Vector3f(4, 2, 0.2f));
				addMapObject(fence);
			}
		}
	}
	
	/*
	 * Check if a position is too close from others already put objects
	 */
	private boolean isTooCloseToOtherObjects(Vector3f position, float minDistance) {
		for (MapObject obj : mapObjects) {
			float distance = obj.getPosition().distance(position);
			if (distance < minDistance) {
				return true;
			}
		}
		return false;
	}
	
	/* 
	 * Add a MapObject to the list and to the scene
	 */
	private void addMapObject(MapObject object) {
		mapObjects.add(object);
		object.setDebugMode(debugMode);
		mapNode.attachChild(object.getNode());
	}
	
	/*
	 * Enable/Disable debug mode
	 */
	public void setDebugMode(boolean debug) {
		this.debugMode = debug;
		for (MapObject obj : mapObjects) {
			obj.setDebugMode(debug);
		}
	}
	
	/*
	 * Check collisions with all objects of the map
	 */
	public boolean checkCollision(Vector3f position, float radius) {
		// Check map limits
		if (position.x - radius < - mapSize / 2 || position.x + radius > mapSize / 2 ||
				position.z - radius < - mapSize / 2 || position.z + radius > mapSize / 2) {
			return true;
		}
		
		// Check collision with objects
		for (MapObject obj : mapObjects) {
			if (obj.checkCollision(position, radius)) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * Find ground height at a given position (count platforms)
	 */
	public float getGroundHeightAt(Vector3f position) {
		float maxHeight = 0f; // base ground height
		
		// Check platforms
		for (MapObject obj : mapObjects) {
			if (obj.getType() == MapObject.ObjectType.PLATFORM) {
				float platformHeight = obj.getGroundHeightAt(position);
				if (platformHeight > maxHeight) {
					maxHeight = platformHeight;
				}
			}
		}
		
		return maxHeight;
	}
	/* 
	 * Delete all map elements
	 */
	public void unloadMap() {
		if (mapNode.getParent() != null) {
			app.getRootNode().detachChild(mapNode);
		}
		mapNode.detachAllChildren();
		mapObjects.clear();
	}
	
	// Getters 
	
	public Node getMapNode() {
		return mapNode;
	}
	
	public float getMapSize() {
		return mapSize;
	}
	
	/* 
	 * Return a default spawn position for the player
	 */
	public Vector3f getPlayerSpawnPosition() {
		return new Vector3f(0, 7f, 0); // on the central platform
	}
	
	/* 
	 * Verify if a position is in the map
	 */
	public boolean isPositionValid(Vector3f position, float playerWidth) {
		return !checkCollision(position, playerWidth / 2);
	}
	
	public List<MapObject> getMapObjects() {
		return new ArrayList<>(mapObjects);
	}
}