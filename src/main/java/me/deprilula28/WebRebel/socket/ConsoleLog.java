package me.deprilula28.WebRebel.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConsoleLog{
	
	private LogType logType;
	private String message;
	private List<String> stackTrace;
	private long timestamp;

	@Override
	public String toString(){
		
		return message;
		
	}
	
}
