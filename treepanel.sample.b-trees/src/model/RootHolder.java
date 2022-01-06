package model;

import java.util.Observable;

import synchronization.Interruptable;

public class RootHolder extends Node {
	
	private Tree tree;
	private Node root = null;

	public RootHolder(Tree tree) {
		this.tree = tree;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
		if(root != null)
			root.setParent(this);
	}

	@Override
	public Node getParent() {
		return null;
	}

	@Override
	public void setParent(Node parent) {
		// Do nothing
	}

	@Override
	public int getSubtreeMax() {
		return -1;
	}

	@Override
	public DataNode search(int key) {
		if(root == null)
			return null;
		else
			return root.search(key);
	}

	@Override
	public boolean add(int key, String data) {
		if(root == null){
			Node n = new DataNode(key, data);
			this.setRoot(n);
			this.breakpoint("New root", n);
			return true;
		}else
			return root.add(key, data);
	}

	@Override
	public void insert(Node n) {
		// Falls die Insert-Operation hier landet, 
		// muss der root-Knoten geteilt werden.
		this.breakpoint("Splitting root", root);
		KeyNode k;
		if(root.getSubtreeMax() < n.getSubtreeMax())
			k = new KeyNode(root, n);
		else
			k = new KeyNode(n, root);
		this.breakpoint("Inserting", k);
		this.setRoot(k);
	}
	
	public String toString(){
		if(root == null)
			return "empty";
		else
			return "root";
	}
	
	// Notification &  Synchronization //////////////////////////
	
	@Override
	protected Observable[] getSenders() {
		return new Observable[]{ tree };
	}

	@Override
	protected Interruptable[] getReceivers() {
		if(root == null)
			return new Interruptable[0];
		else
			return new Interruptable[]{ root };
	}
}
