package me.deprilula28.WebRebel.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import me.deprilula28.WebRebel.connection.OperatingSystem;

public class OperatingSystemTreeNode extends DefaultMutableTreeNode{
	
	private OperatingSystem os;
	
	public OperatingSystemTreeNode(OperatingSystem os){
		
		super(os.toString());
		
		this.os = os;
		
	}
	
	public OperatingSystem getOS(){
	
		return os;
	
	}
	
}
