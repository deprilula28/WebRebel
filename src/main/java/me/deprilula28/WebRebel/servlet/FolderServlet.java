package me.deprilula28.WebRebel.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import me.deprilula28.WebRebel.WebRebel;


public class FolderServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		handleFolderServlet(request, response);
		
	}
	
	public static void handleFolderServlet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		try{
			if(WebRebel.REBEL.getFrame().getFolder() == null){
				CommonResponses.errorResponse(response, "Select a folder in the GUI first.", HttpStatus.BAD_REQUEST_400);
				return;
			}

			String path = request.getServletPath();
			
			if(path.length() == 7 || path.substring(7).equals("/")){
				File file = new File(WebRebel.REBEL.getFrame().getFolder().getAbsolutePath() + File.separatorChar + "index.html");
				
				if(!file.exists()){
					CommonResponses.notFoundResponse(response);
					return;
				}
				
				Scanner scann = new Scanner(new FileInputStream(file));
				StringBuilder stringBuilder = new StringBuilder();
				
				while(scann.hasNextLine()) stringBuilder.append(scann.nextLine());
				scann.close();
				
				response.setStatus(HttpStatus.FOUND_302);
				response.getWriter().append(stringBuilder.toString());
			}else{
				path = path.substring(7);
				File file = new File(WebRebel.REBEL.getFrame().getFolder().getAbsolutePath() + File.separatorChar + path.replaceAll("/", File.separatorChar == '\\' ? "\\\\" : "/"));
				
				if(!file.exists()){
					CommonResponses.notFoundResponse(response);
					return;
				}
				
				 CommonResponses.binaryFileSend(response, file);
			}
		}catch(Exception e){
			System.err.println("Error occured while attempt to send connection:");
			e.printStackTrace();

			CommonResponses.errorResponse(response, "Internal server error, please report this.<br>View log for full error.", HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
		
	}
	
}
