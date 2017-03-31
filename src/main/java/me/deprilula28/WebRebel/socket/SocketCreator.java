package me.deprilula28.WebRebel.socket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import me.deprilula28.WebRebel.connection.WebRebelConnection;


public class SocketCreator implements WebSocketCreator{
	
	@Override
	public Object createWebSocket(ServletUpgradeRequest request, ServletUpgradeResponse response){
		
		try{
			WebRebelConnection connection = new WebRebelConnection(request.getHeader("User-Agent"), request.getRemoteSocketAddress());
			WebRebelSocket socket = new WebRebelSocket(connection);
			
			response.setAcceptedSubProtocol("client");
			
			return socket;
		}catch(Exception e){
			System.err.println("Failed to create web socket");
			e.printStackTrace();
			return null;
		}
		
	}

}
