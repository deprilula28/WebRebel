package me.deprilula28.WebRebel.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Browser{
	
	private BrowserType type;
	private String version;

	@Override
	public boolean equals(Object obj){
		
		if(!(obj instanceof Browser)) return false;
		Browser br = (Browser) obj;
		
		return br.type.equals(type) && br.version.equals(version);
		
	}
	
	@Override
	public String toString(){
		
		String typ = type == null ? "Unknown Browser" : type.toString();
		String ver = version == null ? "Unknown Version" : version;
		
		return typ + " " + ver;
		
	}
	
}
