package me.deprilula28.WebRebel.socket;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.json.JSONException;
import org.json.JSON;

import me.deprilula28.WebRebel.ActionType;
import me.deprilula28.WebRebel.Errors;
import me.deprilula28.WebRebel.WebRebel;
import me.deprilula28.WebRebel.connection.Action;
import me.deprilula28.WebRebel.connection.WebRebelConnection;

public class WebRebelSocket implements WebSocketListener, Runnable{

	private ScheduledExecutorService executorService;
	private WebRebelConnection connection;
	private RemoteEndpoint remoteEndpoint;
	private Session session;
	private long shakeRequestTime;
	private boolean shakedHands;
	private long lastPing;
	private int ping;
	private boolean pending;
	
	public WebRebelSocket(WebRebelConnection connection){

		executorService = Executors.newScheduledThreadPool(1);
		this.connection = connection;
		
	}
	
	public void sendAction(Action action){
		
		sendResponse(new JSON().put("action", action.getActionType().toString()).put("id", action.getUUID().toString()).put("info", action.getJSONData()));
		
	}
	
	public void sendResponse(JSON json){
		
		try{
			remoteEndpoint.sendString(json.toString());
		}catch(Exception e){
			System.err.println("Failed to write response");
			e.printStackTrace();
			
			try{
				session.close();
			}catch(Exception e1){
				System.err.println("Failed to close socket");
				e1.printStackTrace();
			}
		}
			
	}
		
	public WebRebelConnection getConnection(){
	
		return connection;
	
	}

	@Override
	public void onWebSocketClose(int code, String message){

		executorService.shutdown();
		System.out.println("Disconnected: " + connection.getUserAgentParser().getBrowser() + ", " + connection.getUserAgentParser().getOperatingSystem() + ", " + connection.getIp());
		WebRebel.REBEL.getConnections().remove(this);
		reloadTree();
		
	}

	@Override
	public void onWebSocketConnect(Session session){
		
		connection.getUserAgentParser().runParser();
		System.out.println("Connected: " + connection.getUserAgentParser().getBrowser() + ", " + connection.getUserAgentParser().getOperatingSystem() + ", " + connection.getIp());
		this.session = session;
		remoteEndpoint = session.getRemote();
		WebRebel.REBEL.getConnections().add(this);
		reloadTree();
		sendAction(new Action(ActionType.SERVER_HANDSHAKE, UUID.randomUUID(), new JSON()));
		
	}

	@Override
	public void onWebSocketError(Throwable throwable){
		
		System.err.println("Web socket error");
		throwable.printStackTrace();
		try{
			session.close();
		}catch(Exception e){}
		
	}

	@Override
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2){
	}

	@Override
	public void onWebSocketText(String message){
		
		JSON json = null;
		
		try{
			json = new JSON(message);
			ActionType action = ActionType.valueOf(json.getString("action"));
			
			if(!shakedHands && !action.equals(ActionType.CLIENT_HANDSHAKE)){
				sendAction(new Action(ActionType.SERVER_ERROR_RESPONSE, UUID.fromString(json.getString("id")), new JSON().put("error", Errors.INVALID_INPUT)
						.put("errorMessage", "Handshake must be completed first!")));
				return;
			}
			
			switch(action){
			case CLIENT_ERROR:
				JSON error = json.getJSONObject("info");
				String errorTag = error.getString("error");
				String errorMessage = error.getString("message");
				
				System.err.println("Client returned error (" + connection.getIp() + ")");
				System.err.println(errorTag + ": " + errorMessage);
				break;
			case CLIENT_REQUEST_CODE_EDIT_PERM:
				if(connection.isCodeEditingPermissable()) throw new IOException("Already permissable!");
				break;
			case CLIENT_CODE_EDIT:
				if(!connection.isCodeEditingPermissable()) throw new IOException("Access denied.");
				break;
			case CLIENT_CONSOLE_LOG:
				String consoleLog = json.getJSONObject("info").getString("message");
				System.out.println("[" + connection.getUserAgentParser().getOperatingSystem() + " " + connection.getUserAgentParser().getBrowser() + " " + 
						json.getJSONObject("info").getString("type").toUpperCase() + "] " + consoleLog);
				break;
			case CLIENT_HANDSHAKE:
				if(shakedHands) throw new IOException("Already handshaked!");
				shakedHands = true;
				
				shakeRequestTime = -1;
				ping = (int) (System.currentTimeMillis() - shakeRequestTime);
				executorService.scheduleAtFixedRate(this, 0l, 5l, TimeUnit.SECONDS);
				reloadTree();
				break;
			case CLIENT_PONG:
				if(lastPing < 0) throw new IOException("I never pinged you!");
				ping = (int) (System.currentTimeMillis() - lastPing);
				lastPing = -1;
				pending = false;
				reloadTree();
				break;
			default:
				throw new IOException("Invalid request type");
			}
		}catch(JSONException e){
			System.err.println("Invalid message received");
			e.printStackTrace();
		}catch(Exception e){
			sendAction(new Action(ActionType.SERVER_ERROR_RESPONSE, UUID.fromString(json.getString("id")), new JSON().put("error", Errors.INVALID_INPUT)
					.put("errorMessage", e.getMessage())));
			System.err.println("Internal server error");
			e.printStackTrace();
		}
		
	}
	
	public int getPing(){
		
		return ping;
		
	}
	
	public void reloadTree(){
		
		WebRebel.REBEL.getFrame().getClientTree().setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("root"){
				{
					for(WebRebelSocket socket : WebRebel.REBEL.getConnections()) add(new DefaultMutableTreeNode(socket.getConnection().getUserAgentParser().getOperatingSystem() + " " +
							socket.getConnection().getUserAgentParser().getBrowser() + (socket.isPending() ? " Pending..." : " (" + socket.getPing() + "ms)")));
					if(WebRebel.REBEL.getConnections().size() == 0) add(new DefaultMutableTreeNode("No connections."));
				}
			}
		));
		
	}
	
	public boolean isPending(){
		
		return pending;
		
	}

	@Override
	public void run(){
		
		if(lastPing > 0) pending = true;
		else{
			lastPing = System.currentTimeMillis();
			sendAction(new Action(ActionType.SERVER_PING, UUID.randomUUID(), new JSON()));
		}
		
	}
	
}
