package me.deprilula28.WebRebel.socket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import me.deprilula28.WebRebel.servlet.CommonResponses;

public class LiveServlet extends WebSocketServlet{

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		CommonResponses.errorResponse(response, "Did you expect this to work or what", 405);
		
	}
	
	@Override
	public void configure(WebSocketServletFactory factory){

		factory.getPolicy().setIdleTimeout(30000);
		factory.setCreator(new SocketCreator());
		
	}

}
