package me.deprilula28.WebRebel.connection;

import java.awt.Color;
import java.net.InetSocketAddress;
import java.util.Random;

public class WebRebelConnection{
	
	private static Random random = new Random();
	private UseragentParser parser;
	private InetSocketAddress ip;
	private boolean codeEditingPermissable;
	private Color codeColor;
	
	private Color uniqueColor;
	
	public WebRebelConnection(String userAgent, InetSocketAddress ip){
		
		parser = new UseragentParser(userAgent);
		this.ip = ip;
		
		int r = random.nextInt(100);
		int g = random.nextInt(100);
		int b = random.nextInt(100);
		
		System.out.println(r + " " + g + " " + b);
		
		uniqueColor = new Color(r, g, b);
		
	}
	
	public Color getUniqueColor(){
	
		return uniqueColor;
	
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
