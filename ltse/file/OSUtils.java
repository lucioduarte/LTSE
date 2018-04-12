/**
 * 
 */
package ltse.file;

/**
 * Determines the OS being used. Currently, only Windows and Mac OS are 
 * supported.
 * 
 * @author Lucio Mauro Duarte
 * @version 03/04/2013
 */
public final class OSUtils {
	
	private static String OS = null;
   
	public static String getOSName() {
		if(OS == null) { 
			OS = System.getProperty("os.name"); 
		}
		return OS;
  }
	
  public static boolean isWindows() {
  	return getOSName().startsWith("Windows");
  }

  public static boolean isMac() {
  	return getOSName().startsWith("Mac");
  }
  
  public static boolean isLinux() {
  	return getOSName().startsWith("Linux");
  }
  
  public static boolean isUnix() {
  	return getOSName().startsWith("Unix");
  }
}
