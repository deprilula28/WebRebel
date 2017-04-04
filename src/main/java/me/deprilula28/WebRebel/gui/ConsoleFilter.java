package me.deprilula28.WebRebel.gui;

import me.deprilula28.WebRebel.connection.Browser;
import me.deprilula28.WebRebel.connection.OperatingSystem;
import me.deprilula28.WebRebel.connection.WebRebelConnection;

public class ConsoleFilter{
	
	private FilterType filterType;

	private OperatingSystem os;
	private Browser browser;
	private WebRebelConnection device;
	
	public ConsoleFilter(){
		
		filterType = FilterType.ALL;
		
	}

	public ConsoleFilter(OperatingSystem os){
		
		filterType = FilterType.OPERATING_SYSTEM;
		this.os = os;
		
	}

	public ConsoleFilter(Browser browser){
		
		filterType = FilterType.BROWSER;
		this.browser = browser;
		
	}

	public ConsoleFilter(WebRebelConnection device){
		
		filterType = FilterType.DEVICE;
		this.device = device;
		
	}
	
	public boolean doesFilter(WebRebelConnection connection){
		
		switch(filterType){
		case ALL:
			return false;
		case BROWSER:
			return !(connection.getUserAgentParser().getBrowser().equals(browser));
		case DEVICE:
			return !(connection.equals(device));
		case OPERATING_SYSTEM:
			return !(connection.getUserAgentParser().getOperatingSystem().equals(os));
		}
		
		return true;
		
	}
	
}
