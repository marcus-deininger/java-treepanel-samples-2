package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.DataNode;
import model.KeyNode;
import model.Node;
import model.Node.IntermediateResult;
import model.Tree;
import model.Tree.Action;
import model.Tree.ActionResult;
import trees.panel.TreePanel;
import trees.panel.style.Alignment;
import trees.panel.style.Shape;
import trees.panel.style.Size;
import trees.panel.style.Style;

@SuppressWarnings("serial")
public class TwoThreeTreeView extends JFrame implements Observer{
	
	private static final int WIDTH = 800, HEIGHT = 600;
	
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
	
	private TreePanel<Node> treePanel;

	public TwoThreeTreeView(){
		super("2-3 Trees");
		
		// initialize model		
		tree = new Tree();
		tree.addObserver(this);

		
		// create instances of functional widgets here
		treePanel = new TreePanel<>();
		addTreeButton = new JButton("Add Tree ...");
		addButton = new JButton("Add ...");
		nextButton = new JButton("Next");
		searchButton = new JButton("Search ...");
		clearButton = new JButton("Clear");
		console = new JTextArea(7, 20);

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
	}
	
	private JPanel createWidgetLayout() {
		JPanel panel = new JPanel(new BorderLayout());
		// create the layout here - if needed define supporting widgets like labels, etc.
		panel.add(treePanel, BorderLayout.CENTER);
		
		
		JPanel control = new JPanel(new BorderLayout());
		
			JPanel buttons = new JPanel(new WrapLayout());
			buttons.add(addTreeButton);
			buttons.add(addButton);
			buttons.add(nextButton);
			buttons.add(searchButton);
			buttons.add(clearButton);		
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
				String value = JOptionPane.showInputDialog(TwoThreeTreeView.this, "Neue Schlüssel");
				if(value == null)
					return;
				
				int[] keys = null;
				try {
					String[] values = value.split("\\s*,\\s*|\\s+");
					keys = new int[values.length];
					for(int i = 0; i < values.length; i++)
						keys[i] = Integer.parseInt(values[i]);
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Elemente konnten nicht eingetragen werden", "Neue Schlüssel", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}

				boolean ok = true;
				for(int key : keys)
					ok = ok & tree.add(key, (char)('A' + key) + "");				
				if(!ok)
                	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "mind. ein Element konnte nicht eingetragen werden", "Neuer Schlüssel", JOptionPane.PLAIN_MESSAGE);

				treePanel.setTree(tree.getRoot());
				updateButtons(false);
			}			
		});
	
	addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				treePanel.clearNodeColor();
				JTextField input = new JTextField();
				Object[] messages = {"Schlüssel", input};
				JOptionPane option = new JOptionPane(messages, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, Options.values());
				JDialog dialog = option.createDialog(TwoThreeTreeView.this, "Neuer Schlüssel");
				dialog.setVisible(true);
				Options value = (Options)option.getValue();

				if(value == null || value == Options.CANCEL) // cancelled
					return;
				
				int key = 0;
				try {
					key = Integer.parseInt(input.getText());
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Element konnte nicht eingetragen werden", "Neuer Schlüssel", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}
				
				Node.setStepping(false);
					String data = (char)('A' + key) + "";
				boolean ok;
				
				switch(value){
					case CANCEL	: break;
					
					case RUN	:	
						ok = tree.add(key, data);
						if(!ok){
							JOptionPane.showMessageDialog(TwoThreeTreeView.this, 
									"Element konnte nicht eingetragen werden", 
									"Neuer Schlüssel", JOptionPane.PLAIN_MESSAGE);
							return;
						}
						treePanel.setTree(tree.getRoot());
						updateButtons(false);
						break;
						
					case STEP	:
						console.setText("");
						Node.setStepping(true);
						updateButtons(true);
						tree.setAction(Action.ADD);
						tree.setKeyAndData(key, data);
						Thread host = new Thread(tree);
						host.start();
						break;
				}
			}			
		});
	
		nextButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				treePanel.clearNodeColor();
				tree.resume();
			}
			
		});
	
		searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				treePanel.clearNodeColor();
	            String value = JOptionPane.showInputDialog(TwoThreeTreeView.this, "Element suchen");
	            if(value == null)
	            	return;
	            
				int key = 0;
				try {
					key = Integer.parseInt(value);
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Element '" + value.trim() + " nicht gefunden", "Element suchen", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}


				Node searched = tree.search(key);
        		JOptionPane.showMessageDialog(TwoThreeTreeView.this,
        				"Element '" + value.trim() + "' " + (searched == null ? "nicht" : "") + " gefunden",
        				"Element suchen", JOptionPane.PLAIN_MESSAGE);
        		
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
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof IntermediateResult)
			this.update((IntermediateResult)arg);

		if((arg instanceof ActionResult))
			this.update((ActionResult)arg);
		
	}

	private void update(IntermediateResult result) {
		treePanel.setNodeColor(Color.RED, result.node);
		console.append(result + "\n");
	}

	private void update(ActionResult result) {
		updateButtons(false);
		switch(result.action){
			case NONE: break;
			case ADD: 
				if(!result.added){
					JOptionPane.showMessageDialog(TwoThreeTreeView.this, 
							"Element konnte nicht eingetragen werden", 
							"Neuer Schlüssel", JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if(treePanel.getTree() != result.root)
					treePanel.setTree(result.root);
				break;
			case CLEAR:
				break;
			case GET_ROOT:
				break;
			case SEARCH:
				break;
		}
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
