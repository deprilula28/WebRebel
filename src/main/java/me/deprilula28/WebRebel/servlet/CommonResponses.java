package me.deprilula28.WebRebel.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

public class CommonResponses{
	
	public static void errorResponse(HttpServletResponse response, String message, int code) throws IOException{
		
		Scanner scann = new Scanner(new FileInputStream(new File("lib" + File.separatorChar + "error.min.html")));
		StringBuilder stringBuilder = new StringBuilder();
		
		while(scann.hasNextLine()) stringBuilder.append(scann.nextLine());
		scann.close();

		response.setStatus(code);
		Writer writer = new OutputStreamWriter(response.getOutputStream());
		writer.append(stringBuilder.toString().replaceAll("%%_ERROR_MESSAGE_%%", "HTTP Error Code: " + code + " (" + HttpStatus.getMessage(code) + ")<br>" + message));
		writer.close();
		
	}
	
	public static long binaryFileSend(HttpServletResponse response, File file) throws IOException{
		
		FileInputStream inputStream = new FileInputStream(file);
		OutputStream outputStream = response.getOutputStream();
		response.setStatus(HttpStatus.FOUND_302);
		byte[] outputByte = new byte[4096];
		int c = 0;
		long uploadedBytes = 0l;
		
		while((c = inputStream.read(outputByte, 0, 4096)) != -1){
			outputStream.write(outputByte, 0, c);
			uploadedBytes += c;
		}
		inputStream.close();
		
		return uploadedBytes;
		
	}
	
	public static void notFoundResponse(HttpServletResponse response) throws IOException{
		
		errorResponse(response, "Pretty self explanatory error. Make sure the file and path are present.<br>If you believe this is an issue on the servers part, "
				+ "please report it.", HttpStatus.NOT_FOUND_404);
		
	}
	
}
