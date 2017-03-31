package me.deprilula28.WebRebel.connection;

public enum BrowserType{
	
	FIREFOX("Firefox"), EDGE("MS Edge"), IE("MS IE"), CHROME("Chrome"), SAFARI("Safari");
	
	private String name;
	
	private BrowserType(String name){
		
		this.name = name;
		
	}
	
	@Override
	public String toString(){
		
		return name;
		
	}
	
}
