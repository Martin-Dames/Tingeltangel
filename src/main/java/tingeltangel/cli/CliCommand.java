/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli;

import java.util.HashMap;
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


    protected Map<String, CliSwitch> list2map( CliSwitch[] list) {
        Map<String, CliSwitch> switches = new HashMap<String, CliSwitch>();
        for(int i = 0; i < list.length; i++) {
            switches.put(list[i].getName(), list[i]);
        }
        return(switches);
    }
}
