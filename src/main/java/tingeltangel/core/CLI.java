package tingeltangel.core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin dames
 */
public class CLI {
    
    private final static String[][] COMMANDS = {
        {"help", "", "Zeigt diese Hilfe an"},
        {"generate-code", "[-d dpi] [-f png|eps] <Ting-ID>", "Erzeugt ein png oder eps von der gegebenen Ting-ID. Per default wird ein png mit 1200dpi erzeugt."},
        {"generate-raw-code", "[-d 1200|600] [-f png|eps] <Code-ID>", "Erzeugt ein png oder eps von der gegebenen Code-ID. Per default wird ein png mit 1200dpi erzeugt."},
        {"get-ting-id", "<Code-ID>", "Liefert die Ting-ID zur gegebenen Code-ID"},
        {"get-code-id", "<Ting-ID>", "Liefert die Code-ID zur gegebenen Ting-ID"},
        {"stick-debug", "on|off", "Versetzt einen angeschlossenen Ting-Stift in den Debug-Modus bzw. in den Normalzustand."}
    };
    
    public static boolean cli(String[] args) {
        if(args.length == 0) {
            return(false);
        }
        String cmd = args[0].toLowerCase().trim();
        for(int i = 0; i < COMMANDS.length; i++) {
            if(cmd.equals(COMMANDS[i][0])) {
                try {
                    Class.forName("tingeltangel.core.CLI").getMethod(COMMANDS[i][0].replace('-', '_'), String[].class).invoke(null, new Object[]{args});
                    return(true);
                } catch (Exception ex) {
                    throw new Error(ex);
                }
            }
        }
        return(false);
    }
    
    
    
    // commands
    
    public static void help(String[] args) {
        for(int i = 0; i < COMMANDS.length; i++) {
            System.out.println(COMMANDS[i][0] + " " + COMMANDS[i][1]);
            System.out.println("\t" + COMMANDS[i][2]);
        }
    }
    
    public static void generate_code(String[] args) {
        // [-d dpi] [-f png|eps] <Ting-ID>
    }
    
    public static void generate_raw_code(String[] args) {
        // [-d 1200|600] [-f png|eps] <Code-ID>
    }
    
    public static void get_ting_id(String[] args) {
        // <Code-ID>
    }
    
    public static void get_code_id(String[] args) {
        // <Ting-ID>
    }
    
    public static void stick_debug(String[] args) {
        // on|off
    }
    
}
