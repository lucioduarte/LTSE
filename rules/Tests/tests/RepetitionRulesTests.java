package tests;

public class RepetitionRulesTests {
	
	public void m1 (int a) {
		
		/***** PASSED ******/
		
		int b = 2;
		
		while (a > b)
			b++;
		
		/*while (b > a) {
			b--;
			a++;
		}*/
		
		/*while (a > 0)
			while (b < 10) {
				a--;
				b++;
			}*/
		
		/*while (true) {
			a = 2;	
		}*/
		
		/*do {
			a++;
		} while (true);*/
		
		/*do {
			a++;
			b--;
		} while (a<10);*/
		
		/*do 
			do {
				a++;
				b++;
			} while (a<10);
		while(b<10);*/
		
		/*for (int i=1; true; i++)
			a=i;*/
		
		/*for (int i=1; true;)
			a=i;*/
		
		/*for (; true;)
			a=0;*/
		
		/*for (int i=1; i<10; i++)
			a=i;*/
	
		/*for (int i=1; i<10;)
			a=i;*/
	
		/*for (; a==0;)
			a=0;*/
		
		/*for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				i = j;*/
		
		/*while (a < 10)
			do {
				a++;
				b--;
			} while (b > 10);*/
		
		/*do 
			while (a < 10){
				a++;
				b--;
			}
		while (b > 10);*/
		
		/*while (a < 10) 
			for (int i = 0; i < 10; i++) {
				a--;
			}*/
		
		/*for (int i = 0; i < 10; i++)
			while (a < 10)
				a--;*/

		/***** NOT PASSED ******/
		
		
		
	}
	
}
