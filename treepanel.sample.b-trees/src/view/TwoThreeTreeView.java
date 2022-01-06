package view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.DataNode;
import model.KeyNode;
import model.Node;
import model.Tree;
import trees.panel.TreePanel;
import trees.panel.style.Alignment;
import trees.panel.style.Shape;
import trees.panel.style.Size;
import trees.panel.style.Style;

@SuppressWarnings("serial")
public class TwoThreeTreeView extends JFrame {
	
	private static final int WIDTH = 800, HEIGHT = 600;
	
	private Tree tree;

	// Define functionals widgets here
	private JButton addTreeButton, addButton, clearButton;
	
	private TreePanel<Node> treePanel;

	public TwoThreeTreeView(){
		super("2-3 Trees");
		
		// initialize model		
		tree = new Tree();

		
		// create instances of functional widgets here
		treePanel = new TreePanel<>();
		addTreeButton = new JButton("Add Tree ...");
		addButton = new JButton("Add ...");
		clearButton = new JButton("Clear");

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
	}
	
	private JPanel createWidgetLayout() {
		JPanel panel = new JPanel(new BorderLayout());
		// create the layout here - if needed define supporting widgets like labels, etc.
		panel.add(treePanel, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel(new WrapLayout());
		buttons.add(addTreeButton);
		buttons.add(addButton);
		buttons.add(clearButton);
		panel.add(buttons, BorderLayout.SOUTH);
		return panel;
	}
	
	private void createWidgetInteraction() {
		// add Listeners, etc. here
		
		addTreeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {

				String value = JOptionPane.showInputDialog(TwoThreeTreeView.this, "Neue Schlüssel");
				int[] keys = null;
				try {
					String[] values = value.split("\\s+");
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
			}			
		});
	
	addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {

				String value = JOptionPane.showInputDialog(TwoThreeTreeView.this, "Neuer Schlüssel");
				int key = 0;
				try {
					key = Integer.parseInt(value);
				} catch (NumberFormatException e) {
	               	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Element konnte nicht eingetragen werden", "Neuer Schlüssel", JOptionPane.PLAIN_MESSAGE);
	               	return;
				}

				boolean ok = tree.add(key, (char)('A' + key) + "");				
				if(!ok){
                	JOptionPane.showMessageDialog(TwoThreeTreeView.this, "Element konnte nicht eingetragen werden", "Neuer Schlüssel", JOptionPane.PLAIN_MESSAGE);
                	return;
				}

				treePanel.setTree(tree.getRoot());
			}			
		});
	
		clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				tree.clear();
				treePanel.setTree(tree.getRoot());
			}
			
		});
	}
	
	public static void main(String[] args) {
		new TwoThreeTreeView();	
	}
}
