package me.deprilula28.WebRebel.connection;

public enum OperatingSystemType{

	WINDOWS("Windows"), OS_X("Mac OS X"), LINUX("Linux"), ANDROID("Android"), IOS("iOS");
	
	private String name;
	
	private OperatingSystemType(String name){
		
		this.name = name;
		
	}
	
	@Override
	public String toString(){
		
		return name;
		
	}
	
}
