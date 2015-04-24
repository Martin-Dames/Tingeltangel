/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli.cmds;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Book;
import tingeltangel.core.Books;
import tingeltangel.core.Importer;
import tingeltangel.core.Translator;

/**
 *
 * @author mdames
 */
public class Import extends CliCommand {

    @Override
    public String getName() {
        return("import");
    }

    @Override
    public String getDescription() {
        return("Importiert ein Buch aus einer ouf-Datei");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        
        CliSwitch[] list = {
            new CliSwitch() {
                @Override
                public String getName() {
                    return("o");
                }

                @Override
                public String getDescription() {
                    return("die zu importierende ouf-Datei");
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public boolean isOptional() {
                    return(false);
                }

                @Override
                public String getLabel() {
                    return("ouf-Datei");
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    return(new File(value).exists());
                }
            },
            new CliSwitch() {
                @Override
                public String getName() {
                    return("b");
                }

                @Override
                public String getDescription() {
                    return("setzt das (existierende) Zielverzeichniss im dem Buch gespeichert werden soll");
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public boolean isOptional() {
                    return(false);
                }

                @Override
                public String getLabel() {
                    return("Buch-Verzeichniss");
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    return(new File(value).exists() && new File(value).isDirectory());
                }
            },
        };
        
        Map<String, CliSwitch> switches = new HashMap<String, CliSwitch>();
        for(int i = 0; i < list.length; i++) {
            switches.put(list[i].getName(), list[i]);
        }
        return(switches);
        
        
        
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        File ouf = new File(args.get("o"));
        File bookDir = new File(args.get("b"));
        
        Book book = new Book(null, bookDir);
        Importer.importOuf(ouf, book);
        
    }
    
}
