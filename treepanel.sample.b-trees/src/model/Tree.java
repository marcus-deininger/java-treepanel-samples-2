package model;

public class Tree {
	
	private RootHolder rootHolder = new RootHolder();

	public Node getRoot() {
		return rootHolder.getRoot();
	}

//	public void setRoot(Node root) {
//		this.rootHolder = root;
//	}

	public DataNode search(int key) {
//		if(rootHolder == null)
//			return null;
//		else
			return rootHolder.search(key);
	}
	
	public void clear(){
		rootHolder = new RootHolder();
	}

	public boolean add(int key, String data) {
//		if(rootHolder == null){
//			rootHolder = new DataNode(key, data);
//			return true;
//		}else
			return rootHolder.add(key, data);
	}
	
	
	// Printing the Tree

	public void print(){
		System.out.println(rootHolder.toString());
	}
	

}
