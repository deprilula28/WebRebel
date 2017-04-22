package me.deprilula28.WebRebel.connection;

import lombok.Getter;

public class UseragentParser{

    @Getter private String userAgentSource;
	@Getter private OperatingSystem operatingSystem;
	@Getter private Browser browser;
	
	public UseragentParser(String userAgentSource){
		
		this.userAgentSource = userAgentSource;
		
	}
	
	public void runParser(){
		
		operatingSystem = new OperatingSystem(null, null);
		browser = new Browser(null, null);
		
		try{
		if(userAgentSource.contains("Firefox/")) browser = new Browser(BrowserType.FIREFOX, userAgentSource.split("Firefox/")[1].split(" ")[0]);
		else if(userAgentSource.contains("Edge/")) browser = new Browser(BrowserType.EDGE, userAgentSource.split("Edge/")[1].split(" ")[0]);
		else if(userAgentSource.contains("MSIE")) browser = new Browser(BrowserType.IE, userAgentSource.split("MSIE/")[1].split(";")[0]);
		else if(userAgentSource.contains("Trident/")) browser = new Browser(BrowserType.IE, userAgentSource.split("rv:")[1].split(")")[0]);
		else if(userAgentSource.contains("Chrome/")) browser = new Browser(BrowserType.CHROME, userAgentSource.split("Chrome/")[1].split(" ")[0]);
		else if(userAgentSource.contains("Safari/")) browser = new Browser(BrowserType.SAFARI, userAgentSource.split("Safari/")[1].split(" ")[0]);
		
		if(userAgentSource.contains("Windows NT")) operatingSystem = new OperatingSystem(OperatingSystemType.WINDOWS, userAgentSource.split("Windows NT ")[1].split(";")[0]);
		else if(userAgentSource.contains("Macintosh;")){
			String version = userAgentSource.split("Mac OS X ")[1].split("\\)")[0].replaceAll("_", ".");
			
			try{
				int majorVersion = Integer.parseInt(version.split("\\.")[1]);
				version = version + " " + findVersionNameOSX(majorVersion);
			}catch(Exception e){}
			
			operatingSystem = new OperatingSystem(OperatingSystemType.OS_X, version);
		}else if(userAgentSource.contains("iPad;") || userAgentSource.contains("iPhone;")) operatingSystem = new OperatingSystem(OperatingSystemType.IOS, 
				userAgentSource.split(" OS ")[1].split(" like ")[0].replaceAll("_", ".") + " (" + (userAgentSource.contains("iPad") ? "iPad" : "iPhone") + ")");
		else if(userAgentSource.contains("Android ")){
			String version = userAgentSource.split("Android ")[1].split(";")[0];
			final String unmodifiedVersion = version;
			
			try{
				int majorVersion = Integer.parseInt(version.split("\\.")[0]);
				int minorVersion = Integer.parseInt(version.split("\\.")[1]);
				version = version + " " + findVersionNameAndroid(majorVersion, minorVersion);
			}catch(Exception e){}
			
			operatingSystem = new OperatingSystem(OperatingSystemType.ANDROID, version + " (" + userAgentSource.split("Android " + unmodifiedVersion + "; ")[1].split(" Build")[0] + ")");
		}else if(userAgentSource.contains("Linux")) userAgentSource = "Linux";
		}catch(Exception e){
			System.err.println("An issue occured parsing user agent.");
			System.err.println();
			System.err.println("Source:");
			System.err.println(userAgentSource);
			System.err.println();
			System.err.println("Error:");
			e.printStackTrace();
		}
		
	}

	private String findVersionNameAndroid(int majorVersion, int minorVersion){

		if(majorVersion == 6) return "Marshmellow";
		if(majorVersion == 7) return "Nougat";
		if(majorVersion == 5) return "Lollipop";
		if(majorVersion == 4) return "KitKat";
		if(majorVersion == 8) return "The Unknown O";
		if(majorVersion == 4 && minorVersion < 4) return "Jellybean";
		if(majorVersion == 1 && minorVersion == 0) return "Alpha";
		if(majorVersion == 1 && minorVersion < 5) return "Beta";
		if(majorVersion == 1 && minorVersion == 5) return "Cupcake";
		if(majorVersion == 1) return "Donut";
		if(majorVersion == 2 && minorVersion < 2) return "Eclair";
		if(majorVersion == 2 && minorVersion == 2) return "Froyo";
		if(majorVersion == 2) return "Gingerbread";
		if(majorVersion == 3) return "Honeycomb";
		if(majorVersion == 4 && minorVersion == 0) return "Ice Cream Sandwich";
		
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

}
