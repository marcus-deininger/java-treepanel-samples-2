package model;

public class Test {
	
	public static void main(String[] args) {
		
//		Tree t = new Tree(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		Tree t = new Tree(7, 12, 0, 5, 9, 1, 3, 8, 13, 10, 15);
		
		for(Node n : t)
			System.out.println(n);
		
		t.heap();

		for(Node n : t)
			System.out.println(n);
		
	}

}
