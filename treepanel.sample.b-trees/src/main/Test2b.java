package main;

import model.Tree;

public class Test2b {

	public static void main(String[] args) {

		Tree t = new Tree();
				
		t.print();
		
		int[] newKeys = {16, 10, 5, 20, 15, 14, 12, 11, 13};
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
