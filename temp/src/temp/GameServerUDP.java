package temp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;
import ray.rml.Vector3f;


public class GameServerUDP extends GameConnectionServer<UUID>{
	private NPCcontroller npcCtrl;
	public GameServerUDP(int localPort, NPCcontroller n) throws IOException{ 
		super(localPort, ProtocolType.UDP); 
		npcCtrl = n;
		
	}
	@Override
	 public void processPacket(Object o, InetAddress senderIP, int sndPort){
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if(msgTokens.length > 0){
			
			// case where server receives a JOIN message
			// format: join,localid
			if(msgTokens[0].compareTo("join") == 0){ 
				try{ 
					IClientInfo ci;
					ci = getServerSocket().createClientInfo(senderIP, sndPort);
					UUID clientID = UUID.fromString(msgTokens[1]);
					addClient(ci, clientID);
					sendJoinedMessage(clientID, true);
				}
				catch (IOException e){
				e.printStackTrace();
				} 
			}
			
			// case where server receives a CREATE message
			// format: create,localid,x,y,z
			if(msgTokens[0].compareTo("create") == 0){ 
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
			}
			
			// case where server receives a BYE message
			// format: bye,localid
			if(msgTokens[0].compareTo("bye") == 0){ 
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
			}
			
			// case where server receives a DETAILS-FOR message
			if(msgTokens[0].compareTo("dsfr") == 0){
				UUID clientID = UUID.fromString(msgTokens[1]);
				UUID remoteID = UUID.fromString(msgTokens[2]);
				String[] pos = {msgTokens[3], msgTokens[4], msgTokens[5]};
				sndDetailsMsg(clientID, remoteID, pos);
			}
			
			// case where server receives a MOVE message
			if(msgTokens[0].compareTo("move") == 0){
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendMoveMessages(clientID, pos);
			}
			
			//--------------
			if(msgTokens[0].compareTo("needNPC") == 0){ 
				 //. . . 
				String id = msgTokens[1];
				sendNeedNPCMessages(id);
				
			 }
			 
			 if(msgTokens[0].compareTo("collide") == 0){ 
				 //. . . 
			 }
		}
	}
		private void sendNeedNPCMessages(String id) {
		// TODO Auto-generated method stub
			for(int i = 0; i < 5; i ++) {
			Vector3f loc = npcCtrl.getLoc(i);
			String message = new String("needNPC,");
			message += i + ",";
			message += loc.x() + ",";
			message += loc.y() + ",";
			message += loc.z();
			try {
				sendPacketToAll(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
	}
		public void sendJoinedMessage(UUID clientID, boolean success){
			// format: join, success or join, failure
			try{ 
				String message = new String("join,");
				if(success) 
					message += "success";
				else 
					message += "failure";
				sendPacket(message, clientID);
			}
		 catch (IOException e) { e.printStackTrace(); 
		 }
		}
		public void sendCreateMessages(UUID clientID, String[] position){ 
			// format: create, remoteId, x, y, z
			try{ 
				String message = new String("create," + clientID.toString());
				message += "," + position[0];
				message += "," + position[1];
				message += "," + position[2];
				forwardPacketToAll(message, clientID);
			}
			catch (IOException e) { 
				e.printStackTrace();
			} 
		}
		
		public void sndDetailsMsg(UUID clientID, UUID remoteId, String[] position){ 
			try{ //remoteid is the client being sent to and clientID is the ghost id to be made
				String message = new String("dsfr," + clientID.toString());
				message += "," + position[0];
				message += "," + position[1];
				message += "," + position[2];
				sendPacket(message, remoteId);
			}
			catch (IOException e) { 
				e.printStackTrace();
			} 
		}
		
		public void sendWantsDetailsMessages(UUID clientID){ 
			try{ 
				String message = new String("wsds," + clientID.toString());
				forwardPacketToAll(message, clientID);
			}
			catch (IOException e) { 
				e.printStackTrace();
			} 
		}
		public void sendMoveMessages(UUID clientID, String[] position){ 
			try{ 
				String message = new String("move," + clientID.toString());
				message += "," + position[0];
				message += "," + position[1];
				message += "," + position[2];
				forwardPacketToAll(message, clientID);
			}
			catch (IOException e) { 
				e.printStackTrace();
			} 
		}
		
		public void sendByeMessages(UUID clientID){ 
			try{ 
				String message = new String("bye," + clientID.toString());
				forwardPacketToAll(message, clientID);
			}
			catch (IOException e) { 
				e.printStackTrace();
			} 
		}
		
		public void sendNPCinfo(){ // informs clients of new NPC positions{ 
			 for (int i=0; i<npcCtrl.getNumOfNPCs(); i++){  
					 String message = new String("mnpc," + Integer.toString(i));
					 message += "," + (npcCtrl.getNPC(i)).getX();
					 message += "," + (npcCtrl.getNPC(i)).getY();
					 message += "," + (npcCtrl.getNPC(i)).getZ();
					 try {
						sendPacketToAll(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			 }
		
		}
}
