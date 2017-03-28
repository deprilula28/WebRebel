package me.deprilula28.WebRebel.connection;

import java.awt.Color;
import java.net.InetSocketAddress;

public class WebRebelConnection{
	
	private String userAgent;
	private InetSocketAddress ip;
	private boolean codeEditingPermissable;
	private Color codeColor;
	
	public WebRebelConnection(String userAgent, InetSocketAddress ip){
		
		this.userAgent = userAgent;
		this.ip = ip;
		
	}
	
	public String getUserAgent(){
	
		return userAgent;
	
	}
	
	public InetSocketAddress getIp(){
	
		return ip;
	
	}
	
	public boolean isCodeEditingPermissable(){
	
		return codeEditingPermissable;
	
	}
	
	public Color getCodeColor(){
	
		return codeColor;
	
	}
	
}
