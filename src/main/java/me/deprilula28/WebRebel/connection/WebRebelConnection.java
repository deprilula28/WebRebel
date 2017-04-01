package me.deprilula28.WebRebel.connection;

import java.awt.Color;
import java.net.InetSocketAddress;

public class WebRebelConnection{
	
	private UseragentParser parser;
	private InetSocketAddress ip;
	private boolean codeEditingPermissable;
	private Color codeColor;
	
	public WebRebelConnection(String userAgent, InetSocketAddress ip){
		
		parser = new UseragentParser(userAgent);
		this.ip = ip;
		
	}

	public UseragentParser getUserAgentParser(){
	
		return parser;
	
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
	
	@Override
	public String toString(){
		
		return parser.getOperatingSystem().toString() + " " + parser.getBrowser().toString();
		
	}
	
}
