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
package tingeltangel.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.core.Properties;

/**
 *
 * @author mdames
 */
public class Binary {
    
    public final static String ESPEAK = "espeak";
    public final static String LAME = "lame";
    public final static String AVCONV = "avconv";
    
    private final static String[] BINARIES = {ESPEAK, LAME, AVCONV};
    
    private final static Map<String, File> binMap = new HashMap<String, File>();
    
    private final static Logger log = LogManager.getLogger(Binary.class);
    
    static {
        // try to autoconf binaries
        for(int i = 0; i < BINARIES.length; i++) {
            String path = Properties.getStringProperty(BINARIES[i]);
            if((path != null) && new File(path).canExecute()) {
                binMap.put(BINARIES[i], new File(path));
            } else {
                File f = whereis(BINARIES[i]);
                if((f != null) && f.canExecute()) {
                    try {
                        Properties.setProperty(BINARIES[i], f.getCanonicalPath());
                    } catch(IOException ioe) {
                        log.error("unable to save preferences", ioe);
                    }
                    binMap.put(BINARIES[i], f);
                }
            }
        }
    }
    
    public static void setBinary(String binary, File file) {
        if(file.canExecute()) {
            try {
            Properties.setProperty(binary, file.getCanonicalPath());
            } catch(IOException ioe) {
                throw new Error(ioe);
            }
            binMap.put(binary, file);
        }
    }
    
    public static File getBinary(String binary) {
        return(binMap.get(binary));
    }
    
    private static File whereis(String binary) {
        
        if(OS.isWindows()) {
            binary += ".exe";
        }
        
        String pathEnv = System.getenv("PATH");
        if(pathEnv == null) {
            return(null);
        }
        
        String[] path = pathEnv.split(File.pathSeparator);
        for(int i = 0; i < path.length; i++) {
            File pf = new File(path[i]);
            if(pf.isDirectory()) {
                File bf = new File(pf, binary);
                if(bf.canExecute()) {
                    return(bf);
                }
            }
        }
        return(null);
    }
    
    
    
    
    
}
