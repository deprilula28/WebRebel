package me.deprilula28.WebRebel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.json.JSON;

import me.deprilula28.WebRebel.gui.MainFrame;
import me.deprilula28.WebRebel.servlet.FolderServlet;
import me.deprilula28.WebRebel.servlet.MainPageServlet;
import me.deprilula28.WebRebel.socket.LiveServlet;
import me.deprilula28.WebRebel.socket.WebRebelSocket;
import me.deprilula28.WebRebel.updateListener.FolderWatcher;

public class WebRebel{
	
	public static final String VERSION = "0.1_00a";
	public static WebRebel REBEL;
	private MainFrame frame;
	private List<WebRebelSocket> connections;
	
	public static void main(String[] args){
		
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e){
			e.printStackTrace();
		}
		System.out.println("WebRebel v" + VERSION);
		System.out.println("<> with <3 by deprilula28");
		System.out.println("-====-");
		System.out.println();
		
		try{
			Log.setLog(new LogRuleset());
			REBEL = new WebRebel();
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fatal error occured while loading WebRebel:\n" + e.getClass().getName() + ": " + e.getMessage() + "\nPlease report this.",
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		
	}
	
	public static void clear(File folder, boolean delete){
		
		File[] files = folder.listFiles();
		if(files != null)
			for(File f : files)
				if(f.isDirectory()) clear(f, true);
				else f.delete();
		
		if(delete) folder.delete();
		
	}
	
	public WebRebel() throws IOException{
		
		connections = new CopyOnWriteArrayList<>();
		
		frame = new MainFrame();

		frame.setVisible(true);
		
		File metadataFile = new File("folderBackup" + File.separatorChar + "metadata.json");
		if(metadataFile.exists()){
			frame.setTask("Verifying folder", true);
			Scanner scann = new Scanner(new FileInputStream(metadataFile));
			StringBuilder stringBuilder = new StringBuilder();
			
			while(scann.hasNextLine()) stringBuilder.append(scann.nextLine());
			scann.close();
			
			JSON json = new JSON(stringBuilder.toString());
			File target = new File(json.getString("originPath"));
			
			if(target.exists() && target.isDirectory()){
				System.out.println("Folder exists!");
				frame.setFolder(target);
				frame.setTask("Deleting temporary backup folder", true);
				clear(new File("folderBackup" + File.separatorChar + "path"), false);
				System.out.println("Putting new content in");
				new FolderCopyTask(frame, target, new File("folderBackup" + File.separatorChar + "path"));
			}else{
				System.err.println("[Warning] Old selected folder doesn't exist!");
				
				frame.setTask("Deleting temporary backup folder", true);
				metadataFile.delete();
				clear(new File("folderBackup" + File.separatorChar + "path"), false);
				System.err.println("Contents cleared.");
			}
		}
		
		frame.setTask("Creating server", true);
		
		Thread thread = new Thread(){
			
			@Override
			public void run(){
				
				try{
					Server server = new Server(80);
					
					ServletContextHandler mainHandler = new ServletContextHandler(server, "/");
					mainHandler.addServlet(LiveServlet.class, "/socket");
					mainHandler.addServlet(FolderServlet.class, "/folder");
					mainHandler.addServlet(MainPageServlet.class, "/");
					
					try{
						frame.setTask("Initializing server", true);
						server.start();
						server.dump(System.out);
					}catch(Exception e){
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Fatal error occured while setting up Jetty:\n" + e.getClass().getName() + ": " + e.getMessage() + "\nPlease report this.",
								"Error", JOptionPane.ERROR_MESSAGE);
						System.exit(-1);
					}
					
					frame.setTask("Loading file listener", true);

					if(frame.folder != null)
						try{
							frame.watcher = new FolderWatcher(frame.folder);
							frame.watcher.start();
						}catch(Exception e){
							System.err.println("Failed to set file watcher");
							e.printStackTrace();
						}
					
					frame.finishedTask();
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Fatal error occured while setting up Jetty:\n" + e.getClass().getName() + ": " + e.getMessage() + "\nPlease report this.",
							"Error", JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
				
				frame.finishedTask();
				
			}
			
		};
		
		thread.setName("WebRebel");
		thread.setDaemon(false);
		thread.start();		
		
	}
	
	public MainFrame getFrame(){
	
		return frame;
	
	}
	
	public List<WebRebelSocket> getConnections(){
	
		return connections;
	
	}
	
}
