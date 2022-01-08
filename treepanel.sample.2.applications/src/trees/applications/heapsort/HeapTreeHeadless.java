package trees.applications.heapsort;

public class HeapTreeHeadless {
	
	public static void main(String[] args) {
		
//		Tree t = new Tree(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		Tree t = new Tree(7, 12, 0, 5, 9, 1, 3, 8, 13, 10, 15);
		
		System.out.println("unsorted");
		for(Node n : t)
			System.out.print(n + " ");
		
		System.out.println("\nsorted");
		t.heap();
		while(!t.isEmpty()){
			System.out.print(t.pick() + " ");
			t.heap();
		}
	}
}
