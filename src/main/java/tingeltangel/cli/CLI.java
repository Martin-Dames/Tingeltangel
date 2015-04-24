package tingeltangel.cli;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import tingeltangel.cli.cmds.*;
import tingeltangel.core.scripting.SyntaxError;

/**
 *
 * @author martin dames
 */
public class CLI {
    
    private final static Class[] COMMAND_CLASSES = {
        Help.class,
        GenerateCode.class,
        GenerateRawCode.class,
        BooksScan.class,
        BooksUpdate.class,
        Import.class,
            Assemble.class,
            OidCode.class,
    };
    
    private final static Map<String, CliCommand> COMMANDS = new HashMap<String, CliCommand>();
    
    static {
        try {
            for(int i = 0; i < COMMAND_CLASSES.length; i++) {
                CliCommand command = (CliCommand)COMMAND_CLASSES[i].newInstance();
                COMMANDS.put(command.getName(), command);
            }
        } catch(Exception e) {
            throw new Error(e);
        }
    }
    
    public static Collection<CliCommand> getCommands() {
        return(COMMANDS.values());
    }
    
    public static boolean cli(String[] args) throws SyntaxError, Exception {
        if(args.length == 0) {
            return(false);
        }
        
        CliCommand command = COMMANDS.get(args[0].toLowerCase().trim());
        
        if(command == null) {
            throw new SyntaxError("unbekanntes Kommando: " + args[0].toLowerCase().trim());
        }
        
        Map<String, String> argsMap = new HashMap<String, String>();
        for(int i = 1; i < args.length; i++) {
            
            if(args[i].startsWith("-")) {
                String option = args[i].substring(1).toLowerCase().trim();
                
                
                CliSwitch sw = command.getSwitches().get(option);
                if(sw == null) {
                    throw new SyntaxError("unbekannte Option: -" + option);
                }
                
                if(sw.hasArgument()) {
                    // get argument of option
                    if(i + 1 == args.length) {
                        throw new SyntaxError("fehlendes Argument zur Option: " + sw.getLabel());
                    }
                    argsMap.put(sw.getName(), args[++i].trim());
                } else {
                    argsMap.put(sw.getName(), "");
                }
                
            }
            
        }
        // add missing default values
        Iterator<CliSwitch> switches = command.getSwitches().values().iterator();
        while(switches.hasNext()) {
            CliSwitch sw = switches.next();
            if(!argsMap.containsKey(sw.getName())) {
                if(sw.isOptional()) {
                    String def = sw.getDefault();
                    if(def != null) {
                        argsMap.put(sw.getName(), def);
                    }
                } else {
                    throw new SyntaxError("fehlende nicht optionale Option: " + sw.getLabel());
                }
            }
        }
        
        // check args
        switches = command.getSwitches().values().iterator();
        while(switches.hasNext()) {
            CliSwitch sw = switches.next();
            String arg = argsMap.get(sw.getName());
            if((arg != null) && (sw.hasArgument())) {
                if(!sw.acceptValue(arg)) {
                    throw new SyntaxError("fehlerhafter Wert ('" + arg + "') für die Option: " + sw.getLabel());
                }
            }
        }
        
        
        command.execute(argsMap);
        return(true);
    }
    
    
}
