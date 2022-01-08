package trees.applications.heapsort;

import static trees.applications.heapsort.HeapTreeView.State.*;
import static trees.synchronization.Monitor.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import trees.panel.FontPanel;
import trees.panel.TreePanel;
import trees.style.Alignment;
import trees.style.Size;
import trees.style.Style;
import trees.synchronization.MonitorEntryEvent;
import trees.synchronization.MonitorExitEvent;
import trees.synchronization.MonitorListener;

@SuppressWarnings("serial")
public class HeapTreeView extends JFrame{
	
	private static final int WIDTH = 800, HEIGHT = 600;
	
	private Tree tree;

	// Define functionals widgets here
	private JButton addTreeButton, addButton, heapButton, pickButton, stepHeapButton, stepPickButton, clearButton;
	private JTextArea console;
	
	private TreePanel<Node> treePanel;
	
	public enum State{ ADD, HEAP, REORGANIZED, PICK, PICKED }

	private State state;
	
	public HeapTreeView(){
		super("Heap");
		
		// initialize model		
		tree = new Tree();		
//		tree.add(7, 12, 0, 5, 9, 1, 3, 8, 13, 10, 15); // Sample Data
		state = ADD;
		
		// create instances of functional widgets here
		treePanel = new TreePanel<>();
		addTreeButton = new JButton("Add Tree ...");
		addButton = new JButton("Add ...");
		
		heapButton = new JButton("Heap");
		pickButton = new JButton("Pick");
		
		stepHeapButton = new JButton("Heap");
		stepPickButton = new JButton("Pick");
		
		clearButton = new JButton("Clear");
		console = new JTextArea(7, 40);

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
	
	private FontPanel fontPanel;
	
	private void initializeWidgets() {
		Style style = new Style(20, 20, 40);
		style.setSize(Size.MIN_VARIABLE(40, 30));
		style.setHorizontalAlignment(Alignment.ROOT_CENTER);
		style.setRootPointer("root");
		style.setPlaceHolder(true);
		treePanel.setStyle(style);
		treePanel.setTree(tree.getRoot());
		fontPanel = new FontPanel(treePanel.getStyle());
		
		console.setEditable(false);
		updateButtons();
	}
	
	private JPanel createWidgetLayout() {
		JPanel panel = new JPanel(new BorderLayout());
		// create the layout here - if needed define supporting widgets like labels, etc.
		
		panel.add(treePanel.addScrollPane(), BorderLayout.CENTER);
				
		JPanel control = new JPanel(new BorderLayout());
		
			JPanel buttons = new JPanel();
			buttons.add(addTreeButton);
			buttons.add(addButton);
			buttons.add(new JLabel("Run:"));
			buttons.add(heapButton);
			buttons.add(pickButton);
			buttons.add(new JLabel("Step:"));
			buttons.add(stepHeapButton);
			buttons.add(stepPickButton);
			buttons.add(clearButton);
			buttons.add(fontPanel);
			control.add(buttons, BorderLayout.NORTH);
	
//			console.setPreferredSize(new Dimension(WIDTH, HEIGHT/4));
			JScrollPane consoleScrollPane = new JScrollPane(console);
			control.add(consoleScrollPane, BorderLayout.CENTER);
			DefaultCaret caret = (DefaultCaret) console.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	
		panel.add(control, BorderLayout.SOUTH);
		
		return panel;
	}
	
	private void createWidgetInteraction() {
		// add Listeners, etc. here
		
		addTreeButton.addActionListener((e) -> { addElements(false); });

		addButton.addActionListener((e) -> { addElements(true); });
	
		heapButton.addActionListener((e) -> { invokeHeap(false); });
	
		stepHeapButton.addActionListener((e) -> { invokeHeap(true); });

		pickButton.addActionListener((e) -> { invokePick(false); });
		
		stepPickButton.addActionListener((e) -> { invokePick(true); });

		clearButton.addActionListener((e) -> {
				treePanel.clearNodeColor();
				tree.clear();
				treePanel.setTree(tree.getRoot());
				console.setText("");
				monitor.disableBreakpoints();
				monitor.resume();
				state = ADD;
				updateButtons();
			}			
		);
		
		fontPanel.addChangeListener((e) -> {
				Font treeFont = treePanel.getStyle().getFont();
				Font consoleFont = console.getFont();				
				Font font = new Font(consoleFont.getFontName(), consoleFont.getStyle(), treeFont.getSize());
				treePanel.repaint();
				console.setFont(font);
			});
		
		monitor.addMonitorListener(new MonitorListener() {
			
			@Override
			public void monitorEntered(MonitorEntryEvent event) {
				treePanel.setNodeColor(Color.RED, event.getSource());
				treePanel.repaint();
				console.append(event + "\n");
				updateButtons();
			}
			
			@Override
			public void monitorExited(MonitorExitEvent event) {
				updateButtons();
				String method = event.getMethodName();
				if(!event.hasSucceeded()){
					JOptionPane.showMessageDialog(HeapTreeView.this, 
							"Error while executing '" + method + "'", 
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				if(method.equals("heap")){
					console.append("Heap restored.\n");
					treePanel.repaint();
					state = REORGANIZED;
					updateButtons();
					return;
				}
				
				if(method.equals("pick")){
					console.append("SELECTED: " + event.getResult() + "\n");
					if(treePanel.getTree() != tree.getRoot())
						treePanel.setTree(tree.getRoot());
					else
						treePanel.repaint();
					if(tree.getRoot() == null)
						treePanel.repaint();
					if(tree.getRoot() != null)
						state = PICKED;
					else
						state = ADD;
					updateButtons();
				}
			}
		});
	}
	
	private void addElements(boolean single){
		monitor.disableBreakpoints();
		monitor.resume();
		treePanel.clearNodeColor();
		console.setText("");
		
		String newValueText = "New Value" + (single ? "" : "s");
      	String errorText = "Could not add element" + (single ? "." : "s.");

		String value = JOptionPane.showInputDialog(HeapTreeView.this, newValueText);
		if(value == null)
			return;
			
			int[] values = null;
			try {
				String[] strValues = value.split("\\s*,\\s*|\\s+");
				values = new int[strValues.length];
				if(single && values.length != 1)
					throw new Exception();
				for(int i = 0; i < strValues.length; i++)
					values[i] = Integer.parseInt(strValues[i]);
			} catch (Exception e) {
 				JOptionPane.showMessageDialog(HeapTreeView.this, errorText, newValueText, JOptionPane.PLAIN_MESSAGE);
               	return;
			}

		tree.add(values);				

		if(treePanel.getTree() != tree.getRoot())
			treePanel.setTree(tree.getRoot());
		else
			treePanel.repaint();
		updateButtons();
	}			

	private void invokeHeap(boolean enableBeakpoints){
		if(enableBeakpoints)
			monitor.enableBreakpoints();
		else
			monitor.disableBreakpoints();
		if(state == ADD || state == PICKED){
			monitor.invokeAsync(tree, "heap");
			state = HEAP;
		}else // state == HEAP
			monitor.resume();
	}
	
	private void invokePick(boolean enableBeakpoints){
		if(enableBeakpoints)
			monitor.enableBreakpoints();
		else
			monitor.disableBreakpoints();
		if(state == REORGANIZED){
			monitor.invokeAsync(tree, "pick");
			state = PICK;
		}else // state == PICK
			monitor.resume();
		
	}
	
	private void updateButtons() {
		switch(state){
			case ADD:			setButtonEnabled(true, true, tree.getRoot() != null, false, tree.getRoot() != null, false); break;
			case PICKED:		;
			case HEAP:			setButtonEnabled(false, false, true, false, true, false); break;
			case REORGANIZED:	;
			case PICK:			setButtonEnabled(false, false, false, true, false, true); break;
		}
	}

	private void setButtonEnabled(boolean add, boolean addTree, boolean heap, boolean pick, boolean sheap, boolean spick){
		addButton.setEnabled(add);
		addTreeButton.setEnabled(addTree);
		heapButton.setEnabled(heap);
		pickButton.setEnabled(pick);
		stepHeapButton.setEnabled(sheap);
		stepPickButton.setEnabled(spick);
		clearButton.setEnabled(tree.getRoot() != null);			
	}
	
	public static void main(String[] args) {
		new HeapTreeView();	
	}
}
