package temp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;



import ray.networking.client.GameConnectionClient;
import ray.rml.Vector3;
import ray.rml.Vector3f;



public class ProtocolClient extends GameConnectionClient{
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	
	public ProtocolClient(InetAddress remAddr, int remPort, ProtocolType pType, MyGame game) throws IOException{ 
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}
	
	@Override
	protected void processPacket(Object msg){ 
		String strMessage = (String)msg;
		String[] messageTokens = strMessage.split(",");
	
		if(messageTokens.length > 0){
			if(messageTokens[0].compareTo("join") == 0){ // receive “join” 
				// format: join, success or join, failure
				if(messageTokens[1].compareTo("success") == 0){ 
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition());
				}
				if(messageTokens[1].compareTo("failure") == 0){ 
					game.setIsConnected(false);
				} 
			}
		
			if(messageTokens[0].compareTo("bye") == 0){ // receive “bye” 
				// format: bye, remoteId
				UUID ghostID = UUID.fromString(messageTokens[1]);
				removeGhostAvatar(ghostID);
			}
			if ((messageTokens[0].compareTo("dsfr") == 0 ) || (messageTokens[0].compareTo("create") == 0)){ // receive “dsfr”{ 
				// format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), Float.parseFloat(messageTokens[3]), Float.parseFloat(messageTokens[4]));
				createGhostAvatar(ghostID, ghostPosition); 
			}
//---------------------check later unsure			
			if(messageTokens[0].compareTo("wsds") == 0){ // rec. “wants…” 
				// etc….. 
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), Float.parseFloat(messageTokens[3]), Float.parseFloat(messageTokens[4]));
				sendDetailsForMessage(ghostID, ghostPosition);
			}
			if(messageTokens[0].compareTo("move") == 0){ // rec. “move...”
				// etc….. 
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), Float.parseFloat(messageTokens[3]), Float.parseFloat(messageTokens[4]));
				game.updateGhost(ghostPosition);
				sendMoveMessage(ghostPosition);
			} 
		}
	}
	
	public void sendJoinMessage() { // format: join, localId
		try{ 
			sendPacket(new String("join," + id.toString()));
		} 
		catch (IOException e) { 
			e.printStackTrace();
		} 
		}
	
	public void sendCreateMessage(Vector3 pos){ 
		// format: (create, localId, x,y,z)
		try{ 
			String message = new String("create," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
	}
	public void sendByeMessage(){ 
		// etc…..
		try{ 
			String message = new String("bye," + id.toString());
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
	}
	public void sendDetailsForMessage(UUID remId, Vector3 pos){ 
		// etc…..
		try{ 
			String message = new String("wsds," + id.toString() + remId.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(Vector3 pos){ 		
		// etc….. 
		try{ 
			String message = new String("move," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		}
	}
	//--------------check later
	private void createGhostAvatar(UUID ghostID, Vector3 ghostPosition){
		GhostAvatar avat = new GhostAvatar(ghostID, ghostPosition);
		ghostAvatars.add(avat);
		
		try {
			game.addGhostAvatarToGameWorld(avat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void removeGhostAvatar(UUID ghostID){
		int i = 0;
		while(ghostAvatars.elementAt(i) != null){
			if(ghostAvatars.elementAt(i).getID() == ghostID){
				ghostAvatars.remove(i);
			}
		}
	}
	
}
	
