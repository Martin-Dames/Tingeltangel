/*
    Copyright (C) 2016   Martin Dames <martin@bastionbytes.de>
  
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import tingeltangel.core.scripting.Commands;

/**
 *
 * @author martin
 */
public class Preferences {
    
    private final static Map<String, PrefEntry> prefs = new HashMap<String, PrefEntry>();
    
    public final static int INTEGER = 1;
    public final static int STRING = 2;
    public final static int BOOL = 3;
    
    private static int str2type(String type) {
        if(type.equals("bool")) {
            return(BOOL);
        }
        if(type.equals("string")) {
            return(STRING);
        }
        if(type.equals("integer")) {
            return(INTEGER);
        }
        throw new Error("unknown type '" + type + "'");
    }
    
    static {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Commands.class.getResourceAsStream("/preferences.properties")));
        
            String row;
            while((row = in.readLine()) != null) {
                
                row = row.trim();
                if((!row.startsWith("#")) && (!row.isEmpty())) {
                    
                    int p = row.indexOf('=');
                    if(p < 0) {
                        throw new Error("missing '=' on line: " + row);
                    }
                    
                    String key = row.substring(0, p).trim();
                    String[] val = row.substring(p + 1).split("\\|");
                    
                    
                    PrefEntry e = new PrefEntry();
                    e.type = str2type(val[0].trim());
                    e.defaultValue = val[1].trim();
                    e.description = val[2].trim();
                    
                    prefs.put(key, e);
                    
                }
                
            }
            
            
            in.close();
        } catch(IOException ioe) {
            throw new Error(ioe);
        }
    }
    
    public static Set<String> getKeys() {
        return(prefs.keySet());
    }
    
    public static String getDefault(String key) {
        return(prefs.get(key).defaultValue);
    }
    
    public static int getDefaultInteger(String key) {
        return(Integer.parseInt(prefs.get(key).defaultValue));
    }
    
    public static String getDescription(String key) {
        return(prefs.get(key).description);
    }
    
    public static int getType(String key) {
        return(prefs.get(key).type);
    }
    
}

class PrefEntry {
    
    int type;
    String defaultValue;
    String description;
    
}
