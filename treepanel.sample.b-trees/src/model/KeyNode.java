package model;

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
		//Ein KeyNode hat immer left und middle!
		if(key <= leftMax)
			return left.search(key);
		else if((right == null) || (leftMax < key && key <= middleMax))
			return middle.search(key);
		else // middleMax < key
			return right.search(key);
	}
	

	@Override
	public boolean add(int key, String data) {
		//Ein KeyNode hat immer left und middle!
		if(key <= leftMax)
			return left.add(key, data);
		else if((right == null) || (leftMax < key && key <= middleMax))
			return middle.add(key, data);
		else // middleMax < key
			return right.add(key, data);
	}
	
	public void insert(Node n){
		if(right == null)	// Slot frei --> einfügen
			enter(n);
		else				// kein Slot frei --> teilen
			split(n);
	}


	private void enter(Node n) {
		// Slot frei --> einfügen
		int nMax = n.getSubtreeMax();
		
		if(nMax < leftMax)						// links einfügen
			this.setLeafs(n, left, middle);
		else if(leftMax < nMax && nMax < middleMax)	// in der Mitte einfügen
			this.setLeafs(left, n, middle);
		else									// rechts einfügen
			this.setLeafs(left, middle, n);
		
		this.updateMax();						// Maxima propagieren
	}


	private void split(Node n) {
		//kein Slot frei --> teilen!

		int nMax = n.getSubtreeMax();
		KeyNode k;
		
		if(nMax < leftMax){									// links einfügen
			k = new KeyNode(n, left);
			this.setLeafs(middle, right);					// alte Knoten verschieben
		}else if(leftMax < nMax && nMax < middleMax){		// zwischen Links und Mitte einfügen
			k = new KeyNode(left, n);
			this.setLeafs(middle, right);					// alte Knoten verschieben
		}else if(middleMax < nMax && nMax < subtreeMax){	// zwischen Mitte und Rechts einfügen
			k = new KeyNode(n, right);
			this.clearRight();								// alten rechten Knoten löschen
		}else{												// rechts einfügen
			k = new KeyNode(right, n);
			this.clearRight();								// alten rechten Knoten löschen			
		}
		
		this.getParent().updateMax();	// konsistenten Zustand wieder herstellen
		this.getParent().insert(k);		// rekursiv weiter einfügen
	}


	public void updateMax() {
		leftMax = left.getSubtreeMax();
		middleMax = middle.getSubtreeMax();
		if(right != null)
			subtreeMax = right.getSubtreeMax();
		else
			subtreeMax = middleMax;
		if(this.getParent() != null)
			this.getParent().updateMax();
	}
	
	public String toString(){
		return leftMax + ":" + middleMax;
//		return leftMax + ":" + middleMax + (right == null ? "" : "[:" + subtreeMax + "]");
	}


}
