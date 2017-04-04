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
	public boolean equals(Object obj){
		
		if(!(obj instanceof OperatingSystem)) return false;
		OperatingSystem os = (OperatingSystem) obj;
		
		return os.type.equals(type) && os.version.equals(version);
		
	}
	
	@Override
	public String toString(){
		
		String typ = type == null ? "Unknown OS" : type.toString();
		String ver = version == null ? "Unknown Version" : version;
		
		return typ + " " + ver;
		
	}
	
}
