package temp;

import java.awt.*;
import java.io.*;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rml.Vector3;
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
	private SceneNode dolphinN2;
	private Action moveFwdAct;
	
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected;
	private Vector<UUID> gameObjectsToRemove;
	
	public MyGame(String serverAddr, int sPort){
		super();
		
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.TCP;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Game game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]);
        
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
		 processNetworking(elapsTime)
		 im.update(elapsTime);
	 }
	 
	 private void setupNetworking(){ 
		 gameObjectsToRemove = new Vector<UUID>();
		 isClientConnected = false;
		 try{ 
			 protClient = new ProtocolClient(InetAddress.
					 getByName(serverAddress), serverPort, serverProtocol, this);
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
			 sm.destroySceneNode(it.next().toString());
		 }
		 gameObjectsToRemove.clear();
	 }
	 
	 public Vector3 getPlayerPosition(){ 
		 SceneNode dolphinN = sm.getSceneNode("dolphinNode");
		 return dolphinN.getWorldPosition();
	 }
	 
	 public void addGhostAvatarToGameWorld(GhostAvatar avatar)
			 throws IOException{ 
		 if (avatar != null){ 
			 Entity ghostE = sm.createEntity("ghost", "whatever.obj");
			 ghostE.setPrimitive(Primitive.TRIANGLES);
			 SceneNode ghostN = sm.getRootSceneNode().
			 createChildSceneNode(avatar.getID().toString());
			 ghostN.attachObject(ghostE);
			 //ghostN.setLocalPosition(desired location...);
			 avatar.setNode(ghostN);
			 avatar.setEntity(ghostE);
			 //avatar.setPosition(node’s position... maybe redundant);
		 } 
	 }
	 
	 public void removeGhostAvatarFromGameWorld(GhostAvatar avatar){ 
		 if(avatar != null) 
			 gameObjectsToRemove.add(avatar.getID());
	 }
	 
	 private class SendCloseConnectionPacketAction extends AbstractInputAction{ 
		 // for leaving the game... need to attach to an input device
		 @Override
		 public void performAction(float time, Event evt){ 
			 if(protClient != null && isClientConnected == true){ 
				 protClient.sendByeMessage();
			 } 
		 } 
	 }
	 
	 
	 
}
