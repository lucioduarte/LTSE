package tests;

public class SelectionRulesTests {

	public void m1 (int a) {
		
		/***** PASSED *****/
		/*if (a > 2) 
			m2 (a);*/
		
		/*if (a > 2) {
			m2 (a);
			m2 (a+1);
		}*/
		
		/*if (a < 1)
			m2(a+1);
		else m2(a-1);*/
		
		/*if (a < 1) {
			m2(a+1);
			m2(a);
		}
		else m2(a-1);*/
		
		/*if (a < 1) 
			m2(a+1);
		else {
			m2(a-1);
			m2(a);
		}*/
		
		/*if (a < 1) {
			m2(a+1);
			m2(a);
		}
		else {
			m2(a-1);
			m2(a);
		}*/
		
		/*switch (a) {
			case 0: m2(3);
			        break;
			case 1: m2(9);
							break;
			default: m2(0);
		}*/
		
		/*if (a==4)
		if (a < 6)
			a++;*/
	
		/*if (a==4)
		if (a < 6)
			a++;
		else a--;*/
	
	/*if (a==4)
		if (a < 6)
			a++;
		else a--;
	else a = 4;*/
	
	/*if (a>5)
		if(a>4)
			if(a>3)
				if(a>2)
					if(a>1)
						a = 0;*/
		
		/***** NOT PASSED *****/
	
		
		/*if (a == 3) 
			return;*/
		
		/*int b;
		b = (a > 2) ? 2 : 1;*/
		
		//int c = (a > 3) ? 5 : 6;
		
	}
	
	private void m2 (int a) {
		
	}
	
	public static void main (String[] args) {
		SelectionRulesTests s = new SelectionRulesTests ();
		s.m2 (1);
  }
}
