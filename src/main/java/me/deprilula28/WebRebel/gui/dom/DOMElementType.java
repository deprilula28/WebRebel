package me.deprilula28.WebRebel.gui.dom;

import java.util.Arrays;
import java.util.List;

public enum DOMElementType{
	
	DIV("div"), LIST("ul", "ol"), LI("li"), TEXT("a", "h1", "h2", "h3", "h4", "h5", "p", "pre"), SCRIPT("script"), IMAGE("img"), NAV("nav"), OTHER;

	private List<String> definitions;

	DOMElementType(String... definitions){

	    this.definitions = Arrays.asList(definitions);

    }

    public static DOMElementType find(String string){

	    for(DOMElementType cur : values())
	        if(cur.definitions.contains(string.toLowerCase()))
                return cur;

	    return DOMElementType.OTHER;

    }

}
