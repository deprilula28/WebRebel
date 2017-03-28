package me.deprilula28.WebRebel.connection;

import java.util.UUID;

import org.json.JSONObject;

import me.deprilula28.WebRebel.ActionType;

public class Action{
	
	private ActionType actionType;
	private UUID uuid;
	private JSONObject jsonData;
	
	public Action(ActionType actionType, UUID uuid, JSONObject jsonData){
		
		this.actionType = actionType;
		this.uuid = uuid;
		this.jsonData = jsonData;
		
	}
	
	public ActionType getActionType(){
		
		return actionType;
		
	}
	
	public JSONObject getJSONData(){
		
		return jsonData;
		
	}
	
	public UUID getUUID(){
		
		return uuid;
		
	}
	
}
