package temp;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class GhostNPC {
	private int id;
	private SceneNode node;
	private Entity entity;
	private Vector3 pos;
	public GhostNPC(int id, Vector3 position){ // constructor 
		this.id = id;
		pos = position;
	}
	public void setPosition(Vector3 position){ 
		node.setLocalPosition(position);
	}
	public Vector3  getPosition(){ 
		return node.getLocalPosition();
	}
	public void setNode(SceneNode ghostNPC) {
		// TODO Auto-generated method stub
		node = ghostNPC;
	}
	public void setEntity(Entity ghostNPCE) {
		// TODO Auto-generated method stub
		entity = ghostNPCE;
	}
	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}
}
