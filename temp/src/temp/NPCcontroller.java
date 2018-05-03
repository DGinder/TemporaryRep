package temp;

import ray.rml.Vector3;
import ray.rml.Vector3f;

public class NPCcontroller{ 
	private NPC[] NPClist = new NPC[5];
	private int numNPCs;
	//. . .
	public void updateNPCs(){ 
		numNPCs = 0;
		setnumNPCs();
		for (int i=0; i<numNPCs; i++){
			NPClist[i].updateLocation();
		} 
	}
	// . . .
	protected void setnumNPCs() {
		int i = 0;
		while(i < 5 && NPClist[i] != null) {
			numNPCs++;
			i++;
		}
	}
	protected int getNumOfNPCs() {
		// TODO Auto-generated method stub
		numNPCs = 0;
		setnumNPCs();
		return numNPCs;
	}
	public NPC getNPC(int i) {
		// TODO Auto-generated method stub
		return NPClist[i];
	}
	public void setupNPCs() {
		// TODO Auto-generated method stub
		for(int i = 0; i < 5; i++) {
			NPC temp = new NPC();
			NPClist[i] = temp;
		}
	}
	public Vector3f getLoc(int id) {
		// TODO Auto-generated method stub
		int tempid = id;
		float tempX = (float) NPClist[tempid].getX();
		float tempY = (float) NPClist[tempid].getY();
		float tempZ = (float) NPClist[tempid].getZ();

		Vector3f tempPos = (Vector3f) Vector3f.createFrom(tempX, tempY, tempZ);
		return tempPos;
	}
}
