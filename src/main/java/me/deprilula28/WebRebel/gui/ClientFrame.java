package me.deprilula28.WebRebel.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import org.json.JSON;

import me.deprilula28.WebRebel.ActionType;
import me.deprilula28.WebRebel.connection.Action;
import me.deprilula28.WebRebel.connection.WebRebelConnection;
import me.deprilula28.WebRebel.socket.ConsoleLog;
import me.deprilula28.WebRebel.socket.WebRebelSocket;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class ClientFrame extends JFrame{

	private JPanel contentPane;
	private JLabel connectionStatusLabel;
	private JButton disconnectButton;
	private JTextPane consoleLogsTextPane;
	private Style style;

	public ClientFrame(WebRebelSocket socket, Image img, JFrame frame){
		
		WebRebelConnection connection = socket.getConnection();
		setTitle("Client [" + connection + "]");
		setIconImage(img);
		setBounds(100, 100, 682, 406);
		setLocationRelativeTo(frame);

		addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(java.awt.event.WindowEvent e){
				
				socket.setFrame(null);
				
			};
			
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0d, 1.0, 0d};
		gbl_contentPane.rowWeights = new double[]{0d, 1.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		
		connectionStatusLabel = new JLabel("...");
		GridBagConstraints gbc_connectionStatusLabel = new GridBagConstraints();
		gbc_connectionStatusLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_connectionStatusLabel.insets = new Insets(0, 0, 5, 5);
		gbc_connectionStatusLabel.gridx = 1;
		gbc_connectionStatusLabel.gridy = 0;
		contentPane.add(connectionStatusLabel, gbc_connectionStatusLabel);
		
		JPanel innerDivisionPanel = new JPanel();
		GridBagConstraints gbc_innerDivisionPanel = new GridBagConstraints();
		gbc_innerDivisionPanel.insets = new Insets(0, 0, 5, 5);
		gbc_innerDivisionPanel.fill = GridBagConstraints.BOTH;
		gbc_innerDivisionPanel.gridx = 1;
		gbc_innerDivisionPanel.gridy = 1;
		contentPane.add(innerDivisionPanel, gbc_innerDivisionPanel);
		GridBagLayout gbl_innerDivisionPanel = new GridBagLayout();
		gbl_innerDivisionPanel.columnWidths = new int[]{0, 0, 0};
		gbl_innerDivisionPanel.rowHeights = new int[]{0, 0};
		gbl_innerDivisionPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_innerDivisionPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		innerDivisionPanel.setLayout(gbl_innerDivisionPanel);
		
		JPanel consoleLogsDivision = new JPanel();
		GridBagConstraints gbc_consoleLogsDivision = new GridBagConstraints();
		gbc_consoleLogsDivision.insets = new Insets(0, 0, 0, 5);
		gbc_consoleLogsDivision.fill = GridBagConstraints.BOTH;
		gbc_consoleLogsDivision.gridx = 0;
		gbc_consoleLogsDivision.gridy = 0;
		innerDivisionPanel.add(consoleLogsDivision, gbc_consoleLogsDivision);
		consoleLogsDivision.setLayout(new BorderLayout(0, 0));
		
		JLabel consoleTitleLabel = new JLabel("Console");
		consoleLogsDivision.add(consoleTitleLabel, BorderLayout.NORTH);
		
		JScrollPane consoleLogsScrollPane = new JScrollPane();
		consoleLogsDivision.add(consoleLogsScrollPane, BorderLayout.CENTER);
		
		consoleLogsTextPane = new JTextPane();
		consoleLogsTextPane.setEditable(false);
		consoleLogsScrollPane.setViewportView(consoleLogsTextPane);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		innerDivisionPanel.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel domLabel = new JLabel("DOM:");
		GridBagConstraints gbc_domLabel = new GridBagConstraints();
		gbc_domLabel.insets = new Insets(0, 0, 5, 0);
		gbc_domLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_domLabel.gridx = 0;
		gbc_domLabel.gridy = 0;
		panel.add(domLabel, gbc_domLabel);
		
		JScrollPane domScrollPane = new JScrollPane();
		GridBagConstraints gbc_domScrollPane = new GridBagConstraints();
		gbc_domScrollPane.fill = GridBagConstraints.BOTH;
		gbc_domScrollPane.gridx = 0;
		gbc_domScrollPane.gridy = 1;
		panel.add(domScrollPane, gbc_domScrollPane);
		
		JTree domTree = new JTree();
		domTree.setRootVisible(false);
		domTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("root") {
				{
					add(new DefaultMutableTreeNode("Loading"));
				}
			}
		));
		domScrollPane.setViewportView(domTree);
		
		JPanel actionsPanel = new JPanel();
		GridBagConstraints gbc_actionsPanel = new GridBagConstraints();
		gbc_actionsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_actionsPanel.fill = GridBagConstraints.BOTH;
		gbc_actionsPanel.gridx = 1;
		gbc_actionsPanel.gridy = 2;
		contentPane.add(actionsPanel, gbc_actionsPanel);
		
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setEnabled(socket.isConnected());
		actionsPanel.add(disconnectButton);
		disconnectButton.addActionListener((e) -> {
			socket.disconnect();
		});
		
		JButton pingButton = new JButton("Ping");
		actionsPanel.add(pingButton);
		pingButton.addActionListener((e) -> {
			pingButton.setEnabled(false);
			pingButton.setText("Ping...");
			
			socket.ping(() -> {
				pingButton.setText("Pong!");
				pingButton.setEnabled(true);
				
				new java.util.Timer().schedule(new TimerTask(){
					
					@Override
					public void run(){
						
						if(pingButton.getText().equals("Pong!")) pingButton.setText("Ping");
						
					}
					
				}, 1000l);
			});
		});
		
		JButton reloadRequestButton = new JButton("Request full reload");
		actionsPanel.add(reloadRequestButton);
		reloadRequestButton.addActionListener((e) -> {
			socket.sendAction(new Action(ActionType.SERVER_REQUEST_FULL_RELOAD, UUID.randomUUID(), new JSON()));
		});

		style = consoleLogsTextPane.addStyle("Connection Style", null);
		StyleConstants.setForeground(style, Color.black);
		
		List<ConsoleLog> logs = WebRebelSocket.logs.get(socket.getConnection());
		Collections.sort(logs, (o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp()));
		
		for(ConsoleLog cur : logs)
			try{
				consoleLogsTextPane.getDocument().insertString(consoleLogsTextPane.getDocument().getLength(), cur.toString() + "\n", style);
			}catch(BadLocationException e1){
				e1.printStackTrace();
			}
		
	}
		
	public void add(ConsoleLog log){

		try{
			consoleLogsTextPane.getDocument().insertString(consoleLogsTextPane.getDocument().getLength(), log.toString() + "\n", style);
		}catch(BadLocationException e){
			e.printStackTrace();
		}		
		
	}
	
	public void updateConnectionStatus(WebRebelSocket socket){
		
		StringBuilder stringBuilder = new StringBuilder("Connection status: ");
		
		if(socket.isConnected()){
			if(socket.isPending()) stringBuilder.append("Pending..");
			else if(socket.getPing() < 200) stringBuilder.append("All Good (" + socket.getPing() + "ms ping)");
			else stringBuilder.append("Slow (" + socket.getPing() + "ms ping)");
		}else stringBuilder.append("Disconnected");
		
		connectionStatusLabel.setText(stringBuilder.toString());
		disconnectButton.setEnabled(socket.isConnected());
		
	}

}
