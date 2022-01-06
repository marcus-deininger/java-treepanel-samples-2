package model;

import java.util.Observable;

import synchronization.Interruptable;
import trees.annotations.Ignore;

public abstract class Node extends Interruptable{
	
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
	
	public Node[] getChildren(){
		Node left = this.getLeft();
		Node middle = this.getMiddle();
		Node right = this.getRight();

		if(left == null)
			return new Node[0];
		
		if(middle == null){
			Node[] children = {left};
			return children;
		}
			
		if(right == null){
			Node[] children = {left, middle};
			return children;
		}
		
		Node[] children = {left, middle, right};
		return children;
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

	// Notification &  Synchronization //////////////////////////
		
	@Override
	protected Observable[] getSenders() {
		return new Observable[]{ parent };
	}

	@Override
	protected Interruptable[] getReceivers() {
		return this.getChildren();
	}
}
