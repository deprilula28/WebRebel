package me.deprilula28.WebRebel.gui.dom;

import java.util.List;
import java.util.Map;

public class DOMElement{
	
	private DOMElementType typeEnum;
	private String typeName;
	private Map<String, String> attributes;
	private boolean hasChildren;
	private boolean childrenKnown;
	private List<DOMElement> children;

	public DOMElement(DOMElementType typeEnum, String typeName, Map<String, String> attributes, boolean hasChildren){
		
		this.typeEnum = typeEnum;
		this.typeName = typeName;
		this.attributes = attributes;
		this.hasChildren = hasChildren;
		
	}

	public boolean isChildrenKnown(){
		
		return childrenKnown;
		
	}
	
	public List<DOMElement> getChildren(){
		
		return children;
		
	}
	
	public void setChildren(List<DOMElement> children){
	
		childrenKnown = true;
		this.children = children;
	
	}
	
	public DOMElementType getTypeEnum(){
	
		return typeEnum;
	
	}

	public String getTypeName(){
	
		return typeName;
	
	}

	public Map<String, String> getAttributes(){
	
		return attributes;
	
	}

	public boolean hasChildren(){
		
		return hasChildren;
		
	}

}
