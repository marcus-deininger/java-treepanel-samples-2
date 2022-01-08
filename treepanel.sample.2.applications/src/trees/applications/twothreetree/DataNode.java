package trees.applications.twothreetree;

public class DataNode extends Node {
	
	protected int key;
	protected String data;

	public DataNode(int key, String data) {
		super();
		this.key = key;
		this.data = data;
	}

	public int getSubtreeMax(){
		return key;
	}

	public int getKey(){
		return key;
	}

	@Override
	public DataNode search(int key) {
		if(this.key == key)			
			return this;
		else
			return null;
	}

	@Override
	public boolean add(int key, String data) {
		if(this.key == key) // already entered
			return false;
		
		Node n = new DataNode(key, data);
		this.getParent().insert(n);
		return true;		
	}

	public String toString(){
		return  key + " (" + data + ")";
	}
}
