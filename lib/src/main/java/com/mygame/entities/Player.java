package com.mygame.entities;

import com.mygame.world.Map;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;


public class Player implements ActionListener, AnalogListener {
	
	private SimpleApplication app;
	private Node playerNode;
	private Geometry playerGeometry;
	private Map gameMap;
	
	// Player stats
	private Vector3f position;
	private Vector3f velocity;
	private float moveSpeed = 10f;
	private float jumpSpeed = 15f;
	private float gravity = -30f;
	private float playerHeight = 2f;
	private float playerWidth = 1f;
	
	// Camera control
	private float horizontalAngle = 0f; // Horizontal rotation (yaw)
	private float verticalAngle = 0f; // Vertical rotation (pitch)
	private float mouseSensitivity = 2f;
	private float maxLookUp = 25f * FastMath.DEG_TO_RAD;
	private float maxLookDown = -25f * FastMath.DEG_TO_RAD;
	
	// Player state
	private boolean isOnGround = false;
	private boolean[] moveStates = new boolean[4]; // Z, Q, S, D || W, A, S, D
	
	public Player(SimpleApplication app, Map gameMap) {
		this.app = app;
		this.gameMap = gameMap;
		this.playerNode = new Node("Player Node");
		this.velocity = new Vector3f(0, 0, 0);
		
		// Spawn position
		this.position = gameMap.getPlayerSpawnPosition().clone();
		
		createPlayerModel();
		setupInputs();
		
		// Hide cursor for a better control
		app.getInputManager().setCursorVisible(false);
	}
	
	/*
	 * Create visual model for player (simple box for now)
	 */
	private void createPlayerModel() {
		Box playerBox = new Box(playerWidth / 2, playerHeight / 2, playerWidth / 2);
		playerGeometry = new Geometry("Player", playerBox);
		
		Material playerMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		playerMaterial.setColor("Color", ColorRGBA.Red);
		playerGeometry.setMaterial(playerMaterial);
		
		playerNode.attachChild(playerGeometry);
		app.getRootNode().attachChild(playerNode);
		
		updatePlayerPosition();
	}
	
	/*
	 * Setup player inputs
	 */
	private void setupInputs() {
		InputManager inputManager = app.getInputManager();
		
		// Keys mapping
		inputManager.addMapping("Move Forward", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Move Backward", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Strafe Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Strafe Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
		
		// Mouse controls
		inputManager.addMapping("Mouse X-", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("Mouse X", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("Mouse Y", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addMapping("Mouse Y-", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		
		
		// Listeners
		inputManager.addListener(this, "Move Forward", "Move Backward", "Strafe Left", "Strafe Right");
		inputManager.addListener(this, "Jump");
		inputManager.addListener(this, "Mouse X", "Mouse X-", "Mouse Y", "Mouse Y-");
	}
	
	/* 
	 * Update player logic
	 */
	public void update(float tpf) {
		// Calculate directions of movement based on the angles of the camera
		Vector3f forward = getCameraDirection();
		Vector3f right = forward.cross(Vector3f.UNIT_Y).normalize();
		
		// Apply horizontal movement
		Vector3f movement = new Vector3f(0, 0, 0);
		
		if (moveStates[0]) movement.addLocal(forward.mult(moveSpeed * tpf));
		if (moveStates[1]) movement.addLocal(right.mult(-moveSpeed * tpf));
		if (moveStates[2]) movement.addLocal(forward.mult(-moveSpeed * tpf));
		if (moveStates[3]) movement.addLocal(right.mult(moveSpeed * tpf));
		
		// Apply gravity
		velocity.y += gravity * tpf;
		
		// Calculate new position
		Vector3f newPosition = position.add(movement).add(velocity.mult(tpf));
		// Verify collisions with map
		if (gameMap.isPositionValid(newPosition, playerWidth)) {
			position.x = newPosition.x;
			position.z = newPosition.z;
		}
		
		// Collisions with ground
		float groundHeight = gameMap.getGroundHeight() + playerHeight / 2;
		if (newPosition.y <= groundHeight) {
			position.y = groundHeight;
			velocity.y = 0;
			isOnGround = true;
		} else {
			position.y = newPosition.y;
			isOnGround = false;
		}
		
		updatePlayerPosition();
		
		// update camera to follow the player
		updateCamera();
 	}
	
	private Vector3f getCameraDirection() {
		Quaternion yawRotation = new Quaternion();
		yawRotation.fromAngleAxis(horizontalAngle, Vector3f.UNIT_Y);
		
		Vector3f forward = yawRotation.mult(Vector3f.UNIT_Z.negate());
		return forward.normalize();
	}
	
	/*
	 * Update model player position 
	 */
	private void updatePlayerPosition() {
		playerNode.setLocalTranslation(position);
	}
	
	/*
	 * Update camera to follow the player
	 */
	private void updateCamera() {
		
		// Create rotations for camera
		Quaternion yawRotation = new Quaternion();
		Quaternion pitchRotation = new Quaternion();
		
		yawRotation.fromAngleAxis(horizontalAngle, Vector3f.UNIT_Y);
		pitchRotation.fromAngleAxis(verticalAngle, Vector3f.UNIT_X);
		
		// Combine rotations
		Quaternion cameraRotation = yawRotation.mult(pitchRotation);
		
		// Position the camera behind the player
		Vector3f cameraOffset = cameraRotation.mult(new Vector3f(0, 2.5f, 5f));
		Vector3f cameraPosition = position.add(cameraOffset);
		
		// Direction of the view
		Vector3f lookDirection = cameraRotation.mult(Vector3f.UNIT_Z.negate());
		Vector3f lookAt = position.add(lookDirection);
		
		// Apply to the camera
		app.getCamera().setLocation(cameraPosition);
		app.getCamera().lookAt(lookAt, Vector3f.UNIT_Y);
	}
	
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
		case "Move Forward":
			moveStates[0] = isPressed;
			break;
		case "Move Backward":
			moveStates[2] = isPressed;
			break;
		case "Strafe Left":
			moveStates[1] = isPressed;
			break;
		case "Strafe Right":
			moveStates[3] = isPressed;
			break;
		case "Jump":
			if (isPressed && isOnGround) {
				velocity.y = jumpSpeed;
				isOnGround = false;
			}
			break;	
		}
	}
	
	@Override 
	public void onAnalog(String name, float value, float tpf) {
		// Mouse controls
		switch (name) {
		case "Mouse X": 
			horizontalAngle += value * mouseSensitivity;
			break;
		case "Mouse X-": 
			horizontalAngle -= value * mouseSensitivity;
			break;
		case "Mouse Y": 
			verticalAngle += value * mouseSensitivity;
			if (verticalAngle > maxLookUp) {
				verticalAngle = maxLookUp;
			}
			break;
		case "Mouse Y-": 
			verticalAngle -= value * mouseSensitivity;
			if (verticalAngle < maxLookDown) {
				verticalAngle = maxLookDown;
			}
			break;
		}
	}
	
	/*
	 * Cleanup player resources
	 */
	public void cleanup() {
		app.getRootNode().detachChild(playerNode);
		
		InputManager inputManager = app.getInputManager();
		inputManager.deleteMapping("Move Forward");
		inputManager.deleteMapping("Move Backward");
		inputManager.deleteMapping("Strafe Left");
		inputManager.deleteMapping("Strafe Right");
		inputManager.deleteMapping("Jump");
		inputManager.deleteMapping("Mouse X");
		inputManager.deleteMapping("Mouse X-");
		inputManager.deleteMapping("Mouse Y");
		inputManager.deleteMapping("Mouse Y-");
		inputManager.removeListener(this);
	}
	
	/*
	 * Return player position
	 */
	public Vector3f getPosition() {
		return position.clone();
	}
	
	/* 
	 * Set a new position
	 */
	public void setPosition(Vector3f newPosition) {
		this.position = newPosition.clone();
		updatePlayerPosition();
	}
	
	public float getHorizontalAngle() {
		return horizontalAngle;
	}
	
	public float getVerticalAngle() {
		return verticalAngle;
	}
}
