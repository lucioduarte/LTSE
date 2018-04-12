/**
 * Apply unit tests to the CallStack class.
 * 
 * @author Lucio Mauro Duarte
 */
package ltse.context.tests;

import static org.junit.Assert.*;

import java.util.Stack;

import org.junit.Test;
import ltse.context.CallStack;

public class CallStackTest {

	private CallStack cs;

	@Test
	public void testNotNull () {
		
		cs = new CallStack ();
		assertNotNull(cs);
	}
	
	@Test
	public void testEmptyNull () {
		try {
			cs.isEmpty ();
		}
		catch (NullPointerException e) {
			assertNull (cs);
		}
	}
	
	@Test
	public void testNoPush () {
		try {
			cs.pushCall ("C1");
		}
		catch (NullPointerException e) {
			assertNull (cs);
		}
	}
	
	@Test
	public void testNoPop () {
		try {
			cs.popCall ();
		}
		catch (NullPointerException e) {
			assertNull (cs);
		}
	}
	
	@Test
	public void testNoCurrentCall () {
		try {
			cs.getCurrentCall ();
		}
		catch (NullPointerException e) {
			assertNull (cs);
		}
	}
	
	@Test
	public void testNoGetCallStack () {
		try {
			cs.getCallStack ();
		}
		catch (NullPointerException e) {
			assertNull (cs);
		}
	}
	
	@Test
	public void testNoGetCalls () {
		try {
			cs.getCalls ();
		}
		catch (NullPointerException e) {
			assertNull (cs);
		}
	}
	
	@Test
	public void testEmpty () {
		
		cs = new CallStack ();
		assertTrue (cs.isEmpty ());
	}
	
	@Test
	public void testOnePush () {
		cs = new CallStack ();
		cs.pushCall ("C1");
		assertEquals ("<C1>", cs.getCalls ());
	}
	
	@Test
	public void testTwoPushes () {
		cs = new CallStack ();
		cs.pushCall ("C1");
		cs.pushCall ("C2");
		assertEquals ("<C1, C2>", cs.getCalls ());
	}
	
	@Test 
	public void testOnePop () {
		testTwoPushes ();
		
		cs.popCall ();
		assertEquals ("<C1>", cs.getCalls ());
	}

	@Test 
	public void testTwoPops () {
		testTwoPushes ();
		cs.popCall ();
		cs.popCall ();
		assertTrue (cs.isEmpty ());
	}
	
	@Test
	public void testGetCurrentCall () {
		testTwoPushes ();
		
		cs.popCall ();
		assertEquals ("C1", cs.getCurrentCall ());
	}
	
	@Test
	public void testGetCallStack () {
		cs = new CallStack ();
		cs.pushCall ("C1");
		cs.pushCall ("C2");
		Stack<String> s = new Stack<String> ();
		s.push ("C1");
		s.push ("C2");
		assertEquals (s, cs.getCallStack ());
	}
	
	@Test
	public void testGetCalls () {
		cs = new CallStack ();
		cs.pushCall ("C1");
		cs.pushCall ("C2");
		assertEquals ("<C1, C2>", cs.getCalls ());
	}
	
	@Test
	public void testPushAndPop () {
		cs = new CallStack ();
		cs.pushCall ("C1");
		cs.popCall ();
		assertTrue (cs.isEmpty ());
	}
	
	@Test
	public void testNoPopFirst () {
		cs = new CallStack ();
		cs.popCall ();
	}
}
