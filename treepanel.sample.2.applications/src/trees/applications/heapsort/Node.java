package trees.applications.heapsort;


public class Node{
	
	protected Node left, right;
	protected int data;

	public Node(int data) {
		super();
		this.data = data;
	}

	@Override
	public String toString() {
		return data +"";
	}
}
