package me.deprilula28.WebRebel.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextPane;

public class ConsoleViewFrame extends JFrame{

	private JPanel contentPane;
	private JTextPane consoleTextPane;
	private JTree filterTree;
	private JComboBox<String> filterTypeComboBox;

	public ConsoleViewFrame(){
		
		setTitle("WebRebel Console View");
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
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
		
		JLabel filterTypeLabel = new JLabel("Filter Type:");
		GridBagConstraints gbc_filterTypeLabel = new GridBagConstraints();
		gbc_filterTypeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_filterTypeLabel.gridx = 0;
		gbc_filterTypeLabel.gridy = 2;
		selectionPanel.add(filterTypeLabel, gbc_filterTypeLabel);
		
		filterTypeComboBox = new JComboBox<>();
		filterTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Specific Clients", "Operating System", "Browser", "Device"}));
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
		filterScrollPane.setViewportView(filterTree);
		
		JScrollPane consoleScrollPane = new JScrollPane();
		consoleTextPane = new JTextPane();
		consoleScrollPane.setViewportView(consoleTextPane);
		splitPane.setRightComponent(consoleScrollPane);		

		setLocationRelativeTo(null);
		
	}
	
	public JTextPane getConsoleTextPane(){
		
		return consoleTextPane;
		
	}
	
	public JTree getFilterTree(){
		
		return filterTree;
		
	}
	
	public JComboBox<String> getFilterTypeComboBox(){
		
		return filterTypeComboBox;
		
	}

}
