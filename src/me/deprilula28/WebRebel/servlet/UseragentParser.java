package me.deprilula28.WebRebel.servlet;


public class UseragentParser{
	
	private String userAgentSource;
	private String operatingSystem;
	private String browser;
	
	public UseragentParser(String userAgentSource){
		
		this.userAgentSource = userAgentSource;
		
	}
	
	public void runParser(){
		
		operatingSystem = "Unknown OS";
		browser = "Unknown Browser";
		
		if(userAgentSource.contains("Firefox/")) browser = "Firefox " + userAgentSource.split("Firefox/")[1].split(" ")[0];
		else if(userAgentSource.contains("Edge/")) browser = "Edge" + userAgentSource.split("Edge/")[1].split(" ")[0];
		else if(userAgentSource.contains("MSIE")) browser = "IE " + userAgentSource.split("MSIE")[1].split(";")[0];
		else if(userAgentSource.contains("Trident/")) browser = "IE " + userAgentSource.split("rv:")[1].split(")")[0];
		else if(userAgentSource.contains("Chrome/")) browser = "Chrome " + userAgentSource.split("Chrome/")[1].split(" ")[0];
		else if(userAgentSource.contains("Safari/")) browser = "Safari " + userAgentSource.split("Safari/")[1].split(" ")[0];
		
		if(userAgentSource.contains("Windows NT")) operatingSystem = "Windows " + userAgentSource.split("Windows NT ")[1].split(";")[0];
		else if(userAgentSource.contains("Macintosh;")){
			String version = userAgentSource.split("Mac OS X ")[1].split("\\)")[0].replaceAll("_", ".");
			operatingSystem = "Mac OS X " + version;
			
			try{
				int majorVersion = Integer.parseInt(version.split("\\.")[1]);
				operatingSystem = operatingSystem + " " + findVersionNameOSX(majorVersion);
			}catch(Exception e){}
		}else if(userAgentSource.contains("iPad;") || userAgentSource.contains("iPhone;")) operatingSystem = "iOS " + userAgentSource.split(" OS ")[1].split(" like ")[0]
				.replaceAll("_", ".") + " (" + (userAgentSource.contains("iPad") ? "iPad" : "iPhone") + ")";
		else if(userAgentSource.contains("Android ")){
			String version = userAgentSource.split("Android ")[1].split(";")[0];
			operatingSystem = "Android " + version;
			
			try{
				int majorVersion = Integer.parseInt(version.split("\\.")[0]);
				int minorVersion = Integer.parseInt(version.split("\\.")[1]);
				operatingSystem = operatingSystem + " " + findVersionNameAndroid(majorVersion, minorVersion);
			}catch(Exception e){}
			
			operatingSystem = operatingSystem + " (" + userAgentSource.split("Android " + version + "; ")[1].split(" Build")[0] + ")";
		}else if(userAgentSource.contains("Linux")) userAgentSource = "Linux";
		
		
		
	}

	private String findVersionNameAndroid(int majorVersion, int minorVersion){

		if(majorVersion == 1 && minorVersion == 0) return "Alpha";
		if(majorVersion == 1 && minorVersion < 5) return "Beta";
		if(majorVersion == 1 && minorVersion == 5) return "Cupcake";
		if(majorVersion == 1) return "Donut";
		if(majorVersion == 2 && minorVersion < 2) return "Eclair";
		if(majorVersion == 2 && minorVersion == 2) return "Froyo";
		if(majorVersion == 2) return "Gingerbread";
		if(majorVersion == 3) return "Honeycomb";
		if(majorVersion == 4 && minorVersion == 0) return "Ice Cream Sandwich";
		if(majorVersion == 4 && minorVersion < 4) return "Jellybean";
		if(majorVersion == 4) return "KitKat";
		if(majorVersion == 5) return "Lollipop";
		if(majorVersion == 6) return "Marshmello";
		if(majorVersion == 7) return "Nougat";
		if(majorVersion == 8) return "The Unknown O";
		
		return "(Unknown codename)";
		
	}
	
	private String findVersionNameOSX(int majorVersion){
		
		switch(majorVersion){
		case 0:
			return "Cheetah";
		case 1:
			return "Puma";
		case 2:
			return "Jaguar";
		case 3:
			return "Panther";
		case 4:
			return "Tiger";
		case 5:
			return "Leopard";
		case 6:
			return "Snow Leopard";
		case 7:
			return "Lion";
		case 8:
			return "Mountain Lion";
		case 9:
			return "Mavericks";
		case 10:
			return "Yosemite";
		case 11:
			return "El Capitan";
		case 12:
			return "Sierra";
		case 666:
			return "Illuminati";
		}
		
		return "(Unknown major version)";
		
	}
	
	public String getUserAgentSource(){
	
		return userAgentSource;
	
	}
	
	public String getOperatingSystem(){
	
		return operatingSystem;
	
	}
	
	public String getBrowser(){
	
		return browser;
	
	}
	
}
