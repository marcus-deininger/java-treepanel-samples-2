package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import queue.LinkedQueue;
import queue.Queue;
import synchronization.Interruptable;
import synchronization.Synchronizable;


public class Tree extends Synchronizable implements Iterable<Node>{
	
	private Node root = null;
	
	public Tree(int ... data){
		this.add(data);
	}

	private class TreeIterator implements Iterator<Node>{
		
		private Queue<Node> q = new LinkedQueue<>();

		public TreeIterator() {
			if(root != null)
				q.enqueue(root);
		}

		@Override
		public boolean hasNext() {
			return !q.isEmpty();
		}

		@Override
		public Node next() {
			// breadth-first Durchwanderung
			Node n = q.dequeue();
			if(n.left != null)	q.enqueue(n.left);
			if(n.right != null) q.enqueue(n.right);
			return n;
		}

		@Override
		public void remove() {}
		
	}

	@Override
	public Iterator<Node> iterator() {
		return new TreeIterator();
	}
	
	public Node getRoot() {
		return root;
	}

	public void add(int data){
		Node entry = new Node(this, data);
		
		if(root == null){
			root = entry;
			return;
		}
		
		for(Node n : this){			
			if(n.left == null){
				n.left = entry;
				return;
			}
			if(n.right == null){
				n.right = entry;
				return;
			}
		}		
	}
	
	public void add(int ... data){
		for(int d : data)
			this.add(d);
	}

	public void heap(){
		List<Node> nodes = new LinkedList<Node>();
		for(Node n : this)
			if(n.left != null || n.right != null)
				nodes.add(0, n);
		for(Node n: nodes)
				heap(n);
	}

	private void heap(Node vater) {
		// setze sohn so, dass gilt:
		// sohn.data = min(vater.data, links.data, rechts.data)
		Node links =  vater.left;
		Node rechts = vater.right;
		Node sohn = vater;
		if (links != null)					// es gibt einen linken Sohn
			if (vater.data > links.data)	// und der linke Sohn ist
				sohn = links;				// kleiner als der Vater
		if (rechts != null)					// es gibt einen rechten Sohn
			if (sohn.data > rechts.data)	// und der rechte Sohn ist 
				sohn = rechts;				// kleiner als Vater oder der linke Sohn
		if (vater != sohn) { 				// der Vater ist nicht das kleinste Element
											// tausche Daten von Vater und Sohn
			int t = sohn.data; sohn.data = vater.data; vater.setData(t); 
			heap(sohn);						// mache beim getauschten Sohn weiter
		}	
	}

	public int pick() {
		if(root == null)			
			throw new NullPointerException("root must not be null");
		
		if(root.left == null){ // only root node
			int picked = root.data;
			root = null;
			return picked;
		}
		
		Node p = null; // last parent
		for(Node n : this)
			if(n.left != null)
				p = n;
			else
				break;
		
		int picked = root.data;
		if(p.right != null){
			root.data = p.right.getData();
			p.right = null;
		}else{
			root.data = p.left.getData();
			p.left = null;
		}
		return picked;
	}

	@Override
	protected Interruptable[] getReceivers() {
		return new Interruptable[]{root};
	}

	public void clear() {
		root = null;
	}
}
