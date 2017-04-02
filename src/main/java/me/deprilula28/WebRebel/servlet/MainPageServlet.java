package me.deprilula28.WebRebel.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;


public class MainPageServlet extends HttpServlet{
	
	private static final List<String> FAVICON_REQUESTABLE = Arrays.asList(new String[]{
		"android-chrome-192x192.png", "android-chrome-512x512.png", "apple-touch-icon.png", "favicon.ico",
		"favicon-16x16.png", "favicon-32x32.png", "manifest.json", "mstile-70x70.png", "mstile-150x150.png",
		"mstile-310x150.png", "mstile-310x310.png", "safari-pinned-tab.svg"
	});
	private static final List<String> FILEPATH_REQUESTABLE = Arrays.asList(new String[]{
		"webRebel.js", "jQuery.js", "webRebel.min.css", "webRebel.css"
	});
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

		try{
			
			String split = request.getServletPath().substring(1).split("/")[0];
			
			if(split == "admin"){
				//TODO Admin page
			}else if(FILEPATH_REQUESTABLE.contains(split)) CommonResponses.binaryFileSend(response, new File("lib" + File.separatorChar + "webPage" + File.separator + split));
			else if(FAVICON_REQUESTABLE.contains(split)) CommonResponses.binaryFileSend(response, new File("lib" + File.separatorChar + "favicon" + File.separator + split));
			else if(request.getServletPath().equals("/")){
				Scanner scann = new Scanner(new FileInputStream(new File("lib" + File.separatorChar + "webPage" + File.separatorChar + "index.html")));
				StringBuilder stringBuilder = new StringBuilder();
				
				while(scann.hasNextLine()) stringBuilder.append(scann.nextLine());
				scann.close();
				
				response.setStatus(HttpStatus.FOUND_302);
				response.getWriter().append(stringBuilder.toString());
			}else if(request.getServletPath().startsWith("/folder/")) FolderServlet.handleFolderServlet(request, response);
			else CommonResponses.notFoundResponse(response);
		}catch(FileNotFoundException e){
			System.err.println("File not found:");
			e.printStackTrace();

			CommonResponses.notFoundResponse(response);
		}catch(Exception e){
			System.err.println("Error occured while attempt to send connection:");
			e.printStackTrace();

			CommonResponses.errorResponse(response, "Internal server error, please report this.<br>View log for full error.", HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
		
	}
	
}
