package me.deprilula28.WebRebel.gui.dom;

import lombok.Data;

import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

@Data
public class DOMElement{
	
	private DOMElementType typeEnum;
	private String typeName;
	private Map<String, String> attributes;
	private boolean hasChildren;
	private boolean childrenKnown;
	private List<DOMElement> children;
	private List<String> path;
	private DefaultMutableTreeNode treeNode;

	public DOMElement(DOMElementType typeEnum, String typeName, Map<String, String> attributes, boolean hasChildren, List<String> path, DefaultMutableTreeNode treeNode){

		this.typeEnum = typeEnum;
		this.typeName = typeName;
		this.attributes = attributes;
		this.hasChildren = hasChildren;
		this.path = path;
		this.treeNode = treeNode;

	}

}
