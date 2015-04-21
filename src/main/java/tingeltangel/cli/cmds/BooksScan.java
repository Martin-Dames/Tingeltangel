/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli.cmds;

import java.util.HashMap;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Books;

/**
 *
 * @author mdames
 */
public class BooksScan extends CliCommand {

    @Override
    public String getName() {
        return("books-scan");
    }

    @Override
    public String getDescription() {
        return("Aktualisiert die gesammte Buchliste (Vorsicht: kann viel Zeit beanspruchen)");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        return(new HashMap<String, CliSwitch>());
        
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        Books.search();
    }
    
}
