package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import model.Node;
import synchronization.Interruptable.IntermediateResult;
import synchronization.Synchronizable.ActionResult;
import model.Tree;
import trees.panel.FontPanel;
import trees.panel.TreePanel;
import trees.panel.style.Alignment;
import trees.panel.style.Size;
import trees.panel.style.Style;
import static view.HeapTreeView.State.*;

@SuppressWarnings("serial")
public class HeapTreeView extends JFrame implements Observer{
	
	private static final int WIDTH = 800, HEIGHT = 600;
	
	private Tree tree;

	// Define functionals widgets here
	private JButton addTreeButton, addButton, heapButton, pickButton, clearButton;
	private JCheckBox steppingBox;
	private JTextArea console;
	
	private TreePanel<Node> treePanel;
	
	public enum State{ ADD, HEAP, RUNNING_HEAP, STEPPING_HEAP, PICK, RUNNING_PICK, STEPPING_PICK }

	private State state;
	
	public HeapTreeView(){
		super("Heap");
		
		// initialize model		
		tree = new Tree();		
//		tree.add(7, 12, 0, 5, 9, 1, 3, 8, 13, 10, 15); // Sample Data
		tree.addObserver(this);
		state = ADD;
		
		// create instances of functional widgets here
		treePanel = new TreePanel<>();
		addTreeButton = new JButton("Add Tree ...");
		addButton = new JButton("Add ...");
		steppingBox = new JCheckBox("Stepping");
		heapButton = new JButton("Heap");
		pickButton = new JButton("Pick");
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
		Style style = new Style(20, 20, 40);
		style.setSize(Size.MIN_VARIABLE(40, 30));
		style.setHorizontalAlignment(Alignment.ROOT_CENTER);
		style.setRootPointer("root");
		style.setPlaceHolder(true);
		treePanel.setStyle(style);
		treePanel.setTree(tree.getRoot());
		
		console.setEditable(false);
		updateButtons();
	}
	
	private JPanel createWidgetLayout() {
		JPanel panel = new JPanel(new BorderLayout());
		// create the layout here - if needed define supporting widgets like labels, etc.
		panel.add(treePanel, BorderLayout.CENTER);
				
		JPanel control = new JPanel(new BorderLayout());
		
			JPanel buttons = new JPanel(new WrapLayout());
			buttons.add(addTreeButton);
			buttons.add(addButton);
			buttons.add(steppingBox);
			buttons.add(heapButton);
			buttons.add(pickButton);
			buttons.add(clearButton);
			buttons.add(new FontPanel(treePanel.getStyle()));
			control.add(buttons, BorderLayout.NORTH);
	
			JScrollPane scrollPane = new JScrollPane(console,
	  							JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	  							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			control.add(scrollPane, BorderLayout.CENTER);
			DefaultCaret caret = (DefaultCaret) console.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	
		panel.add(control, BorderLayout.SOUTH);
		
		return panel;
	}
	
	private void createWidgetInteraction() {
		// add Listeners, etc. here
		
		addTreeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				treePanel.clearNodeColor();
				console.setText("");
				Node.setStepping(false);

				String value = JOptionPane.showInputDialog(HeapTreeView.this, "Neue Werte");
				if(value == null)
					return;
				
				int[] values = null;
				try {
					String[] strValues = value.split("\\s*,\\s*|\\s+");
					values = new int[strValues.length];
					for(int i = 0; i < strValues.length; i++)
						values[i] = Integer.parseInt(strValues[i]);
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(HeapTreeView.this, "Elemente konnten nicht eingetragen werden", "Neue Werte", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}

				tree.add(values);				

				if(treePanel.getTree() != tree.getRoot())
					treePanel.setTree(tree.getRoot());
				else
					treePanel.repaint();
				updateButtons();
			}			
		});
	
		addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				treePanel.clearNodeColor();
				Node.setStepping(false);
				console.setText("");

				String strValue = JOptionPane.showInputDialog(HeapTreeView.this, "Neuer Wert");
				if(strValue == null)
					return;
				
				int value = 0;
				try {
					value = Integer.parseInt(strValue);
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(HeapTreeView.this, "Element konnte nicht eingetragen werden", "Neuer Wert", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}

				tree.add(value);				

				if(treePanel.getTree() != tree.getRoot())
					treePanel.setTree(tree.getRoot());
				else
					treePanel.repaint();
				updateButtons();
			}			
		});
	
		heapButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(steppingBox.isSelected()){
					Node.setStepping(true);
					if(state == ADD || state == HEAP){
						state = STEPPING_HEAP;
						updateButtons();
						tree.setAction("heap");
						Thread host = new Thread(tree);
						host.start();
					}else // state == STEPPING_HEAP
						tree.resume();
				}else{
					state = RUNNING_HEAP;
					tree.heap();
					treePanel.repaint();
					console.append("Heap-Eigenschaft hergestellt\n");
					state = PICK;
					updateButtons();
				}
			}			
		});
	
		pickButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(tree.getRoot() == null)
					return;
				
				treePanel.clearNodeColor();
				
				if(steppingBox.isSelected()){
					Node.setStepping(true);
					if(state == PICK){
						state = STEPPING_PICK;
						updateButtons();
						tree.setAction("pick");
						Thread host = new Thread(tree);
						host.start();
					}else // state == STEPPING_PICK
						tree.resume();
				}else{
					state = RUNNING_PICK;
					int picked = tree.pick();
					console.append("ausgewählt: " + picked + "\n");
					if(treePanel.getTree() != tree.getRoot())
						treePanel.setTree(tree.getRoot());
					else
						treePanel.repaint();
					if(tree.getRoot() != null)
						state = RUNNING_HEAP;
					else
						state = ADD;
					updateButtons();
				}
			}
		});
		
		clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				treePanel.clearNodeColor();
				tree.clear();
				treePanel.setTree(tree.getRoot());
				console.setText("");
				state = ADD;
				updateButtons();
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
		treePanel.setNodeColor(Color.RED, result.source);
		treePanel.repaint();
		console.append(result + "\n");
		updateButtons();
	}

	private void update(ActionResult result) {
		updateButtons();
		if(!result.succeeded){
			JOptionPane.showMessageDialog(HeapTreeView.this, 
					"Fehler bei der Ausführung von '" + result.action + "'", 
					"Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}
			
		if(result.action.equals("heap")){
			console.append("Heap-Eigenschaft hergestellt\n");
			treePanel.repaint();
			state = PICK;
			updateButtons();
		}else if(result.action.equals("pick")){
			console.append("ausgewählt: " + result.returned + "\n");
			if(treePanel.getTree() != tree.getRoot())
				treePanel.setTree(tree.getRoot());
			else
				treePanel.repaint();
			if(tree.getRoot() != null)
				state = HEAP;
			else
				state = ADD;
			updateButtons();
		}
	}

	private void updateButtons() {
		switch(state){
			case ADD:			setButtonEnabled(true, true, tree.getRoot() != null, false); break;
			case HEAP:			;
			case RUNNING_HEAP:	;
			case STEPPING_HEAP:	setButtonEnabled(false, false, true, false); break;
			case PICK:			;
			case RUNNING_PICK:	;
			case STEPPING_PICK:	setButtonEnabled(false, false, false, true); break;
		}
	}
	
	private void setButtonEnabled(boolean add, boolean addTree, boolean heap, boolean pick){
		addButton.setEnabled(add);
		addTreeButton.setEnabled(addTree);
		heapButton.setEnabled(heap);
		pickButton.setEnabled(pick);
		clearButton.setEnabled(tree.getRoot() != null);			
	}

	
	public static void main(String[] args) {
		new HeapTreeView();	
	}

}
