package temp;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import net.java.games.input.Event;

public class MoveLeftAction extends AbstractInputAction{
	private Node avN;
	private ProtocolClient protClient;
	
	
	public MoveLeftAction(SceneNode n, ProtocolClient p){
		avN = n;
		protClient = p;
	}
	public void performAction(float time, Event e){
		avN.moveLeft(0.05f);
		protClient.sendMoveMessage(avN.getWorldPosition());
	}
}
