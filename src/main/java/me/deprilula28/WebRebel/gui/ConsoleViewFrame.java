package me.deprilula28.WebRebel.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import me.deprilula28.WebRebel.Utils;
import me.deprilula28.WebRebel.WebRebel;
import me.deprilula28.WebRebel.connection.Browser;
import me.deprilula28.WebRebel.connection.OperatingSystem;
import me.deprilula28.WebRebel.connection.WebRebelConnection;
import me.deprilula28.WebRebel.socket.ConsoleLog;
import me.deprilula28.WebRebel.socket.WebRebelSocket;

public class ConsoleViewFrame extends JFrame implements TreeCellRenderer{

	private JPanel contentPane;
	private JTree filterTree;
	private JComboBox<String> filterTypeComboBox;
	private JScrollPane consoleScrollPane;
	
	private StyledDocument styledDocument;
	private ConsoleFilter filter;
	private JTextPane textPane;
	private Map<WebRebelConnection, Style> connStyles;
	private DefaultTreeCellRenderer treeCellRenderer;
	
	public ConsoleViewFrame(){
		
		setTitle("WebRebel Console View");
		
		try{
			setIconImage(ImageIO.read(new File("lib" + File.separatorChar + "WebRebelConsole.png")));
		}catch(Exception e){
			e.printStackTrace();
		}

		addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(java.awt.event.WindowEvent e){
				
				WebRebel.REBEL.getFrame().getConsoleViewButton().setText("Open Console View");
				
			};
			
		});
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 600, 450);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		setContentPane(contentPane);
		
		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JPanel selectionPanel = new JPanel();
		splitPane.setLeftComponent(selectionPanel);
		GridBagLayout gbl_selectionPanel = new GridBagLayout();
		gbl_selectionPanel.columnWidths = new int[]{69, 0};
		gbl_selectionPanel.rowHeights = new int[]{23, 0, 0, 0, 0, 0};
		gbl_selectionPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_selectionPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		selectionPanel.setLayout(gbl_selectionPanel);
		
		JLabel allClientsButtonLabel = new JLabel("All Clients");
		GridBagConstraints gbc_allClientsButtonLabel = new GridBagConstraints();
		gbc_allClientsButtonLabel.insets = new Insets(0, 0, 5, 0);
		gbc_allClientsButtonLabel.gridx = 0;
		gbc_allClientsButtonLabel.gridy = 0;
		selectionPanel.add(allClientsButtonLabel, gbc_allClientsButtonLabel);
		allClientsButtonLabel.setToolTipText("Click this to go back to all clients (If you're not on that)");
		
		JLabel filterTypeLabel = new JLabel("Filter Type:");
		GridBagConstraints gbc_filterTypeLabel = new GridBagConstraints();
		gbc_filterTypeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_filterTypeLabel.gridx = 0;
		gbc_filterTypeLabel.gridy = 2;
		selectionPanel.add(filterTypeLabel, gbc_filterTypeLabel);
		
		filterTypeComboBox = new JComboBox<String>();
		filterTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Specific Clients", "Operating System", "Browser"}));
		filterTypeComboBox.setSelectedIndex(0);
		GridBagConstraints gbc_filterTypeComboBox = new GridBagConstraints();
		gbc_filterTypeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_filterTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_filterTypeComboBox.gridx = 0;
		gbc_filterTypeComboBox.gridy = 3;
		selectionPanel.add(filterTypeComboBox, gbc_filterTypeComboBox);
		
		JScrollPane filterScrollPane = new JScrollPane();
		GridBagConstraints gbc_filterScrollPane = new GridBagConstraints();
		gbc_filterScrollPane.fill = GridBagConstraints.BOTH;
		gbc_filterScrollPane.gridx = 0;
		gbc_filterScrollPane.gridy = 4;
		selectionPanel.add(filterScrollPane, gbc_filterScrollPane);
		
		filterTree = new JTree();
		filterTree.setRootVisible(false);
		filterTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("root"){
				{
					add(new DefaultMutableTreeNode("No clients"));
				}
			}
		));
		
		allClientsButtonLabel.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent e){
				
				filter = new ConsoleFilter();
				allClientsButtonLabel.setText("All Clients");
				recalculateMessages();
				
			}
			
		});
		
		filterTree.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e){
				
				int selRow = filterTree.getRowForLocation(e.getX(), e.getY());
		        TreePath selPath = filterTree.getPathForLocation(e.getX(), e.getY());
		        if(selRow != -1 && e.getClickCount() == 2){
		        	TreeNode node = (TreeNode) selPath.getLastPathComponent();
		        	
		        	if(node instanceof ConnectionTreeNode){
		        		filter = new ConsoleFilter(((ConnectionTreeNode) node).getConnection());
		        		allClientsButtonLabel.setText("Single device");
		        	}else if(node instanceof OperatingSystemTreeNode){
		        		OperatingSystem os = ((OperatingSystemTreeNode) node).getOS();
		        		filter = new ConsoleFilter(os);
		        		allClientsButtonLabel.setText(os.toString());
		        	}else if(node instanceof BrowserTreeNode){
		        		Browser browser = ((BrowserTreeNode) node).getBrowser();
		        		filter = new ConsoleFilter(browser);
		        		allClientsButtonLabel.setText(browser.toString());
		        	}else return;
		        	
		        	recalculateMessages();
		        }
				
			}
			
		});
		filterScrollPane.setViewportView(filterTree);
		treeCellRenderer = (DefaultTreeCellRenderer) filterTree.getCellRenderer();
		filterTree.setCellRenderer(this);
		
		consoleScrollPane = new JScrollPane();
		splitPane.setRightComponent(consoleScrollPane);		
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		consoleScrollPane.setViewportView(textPane);
		styledDocument = textPane.getStyledDocument();

		setLocationRelativeTo(null);
		
		filterTypeComboBox.addItemListener((e) -> {
			WebRebelSocket.reloadTree();
		});
		filter = new ConsoleFilter();
		connStyles = new HashMap<>();
		
	}
	
	public void recalculateMessages(){
		
		Map<ConsoleLog, WebRebelConnection> usableLogs = new HashMap<>();
		
		for(Entry<WebRebelConnection, List<ConsoleLog>> cur : WebRebelSocket.logs.entrySet()){
			if(filter.doesFilter(cur.getKey())) continue;
			for(ConsoleLog curv : cur.getValue()) usableLogs.put(curv, cur.getKey());
		}
		
		textPane.setText("");
		
		for(Entry<ConsoleLog, WebRebelConnection> cur : Utils.sortByKeys(usableLogs, (o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp()))){
			try{
				styledDocument.insertString(styledDocument.getLength(), cur.getKey().toString() + "\n", connStyles.get(cur.getValue()));
			}catch(BadLocationException e){
				e.printStackTrace();
			}
		}
		
	}
	
	public void genConnStyle(WebRebelConnection connection){
		
		Style style = textPane.addStyle("Connection Style", null);
		StyleConstants.setForeground(style, connection.getUniqueColor());
		connStyles.put(connection, style);
		
	}
	
	public void addMessage(WebRebelConnection connection, ConsoleLog log){
		
		if(filter.doesFilter(connection)) return;
		JScrollBar bar = consoleScrollPane.getVerticalScrollBar();
		boolean atMax = bar.getValue() + bar.getModel().getExtent() + 12 >= bar.getMaximum();
		
		try{
			styledDocument.insertString(styledDocument.getLength(), log.toString() + "\n", connStyles.get(connection));
		}catch(BadLocationException e){
			e.printStackTrace();
		}
		
		if(atMax) textPane.setCaretPosition(styledDocument.getLength());
		
	}
		
	public JTree getFilterTree(){
		
		return filterTree;
		
	}
	
	public JComboBox<String> getFilterTypeComboBox(){
		
		return filterTypeComboBox;
		
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){
		
		DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
		
		try{
			if(nodo instanceof ConnectionTreeNode){
				ConnectionTreeNode connectionTreeNode = (ConnectionTreeNode) nodo;
				BufferedImage bufferedImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
				Graphics2D drawing = bufferedImage.createGraphics();
				
				WebRebelConnection connection = connectionTreeNode.getConnection();
				
				drawing.setColor(connection.getUniqueColor());
				drawing.fill(new Rectangle(1, 1, 14, 14));
				
				ImageIcon icon = new ImageIcon(bufferedImage);
				treeCellRenderer.setClosedIcon(icon);
				treeCellRenderer.setOpenIcon(icon);
				treeCellRenderer.setLeafIcon(icon);
				drawing.dispose();
			}else if(nodo instanceof OperatingSystemTreeNode){
				ImageIcon icon = null;
				
				switch(((OperatingSystemTreeNode) nodo).getOS().getType()){
				case ANDROID:
					icon = new ImageIcon(WebRebel.REBEL.getFrame().android);
					break;
				case IOS:
					icon = new ImageIcon(WebRebel.REBEL.getFrame().ios);
					break;
				case LINUX:
					icon = new ImageIcon(WebRebel.REBEL.getFrame().linux);
					break;
				case OS_X:
					icon = new ImageIcon(WebRebel.REBEL.getFrame().osx);
					break;
				case WINDOWS:
					icon = new ImageIcon(WebRebel.REBEL.getFrame().windows);
					break;
				}
				
				treeCellRenderer.setClosedIcon(icon);
				treeCellRenderer.setOpenIcon(icon);
				treeCellRenderer.setLeafIcon(icon);
			}
		}catch(Exception e){
			System.err.println("Error setting JTree Node Image:");
			e.printStackTrace();
		}
		
		return treeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
	}
	
}
