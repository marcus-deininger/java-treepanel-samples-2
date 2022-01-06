package model;

import java.util.Observable;

import synchronization.Interruptable;

public class Node extends Interruptable{
	
	private Tree tree;
	protected Node left, right;
	protected int data;

	public Node(int data) {
		super();
		this.data = data;
	}

	public Node(Tree tree, int data) {
		this(data);
		this.tree = tree;
	}

	protected int getData() {
		this.breakpoint("Wert tauschen", this);
		return data;
	}

	protected void setData(int data) {
		this.data = data;
		this.breakpoint("Wert getauscht", this);
	}

	@Override
	public String toString() {
		return data +"";
	}

	@Override
	protected Observable[] getSenders() {
		return new Observable[]{tree};
	}

	@Override
	protected Interruptable[] getReceivers() {
		return new Interruptable[]{left, right};
	}
}
