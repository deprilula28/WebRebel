package me.deprilula28.WebRebel.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperatingSystem{

	private OperatingSystemType type;
	private String version;

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
