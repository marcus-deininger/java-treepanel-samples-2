package trees.applications.twothreetree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import trees.panel.FontPanel;
import trees.panel.TreePanel;
import trees.style.Alignment;
import trees.style.Shape;
import trees.style.Size;
import trees.style.Style;
import trees.synchronization.MonitorEntryEvent;
import trees.synchronization.MonitorExitEvent;
import trees.synchronization.MonitorListener;

import static trees.synchronization.Monitor.monitor;

@SuppressWarnings("serial")
public class TwoThreeTreeView extends JFrame{
	
	private static final int WIDTH = 800, HEIGHT = 600;
	private static final int DIALOG_WIDTH = 300, DIALOG_HEIGHT = 300;
	
	private enum Options{
		CANCEL("Cancel"), RUN("Run"), STEP("Step");
		
		private String label;
		private Options(String label) { this.label = label; }		
		public String toString(){ return label; }
	}

	private Tree tree;

	// Define functionals widgets here
	private JButton addTreeButton, addButton, nextButton, searchButton, clearButton;
	private JTextArea console;
	
	private JDialog dialog;
	
	private TreePanel<Node> treePanel, dialogTreePanel;

	public TwoThreeTreeView(){
		super("2-3 Trees");
		
		// initialize model		
		tree = new Tree();
		
		// create instances of functional widgets here
		treePanel = new TreePanel<>();
		addTreeButton = new JButton("Add Tree ...");
		addButton = new JButton("Add ...");
		nextButton = new JButton("Next");
		searchButton = new JButton("Search ...");
		clearButton = new JButton("Clear");
		console = new JTextArea(7, 20);	
		
		dialog = new JDialog(this, "Dangling Subtree", false);
		dialogTreePanel = new TreePanel<>();

		initializeWidgets();
		JPanel panel = createWidgetLayout();		
		createWidgetInteraction();
	
		this.setContentPane(panel);
		
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.pack();
		this.setVisible(true);
	}
	
	private void initializeWidgets() {
		Style style = new Style(10, 10, 60);
		style.setHorizontalAlignment(Alignment.ROOT_CENTER);
		style.setRootPointer("root");
		style.setPointerBoxes(KeyNode.class, true);
		style.setSize(KeyNode.class, Size.MIN_VARIABLE(40, 20));
		style.setShape(DataNode.class, Shape.ROUNDED_RECTANGLE);
		treePanel.setStyle(style);
		
		console.setEditable(false);
		updateButtons(false);		
		
		dialog.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		dialogTreePanel.setStyle(style, false);
		dialog.add(dialogTreePanel);
		dialog.pack();
	}
	
	private JPanel createWidgetLayout() {
		JPanel panel = new JPanel(new BorderLayout());
		// create the layout here - if needed define supporting widgets like labels, etc.
		panel.add(treePanel.addScrollPane(), BorderLayout.CENTER);
		
		
		JPanel control = new JPanel(new BorderLayout());
		
			JPanel buttons = new JPanel();
			buttons.add(addTreeButton);
			buttons.add(addButton);
			buttons.add(nextButton);
			buttons.add(searchButton);
			buttons.add(clearButton);
			buttons.add(new FontPanel(treePanel.getStyle()));
			control.add(buttons, BorderLayout.NORTH);
	
			JScrollPane scrollPane = new JScrollPane(console,
	  							JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	  							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			control.add(scrollPane, BorderLayout.CENTER);
	
		panel.add(control, BorderLayout.SOUTH);
		
		return panel;
	}
	
	private void createWidgetInteraction() {
		// add Listeners, etc. here
		
		addTreeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				treePanel.clearNodeColor();
				monitor.disableBreakpoints();

				String value = JOptionPane.showInputDialog(TwoThreeTreeView.this, "New keys");
				if(value == null)
					return;
				
				int[] keys = null;
				try {
					String[] values = value.split("\\s*,\\s*|\\s+");
					keys = new int[values.length];
					for(int i = 0; i < values.length; i++)
						keys[i] = Integer.parseInt(values[i]);
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Elements could not be entered.", "New keys", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}

				boolean ok = true;
				for(int key : keys)
					ok = ok & tree.add(key, (char)('A' + key) + "");				
				if(!ok)
                	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Some elements could not be entered.", "New key", JOptionPane.PLAIN_MESSAGE);

				treePanel.setTree(tree.getRoot());
				updateButtons(false);
			}			
		});
	
	addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				treePanel.clearNodeColor();
				monitor.disableBreakpoints();
				
				JTextField input = new JTextField();
				Object[] messages = {"Key", input};
				JOptionPane option = new JOptionPane(messages, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, Options.values());
				JDialog dialog = option.createDialog(TwoThreeTreeView.this, "New key");
				dialog.setVisible(true);
				Options value = (Options)option.getValue();

				if(value == null || value == Options.CANCEL) // cancelled
					return;
				
				int key = 0;
				try {
					key = Integer.parseInt(input.getText());
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Element could not be entered.", "New key", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}
				
				
				String data = (char)('A' + key) + "";
				boolean ok;
				
				switch(value){
					case CANCEL	: break;
					
					case RUN	:	
						ok = tree.add(key, data);
						if(!ok){
							JOptionPane.showMessageDialog(TwoThreeTreeView.this, 
									"Element could not be entered.", 
									"New key", JOptionPane.PLAIN_MESSAGE);
							return;
						}
						treePanel.setTree(tree.getRoot());
						updateButtons(false);
						break;
						
					case STEP	:
						console.setText("");
						monitor.enableBreakpoints();
						updateButtons(true);
						monitor.invokeAsync(tree, "add", key, data);
						break;
				}
			}			
		});
	
		nextButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				treePanel.clearNodeColor();
				monitor.resume();
			}
			
		});
	
		searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				treePanel.clearNodeColor();
	            String value = JOptionPane.showInputDialog(TwoThreeTreeView.this, "Search element");
	            if(value == null)
	            	return;
	            
				int key = 0;
				try {
					key = Integer.parseInt(value);
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Element '" + value.trim() + " not found.", "Search element", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}


				Node searched = tree.search(key);
        		JOptionPane.showMessageDialog(TwoThreeTreeView.this,
        				"Element '" + value.trim() + "' " + (searched == null ? "not" : "") + " found.",
        				"Search element", JOptionPane.PLAIN_MESSAGE);
        		
        		if(searched != null)
        			treePanel.setNodeColor(Color.BLUE, searched);
			}			
		});
		
		clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				treePanel.clearNodeColor();
				tree.clear();
				treePanel.setTree(tree.getRoot());
				console.setText("");
			}
			
		});
		
		monitor.addMonitorListener(new MonitorListener() {
			
			@Override
			public void monitorEntered(MonitorEntryEvent event) {
				treePanel.setNodeColor(Color.RED, event.getSource());
				console.append(event + "\n");
				
				Node dangling = tree.getRoot().getDangling();
				if(dangling == null){
					dialog.setVisible(false);
					dialogTreePanel.setTree(null);
				}else{
					dialogTreePanel.setTree(dangling);
					if(!dialog.isVisible()){
						Point p = getLocation();
						p = new Point(p.x - dialog.getWidth() / 2, p.y + 50);
						dialog.setLocation(p);
						dialog.setVisible(true);
					}
				}
			}
			
			@Override
			public void monitorExited(MonitorExitEvent event) {
				updateButtons(false);
				String method = event.getMethodName();
				if(!event.hasSucceeded()){
					JOptionPane.showMessageDialog(TwoThreeTreeView.this, 
							"Error while executing '" + method + "'", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				if(method.equals("add")){
					// only this action is asynchronous so far 
					boolean added = (boolean) event.getResult();
					if(!added){
						JOptionPane.showMessageDialog(TwoThreeTreeView.this, 
								"Element could not be entered.", 
								"New key", JOptionPane.PLAIN_MESSAGE);
						return;
					}
					if(treePanel.getTree() != tree.getRoot())
						treePanel.setTree(tree.getRoot());
				}
			
			}
		});
	}
	
	private void updateButtons(boolean stepping) {
		if(stepping){
			addButton.setEnabled(false);
			addTreeButton.setEnabled(false);
			nextButton.setEnabled(true);
			searchButton.setEnabled(false);
			clearButton.setEnabled(false);
		}else{
			addButton.setEnabled(true);
			addTreeButton.setEnabled(true);
			nextButton.setEnabled(false);
			searchButton.setEnabled(true);
			clearButton.setEnabled(tree.getRoot() != null);			
		}
	}
	
	public static void main(String[] args) {
		new TwoThreeTreeView();	
	}
}
