/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli;

import java.util.Map;

/**
 *
 * @author mdames
 */
public abstract class CliCommand {
    
    public abstract String getName();
    public abstract String getDescription();
    public abstract Map<String, CliSwitch> getSwitches();
    public abstract void execute(Map<String, String> args) throws Exception;
    
}
