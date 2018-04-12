package tests;

public class ExceptionRulesTests {
	
	private void m () throws Exception {
		try {
			int i = 2;
			i = i + 1;
		}
		catch (Exception e) {
			System.out.println ("test");
			throw new Exception ();
		}
		
		Exception e = new Exception ();
		throw e;
	}
	
	public static void main (String[] args) {
		ExceptionRulesTests e = new ExceptionRulesTests ();
		try {
			e.m ();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
  }

}
