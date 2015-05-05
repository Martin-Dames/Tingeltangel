/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli.cmds;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Book;
import tingeltangel.core.Repository;
import tingeltangel.core.Importer;

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
                    return("t");
                }

                @Override
                public String getDescription() {
                    return("die zu importierende txt-Datei");
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public boolean isOptional() {
                    return(true);
                }

                @Override
                public String getLabel() {
                    return("txt-Datei");
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
                    return("s");
                }

                @Override
                public String getDescription() {
                    return("die zu importierende src-Datei");
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public boolean isOptional() {
                    return(true);
                }

                @Override
                public String getLabel() {
                    return("src-Datei");
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
        
        String _txt = args.get("t");
        String _src = args.get("s");
        File src = null;
        if(_src != null) {
            src = new File(_src);
        }
        HashMap<String, String> txt = null;
        if(_txt != null) {
            txt = Repository.getBook(new File(_txt));
        }
        
        // preread mid
        DataInputStream in = new DataInputStream(new FileInputStream(ouf));
        in.readInt();
        int id = in.readInt();
        in.close();
        
        Importer.importOuf(ouf, txt, src, new Book(id, null), null);
        
    }
    
}
