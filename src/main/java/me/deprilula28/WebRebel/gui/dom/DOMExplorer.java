package me.deprilula28.WebRebel.gui.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.tree.DefaultMutableTreeNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.json.JSON;
import org.json.JSONArray;

import me.deprilula28.WebRebel.ActionType;
import me.deprilula28.WebRebel.connection.Action;
import me.deprilula28.WebRebel.gui.ClientFrame;
import me.deprilula28.WebRebel.socket.WebRebelSocket;

public class DOMExplorer{
	
	private WebRebelSocket socket;
    @Getter private List<DOMElement> topDOMElements;
    private Map<UUID, DOMElement> pendingRequests;
    private ClientFrame frame;
    private Map<UUID, DOMElement> elementIDs;
	
	public DOMExplorer(WebRebelSocket socket, ClientFrame frame){
		
		this.socket = socket;
		this.topDOMElements = new ArrayList<>();
		this.frame = frame;
		
		pendingRequests = new HashMap<>();
        elementIDs = new HashMap<>();
		
		exploreMain();
		
	}
	
	private void exploreMain(){

	    UUID uuid = UUID.randomUUID();
	    Action action = new Action(ActionType.SERVER_DOM_REQUEST, uuid, JSON.json(
            "expand", JSONArray.json("")
        ));
	    pendingRequests.put(uuid, null);
	    socket.sendAction(action);

	}

	public void domExpand(DOMElement element){

	    UUID uuid = UUID.randomUUID();
	    Action action = new Action(ActionType.SERVER_DOM_REQUEST, uuid, JSON.json(
            "expand", JSONArray.parse(element.getPath())
        ));
	    pendingRequests.put(uuid, element);
	    socket.sendAction(action);

    }
	
	public void handleDOMActionResponse(UUID id, JSON json) throws IOException{
		
		if(!pendingRequests.containsKey(id)) throw new IOException("No DOM request found.");
		
		DOMElement expand = pendingRequests.get(id);
		
		if(expand == null){
			//Main Panel
			topDOMElements.addAll(parseChildren(json, frame.getRootNode()));
			if(frame.getLoadingNode() != null){
				frame.getRootNode().remove(frame.getLoadingNode());
				frame.unloadLoadingNode();
			}
		}else{
			//Any other element
			expand.setChildren(parseChildren(json, elementIDs.get(UUID.fromString(expand.getPath().get(expand.getPath().size() - 1))).getTreeNode()));
		}
		
	}
	
	private List<DOMElement> parseChildren(JSON json, DefaultMutableTreeNode masterNode){
		
		List<DOMElement> elements = new ArrayList<>();
		
		for(Object cur : json.getJSONArray("domElements").myArrayList){
		    JSON curj = (JSON) cur;
		    String type = curj.getString("elementType");
            DOMElementType elType = DOMElementType.find(type);
            Map<String, String> attributes = curj.getMapJSONObject("attributes", String.class);
            boolean hasChildren = curj.getBoolean("hasChildren");
            List<String> paths = curj.getListJSONArray("path", String.class);
            
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(type.toLowerCase());
            masterNode.add(newNode);
            
            DOMElement element = new DOMElement(elType, type, attributes, hasChildren, paths, newNode);
            elements.add(element);
            elementIDs.put(UUID.fromString(curj.getString("id")), element);
        }
		
		return elements;
		
	}

}
