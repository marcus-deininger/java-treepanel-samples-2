package model;

import synchronization.Interruptable;
import synchronization.Synchronizable;

public class Tree extends Synchronizable{
		
	private RootHolder rootHolder = new RootHolder(this);

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
		rootHolder = new RootHolder(this);
	}

	@Override
	protected Interruptable[] getReceivers() {
		return new Interruptable[]{rootHolder};
	}
}
