package tests;
public class ActionRulesResults {
  private final int _thisInstanceID = this.hashCode ();
  private final String _thisClassName = ActionRulesResults.class.getName ();

  private void m () {
    System.err.println ("MET_ENTER" + ":" + "m" + "#" + _thisClassName + " = " + this._thisInstanceID + "#" + "{" + "" + "}" + "#" + "0" + ";");
    {
      System.err.println ("ACTION" + ":" + "myAction" + "#" + _thisClassName + " = " + this._thisInstanceID + ";");
      System.err.println ("ACTION" + ":" + "in" + "." + "myInput" + "#" + _thisClassName + " = " + this._thisInstanceID + ";");
      System.err.println ("ACTION" + ":" + "out" + "." + "myOutput" + "#" + _thisClassName + " = " + this._thisInstanceID + ";");
    } System.err.println ("MET_END" + ":" + "m" + "#" + _thisClassName + " = " + this._thisInstanceID + "#" + "0" + ";");
  }
  
  public static void main (String[] args) {
  	(new ActionRulesResults ()).m ();
  }

}

