package me.deprilula28.WebRebel.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.Color;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class WebRebelConnection{

	private UseragentParser userAgentParser;
	private InetSocketAddress ip;
	private boolean codeEditingPermissable;
	private Color codeColor;
	
	private Color uniqueColor;

	public WebRebelConnection(String userAgent, InetSocketAddress ip){

	    userAgentParser = new UseragentParser(userAgent);
	    this.ip = ip;
	    codeEditingPermissable = true;

	    Random random = ThreadLocalRandom.current();
        codeColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        uniqueColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));

    }

	@Override
	public String toString(){
		
		return userAgentParser.getOperatingSystem().toString() + " " + userAgentParser.getBrowser().toString();
		
	}
	
}
