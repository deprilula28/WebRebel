package me.deprilula28.WebRebel.gui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import me.deprilula28.WebRebel.ActionType;
import me.deprilula28.WebRebel.connection.*;
import me.deprilula28.WebRebel.socket.WebRebelSocket;
import org.json.JSON;

import me.deprilula28.WebRebel.FolderCopyTask;
import me.deprilula28.WebRebel.WebRebel;
import me.deprilula28.WebRebel.updateListener.FolderWatcher;

public class MainFrame extends JFrame implements TreeCellRenderer{

	public FolderWatcher watcher;
	public File folder;
	private JLabel taskLabel;
	private JProgressBar taskProgressBar;
	private JLabel folderSelectedLabel;
	private JTree clientsTree;
	private JButton consoleViewButton;
	private DefaultTreeCellRenderer treeCellRenderer;
    private ConsoleViewFrame consoleViewFrame;
	
	private Image noEntry;

    public Image unknown;
    public Image osx;
    public Image windows;
    public Image linux;
    public Image android;
    public Image ios;
	
	private Image chrome;
	private Image firefox;
	private Image edge;
	private Image ie;
	private Image safari;

	private Image pending;
	private Image oneBar;
	private Image twoBar;
	private Image threeBar;
	private Image fourBar;
	private Image fiveBar;

	public MainFrame(){
		
		setTitle("WebRebel " + WebRebel.VERSION);
		try{
			setIconImage(ImageIO.read(new File("lib" + File.separatorChar + "Logo.png")));
			noEntry = ImageIO.read(new File("lib" + File.separatorChar + "noConnectionsNode.png"));
			
			unknown = ImageIO.read(new File("lib" + File.separatorChar + "OS" + File.separatorChar + "unknownOS.png"));
			osx = ImageIO.read(new File("lib" + File.separatorChar + "OS" + File.separatorChar + "osx.png"));
			linux = ImageIO.read(new File("lib" + File.separatorChar + "OS" + File.separatorChar + "linux.png"));
			windows = ImageIO.read(new File("lib" + File.separatorChar + "OS" + File.separatorChar + "windows.png"));
			android = ImageIO.read(new File("lib" + File.separatorChar + "OS" + File.separatorChar + "android.png"));
			ios = ImageIO.read(new File("lib" + File.separatorChar + "OS" + File.separatorChar + "ios.png"));

			chrome = ImageIO.read(new File("lib" + File.separatorChar + "Browser" + File.separatorChar + "Chrome.png"));
			firefox = ImageIO.read(new File("lib" + File.separatorChar + "Browser" + File.separatorChar + "Firefox.png"));
			edge = ImageIO.read(new File("lib" + File.separatorChar + "Browser" + File.separatorChar + "Edge.png"));
			ie = ImageIO.read(new File("lib" + File.separatorChar + "Browser" + File.separatorChar + "IE.png"));
			safari = ImageIO.read(new File("lib" + File.separatorChar + "Browser" + File.separatorChar + "Safari.png"));

			pending = ImageIO.read(new File("lib" + File.separatorChar + "ConnectionQuality" + File.separatorChar + "pending.png"));
			oneBar = ImageIO.read(new File("lib" + File.separatorChar + "ConnectionQuality" + File.separatorChar + "oneBar.png"));
			twoBar = ImageIO.read(new File("lib" + File.separatorChar + "ConnectionQuality" + File.separatorChar + "twoBar.png"));
			threeBar = ImageIO.read(new File("lib" + File.separatorChar + "ConnectionQuality" + File.separatorChar + "threeBar.png"));
			fourBar = ImageIO.read(new File("lib" + File.separatorChar + "ConnectionQuality" + File.separatorChar + "fourBar.png"));
			fiveBar = ImageIO.read(new File("lib" + File.separatorChar + "ConnectionQuality" + File.separatorChar + "fiveBar.png"));
		}catch(IOException e){
			System.err.println("Failed to set WebRebel frame icon:");
			e.printStackTrace();
		}
		
		addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent){

				Toolkit.getDefaultToolkit().beep();
				int response = JOptionPane.showConfirmDialog(MainFrame.this, "All the connections will be left pending.", "Are you sure?", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				
				if(response == JOptionPane.YES_OPTION) System.exit(-1);
				
			}
			
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 443);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{5, 0, 5};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0d, 1.0, 0d};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0d, 0d, 0.0, 0.0, 1d, 0.0, 0d};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel folderSelectedTitleLabel = new JLabel("Selected Folder:");
		GridBagConstraints gbc_folderSelectedTitleLabel = new GridBagConstraints();
		gbc_folderSelectedTitleLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_folderSelectedTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_folderSelectedTitleLabel.gridx = 1;
		gbc_folderSelectedTitleLabel.gridy = 0;
		contentPane.add(folderSelectedTitleLabel, gbc_folderSelectedTitleLabel);
		
		folderSelectedLabel = new JLabel("None selected yet");
		GridBagConstraints gbc_folderSelectedLabel = new GridBagConstraints();
		gbc_folderSelectedLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_folderSelectedLabel.insets = new Insets(0, 0, 5, 5);
		gbc_folderSelectedLabel.gridx = 1;
		gbc_folderSelectedLabel.gridy = 1;
		contentPane.add(folderSelectedLabel, gbc_folderSelectedLabel);
		folderSelectedLabel.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent e){
			
				try{
					JFileChooser fileChooser = new JFileChooser();		
					fileChooser.setDialogTitle("Directory for input");		
					fileChooser.setCurrentDirectory(new File("."));		
					fileChooser.setAcceptAllFileFilterUsed(false);		
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
					int returnValue = fileChooser.showOpenDialog(MainFrame.this);		
					 					
		 			if(returnValue == JFileChooser.APPROVE_OPTION){		
		 				setTask("Cleaning old folder", true);		
		 				WebRebel.clear(new File("folderBackup" + File.separatorChar + "path"), false);		
		 						
		 				File folder = fileChooser.getSelectedFile() == null ? fileChooser.getCurrentDirectory() : fileChooser.getSelectedFile();		
		 				System.out.println("Selected folder: " + folder.getAbsolutePath());		
		 						
		 				try{		
		 					new FolderCopyTask(MainFrame.this, folder, new File("folderBackup" + File.separatorChar + "path"));		
		 				}catch(Exception e3){		
		 					System.err.println("Failed to load selected folder:");		
		 					e3.printStackTrace();		
		 				}		
		 						
		 				setTask("Configuring metadata file", true);		
		 				File metadataFile = new File("folderBackup" + File.separatorChar + "metadata.json");		
		 				if(metadataFile.exists()) metadataFile.delete();		
		 				try{		
		 					metadataFile.createNewFile();		
		 				}catch(IOException e3){		
		 					e3.printStackTrace();		
		 				}		
		 						
		 				try{		
		 					FileWriter writer = new FileWriter(metadataFile);		
		 							
		 					try{		
		 						writer.write(new JSON().put("originPath", folder.getAbsolutePath()).toString());		
		 					}catch(Exception e2){		
		 						System.err.println("Failed to write metadata file.");		
		 						e2.printStackTrace();		
		 					}finally{		
		 						writer.close();		
		 					}		
		 				}catch(IOException e3){		
		 					System.err.println("Failed to write file.");		
		 					e3.printStackTrace();		
		 				}		
		 				finishedTask();		
		 						
		 				folderSelectedLabel.setText(folder.getAbsolutePath());		
		 			setFolder(folder);
		 				if(watcher != null) watcher.stop = true;		
		 				try{		
		 					watcher = new FolderWatcher(folder);		
		 					watcher.start();		
		 				}catch(Exception e3){		
		 					System.err.println("Failed to set file watcher");		
		 					e3.printStackTrace();		
		 				}		
		 			}
				}catch(Exception e2){
					e2.printStackTrace();
				}
				
			}
			
		});
		
		taskLabel = new JLabel("No Task");
		GridBagConstraints gbc_taskLabel = new GridBagConstraints();
		gbc_taskLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_taskLabel.insets = new Insets(0, 0, 5, 5);
		gbc_taskLabel.gridx = 1;
		gbc_taskLabel.gridy = 3;
		contentPane.add(taskLabel, gbc_taskLabel);
		
		taskProgressBar = new JProgressBar();
		GridBagConstraints gbc_taskProgressBar = new GridBagConstraints();
		gbc_taskProgressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_taskProgressBar.insets = new Insets(0, 0, 5, 5);
		gbc_taskProgressBar.gridx = 1;
		gbc_taskProgressBar.gridy = 4;
		contentPane.add(taskProgressBar, gbc_taskProgressBar);
		
		JLabel clientsTitleLabel = new JLabel("Clients:");
		GridBagConstraints gbc_clientsTitleLabel = new GridBagConstraints();
		gbc_clientsTitleLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_clientsTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_clientsTitleLabel.gridx = 1;
		gbc_clientsTitleLabel.gridy = 6;
		contentPane.add(clientsTitleLabel, gbc_clientsTitleLabel);

        JScrollPane clientsScrollPane = new JScrollPane();
		GridBagConstraints gbc_clientsScrollPane = new GridBagConstraints();
		gbc_clientsScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_clientsScrollPane.fill = GridBagConstraints.BOTH;
		gbc_clientsScrollPane.gridx = 1;
		gbc_clientsScrollPane.gridy = 7;
		contentPane.add(clientsScrollPane, gbc_clientsScrollPane);
		
		clientsTree = new JTree();
		clientsTree.setRootVisible(false);
		clientsTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("root"){
				{
					add(new DefaultMutableTreeNode("No clients"));
				}
			}
		));
		clientsTree.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e){
				
				int selRow = clientsTree.getRowForLocation(e.getX(), e.getY());
		        TreePath selPath = clientsTree.getPathForLocation(e.getX(), e.getY());
		        if(selRow != -1 && e.getClickCount() == 2){
		        	TreeNode node = (TreeNode) selPath.getLastPathComponent();
		        	if(node instanceof ConnectionTreeNode){
			        	try{
							Image image = getImageForNode(node);
							ClientFrame clientFrame = new ClientFrame(((ConnectionTreeNode) node).getSocket(), image, MainFrame.this);
							clientFrame.setVisible(true);
							((ConnectionTreeNode) node).getSocket().setFrame(clientFrame);
							clientFrame.updateConnectionStatus(((ConnectionTreeNode) node).getSocket());
						}catch(Exception e1){
							System.err.println("Failed to open client panel");
							e1.printStackTrace();
						}
		        	}
		        }
				
			}
			
		});
		treeCellRenderer = (DefaultTreeCellRenderer) clientsTree.getCellRenderer();
		clientsTree.setCellRenderer(this);
		clientsScrollPane.setViewportView(clientsTree);

        JPanel buttonsPanel = new JPanel();
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_buttonsPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonsPanel.gridx = 1;
		gbc_buttonsPanel.gridy = 9;
		contentPane.add(buttonsPanel, gbc_buttonsPanel);
		GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		gbl_buttonsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_buttonsPanel.rowHeights = new int[]{0, 0};
		gbl_buttonsPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_buttonsPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		buttonsPanel.setLayout(gbl_buttonsPanel);
		
		consoleViewButton = new JButton("Open Console View");
		GridBagConstraints gbc_consoleViewButton = new GridBagConstraints();
		gbc_consoleViewButton.insets = new Insets(0, 0, 0, 5);
		gbc_consoleViewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_consoleViewButton.gridx = 0;
		gbc_consoleViewButton.gridy = 0;
		buttonsPanel.add(consoleViewButton, gbc_consoleViewButton);

        JButton refreshPageEverywhereButton = new JButton("Refresh Everywhere");
		GridBagConstraints gbc_refreshPageEverywhereButton = new GridBagConstraints();
		gbc_refreshPageEverywhereButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_refreshPageEverywhereButton.gridx = 1;
		gbc_refreshPageEverywhereButton.gridy = 0;
		buttonsPanel.add(refreshPageEverywhereButton, gbc_refreshPageEverywhereButton);
		
		consoleViewFrame = new ConsoleViewFrame();
		consoleViewButton.addActionListener((event) -> {
			consoleViewFrame.setLocationRelativeTo(MainFrame.this);
			consoleViewFrame.setVisible(!consoleViewFrame.isVisible());
			consoleViewButton.setText(consoleViewFrame.isVisible() ? "Hide Console View" : "Open Console View");
		});

        refreshPageEverywhereButton.addActionListener((event) -> {
            for(WebRebelSocket cur : WebRebel.REBEL.getConnections()) cur.sendAction(new Action(ActionType.SERVER_REQUEST_FULL_RELOAD,
                    UUID.randomUUID(), new JSON()));
        });
		
		setLocationRelativeTo(null);
		
	}
	
	public JButton getConsoleViewButton(){
	
		return consoleViewButton;
	
	}

	public JTree getClientTree(){
	
		return clientsTree;
	
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
	
	public Image getImageForNode(TreeNode nodo) throws Exception{
		
		if(nodo instanceof ConnectionTreeNode){
			ConnectionTreeNode connectionTreeNode = (ConnectionTreeNode) nodo;
			BufferedImage bufferedImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics2D drawing = bufferedImage.createGraphics();
			
			WebRebelConnection connection = connectionTreeNode.getConnection();
			UseragentParser useragentParser = connection.getUserAgentParser();
			
			OperatingSystemType os = useragentParser.getOperatingSystem().getType();
			if(os == null) drawing.drawImage(unknown, 0, 0, null);
			else switch(os){
			case ANDROID:
				drawing.drawImage(android, 0, 0, null);
				break;
			case IOS:
				drawing.drawImage(ios, 0, 0, null);
				break;
			case LINUX:
				drawing.drawImage(linux, 0, 0, null);
				break;
			case OS_X:
				drawing.drawImage(osx, 0, 0, null);
				break;
			case WINDOWS:
				drawing.drawImage(windows, 0, 0, null);
				break;
			}
			
			BrowserType browser = useragentParser.getBrowser().getType();
			if(browser != null) switch(browser){
			case CHROME:
				drawing.drawImage(chrome, 10, 10, null);
				break;
			case EDGE:
				drawing.drawImage(edge, 10, 10, null);
				break;
			case FIREFOX:
				drawing.drawImage(firefox, 10, 10, null);
				break;
			case IE:
				drawing.drawImage(ie, 10, 10, null);
				break;
			case SAFARI:
				drawing.drawImage(safari, 10, 10, null);
				break;
			}
			
			if(connectionTreeNode.isPending()) drawing.drawImage(pending, 10, 0, null);
			else{
				int ping = connectionTreeNode.getPing();

				if(ping < 50) drawing.drawImage(fiveBar, 10, 0, null);
				else if(ping < 100) drawing.drawImage(fourBar, 10, 0, null);
				else if(ping < 400) drawing.drawImage(threeBar, 10, 0, null);
				else if(ping < 999) drawing.drawImage(twoBar, 10, 0, null);
				else drawing.drawImage(oneBar, 10, 0, null);
			}
			
			return bufferedImage;
		}else{
			return noEntry;
		}
		
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){
		
		DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
		
		try{
			ImageIcon icon = new ImageIcon(getImageForNode(nodo));
			treeCellRenderer.setClosedIcon(icon);
			treeCellRenderer.setOpenIcon(icon);
			treeCellRenderer.setLeafIcon(icon);
		}catch(Exception e){
			System.err.println("Error setting JTree Node Image:");
			e.printStackTrace();
		}
		
		return treeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
	}
	
	public ConsoleViewFrame getConsoleViewFrame(){
	
		return consoleViewFrame;
	
	}
	
}
