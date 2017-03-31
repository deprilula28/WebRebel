package me.deprilula28.WebRebel.connection;

import java.util.UUID;

import org.json.JSON;

import me.deprilula28.WebRebel.ActionType;

public class Action{
	
	private ActionType actionType;
	private UUID uuid;
	private JSON jsonData;
	
	public Action(ActionType actionType, UUID uuid, JSON jsonData){
		
		this.actionType = actionType;
		this.uuid = uuid;
		this.jsonData = jsonData;
		
	}
	
	public ActionType getActionType(){
		
		return actionType;
		
	}
	
	public JSON getJSONData(){
		
		return jsonData;
		
	}
	
	public UUID getUUID(){
		
		return uuid;
		
	}
	
}
