package queue;

public interface Queue<T> {

	public abstract void enqueue(T value);

	public abstract T peek() throws QueueEmptyException;

	public abstract T dequeue() throws QueueEmptyException;

	public abstract boolean isEmpty();

}