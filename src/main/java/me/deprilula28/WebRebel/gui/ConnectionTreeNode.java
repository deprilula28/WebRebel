package me.deprilula28.WebRebel.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import me.deprilula28.WebRebel.connection.WebRebelConnection;
import me.deprilula28.WebRebel.socket.WebRebelSocket;

public class ConnectionTreeNode extends DefaultMutableTreeNode{
	
	private WebRebelConnection connection;
	private WebRebelSocket socket;
	private boolean pending;
	private int ping;
	
	public ConnectionTreeNode(WebRebelConnection connection, WebRebelSocket socket, boolean pending, int ping){
		
		super(connection.getUserAgentParser().getOperatingSystem() + " " + connection.getUserAgentParser().getBrowser() + (pending ? " Pending..." : " (" + ping + "ms)"));
		
		this.socket = socket;
		this.connection = connection;
		this.pending = pending;
		this.ping = ping;
		
	}
	
	public WebRebelSocket getSocket(){
	
		return socket;
	
	}
	
	public boolean isPending(){
		
		return pending;
		
	}
	
	public int getPing(){
		
		return ping;
		
	}
	
	public WebRebelConnection getConnection(){
	
		return connection;
	
	}
	
}
