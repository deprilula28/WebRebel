package me.deprilula28.WebRebel.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import lombok.Data;
import me.deprilula28.WebRebel.connection.Browser;

@Data
public class BrowserTreeNode extends DefaultMutableTreeNode{
	
	private Browser browser;
	
	public BrowserTreeNode(Browser browser){
		
		super(browser.toString());
		
		this.browser = browser;
		
	}

}
