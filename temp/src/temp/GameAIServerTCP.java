package temp;

import java.io.IOException;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;

public class GameAIServerTCP extends GameConnectionServer<UUID>{

	public GameAIServerTCP(int localPort, ProtocolType protocolType) throws IOException {
		super(localPort, protocolType);
		// TODO Auto-generated constructor stub
	}
	
	 public void sendNPCinfo(){ // informs clients of new NPC positions{ 
		 for (int i=0; i<npcCtrl.getNumOfNPCs(); i++){  
				 String message = new String("mnpc," + Integer.toString(i));
				 message += "," + (npcCtrl.getNPC(i)).getX();
				 message += "," + (npcCtrl.getNPC(i)).getY();
				 message += "," + (npcCtrl.getNPC(i)).getZ();
				 sendPacketToAll(message);
				 //. . .
				 // also additional cases for receiving messages about NPCs, such as:
				 if(messageTokens[0].compareTo("needNPC") == 0){ 
					 //. . . 
				 }
				 
				 if(messageTokens[0].compareTo("collide") == 0){ 
					 //. . . 
				 }
		 }
	 }
}
