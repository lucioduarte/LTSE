package tests;
//import java.util.List;
//import java.util.Vector;

public class MethodRulesTests {

	public MethodRulesTests () {
		
	}
	
	public void m1 () {
		
		
		
		
		
		/* PASSED */
		
		//this.m2 (0);
		
		//this.m7 (1);
		
		//m2(3); 
		
		//m3 (6 + 2);
		
		//m7(2);
		
		//Integer i = new Integer (3);	
		//i.notify ();
		
		
		//int g;
		
		//g = m2 (7);
		
		//g = m8(9);
		
		//g = i.intValue ();
		
		//int e = i.intValue ();
		
		//int a = m2 (7);
		
		//int c = m8(9);
		
		/* FAILED */
		
		//m3 (m2 (5));
		
		//g = m2(1) + m3(8);
		
		//g = m6(5) + m8(2);
		
		//int b = m2(1) + m3(8);
		
		//int d = m6(5) + m8(2);
		
		if (m2(6) == 3)
		{
			
		}
	}
	
	private int m2 (int a) {
		
		//Integer i = new Integer (4);
		//i.toString ();
		
		//int s = i.intValue ();
		
		return a * 5;
	}
	
	/*protected int m3 (int a) {
		return 4 / a;
		
	}
	
	public int m4 (int a) {
		return a * a;
	}
	
	public int[] m5 (Vector<Integer> a) {
		
		return new int[5];	
	}
	
	private static int m6 (int a) {
		
		
		return a + m8(a);
	}
	
	protected static void m7 (int a) {
		m8 (2);
	}
	
	public static int m8 (int a) {
		
		Integer i = new Integer (4);
		i.toString ();
		
		int s = i.intValue ();
		
		return 4;
	}
	
	public static int[] m9 (List<Integer> a) {
		return new int [3];
	}*/
	
	public static void main (String args[]) {
		MethodRulesTests m = new MethodRulesTests ();
		m.m1 ();
	}
}
