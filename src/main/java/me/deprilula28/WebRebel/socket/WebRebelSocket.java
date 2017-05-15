package me.deprilula28.WebRebel.socket;

import static me.deprilula28.WebRebel.ColoredConsole.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.json.JSON;
import org.json.JSONException;

import me.deprilula28.WebRebel.ActionType;
import me.deprilula28.WebRebel.WebRebel;
import me.deprilula28.WebRebel.connection.Action;
import me.deprilula28.WebRebel.connection.Browser;
import me.deprilula28.WebRebel.connection.OperatingSystem;
import me.deprilula28.WebRebel.connection.WebRebelConnection;
import me.deprilula28.WebRebel.gui.BrowserTreeNode;
import me.deprilula28.WebRebel.gui.ClientFrame;
import me.deprilula28.WebRebel.gui.ConnectionTreeNode;
import me.deprilula28.WebRebel.gui.ConsoleViewFrame;
import me.deprilula28.WebRebel.gui.OperatingSystemTreeNode;

public class WebRebelSocket implements WebSocketListener, Runnable{
	
	public static Map<WebRebelConnection, List<ConsoleLog>> logs = new HashMap<>();
	private ScheduledExecutorService executorService;
	private WebRebelConnection connection;
	private RemoteEndpoint remoteEndpoint;
	private Session session;
	private long shakeRequestTime;
	private boolean shakedHands;
	private long lastPing;
	private int ping;
	private boolean pending;
	private boolean connected;
	private ClientFrame frame;
	private PongListener pong;
	
	public WebRebelSocket(WebRebelConnection connection){

		executorService = Executors.newScheduledThreadPool(1);
		this.connection = connection;
		
	}
	
	public boolean isConnected(){
	
		return connected;
	
	}
	
	public void sendAction(Action action){
		
		sendResponse(new JSON().put("action", action.getActionType().toString()).put("id", action.getUuid().toString()).put("info", action.getJsonData()));
		
	}
	
	public void sendResponse(JSON json){
		
		try{
			remoteEndpoint.sendStringByFuture(json.toString());
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
		System.out.println(BLUE + "(" + connection.toString() + ")" + WHITE + " Disconnected" + RESET);
		WebRebel.REBEL.getConnections().remove(this);
		reloadTree();
		connected = false;
		if(frame != null) frame.updateConnectionStatus(this);
		
	}

	@Override
	public void onWebSocketConnect(Session session){
		
		connection.getUserAgentParser().runParser();
		System.out.println(BLUE + "(" + connection.toString() + ")" + GREEN + " Connected" + RESET);
		this.session = session;
		remoteEndpoint = session.getRemote();
		WebRebel.REBEL.getConnections().add(this);
		WebRebel.REBEL.getFrame().getConsoleViewFrame().genConnStyle(connection);
		reloadTree();
		sendAction(new Action(ActionType.SERVER_HANDSHAKE, UUID.randomUUID(), new JSON()));
		
		logs.put(connection, new ArrayList<>());
		connected = true;
		if(frame != null) frame.updateConnectionStatus(this);
		
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
	public void onWebSocketBinary(byte[] data, int idk, int idkEither){
	}

	@Override
	public void onWebSocketText(String message){
		
		JSON json = null;
		
		try{
			json = new JSON(message);
			ActionType action = ActionType.valueOf(json.getString("action"));
			
			if(!shakedHands && !action.equals(ActionType.CLIENT_HANDSHAKE)){
				sendAction(new Action(ActionType.SERVER_ERROR_RESPONSE, UUID.fromString(json.getString("id")), new JSON().put("error", "Invalid input")
						.put("errorMessage", "Handshake must be completed first!")));
				return;
			}
			
			switch(action){
			case CLIENT_ERROR:
				JSON error = json.getJSONObject("info");
				String errorTag = error.getString("error");
				String errorMessage = error.getString("message");

				System.out.println(BLUE + "(" + connection.toString() + ")" + RED + " WebRebel Error >> " + errorTag + ": " + errorMessage + RESET);
				break;
			case CLIENT_REQUEST_CODE_EDIT_PERM:
				if(connection.isCodeEditingPermissable()) throw new IOException("Already permissable!");
				break;
			case CLIENT_CODE_EDIT:
				if(!connection.isCodeEditingPermissable()) throw new IOException("Access denied.");
				break;
			case CLIENT_CONSOLE_LOG:
				JSON info = json.getJSONObject("info");
				List<String> stackTrace = null;
				
				if(info.has("stackTrace")){
					stackTrace = new ArrayList<>();
					for(Object cur : info.getJSONArray("stackTrace").myArrayList) stackTrace.add((String) cur);
				}
				ConsoleLog logInstance = new ConsoleLog(LogType.valueOf(info.getString("type").toUpperCase()), info.getString("message"), stackTrace, System.currentTimeMillis());
				logs.get(connection).add(logInstance);
				WebRebel.REBEL.getFrame().getConsoleViewFrame().addMessage(connection, logInstance);
				System.out.println(BLUE + "(" + connection.toString() + ") " + info.getString("type").toLowerCase() + " >> " + RESET + logInstance.toString() + RESET);
				if(frame != null) frame.add(logInstance);
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
				
				sendAction(new Action(ActionType.SERVER_CLIENT_PING_INFO, UUID.randomUUID(), JSON.json("ping", ping)));
				if(frame != null) frame.updateConnectionStatus(this);
				
				if(pong != null){
					pong.pong();
					pong = null;
				}
				break;
			case CLIENT_DOM_RESPONSE:
				if(frame != null) frame.getDOMExplorer().handleDOMActionResponse(UUID.fromString(json.getString("id")), json.getJSONObject("info"));
				break;
			default:
				throw new IOException("Invalid request type");
			}
		}catch(JSONException e){
			System.err.println("Invalid message received");
			e.printStackTrace();
		}catch(Exception e){
			sendAction(new Action(ActionType.SERVER_ERROR_RESPONSE, UUID.fromString(json.getString("id")), new JSON().put("error", "Invalid Input")
					.put("errorMessage", e.getMessage())));
			System.err.println("Internal server error");
			e.printStackTrace();
		}
		
	}
	
	public int getPing(){
		
		return ping;
		
	}
	
	public static void reloadTree(){
		
		DefaultTreeModel model = new DefaultTreeModel(
			new DefaultMutableTreeNode("root"){
				{
					for(WebRebelSocket socket : WebRebel.REBEL.getConnections()) add(new ConnectionTreeNode(socket.getConnection(), socket, socket.isPending(), socket.getPing()));
					if(WebRebel.REBEL.getConnections().size() == 0) add(new DefaultMutableTreeNode("No connections."));
				}
			}
		);
		WebRebel.REBEL.getFrame().getClientTree().setModel(model);
		
		//TODO Make this look better
		ConsoleViewFrame frame = WebRebel.REBEL.getFrame().getConsoleViewFrame();
		
		switch(frame.getFilterTypeComboBox().getSelectedIndex()){
		case 0:
			frame.getFilterTree().setModel(model);
			break;
		case 1:
			frame.getFilterTree().setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("root"){
					{
						List<OperatingSystem> dealtWith = new ArrayList<>();
						for(WebRebelSocket socket : WebRebel.REBEL.getConnections()){
							OperatingSystem os = socket.getConnection().getUserAgentParser().getOperatingSystem();
							if(dealtWith.contains(os)) continue;
							
							dealtWith.add(os);
							add(new OperatingSystemTreeNode(os));
						}
					}
				}
			));
			break;
		case 2:
			frame.getFilterTree().setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("root"){
					{
						List<Browser> dealtWith = new ArrayList<>();
						for(WebRebelSocket socket : WebRebel.REBEL.getConnections()){
							Browser browser = socket.getConnection().getUserAgentParser().getBrowser();
							if(dealtWith.contains(browser)) continue;
							
							dealtWith.add(browser);
							add(new BrowserTreeNode(browser));
						}
					}
				}
			));
			break;
		}
		
	}
	
	public boolean isPending(){
		
		return pending;
		
	}

	@Override
	public void run(){
		
		if(lastPing > 0){
			if(!pending) System.out.println(BLUE + "(" + connection.toString() + ") " + YELLOW + "Connection pending..." + RESET);
			pending = true;
		}else{
			lastPing = System.currentTimeMillis();
			sendAction(new Action(ActionType.SERVER_PING, UUID.randomUUID(), new JSON()));
		}
		
	}
	
	public void ping(PongListener pong){
		
		this.pong = pong;
		lastPing = System.currentTimeMillis();
		sendAction(new Action(ActionType.SERVER_PING, UUID.randomUUID(), new JSON()));
		
	}
	
	public static interface PongListener{
		
		public void pong();
		
	}
	
	public void setFrame(ClientFrame frame){
	
		this.frame = frame;
	
	}
	
	public void disconnect(){
		
		session.close();
		
	}
	
}
