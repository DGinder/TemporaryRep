package temp;

import java.awt.*;
import java.io.*;
import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import temp.MoveForwardAction;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.input.*;
import ray.input.action.*;

public class MyGame extends ray.rage.game.VariableFrameRateGame{

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	private InputManager im =  new GenericInputManager();
	private Camera camera;
	private SceneNode dolphinN;
	private SceneNode dolphinN2;
	private Action moveFwdAct;
	
	public MyGame(){
		super();
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Game game = new MyGame();
        
		try {
            game.startup();
            game.run();
        } 
		catch (Exception e) {
            e.printStackTrace(System.err);
        } 
		finally {
			game.shutdown();
			game.exit();
        }
	}
	
	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
	}
	
	@Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) {
    	SceneNode rootNode = sm.getRootSceneNode();
    	Camera camera = sm.createCamera("MainCamera",
    	Projection.PERSPECTIVE);
    	rw.getViewport(0).setCamera(camera);
    	SceneNode cameraN =
    	rootNode.createChildSceneNode("MainCameraNode");
    	cameraN.attachObject(camera);
    	camera.setMode('n');
    	camera.getFrustum().setFarClipDistance(1000.0f);

    }
	 @Override
	    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
	        Entity dolphinE = sm.createEntity("myDolphin", "dolphinHighPoly.obj");
	        dolphinE.setPrimitive(Primitive.TRIANGLES);

	        dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
	        dolphinN.moveBackward(1.0f);
	        dolphinN.attachObject(dolphinE);
	        
	        
	        setupInputs();
	        
	        sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
			
			Light plight = sm.createLight("testLamp1", Light.Type.POINT);
			plight.setAmbient(new Color(.3f, .3f, .3f));
	        plight.setDiffuse(new Color(.7f, .7f, .7f));
			plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
	        plight.setRange(5f);
			
			SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
	        plightNode.attachObject(plight);
	    }
	 protected void setupInputs(){ 
			String kbName = im.getKeyboardName();
			String gpName = im.getFirstGamepadName();
			
			SceneNode dolphinN = getEngine().getSceneManager().getSceneNode("myDolphinNode");
			
			moveFwdAct = new MoveForwardAction(dolphinN);

			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, moveFwdAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			

	    }
	 protected void update(Engine engine) {
		 im.update(elapsTime);
	 }
	
}
