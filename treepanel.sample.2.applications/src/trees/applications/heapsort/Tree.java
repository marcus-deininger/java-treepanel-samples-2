package trees.applications.heapsort;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static trees.synchronization.Monitor.*;

public class Tree implements Iterable<Node>{
	
	private Node root = null;
	
	public Tree(int ... data){
		this.add(data);
	}

	private class TreeIterator implements Iterator<Node>{
		
		private Deque<Node> q = new LinkedList<>();

		public TreeIterator() {
			if(root != null)
				q.addLast(root);
		}

		@Override
		public boolean hasNext() {
			return !q.isEmpty();
		}

		@Override
		public Node next() {
			// breadth-first iteration
			Node n = q.removeFirst();
			if(n.left != null)	q.addLast(n.left);
			if(n.right != null) q.addLast(n.right);
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
		Node entry = new Node(data);
		
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
		List<Node> innerNodes = new LinkedList<Node>();
		for(Node n : this)
			if(n.left != null || n.right != null)
				innerNodes.add(0, n);
		for(Node n: innerNodes)
				heap(n);
	}

	private void heap(Node parent) {
		// reorganize the tree, that the following condition holds:
		// parent.data = min(parent.data, left.data, right.data)
		Node left =  parent.left;
		Node right = parent.right;
		Node node = parent;
		if (left != null)					// there is a left son
			if (parent.data > left.data)	// and the left son is 
				node = left;				// smaller than its parent
		if (right != null)					// and there is a right son
			if (node.data > right.data)		// and the right son is 
				node = right;				// smaller than its parent or the left son
		if (parent != node) { 				// if the parent wasn't the smallest
											// than swap parent and sons data
			int t = node.data; node.data = parent.data; parent.data = t; 
			monitor.breakpoint(parent, "Swap");
			heap(node);						// continue with the swapped son
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
			monitor.breakpoint(p.right, "Move");
			root.data = p.right.data;
			p.right = null;
		}else{
			monitor.breakpoint(p.left, "Move");
			root.data = p.left.data;
			p.left = null;
		}
		return picked;
	}

	public void clear() {
		root = null;
	}
	
	public boolean isEmpty(){
		return root == null;
	}
}
