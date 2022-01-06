package model;

import trees.annotations.Ignore;

public abstract class Node{
	
	public class IntermediateResult{
		public String message;
		public Node node;
		public IntermediateResult(String message, Node node) {
			this.message = message; this.node = node;
		}
		@Override
		public String toString() {
			return message + ": " + node.getClass().getSimpleName() + " " + node.toString();
		}
	}
	
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
	
	// Notification //////////////////////////////////////////
	
	protected void notifyObservers(Object argument){
		parent.notifyObservers(argument);
	}
	
	// Synchronization //////////////////////////////////////////
	
	private static boolean stepping = false;
	protected boolean suspended = false;
	
	protected void breakpoint(String message, Node node) {
		if(!stepping)
			return;
		
		suspended = true; // set yourself on waiting
		this.notifyObservers(new IntermediateResult(message, node));
		
		synchronized(this){
			while(suspended)
				try {
					this.wait();
				} catch (InterruptedException e) {}
		}
	}

	public synchronized void resume(){
		if(suspended){
			suspended = false;
			this.notifyAll();
	    }
		
		for(Node node : this.getChildren())
			node.resume();
	}

	public static void setStepping(boolean stepping) {
		Node.stepping = stepping;
	}

}
