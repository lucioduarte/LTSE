package tests;


public class AttributeRulesTests {

	int [] o1 = new int[5]; // PASSED

	public int o2; // PASSED

	private int o3; // PASSED

	protected int o4; // PASSED
	
	static int o5; // PASSED

	static public int o6;  // PASSED

	static private int o7; // PASSED

	static protected int o8; // PASSED

	public static int o9; // PASSED

	private static int o10; // PASSED

	protected static int o11; // PASSED
	
	int o12 = 5; // PASSED

	public int o13 = 5; // PASSED

	private int o14 = 5; // PASSED

	protected int o15 = 5; // PASSED

	static int o16 = 5; // PASSED

	static public int o17 = 5; // PASSED

	static private int o18 = 5; // PASSED

	static protected int o19 = 5; // PASSED

	public static int o20 = 5; // PASSED

	private static int o21 = 5; // PASSED

	protected static int o22 = 5; // PASSED
	
	//#attribute:"o23"=(5+4); // PASSED
	
	final static int n1 = 5; // PASSED
	
	final static public int n2 = 5; // PASSED

	final static private int n3 = 5; // PASSED

	final static protected int n4 = 5; // PASSED

	//static final int[] n5 = 5; // PASSED

	static final public int n6 = 5; // PASSED

	static final private int n7 = 5; // PASSED

	static final protected int n8 = 5; // PASSED

	
	public void m (int a) {
		this.o3 = n3 + n7;
		System.out.println (o3 + o7 + o10 + o14 + o18 + o21);
	}
	
	public static void main (String[] args) {
		AttributeRulesTests a = new AttributeRulesTests ();
		a.m (1);
  }
}