package model;

import trees.annotations.Ignore;

public abstract class Node {
	
	@Ignore
	private Node parent;
	
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	// wird nur für toString benötigt
	public Node getLeft(){
		return null;
	}

	public Node getMiddle(){
		return null;
	}

	public Node getRight(){
		return null;
	}
	
	public abstract int getSubtreeMax();

	public abstract DataNode search(int key);
	
	public abstract boolean add(int key, String data);

	public void insert(Node n) {
		// do nothing
	}

	public void updateMax() {
		// do nothing
	}	
}
