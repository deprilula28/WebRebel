package me.deprilula28.WebRebel.connection;

public class Browser{
	
	private BrowserType type;
	private String version;
	
	public Browser(BrowserType type, String version){
		
		this.type = type;
		this.version = version;
		
	}
	
	public BrowserType getType(){
		
		return type;
		
	}
	
	public String getVersion(){
		
		return version;
		
	}

	@Override
	public boolean equals(Object obj){
		
		if(!(obj instanceof Browser)) return false;
		Browser br = (Browser) obj;
		
		return br.type.equals(type) && br.version.equals(version);
		
	}
	
	@Override
	public String toString(){
		
		String typ = type == null ? "Unknown Browser" : type.toString();
		String ver = version == null ? "Unknown Browser Version" : version;
		
		return typ + " " + ver;
		
	}
	
}
