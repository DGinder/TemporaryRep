package temp;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class GhostNPC {
	private int id;
	private SceneNode node;
	private Entity entity;
	public GhostNPC(int id, Vector3 position){ // constructor 
		this.id = id;
	}
	public void setPosition(Vector3 position){ 
		node.setLocatlPosition(position);
	}
	public void getPosition(Vector3 position){ 
		node.setLocatlPosition(position);
	}
}
