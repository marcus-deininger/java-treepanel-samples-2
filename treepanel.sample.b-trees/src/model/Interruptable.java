package model;

import java.util.Observable;

public abstract class Interruptable extends Observable{

	public class IntermediateResult{
		public String message;
		public Node node;
		public IntermediateResult(String message, Node node) {
			this.message = message; this.node = node;
		}
		@Override
		public String toString() {
			return message + ": " + node.getClass().getSimpleName() + " " + node.toString();
		}
	}
	
	private static boolean stepping = false;
	protected boolean suspended = false;
	
	// Notification //////////////////////////////////////////
	
	protected abstract Observable[] getSenders();

	@Override
	public final void notifyObservers(Object argument){
		Observable[] senders = this.getSenders();
		if(senders != null)
			for(Observable sender : senders)
				if(sender != null)
					sender.notifyObservers(argument);
	}
		
	// Synchronization //////////////////////////////////////////
	
	protected abstract Interruptable[] getReceivers();

	protected final void breakpoint(String message, Node node) {
		if(!stepping)
			return;
		
		suspended = true; // set yourself on waiting
		this.notifyObservers(new IntermediateResult(message, node));
		
		synchronized(this){
			while(suspended)
				try {
					this.wait();
				} catch (InterruptedException e) {}
		}
	}
	
	public final synchronized void resume(){
		if(suspended){
			suspended = false;
			this.notifyAll();
	    }
			
		Interruptable[] receivers = this.getReceivers();
		if(receivers != null)
			for(Interruptable receiver : receivers)
				if(receiver != null)
					receiver.resume();
	}
	
	public static void setStepping(boolean stepping) {
		Interruptable.stepping = stepping;
	}
}
