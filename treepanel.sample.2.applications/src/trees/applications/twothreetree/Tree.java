package trees.applications.twothreetree;

public class Tree{
		
	private RootHolder rootHolder = new RootHolder();

	public Node getRoot() {
		return rootHolder.getRoot();
	}

	public boolean add(int key, String data) {
		return rootHolder.add(key, data);
	}
		
	public DataNode search(int key) {
		return rootHolder.search(key);
	}
	
	public void clear(){
		rootHolder = new RootHolder();
	}
}
