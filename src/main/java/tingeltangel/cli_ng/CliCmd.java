/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli_ng;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
abstract class  CliCmd {
    
    int OK = 0;
    int ERROR = 1;
    
    
    Logger log = LogManager.getLogger("CliCmd" + getName());
    
    int error(String txt) {
        log.error(txt);
        System.err.println("ERROR: " + txt);
        return(ERROR);
    }
    
    int error(String txt, Exception e) {
        log.error(txt, e);
        e.printStackTrace(System.err);
        System.err.println("ERROR: " + txt);
        return(ERROR);
    }
    
    int ok() {
        System.err.println("OK");
        return(OK);
    }
    
    abstract String getName();
    abstract String getDescription();
    abstract int execute(String[] args);
    
    
}
