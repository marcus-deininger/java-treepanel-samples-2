package trees.applications.twothreetree;

import static trees.synchronization.Monitor.monitor;

public class RootHolder extends Node {
	
	private Node root = null;

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
			monitor.breakpoint(n, "New root");
			return true;
		}else
			return root.add(key, data);
	}

	@Override
	public void insert(Node n) {
		// Falls die Insert-Operation hier landet, 
		// muss der root-Knoten geteilt werden.
		monitor.breakpoint(root, "Splitting root");
		this.setDangling(null);

		KeyNode k;
		if(root.getSubtreeMax() < n.getSubtreeMax())
			k = new KeyNode(root, n);
		else
			k = new KeyNode(n, root);
		monitor.breakpoint(k, "Inserting");

		this.setRoot(k);
	}
	
	public String toString(){
		if(root == null)
			return "empty";
		else
			return "root";
	}
	
	private Node dangling = null;

	@Override
	protected void setDangling(Node dangling) {
		this.dangling = dangling;
	}

	@Override
	public Node getDangling() {
		return dangling;
	}	
}
