package me.deprilula28.WebRebel.socket;

import java.util.List;

public class ConsoleLog{
	
	private LogType logType;
	private String message;
	private List<String> stackTrace;
	
	public ConsoleLog(LogType logType, String message, List<String> stackTrace){
		
		this.logType = logType;
		this.message = message;
		this.stackTrace = stackTrace;
		
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
	
}
