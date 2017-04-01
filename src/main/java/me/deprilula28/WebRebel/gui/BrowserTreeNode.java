package me.deprilula28.WebRebel.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import me.deprilula28.WebRebel.connection.Browser;

public class BrowserTreeNode extends DefaultMutableTreeNode{
	
	private Browser browser;
	
	public BrowserTreeNode(Browser browser){
		
		super(browser.toString());
		
		this.browser = browser;
		
	}
	
	public Browser getBrowser(){
	
		return browser;
	
	}
	
}
