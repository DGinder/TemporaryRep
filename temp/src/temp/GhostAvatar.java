package temp;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class GhostAvatar {
	private UUID id;
	private SceneNode node;
	private Entity entity;
	private Vector3 pos;
	
	public GhostAvatar(UUID id, Vector3 position){ 
		this.id = id;
		pos = position;
		
	}
	// accessors and setters for id, node, entity, and position

	public void setNode(SceneNode ghostN) {
		node = ghostN;
	}

	public void setEntity(Entity ghostE) {
		entity = ghostE;
	}


	public void setPosition(Vector3 p) {
		pos.add(p);
		
	}

	public UUID getID() {
		return id;
	}
	
	public Vector3 getPos() {
		return pos;
	}
	
	public SceneNode getNode() {
		return node;
	}
	
	public Entity getEntity() {
		return entity;
	}

	public void setInitialPosition(float f, float g, float h) {
		// TODO Auto-generated method stub
		pos.add(f, g, h);
	}
	
}
