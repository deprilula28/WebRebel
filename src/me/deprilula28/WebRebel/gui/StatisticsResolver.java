package me.deprilula28.WebRebel.gui;

import me.deprilula28.WebRebel.WebRebel;

public class StatisticsResolver{
	
	private static int connections = 0;
	
	public static void logConnection(){
		
		connections ++;
		WebRebel.REBEL.getFrame().getRequestsLabel().setText(connections + " requests");
		
	}

}
