package com.mygame.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.texture.Texture;

/*
 * Unified class for all objects of the map : collision + visual
 */
public class MapObject {
	
	public enum ObjectType {
		WALL,
		HOUSE,
		TREE,
		PLATFORM,
		PILLAR,
		FENCE
	}
	
	private SimpleApplication app;
	private Node objectNode;
	private Geometry visualGeometry;
	private Geometry collisionGeometry;
	private ObjectType type;
	private Vector3f position;
	private Vector3f size;
	private boolean debugMode = false;
	
	public MapObject(SimpleApplication app, ObjectType type, Vector3f position, Vector3f size) {
		this.app = app;
		this.type = type;
		this.position = position.clone();
		this.size = size.clone();
		this.objectNode = new Node("MapObject_" + type.name());
		
		createObject();
	}
	
	/* 
	 * Create the object with collision and visual according to its type
	 */
	private void createObject() {
		switch (type) {
			case WALL:
				createWall();
				break;
			case HOUSE:
				createHouse();
				break;
			case TREE:
				createTree();
				break;
			case PLATFORM:
				createPlatform();
				break;
			case PILLAR:
				createPillar();
				break;
			case FENCE:
				createFence();
				break;
		}
		
		// Position the object
		objectNode.setLocalTranslation(position);
	}
	
	/* 
	 * Create a wall with a brick texture
	 */
	private void createWall() {
		// Invisible collision shape
		Box collisionBox = new Box(size.x / 2, size.y / 2, size.z / 2);
		collisionGeometry = new Geometry("Wall Collision", collisionBox);
		
		// Visual with texture
		Box visualBox = new Box(size.x / 2, size.y / 2, size.z / 2);
		visualGeometry = new Geometry("wall Visual", visualBox);
		
		Material wallMaterial = createMaterial("Brick");
		visualGeometry.setMaterial(wallMaterial);
		
		objectNode.attachChild(visualGeometry);
		if (debugMode) objectNode.attachChild(visualGeometry);
	}
	
	/* 
	 * Create a simple house
	 */
	private void createHouse() {
		// Invisible collision shape : simple box
		Box collisionBox = new Box(size.x / 2, size.y / 2, size.z / 2);
		collisionGeometry = new Geometry("House Collision", collisionBox);
		
		// Visual : differents parts
		Node houseNode = new Node("House Visual");
		
		// Walls
		Box houseWalls = new Box(size.x / 2, size.y / 2, size.z / 2);
		Geometry wallsGeometry = new Geometry("House Walls", houseWalls);
		wallsGeometry.setMaterial(createMaterial("House"));
		houseNode.attachChild(wallsGeometry);
		
		// Roof
		Box roof = new Box(size.x / 2 + 0.2f, 0.3f , size.z / 2 + 0.2f);
		Geometry roofGeometry = new Geometry("House Roof", roof);
		roofGeometry.setLocalTranslation(0, size.y / 2 + 0.3f, 0);
		roofGeometry.setMaterial(createMaterial("Roof"));
		houseNode.attachChild(roofGeometry);
		
		visualGeometry = wallsGeometry; // For references
		objectNode.attachChild(houseNode);
		if (debugMode) objectNode.attachChild(collisionGeometry);
	}
	
	/* 
	 * Create a tree (cylinder + leaves )
	 */
	private void createTree() {
		// Collision shape : cylinder
		float trunkRadius = Math.min(size.x, size.z) * 0.15f;
		Box collisionBox = new Box(trunkRadius, size.y / 2, trunkRadius);
		collisionGeometry = new Geometry("Tree Collision", collisionBox);
		// Visual - trunk + foliage
		Node treeNode = new Node("Tree Visual");
		
		// Trunk
		float visualTrunkRadius = Math.min(size.x, size.z) * 0.3f;
		Cylinder trunk = new Cylinder(8, 16, visualTrunkRadius, size.y * 0.7f, true);
		Geometry trunkGeometry = new Geometry("Tree Trunk", trunk);
		trunkGeometry.setLocalTranslation(0, size.y * 0.35f - size.y / 2, 0);
		trunkGeometry.setMaterial(createMaterial("TreeTrunk"));

		trunkGeometry.rotate(-90f * (float)Math.PI / 180f, 0, 0);
		treeNode.attachChild(trunkGeometry);
		
		// Foliage
		float foliageSize = Math.min(size.x, size.z) * 0.8f;
		Box foliage = new Box(foliageSize, foliageSize, foliageSize);
		Geometry foliageGeometry = new Geometry("Tree Foliage", foliage);
		foliageGeometry.setLocalTranslation(0, size.y * 0.7f - size.y / 2, 0);
		foliageGeometry.setMaterial(createMaterial("Foliage"));
		treeNode.attachChild(foliageGeometry);
		
		visualGeometry = trunkGeometry;
		objectNode.attachChild(treeNode);
		if (debugMode) objectNode.attachChild(collisionGeometry);
		
	}
	
	/*
	 * Create a platform
	 */
	private void createPlatform() {
		// Collision and visual are the same for visuals
		Box collisionBox = new Box(size.x / 2, size.y / 2, size.z / 2);
		collisionGeometry = new Geometry("Platform Collision",  collisionBox);

		Box platformBox = new Box(size.x / 2, size.y / 2, size.z / 2);
		Geometry visualGeometry = new Geometry("Platform", platformBox);
		
		visualGeometry.setMaterial(createMaterial("Stone"));
		
		objectNode.attachChild(visualGeometry);
		if (debugMode)  objectNode.attachChild(collisionGeometry);
	}
	
	/*
	 * Create a pillar
	 */
	private void createPillar() {
		float radius = Math.min(size.x, size.z) / 2;
		
		// Collision - simple box
		Box collisionBox = new Box(radius, size.y / 2, radius);
		collisionGeometry = new Geometry("Pillar Collision", collisionBox);
		
		// Visual - cylinder with details
		Cylinder pillarCylinder = new Cylinder(12, 24, radius, size.y, true);
		visualGeometry = new Geometry("Pillar", pillarCylinder);
		visualGeometry.setMaterial(createMaterial("Marble"));
		visualGeometry.rotate(-90f * (float)Math.PI / 180f, 0, 0);
		
		objectNode.attachChild(visualGeometry);
		if (debugMode) objectNode.attachChild(collisionGeometry);
	}
	
	/*
	 * Create a fence
	 */
	private void createFence() {
		// Colision - simple box
		Box collisionBox = new Box(size.x / 2, size.y / 2, size.z / 2);
		collisionGeometry = new Geometry("Fence Collision", collisionBox);
		
		// Visual - textured fence
		Box fenceBox = new Box(size.x / 2, size.y / 2, size.z / 2);
		visualGeometry = new Geometry("Fence", fenceBox);
		visualGeometry.setMaterial(createMaterial("Wood"));
		
		objectNode.attachChild(visualGeometry);
		if (debugMode) objectNode.attachChild(collisionGeometry);
	}
	
	
	private Material createMaterial(String matType) {
		Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");;
		switch (matType) {
			case "Brick":
		        mat.setColor("Color", new ColorRGBA(0.7f, 0.4f, 0.3f, 1.0f)); // Red Brick
		        break;
			case "House":
		        mat.setColor("Color", new ColorRGBA(0.9f, 0.9f, 0.8f, 1.0f)); // Off-White
		        break;
			case "Roof":
		        mat.setColor("Color", new ColorRGBA(0.5f, 0.2f, 0.2f, 1.0f)); // Dark Red
		        break;
			case "TreeTrunk":
		        mat.setColor("Color", new ColorRGBA(0.4f, 0.2f, 0.1f, 1.0f)); // Brown
		        break;
			case "Foliage":
		        mat.setColor("Color", new ColorRGBA(0.1f, 0.6f, 0.1f, 1.0f)); // Green Foliage
				break;
			case "Stone":
		        mat.setColor("Color", new ColorRGBA(0.6f, 0.6f, 0.6f, 1.0f)); // Gray Stone
				break;
			case "Marble":
		        mat.setColor("Color", new ColorRGBA(0.95f, 0.95f, 0.9f, 1.0f)); // White Marble
				break;
			case "Wood":
		        mat.setColor("Color", new ColorRGBA(0.6f, 0.4f, 0.2f, 1.0f)); // Brown wood
				break;
		}
		return mat;
	}
	
	// Public Methods
	
	/*
	 * Test collision with a given position
	 */
	public boolean checkCollision(Vector3f testPosition, float radius) {
		Vector3f worldPos = objectNode.getWorldTranslation();
		
		// Standard collision for most objects
		boolean collision = testPosition.x + radius > worldPos.x - size.x / 2 &&
							testPosition.x - radius < worldPos.x + size.x / 2 &&
							testPosition.z + radius > worldPos.z - size.z / 2 &&
							testPosition.z - radius < worldPos.z + size.z / 2;
		
		// For platfrom, check height differently
		if (type == ObjectType.PLATFORM) {
			// The player can climb onto the platform if he comes from above
			boolean onTop = testPosition.y >= worldPos.y + size.y / 2 - 0.5f;
			boolean inRange = testPosition.y <= worldPos.y + size.y / 2 + 2f;
			
			if (collision && onTop && inRange) {
				// No collision if we're over the platform
				return false;
			}
		}
		if (collision) {
			return testPosition.y + radius > worldPos.y - size.y / 2 &&
					testPosition.y - radius < worldPos.y + size.y / 2;
		}
		return false;
	}
	
	/*
	 * Return ground height at this position (for platforms)
	 */
	public float getGroundHeightAt(Vector3f testPosition) {
		if (type == ObjectType.PLATFORM) {
			Vector3f worldPos = objectNode.getWorldTranslation();
			
			// Check if we're above the platform
			boolean overPlatform = testPosition.x >= worldPos.x - size.x / 2 &&
									testPosition.x <= worldPos.x + size.x / 2 &&
									testPosition.z >= worldPos.z - size.z / 2 &&
									testPosition.z <= worldPos.z + size.z / 2;
									
			if (overPlatform) {
				return worldPos.y + size.y / 2; // Height above the platform
			}
		}
		
		return Float.NEGATIVE_INFINITY; // No ground here
	}
	/* 
	 * Enable/Disable debug mode (show collisions boxes)
	 */
	public void setDebugMode(boolean debug) {
		this.debugMode = debug;
		
		if (debug && collisionGeometry != null && collisionGeometry.getParent() == null) {
			Material debugMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
			debugMat.setColor("Color", new ColorRGBA(1, 0, 0, 0.3f));
			debugMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
			collisionGeometry.setMaterial(debugMat);
			objectNode.attachChild(collisionGeometry);
		} else if (!debug && collisionGeometry != null) {
			objectNode.detachChild(collisionGeometry);
		}
	}
	
	public Node getNode() {
		return objectNode;
	}
	
	public Vector3f getPosition() {
		return position.clone();
	}	
	
	public Vector3f getSize() {
		return size.clone();
	}
	
	public ObjectType getType() {
		return type;
	}
}
