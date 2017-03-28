package me.deprilula28.WebRebel.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.json.JSONObject;

import me.deprilula28.WebRebel.FolderCopyTask;
import me.deprilula28.WebRebel.WebRebel;


public class WebRebelFrame extends JFrame{

	private JPanel contentPane;
	private JProgressBar taskProgressBar;
	private JLabel taskLabel;
	private File folder;
	private JLabel folderSelectedLabel;
	private JLabel requestsLabel;
	private JTree clientTree;
	
	public WebRebelFrame(){
		
		setResizable(false);
		setTitle("Web Rebel v" + WebRebel.VERSION);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 376, 462);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel clientsLabel = new JLabel("Clients");
		clientsLabel.setBounds(10, 270, 350, 14);
		contentPane.add(clientsLabel);
		
		JScrollPane clientsScrollPane = new JScrollPane();
		clientsScrollPane.setBounds(10, 288, 350, 109);
		contentPane.add(clientsScrollPane);
		
		clientTree = new JTree();
		clientTree.setRootVisible(false);
		clientTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("root"){
				{
					add(new DefaultMutableTreeNode("No connections."));
				}
			}
		));
		clientsScrollPane.setViewportView(clientTree);
		
		JLabel folderLabel = new JLabel("Folder:");
		folderLabel.setBounds(10, 11, 350, 14);
		contentPane.add(folderLabel);
		
		folderSelectedLabel = new JLabel("Choose one");
		folderSelectedLabel.setBounds(10, 29, 350, 14);
		contentPane.add(folderSelectedLabel);
		
		JButton selectButton = new JButton("Select");
		selectButton.setBounds(10, 54, 89, 23);
		contentPane.add(selectButton);
		
		selectButton.addActionListener((event) -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Directory for input");
			fileChooser.setCurrentDirectory(new File("."));
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = fileChooser.showOpenDialog(WebRebelFrame.this);
			
			if(returnValue == JFileChooser.APPROVE_OPTION){
				setTask("Cleaning old folder", true);
				WebRebel.clear(new File("folderBackup" + File.separatorChar + "path"), false);
				
				File folder = fileChooser.getSelectedFile() == null ? fileChooser.getCurrentDirectory() : fileChooser.getSelectedFile();
				System.out.println("Selected folder: " + folder.getAbsolutePath());
				
				try{
					new FolderCopyTask(this, folder, new File("folderBackup" + File.separatorChar + "path"));
				}catch(Exception e){
					System.err.println("Failed to load selected folder:");
					e.printStackTrace();
				}
				
				setTask("Configuring metadata file", true);
				File metadataFile = new File("folderBackup" + File.separatorChar + "metadata.json");
				if(metadataFile.exists()) metadataFile.delete();
				try{
					metadataFile.createNewFile();
				}catch(IOException e){
					e.printStackTrace();
				}
				
				try{
					FileWriter writer = new FileWriter(metadataFile);
					
					try{
						writer.write(new JSONObject().put("originPath", folder.getAbsolutePath()).toString());
					}catch(Exception e2){
						System.err.println("Failed to write metadata file.");
						e2.printStackTrace();
					}finally{
						writer.close();
					}
				}catch(IOException e){
					System.err.println("Failed to write file.");
					e.printStackTrace();
				}
				finishedTask();
				
				folderSelectedLabel.setText(folder.getAbsolutePath());
				this.folder = folder;
			}
		});
		
		taskLabel = new JLabel("No tasks");
		taskLabel.setBounds(10, 87, 350, 14);
		contentPane.add(taskLabel);
		
		taskProgressBar = new JProgressBar();
		taskProgressBar.setBounds(10, 104, 350, 14);
		contentPane.add(taskProgressBar);
		
		JLabel statisticsLabel = new JLabel("Statistics for Paranoids");
		statisticsLabel.setBounds(10, 135, 350, 14);
		contentPane.add(statisticsLabel);
		
		requestsLabel = new JLabel("0 requests");
		requestsLabel.setBounds(10, 160, 141, 14);
		contentPane.add(requestsLabel);
				
	}
	
	public JTree getClientTree(){
	
		return clientTree;
	
	}
	
	public JLabel getRequestsLabel(){
		
		return requestsLabel;
		
	}
	
	public void setFolder(File folder){
	
		this.folder = folder;
		folderSelectedLabel.setText(folder.getAbsolutePath());
	
	}
	
	public File getFolder(){
	
		return folder;
	
	}
	
	public void setTask(String name, boolean inderterminateProgress){
		
		taskLabel.setText(name);
		
		if(inderterminateProgress) taskProgressBar.setIndeterminate(true);
		else taskProgressBar.setValue(0);
		
	}
	
	public void finishedTask(){
		
		taskProgressBar.setValue(0);
		taskProgressBar.setIndeterminate(false);
		taskLabel.setText("No tasks");
		
	}
	
	public void setTaskProgress(int value){
		
		taskProgressBar.setValue(value);
		
	}
}
