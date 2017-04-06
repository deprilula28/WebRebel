package me.deprilula28.WebRebel.gui;

import me.deprilula28.WebRebel.ActionType;
import me.deprilula28.WebRebel.connection.Action;
import me.deprilula28.WebRebel.gui.dom.DOMElement;
import me.deprilula28.WebRebel.gui.dom.DOMElementType;
import me.deprilula28.WebRebel.socket.WebRebelSocket;
import org.json.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DOMExplorer{
	
	private WebRebelSocket socket;
	private List<DOMElement> topDOMElements;
	private Map<UUID, DOMElement> pendingRequests;
	
	public DOMExplorer(WebRebelSocket socket){
		
		this.socket = socket;
		this.topDOMElements = new ArrayList<>();
		
		exploreMain();
		
	}
	
	private void exploreMain(){
		
		
		
	}

	public void domExpand(DOMElement element){

	    UUID uuid = UUID.randomUUID();
	    Action action = new Action(ActionType.SERVER_DOM_REQUEST, uuid, JSON.json(
            "domElement", ""
        ));
	    pendingRequests.put(uuid, element);
	    socket.sendAction(action);

    }
	
	public void handleDOMActionResponse(UUID id, JSON json) throws IOException{
		
		if(!pendingRequests.containsKey(id)) throw new IOException("No DOM request found.");
		
		DOMElement expand = pendingRequests.get(id);
		
		if(expand == null){
			//Main Panel
			topDOMElements.addAll(parseChildren(json));
		}else{
			//Any other element
			expand.setChildren(parseChildren(json));
		}
		
	}
	
	private List<DOMElement> parseChildren(JSON json){
		
		List<DOMElement> elements = new ArrayList<>();
		
		for(Object cur : json.getJSONArray("domElements").myArrayList){
		    JSON curj = (JSON) cur;
		    String type = curj.getString("elementType");
            DOMElementType elType = DOMElementType.find(type);
            Map<String, String> attributes = curj.getMapJSONObject("attributes", String.class);
            boolean hasChildren = curj.getBoolean("hasChildren");

            elements.add(new DOMElement(elType, type, attributes, hasChildren));
        }
		
		return elements;
		
	}
	
	public List<DOMElement> getTopDOMElements(){
	
		return topDOMElements;
	
	}
	
}
