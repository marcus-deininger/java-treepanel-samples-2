package trees.applications.twothreetree;

import static trees.synchronization.Monitor.monitor;

public class KeyNode extends Node {
	
	private int leftMax, middleMax, subtreeMax;
	private Node left, middle, right;
	
	public KeyNode(Node left, Node middle){
		this.setLeafs(left, middle);
	}
	
	
	public int getSubtreeMax(){
		return subtreeMax;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node n){
		left = n;
		leftMax = n.getSubtreeMax();
		n.setParent(this);
	}
	
	public Node getMiddle() {
		return middle;
	}

	public void setMiddle(Node n){
		middle = n;
		middleMax = n.getSubtreeMax();
		n.setParent(this);
		if(right == null)
			subtreeMax = middleMax;
	}
	
	public Node getRight() {
		return right;
	}

	public void setRight(Node n){
		right = n;
		if(n == null)
			subtreeMax = middleMax;
		else{
			n.setParent(this);
			subtreeMax = n.getSubtreeMax();
		}
	}
	
	public void clearRight(){
		right = null;
		subtreeMax = middleMax;
	}
	
	public void setLeafs(Node left, Node middle, Node right){
		this.setLeft(left);
		this.setMiddle(middle);
		this.setRight(right);
	}
	
	public void setLeafs(Node left, Node middle){
		this.setLeafs(left, middle, null);
	}
	
	@Override
	public DataNode search(int key) {
		// A keynode always has 'left' and 'middle'!
		if(key <= leftMax)
			return left.search(key);
		else if((right == null) || (leftMax < key && key <= middleMax))
			return middle.search(key);
		else // middleMax < key
			return right.search(key);
	}
	

	@Override
	public boolean add(int key, String data) {
		// A keynode always has 'left' and 'middle'!
		if(key <= leftMax)
			return left.add(key, data);
		else if((right == null) || (leftMax < key && key <= middleMax))
			return middle.add(key, data);
		else // middleMax < key
			return right.add(key, data);
	}
	
	public void insert(Node n){
		monitor.breakpoint(n, "Insert Node");
		
		if(right == null)	// free slot -> enter
			enter(n);
		else				// no free slot -> split
			split(n);
	}


	private void enter(Node n) {
		monitor.breakpoint(this, "Entering at");
		this.setDangling(null);
		
		// free slot -> enter
		int nMax = n.getSubtreeMax();
		
		if(nMax < leftMax)							// enter left
			this.setLeafs(n, left, middle);
		else if(leftMax < nMax && nMax < middleMax)	// enter in the middle
			this.setLeafs(left, n, middle);
		else										// enter right
			this.setLeafs(left, middle, n);
		
		
		this.updateMax();							// propagate maxima
	}


	private void split(Node n) {
		// no free slot -> split

		monitor.breakpoint(this, "Splitting");

		int nMax = n.getSubtreeMax();
		KeyNode k;
		
		if(nMax < leftMax){									// enter left
			k = new KeyNode(n, left);
			this.setLeafs(middle, right);					// move old node
		}else if(leftMax < nMax && nMax < middleMax){		// enter between left and middle
			k = new KeyNode(left, n);
			this.setLeafs(middle, right);					// move old node
		}else if(middleMax < nMax && nMax < subtreeMax){	// enter between middle and right
			k = new KeyNode(n, right);
			this.clearRight();								// delete old right node
		}else{												// enter right
			k = new KeyNode(right, n);
			this.clearRight();								// delete old right node			
		}
		
		this.setDangling(k);
		
		this.getParent().updateMax();						// restore consistent state
		this.getParent().insert(k);							// continue recursively
	}


	public void updateMax() {
		leftMax = left.getSubtreeMax();
		middleMax = middle.getSubtreeMax();
		if(right != null)
			subtreeMax = right.getSubtreeMax();
		else
			subtreeMax = middleMax;

		monitor.breakpoint(this, "Updated max");

		if(this.getParent() != null)
			this.getParent().updateMax();
	}
	
	public String toString(){
		return leftMax + ":" + middleMax;
//		return leftMax + ":" + middleMax + (right == null ? "" : "[:" + subtreeMax + "]");
	}

}
