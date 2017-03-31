package me.deprilula28.WebRebel.connection;

public class OperatingSystem{

	private OperatingSystemType type;
	private String version;
	
	public OperatingSystem(OperatingSystemType type, String version){
		
		this.type = type;
		this.version = version;
		
	}

	public OperatingSystemType getType(){
		
		return type;
		
	}

	public String getVersion(){
		
		return version;
		
	}
	
	@Override
	public String toString(){
		
		String typ = type == null ? "Unknown OS" : type.toString();
		String ver = version == null ? "Unknown Version" : version;
		
		return typ + " " + ver;
		
	}
	
}
