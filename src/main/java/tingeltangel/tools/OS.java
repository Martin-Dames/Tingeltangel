/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.tools;

/**
 *
 * @author mdames
 */
public class OS {

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
    
}
