package me.deprilula28.WebRebel.updateListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import javax.swing.JOptionPane;

import me.deprilula28.WebRebel.WebRebel;

public class FileWatcher extends Thread{
	
	private WatchService watcher;
	private File folder;
	public boolean stop;
	
	public FileWatcher(File folder) throws IOException{

		watcher = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(folder.toURI());
		path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
		this.folder = folder;
		
		setName("File Update Listener");
		
	}
	
	@Override
	public void run(){
		
		System.out.println("Started");
		
		while(!stop){
			try{
				final WatchKey wk = watcher.take();
				if(wk != null){
					List<WatchEvent<?>> events = wk.pollEvents();
	
					for(WatchEvent<?> cur : events){
						if(cur.kind().equals(StandardWatchEventKinds.OVERFLOW)){
							System.out.println("Overflow :\\");
							continue;
						}
						WatchEvent<Path> ev = (WatchEvent<Path>) cur;
						File backupFolderLocation = new File("folderBackup" + File.separatorChar + "path" + File.separatorChar + ev.context().toString());
						File file = new File(folder.getAbsolutePath() + File.separatorChar + ev.context().toString());
						
						if(cur.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)){
							//Creating file in the backup folder
							if(backupFolderLocation.exists()) backupFolderLocation.delete();
							if(!backupFolderLocation.getParentFile().exists()) backupFolderLocation.getParentFile().mkdirs();
							backupFolderLocation.createNewFile();
							
							InputStream inputStream = new FileInputStream(file);
							OutputStream outputStream = new FileOutputStream(backupFolderLocation);
							byte[] outputByte = new byte[4096];
							int c = 0;
							
							while((c = inputStream.read(outputByte, 0, 4096)) != -1) outputStream.write(outputByte, 0, c);
							
							inputStream.close();
							outputStream.close();
						}else if(cur.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)){
							if(!backupFolderLocation.exists()){
								System.err.println("[Error] Informed of file deletion at " + file.getAbsolutePath() + " but it doesn't exist in " + backupFolderLocation.getAbsolutePath());
								continue;
							}
							
							backupFolderLocation.delete();
						}else if(cur.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)){
							if(!backupFolderLocation.exists()){
								System.err.println("[Error] Informed of file change at " + file.getAbsolutePath() + " but it doesn't exist in " + backupFolderLocation.getAbsolutePath());
								continue;
							}
							
							String extension = "";
							String fileName = backupFolderLocation.getAbsolutePath();
							
							int i = fileName.lastIndexOf('.');
							int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
	
							if (i > p) extension = fileName.substring(i + 1);
							
							if(extension.equals("js") || extension.equals("css") || extension.equals("html"))
								UpdateHandler.doFileUpdate(backupFolderLocation, file, extension);
							
							backupFolderLocation.delete();
							backupFolderLocation.createNewFile();
							
							InputStream inputStream = new FileInputStream(file);
							OutputStream outputStream = new FileOutputStream(backupFolderLocation);
							byte[] outputByte = new byte[4096];
							int c = 0;
							
							while((c = inputStream.read(outputByte, 0, 4096)) != -1) outputStream.write(outputByte, 0, c);
							
							inputStream.close();
							outputStream.close();
						}
					}
					
					if(!wk.reset()){
						JOptionPane.showMessageDialog(WebRebel.REBEL.getFrame(), "An error occured while handling system file update event\nFailed to reset key");
						break;
					}
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(WebRebel.REBEL.getFrame(), "An error occured while handling system file update event\n" + e.getClass().getName() + ": " +
						e.getMessage());
				System.err.println("Failed to handle file watcher event");
				e.printStackTrace();
			}
		}
		
		try{
			watcher.close();
		}catch(IOException e){
			System.err.println("Failed to close watcher");
			e.printStackTrace();			
		}
		
	}
	
}
