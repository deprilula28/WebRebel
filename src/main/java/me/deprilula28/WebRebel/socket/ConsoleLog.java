package me.deprilula28.WebRebel.socket;

import java.util.List;

public class ConsoleLog{
	
	private LogType logType;
	private String message;
	private List<String> stackTrace;
	private long timestamp;
	
	public ConsoleLog(LogType logType, String message, List<String> stackTrace){
		
		this.logType = logType;
		this.message = message;
		this.stackTrace = stackTrace;
		timestamp = System.currentTimeMillis();
		
	}
	
	public long getTimestamp(){
	
		return timestamp;
	
	}

	public LogType getLogType(){
		
		return logType;
		
	}

	public String getMessage(){
		
		return message;
		
	}

	public List<String> getStackTrace(){
		
		return stackTrace;
		
	}
	
	@Override
	public String toString(){
		
		return message;
		
	}
	
}
