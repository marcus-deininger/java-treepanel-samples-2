package queue;

public class LinkedQueue<T> implements Queue<T>{
	
	private class Node{
		T value;
		Node next = null;
	}
	
	private Node head = null;
	private Node tail = null;

	private int size = 0;


	public void enqueue(T value) {
		Node p = new Node();
		p.value = value;
		
		if(tail == null){ // Liste war leer
			tail = p;
			head = p;
		}else{		
			tail.next = p;
			tail = p;
		}

		size++;
	}

	public T peek() {
		if(this.isEmpty())
			throw new QueueEmptyException();

		return head.value;
	}

	public T dequeue() {
		if(this.isEmpty())
			throw new QueueEmptyException();
		
		T value = head.value;
		head = head.next;
		
		if(head == null) // Liste ist jetzt leer
			tail = null;
		
		size--;
		return value;
	}

	public boolean isEmpty() {
		return size == 0;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("» ");
		for(Node p = head; p != null; p = p.next)
			sb.insert(2, p.value + " ");
		sb.append('»');
		return sb.toString();
	}

}
