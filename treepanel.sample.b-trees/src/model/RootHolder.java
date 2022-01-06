package model;

//import java.util.ArrayList;
//import java.util.SortedMap;
//import java.util.TreeMap;

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
		// TODO Auto-generated method stub
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
			return true;
		}else
			return root.add(key, data);
	}

	@Override
	public void insert(Node n) {
		// Falls die Insert-Operation hier landet, 
		// muss der root-Knoten geteilt werden.
		KeyNode k;
		if(root.getSubtreeMax() < n.getSubtreeMax())
			k = new KeyNode(root, n);
		else
			k = new KeyNode(n, root);
		this.setRoot(k);
	}
	
	public String toString(){
		if(root == null)
			return "empty";
		else
			return "root";
	}
	
	
//	public String toString(){
//		
//		if(root == null)
//			return "[]";
//		
//		StringBuffer sb = new StringBuffer();
//
//		SortedMap<Integer, ArrayList<Node>> levels = new TreeMap<Integer, ArrayList<Node>>();
//		collect(root, 0, levels);
//		
//		int[] layout = charCount(levels.lastKey(), levels);
//		
//		int ref = (layout[0] - 1) * 2 + layout[1];
//		
//		
//		for(int i : levels.keySet()){
//			String space;
//			if(i != levels.lastKey()){
//				layout = charCount(i, levels);
//				int spacing = ref - layout[1];
//				spacing = spacing / (layout[0] + 1);
//				space = "";
//				for(int k = 0; k < spacing; k++) space = space + " ";
//				
//				sb.append(space);
//			}else{
//				space = "  ";
//			}
//			
//			for(Node n : levels.get(i)){
//				sb.append(n + space);
//			}
//			sb.append('\n');
//			sb.append('\n');
//		}
//		return sb.toString();
//	}
//
//	private void collect(Node n, int level, SortedMap<Integer, ArrayList<Node>> levels){
//		if(n == null)
//			return;
//		
//		ArrayList<Node> current = levels.get(level);
//		if(current == null){
//			current = new ArrayList<Node>();
//			levels.put(level, current);
//		}
//		current.add(n);
//		collect(n.getLeft(), level + 1, levels);
//		collect(n.getMiddle(), level + 1, levels);
//		collect(n.getRight(), level + 1, levels);
//	}
//	
//	private int[] charCount(int level, SortedMap<Integer, ArrayList<Node>> levels){
//		int count = 0;
//		ArrayList<Node> elements = levels.get(level);
//		
//		for(Node n : elements)
//			count = count + n.toString().length();
//		
//		int[] result = {elements.size(), count};
//		return result;
//	}

}
