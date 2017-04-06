package me.deprilula28.WebRebel.updateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSON;
import org.json.JSONArray;

import me.deprilula28.WebRebel.ActionType;
import me.deprilula28.WebRebel.WebRebel;
import me.deprilula28.WebRebel.connection.Action;
import me.deprilula28.WebRebel.socket.WebRebelSocket;

public class UpdateHandler{
	
	public static void doFileUpdate(File oldFile, File newFile, String relativePath, String extension) throws Exception{
		
		WebRebel.REBEL.getFrame().setTask("Parsing changes...", true);
		//List<String> oldLines = read(oldFile);
		List<String> newLines = read(newFile);
		/*
		long start = System.currentTimeMillis();
		Patch patch = DiffUtils.diff(oldLines, newLines);
		long time = System.currentTimeMillis() - start;
		
		List<Integer> deletions = new ArrayList<>();
		Map<Integer, String> changes = new HashMap<>();
		Map<Integer, String> insertions = new HashMap<>();
		
		for(Delta delta : patch.getDeltas()){
			int pos = delta.getRevised().getPosition();
			List<?> linesRevised = delta.getRevised().getLines();
			List<?> linesOriginal = delta.getOriginal().getLines();
			
			if(delta instanceof DeleteDelta) for(int i = 0; i < linesOriginal.size(); i ++) deletions.add(pos + i);
			else if(delta instanceof ChangeDelta) for(int i = 0; i < linesRevised.size(); i ++) changes.put(pos + i, (String) linesRevised.get(i));
			else if(delta instanceof InsertDelta) for(int i = 0; i < linesRevised.size(); i ++) insertions.put(pos + i, (String) linesRevised.get(i));
		}
		*/
		
		//File change action
		WebRebel.REBEL.getFrame().setTask("Sending changes...", true);
		JSON json = new JSON();
		json.put("type", extension);
	
		if(File.separatorChar == '\\') relativePath = relativePath.replaceAll("\\\\", "/");
		relativePath = relativePath.substring(1);
		json.put("source", relativePath);
		
		JSONArray lines = new JSONArray();
		
		if(extension.equals("html")){
			boolean body = false;
			for(String cur : newLines){
				if(cur.contains("<body>")){
					body = true;
					cur = cur.replaceFirst("<body>", "");
				}else if(cur.contains("</body>")) body = false;
				
				if(body) lines.put(cur);
			}
		}else for(String cur : newLines) lines.put(cur);
		
		json.put("lines", lines);
		
		/*
		json.put("parseTime", time);

		JSONArray deletionsArray = new JSONArray();
		for(int cur : deletions) deletionsArray.put(cur);
		json.put("deletions", deletionsArray);

		JSON changesMap = new JSON();
		for(Entry<Integer, String> cur : changes.entrySet()) changesMap.put(String.valueOf(cur.getKey()), cur.getValue());
		json.put("changes", changesMap);

		JSON insertionsMap = new JSON();
		for(Entry<Integer, String> cur : insertions.entrySet()) insertionsMap.put(String.valueOf(cur.getKey()), cur.getValue());
		json.put("insertions", insertionsMap);
		*/
		
		//TODO Find a better library for code changes
		
		Action action = new Action(ActionType.SERVER_CODE_UPDATE, UUID.randomUUID(), json);
		for(WebRebelSocket cur : WebRebel.REBEL.getConnections()) cur.sendAction(action);
		WebRebel.REBEL.getFrame().finishedTask();
		
		System.out.println("Streamed code changes successfully.");
		
	}
	
	private static List<String> read(File file) throws FileNotFoundException{
		
		List<String> lines = new ArrayList<>();
		Scanner scanner = new Scanner(file);
		
		while(scanner.hasNextLine()){
			String scannerLine = scanner.nextLine();
			lines.add(scannerLine);
		}
		scanner.close();
		
		return lines;
		
	}
	
}
