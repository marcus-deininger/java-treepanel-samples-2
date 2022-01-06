package main;

import model.Tree;

public class Test1b {

	public static void main(String[] args) {

		Tree t = new Tree();
				
		t.print();
		
		int[] newKeys = {5, 7, 10, 12, 14, 16, 11, 9, 17, 18, 19, 20, 5, 2, 1};
		for(int key : newKeys){
			boolean done;
			System.out.print("Eintrag: " + key + " ... ");
			done = t.add(key, "B" + key);
			System.out.println((done ? "" : "nicht ") + "erfolgreich\n"); 
			t.print();
			Input.readString();
//			System.out.println();
		}		
	}
}
