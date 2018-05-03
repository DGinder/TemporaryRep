package temp;

import java.io.IOException;
import ray.networking.IGameConnection.ProtocolType;

public class NetworkingServer {
	private GameServerUDP thisUDPServer;
	
	private NPCcontroller npcCtrl;
	
	private long startTime;
	private long lastUpdateTime;
	
	public NetworkingServer(int serverPort, String protocol){ 
		
		startTime = System.nanoTime();
		lastUpdateTime = startTime;
		npcCtrl = new NPCcontroller();
		try{ 
			System.out.println("Server Start up");
			thisUDPServer = new GameServerUDP(serverPort, npcCtrl);
		}
		catch (IOException e){ 
			e.printStackTrace();
		}
		 //. . .
		 // start networking TCP server (as before)
		 //. . .
		 // start NPC control loop
		 npcCtrl.setupNPCs();
		 npcLoop();
	}
	
	public void npcLoop(){
		while (true){ 
			long frameStartTime = System.nanoTime();
			float elapMilSecs = (frameStartTime-lastUpdateTime)/(1000000.0f);
			if (elapMilSecs >= 50.0f){ 
				lastUpdateTime = frameStartTime;
				npcCtrl.updateNPCs();
				thisUDPServer.sendNPCinfo();
			}
			Thread.yield();
		}
	}
	
	public static void main(String[] args){ 
		if(args.length > 1){ 
			NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
		} 
	} 
	
	
}
