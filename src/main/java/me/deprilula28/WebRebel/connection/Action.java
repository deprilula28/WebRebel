package me.deprilula28.WebRebel.connection;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSON;

import me.deprilula28.WebRebel.ActionType;

@Data
@AllArgsConstructor
public class Action{
	
	private ActionType actionType;
	private UUID uuid;
	private JSON jsonData;

}
