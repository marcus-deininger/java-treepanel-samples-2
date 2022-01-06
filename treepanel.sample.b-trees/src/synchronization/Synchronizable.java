package synchronization;

import java.lang.reflect.Method;
import java.util.Observable;

public abstract class Synchronizable extends Observable implements Runnable{

	public void setAction(String action, Object ... parameters){
		this.action = action;
		this.parameters = parameters;
	}
	
	private String action = null;	
	private Object[] parameters = null;

	public class ActionResult{
		public String action;
		public Object[] parameters;
		public boolean succeeded;
		public Object returned;
	}
	
	
	
	@Override
	public void notifyObservers(Object argument) {
		this.setChanged();
		super.notifyObservers(argument);
		this.clearChanged();
	}

	@Override
	public void run() {
		ActionResult result = new ActionResult();
		result.action = action;
		result.parameters = parameters;
		
		Class<?>[] types = new Class<?>[parameters.length];
		for(int i = 0; i < parameters.length; i++)
			if(parameters[i] != null)
				types[i] = mapClass(parameters[i]);
			else
				types[i] = Object.class;
		Class<?> cls = this.getClass();
		try {
			Method method = cls.getDeclaredMethod(action, types);
			result.returned = method.invoke(this, parameters);
			result.succeeded = true;
		} catch (Exception e) {
			result.succeeded = false;
			e.printStackTrace();
		}

		this.setAction(null); // Reset action
		this.setChanged();
		this.notifyObservers(result);
		this.clearChanged();
	}
	
	private Class<?> mapClass(Object object) {
		Class<?> cls = object.getClass();
		if(cls == Integer.class)
			return int.class;
		else if(cls == Integer.class)
			return int.class;
		else if(cls == Long.class)
			return long.class;
		else if(cls == Float.class)
			return float.class;
		else if(cls == Double.class)
			return double.class;
		else if(cls == Boolean.class)
			return boolean.class;
		else if(cls == Character.class)
			return char.class;
		else return cls;
	}

	protected abstract Interruptable[] getReceivers();

	public final synchronized void resume(){
		Interruptable[] receivers = this.getReceivers();
		if(receivers != null)
			for(Interruptable receiver : receivers)
				if(receiver != null)
					receiver.resume();
	}

}
