package model;

import java.util.Observable;

public class Tree extends Observable implements Runnable{
		
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
	
	// Notification //////////////////////////////////////////
	
	@Override
	public void notifyObservers(Object argument){
		this.setChanged();
		super.notifyObservers(argument);
		this.clearChanged();
	}
	

	// Synchronization //////////////////////////////////////////
		
	public enum Action{ NONE, GET_ROOT, ADD, SEARCH, CLEAR }

	private Action action = Action.NONE;
	private int key;
	private String data;
	
	public void setAction(Action action) {
		this.action = action;
	}
	public void setKey(int key) {
		this.key = key;
	}

	public void setKeyAndData(int key, String data) {
		this.key = key;
		this.data = data;
	}

	public class ActionResult{
		public Action action;
		public Node root;
		public DataNode searched;
		public boolean added;
		public int key;
	}
	
	@Override
	public void run() {
		ActionResult result = new ActionResult();
		result.action = action;
		switch(action){
			case NONE: 		break; // do nothing
			case GET_ROOT:	break; // do below
			case ADD:		result.added = this.add(key, data); result.key = key; break;
			case CLEAR:		this.clear(); break;
			case SEARCH:	result.searched = this.search(key); break;
		}
		result.root = this.getRoot(); 
		this.notifyObservers(result);
	}
	
	public synchronized void resume(){
		rootHolder.resume();
	}

}
