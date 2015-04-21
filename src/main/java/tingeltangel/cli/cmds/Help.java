/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli.cmds;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import tingeltangel.cli.CLI;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;

/**
 *
 * @author mdames
 */
public class Help extends CliCommand {
    
    @Override
    public String getName() {
        return("help");
    }

    @Override
    public String getDescription() {
        return("zeigt diese Hilfe an");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        return(new HashMap<String, CliSwitch>());
    }

    @Override
    public void execute(Map<String, String> args) {
        Iterator<CliCommand> commands = CLI.getCommands().iterator();
        System.out.println("Tingeltangel Hilfe:");
        
        if(System.getProperty("os.name").startsWith("Windows")) {
            System.out.println("\tTingeltangel.bat [<Kommando> [<Optionen>]]");
        } else {
            System.out.println("\tTingeltangel.sh [<Kommando> [<Optionen>]]");
        }
        System.out.println("\twenn kein Kommando gegeben ist, wird die GUI gestartet.");
        System.out.println();
        System.out.println("Kommandos");
        
        while(commands.hasNext()) {
            System.out.println();
            CliCommand command = commands.next();
            System.out.print("\t" + command.getName());
            Iterator<CliSwitch> switches = command.getSwitches().values().iterator();
            while(switches.hasNext()) {
                CliSwitch sw = switches.next();
                String swTxt = "-" + sw.getName();
                if(sw.hasArgument()) {
                    swTxt += " <" + sw.getLabel() + ">";
                }
                if(sw.isOptional()) {
                    swTxt = "[" + swTxt + "]";
                }
                System.out.print(" " + swTxt);
            }
            System.out.println();
            System.out.println("\t" + command.getDescription());
            if(!command.getSwitches().isEmpty()) {
                System.out.println("\tOptionen:");
                switches = command.getSwitches().values().iterator();
                while(switches.hasNext()) {
                    CliSwitch sw = switches.next();
                    String swTxt = "-" + sw.getName();
                    if(sw.hasArgument()) {
                        swTxt += " <" + sw.getLabel() + ">";
                    }
                    System.out.print("\t\t" + swTxt + ": " + sw.getDescription());
                    if(sw.getDefault() != null) {
                        System.out.print(" (default: " + sw.getDefault() + ")");
                    }
                    System.out.println();
                }
            }
        }
    }
    
}
