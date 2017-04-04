package me.deprilula28.WebRebel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.deprilula28.WebRebel.gui.MainFrame;

public class FolderCopyTask implements Runnable{

	private ScheduledExecutorService executorService;
	private MainFrame frame;
	private long totalSumBytes;
	private long progress;

	private static long getSize(File folder){
		
		File[] files = folder.listFiles();
		long size = 0l;
		
		if(files != null)
			for(File f : files)
				if(f.isDirectory()) size += getSize(f);
				else size += f.length();
		
		return size;
		
	}
	
	private void processFolder(File baseFolder, File baseFolderTarget, File folder, File target) throws IOException{
		
		File[] files = folder.listFiles();
		
		if(files != null)
			for(File f : files)
				if(f.isDirectory()) processFolder(baseFolder, baseFolderTarget, f, target);
				else processFile(baseFolder, baseFolderTarget, f, target);
		
	}
	
	private void processFile(File baseFolderSource, File baseFolderTarget, File file, File targetFolder) throws IOException{
		
		File targetFile = new File(baseFolderTarget.getAbsolutePath() + file.getAbsolutePath().substring(baseFolderSource.getAbsolutePath().length()));
		if(targetFile.exists()) targetFile.delete();
		if(!targetFile.getParentFile().exists()) targetFile.getParentFile().mkdirs();
		targetFile.createNewFile();
		
		FileInputStream inputStream = new FileInputStream(file);
		FileOutputStream outputStream = new FileOutputStream(targetFile);
		
		byte[] outputByte = new byte[4096];
		int c = 0;
		
		while((c = inputStream.read(outputByte, 0, 4096)) != -1){
			outputStream.write(outputByte, 0, 4096);
			progress += c;
		}
		inputStream.close();
		outputStream.close();
		
	}
	
	public FolderCopyTask(MainFrame frame, File source, File target) throws IOException{
		
		frame.setTask("Copying files", false);
		executorService = Executors.newScheduledThreadPool(1);
		executorService.scheduleAtFixedRate(this, 0l, 250l, TimeUnit.MILLISECONDS);
		
		this.frame = frame;
		totalSumBytes = getSize(source);
		progress = 0;
		
		processFolder(source, target, source, target);
		
		executorService.shutdown();
		frame.finishedTask();
		
	}
	
	@Override
	public void run(){
		
		frame.setTaskProgress((int) Math.min(((float) progress / (float) totalSumBytes) * 100f, 100f));
		
	}
	
}
