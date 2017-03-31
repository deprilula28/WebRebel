package me.deprilula28.WebRebel.socket;

import javax.swing.tree.DefaultMutableTreeNode;

import me.deprilula28.WebRebel.connection.WebRebelConnection;

public class ConnectionTreeNode extends DefaultMutableTreeNode{
	
	private WebRebelConnection connection;
	private boolean pending;
	private int ping;
	
	public ConnectionTreeNode(WebRebelConnection connection, boolean pending, int ping){
		
		super(connection.getUserAgentParser().getOperatingSystem() + " " + connection.getUserAgentParser().getBrowser() + (pending ? " Pending..." : " (" + ping + "ms)"));
		
		this.connection = connection;
		this.pending = pending;
		this.ping = ping;
		
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
