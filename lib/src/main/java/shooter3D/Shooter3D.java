package shooter3D;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.material.RenderState;

/* 
//Autres formes
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;

//Animation
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;

//Physique
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;

//Audio
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData;

//Éclairage
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;

//Effets
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
*/

public class Shooter3D extends SimpleApplication implements ActionListener {
	
	// Game states
	private enum GameState {
		MENU,
		GAME
	}
	
	private GameState currentState = GameState.MENU;
	
	// Nodes to organise the scene
	private Node menuNode;
	private Node gameNode;
	
	// Menu elements
	private BitmapText titleText;
	private BitmapText playButtonText;
	private Geometry playButton;
	
	public static void main(String[] args) {
		Shooter3D app = new Shooter3D();
		
		// ==================== //
		// SCREEN CONFIGURATION //
		// ==================== //
		AppSettings settings = new AppSettings(true);
		settings.setTitle("Shooter 3D In Java"); 
		settings.setResolution(1200, 1024);
		settings.setFullscreen(false);
		settings.setVSync(true); // Vertical synchro
		settings.setSamples(4); // Anti-aliasing (0, 2, 4, 8, 16)
		settings.setFrameRate(60); // Max FPS
		
		app.setSettings(settings);
		app.setShowSettings(false); // Hide configuration screen at start
		app.start();	
	}
	
	@Override
	public void simpleInitApp() {
		menuNode = new Node("Menu");
		gameNode = new Node("Game");
		
		// Attach node to the root
		rootNode.attachChild(menuNode);
		rootNode.attachChild(gameNode);
		
		// controls configuration
		setupControls();
		
		showMenu();
	}
	
	/* 
	 * Configure mouse controls
	 */
	private void setupControls() {
		inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(this, "Click");
	}
	
	
	private void showMenu() {
		currentState = GameState.MENU;
		
		// Empty nodes
		menuNode.detachAllChildren();
		gameNode.detachAllChildren();
		
		// unable free cam for the menu
		flyCam.setEnabled(false);
		
		// Position the camera 
		cam.setLocation(new Vector3f(0,0 , 10));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		// Create title
		BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
		
		titleText = new BitmapText(font, false);
		titleText.setSize(60);
		titleText.setText("SHOOTER 3D");
		titleText.setColor(ColorRGBA.White);
		// Center title
		titleText.setLocalTranslation(
			settings.getWidth() / 2 - titleText.getLineWidth() / 2,
			settings.getHeight() / 2 + 100,
			0
		);
		guiNode.attachChild(titleText);
		
		createPlayButton();
	}
	
	private void createPlayButton() {
		// Geometry of the button (rectangle)
		Box buttonShape = new Box(100, 30, 1);
		playButton = new Geometry("PlayButton", buttonShape);
		
		// Material of the button
		Material buttonMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		buttonMat.setColor("Color", ColorRGBA.Green);
		
		playButton.setMaterial(buttonMat);
		
		// Position the button in 3D
		playButton.setLocalTranslation(0, -2, 0);
		menuNode.attachChild(playButton);
		
		// Text
		BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
		playButtonText = new BitmapText(font, false);
		playButtonText.setSize(24);
		playButtonText.setText("START GAME");
		playButtonText.setColor(ColorRGBA.White);
		
		// Center
		playButtonText.setLocalTranslation(
			settings.getWidth() / 2 - playButtonText.getLineWidth() / 2,
			settings.getHeight() / 2 - 50,
			1
		);
		guiNode.attachChild(playButtonText);		
	}
	
	// Start Game
	private void startGame() {
		currentState = GameState.GAME;
		
		// Clear Menu
		guiNode.detachAllChildren();
		menuNode.detachAllChildren();
		
		// Activate flying cam
		flyCam.setEnabled(true);
		flyCam.setMoveSpeed(10);
		
		// Reset cam position
		cam.setLocation(new Vector3f(0, 0, 10));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		createGameScene();
	}
	
	// Game Scene
	private void createGameScene() {
		Box box = new Box(1, 1, 1);
		Geometry geom = new Geometry("Cube", box);
		
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		
		gameNode.attachChild(geom);
		
		System.out.println("Game started ! USE WASD + mouse to move");
	}
	
	/*
	 * Manage mouse actions
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals("Click") && isPressed) {
			if (currentState == GameState.MENU) {
				checkMenuClick();
			}
		}
	}
	
	/*
	 * Check menu clicks 
	 */
	private void checkMenuClick() {
		// Get mouse position
		Vector2f click2d = inputManager.getCursorPosition().clone();
		
		// Convert into 3D coordonates
		Vector3f click3d = cam.getWorldCoordinates(click2d, 0f);
		Vector3f dir = cam.getWorldCoordinates(click2d, 1f).subtractLocal(click3d);
		
		// Create ray from cam
		Ray ray = new Ray(click3d, dir);
		
		// Test collision with button
		CollisionResults results = new CollisionResults();
		playButton.collideWith(ray, results);
		
		if (results.size() > 0) {
			// Button got clicked
			startGame();
		}
	}
	
	/*
	 * Method called every frame
	 * Add game logic here
	 */
	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		
		if (currentState == GameState.GAME) {
			// game logic
			// example : collisions, ennemies... 
		}
	}
}







