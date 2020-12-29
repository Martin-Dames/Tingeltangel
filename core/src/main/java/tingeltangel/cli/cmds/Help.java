/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
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
