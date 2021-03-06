package temp;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import temp.Camera3Pcontroller;
import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import temp.MoveForwardAction;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.input.*;
import ray.input.action.*;

import ray.networking.IGameConnection.ProtocolType;


public class MyGame extends ray.rage.game.VariableFrameRateGame{

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	private InputManager im =  new GenericInputManager();
	private Camera camera;
	private SceneNode dolphinN;
	private Action moveFwdAct, moveBwdAct, moveLftAct, moveRgtAct,  quit;
	
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected;
	private Vector<UUID> gameObjectsToRemove;
	private boolean isConnected;
	private SceneManager sman;
	
	private SceneNode cameraNode;
	private Camera3Pcontroller orbitController;
	
	private int tempn = 0;
	
	public MyGame(String serverAddr, int sPort){
		super();
		
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.UDP;
		isConnected = true;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Game game = new MyGame(args[0], Integer.parseInt(args[1]));
        
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
    	/*SceneNode rootNode = sm.getRootSceneNode();
    	Camera camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
    	rw.getViewport(0).setCamera(camera);
    	SceneNode cameraN = rootNode.createChildSceneNode("MainCameraNode");
    	cameraN.attachObject(camera);
    	cameraN.setLocalPosition(0f, 0f, -5f);
    	camera.setMode('n');
    	camera.getFrustum().setFarClipDistance(1000.0f);*/
    	
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
		  	sman=sm;
	        Entity dolphinE = sm.createEntity("myDolphin", "dolphinHighPoly.obj");
	        dolphinE.setPrimitive(Primitive.TRIANGLES);

	        dolphinN = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
	        dolphinN.moveBackward(1.0f);
	        dolphinN.attachObject(dolphinE);
	        
	        
	        
	        setupNetworking();
	        setupInputs();
	        setupOrbitCamera(eng, sm);
	        
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
			
			moveFwdAct = new MoveForwardAction(dolphinN, protClient);
			moveBwdAct = new MoveForwardAction(dolphinN, protClient);
			moveLftAct = new MoveForwardAction(dolphinN, protClient);
			moveRgtAct = new MoveForwardAction(dolphinN, protClient);
			quit = new SendCloseConnectionPacketAction();
			
			im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveFwdAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBwdAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveLftAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveRgtAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

			im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.P, quit , InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, moveFwdAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._0, moveBwdAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._2, moveLftAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._1, moveRgtAct , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._9, quit , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

	    }
	 protected void update(Engine engine) {
		 processNetworking(elapsTime);
		 orbitController.updateCameraPosition();
		 im.update(elapsTime);
	 }
	 
	 private void setupNetworking(){ 
		 gameObjectsToRemove = new Vector<UUID>();
		 isClientConnected = false;
		 try{ 
			 protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		 } 
		 catch (UnknownHostException e) { 
			 e.printStackTrace();
		 } 
		 catch (IOException e) { 
			 e.printStackTrace();
		 }
		 if (protClient == null){ 
			 System.out.println("missing protocol host"); 
			 }
		 else{ 
			 // ask client protocol to send initial join message
			 //to server, with a unique identifier for this client
			 protClient.sendJoinMessage();
		 } 
	 }
	 
	 protected void processNetworking(float elapsTime){ 
		 // Process packets received by the client from the server
		 if (protClient != null)
			 protClient.processPackets();
		 // remove ghost avatars for players who have left the game
		 Iterator<UUID> it = gameObjectsToRemove.iterator();
		 while(it.hasNext()){ 
			 sman.destroySceneNode(it.next().toString());
		 }
		 gameObjectsToRemove.clear();
		
	 
	 }
	 
	 public Vector3 getPlayerPosition(){ 
		 SceneNode dolphinN = sman.getSceneNode("myDolphinNode");
		 return dolphinN.getWorldPosition();
	 }
	 
	 
	 public void addGhostAvatarToGameWorld(GhostAvatar avatar,Vector3 ghostPosition) throws IOException{ 
		 if (avatar != null){ 
			 Entity ghostE = sman.createEntity("ghost", "cone.obj");
			 ghostE.setPrimitive(Primitive.TRIANGLES);
			 SceneNode ghostN = sman.getRootSceneNode().createChildSceneNode(avatar.getID().toString());
			 ghostN.attachObject(ghostE);
			 ghostN.setLocalPosition(ghostPosition);
			 avatar.setNode(ghostN);
			 avatar.setEntity(ghostE);
			 avatar.setPosition(ghostPosition);
			 
		 } 
	 }
	 
	 //still need to call
	 public void removeGhostAvatarFromGameWorld(GhostAvatar avatar){ 
		 if(avatar != null) 
			 gameObjectsToRemove.add(avatar.getID());
	 }
	 
	 public void updateGhost(UUID ghostID, Vector3 p){
		 //SceneNode avatar = sman.getSceneNode(ghostID.toString());
		 SceneNode avatar = this.getEngine().getSceneManager().getSceneNode(ghostID.toString());
		 avatar.setLocalPosition(p);
	 }
	 
	 private class SendCloseConnectionPacketAction extends AbstractInputAction{ 
		 // for leaving the game... need to attach to an input device
		@Override
		public void performAction(float arg0, net.java.games.input.Event arg1) {
			// TODO Auto-generated method stub
			if(protClient != null && isClientConnected == true){ 
				 protClient.sendByeMessage();
				 shutdown();
				 exit();
			 }
		} 
	 }

	protected void setIsConnected(boolean b) {
		// TODO Auto-generated method stub
		isConnected = b;
	}
	
	protected Vector3 getdolpos() {
		return dolphinN.getLocalPosition();
	}
	
	protected void setupOrbitCamera(Engine eng, SceneManager sm){ 
    	dolphinN = sm.getSceneNode("myDolphinNode");
    	SceneNode cameraN = sm.getSceneNode("MainCameraNode");
    	Camera camera = sm.getCamera("MainCamera");
    	String gpName = im.getFirstGamepadName();
    	orbitController = new Camera3Pcontroller(camera, cameraN, dolphinN, gpName, im);
	}
	public void addGhostNPCtoGameWorld(GhostNPC newNPC) throws IOException {
		// TODO Auto-generated method stub
		 if (newNPC != null){ 
			 
			 Entity ghostNPCE = sman.createEntity("NPC"+tempn, "earth.obj");
			 ghostNPCE.setPrimitive(Primitive.TRIANGLES);
			 SceneNode ghostNPC = sman.getRootSceneNode().createChildSceneNode(Integer.toString(newNPC.getID()));
			 ghostNPC.attachObject(ghostNPCE);
			 newNPC.setNode(ghostNPC);
			 newNPC.setEntity(ghostNPCE);
			 newNPC.setPosition(newNPC.getPosition());
			 tempn++;
		 }
	}
		
	 
}
