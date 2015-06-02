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

package tingeltangel.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

public class Properties {
    
    private final static String PROPERTY_FILE = "tt.properties";
    
    private final static HashMap<String, String> PROPERTIES = new HashMap<String, String>();
    
    public final static String WIN_MPG123 ="win_mpg123";
    public final static String _PATH =".path";
    public final static String _ENABLED=".enabled";
    
    static {
        try {
            File propertyFile = new File(PROPERTY_FILE);
            if(propertyFile.createNewFile()) {
                // do init of propertys here
                // setProperty("propertyName", "propertyValue");
            }
            BufferedReader in = new BufferedReader(new FileReader(propertyFile));
            String row;
            while((row = in.readLine()) != null) {
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("#"))) {
                    int p = row.indexOf("=");
                    if(p == -1) {
                        throw new IOException();
                    }
                    PROPERTIES.put(row.substring(0, p).trim(), row.substring(p + 1).trim());
                }
            }
            in.close();
        } catch(IOException ioe) {
            ioe.printStackTrace(System.out);
            throw new Error(ioe);
        }
    }
    
    public static void setProperty(String name, String value) {
        name = name.trim();
        if(value == null) {
            PROPERTIES.remove(name);
        } else {
            PROPERTIES.put(name, value.trim());
        }
        try {
            PrintWriter out = new PrintWriter(new FileWriter(PROPERTY_FILE));
            Iterator<String> keys = PROPERTIES.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
                out.println(key + " = " + PROPERTIES.get(key));
            }
            out.close();
        } catch(IOException ioe) {
            System.err.println("property file can not be saved. Your changes will be lost on next start.");
            ioe.printStackTrace(System.err);
        }
    }
    
    public static void setProperty(String name, int value) {
        setProperty(name, Integer.toString(value));
    }
    
    public static String getStringProperty(String name) {
        return(PROPERTIES.get(name.trim()));
    }
    
    public static int getIntegerProperty(String name) {
        try {
            return(Integer.parseInt(PROPERTIES.get(name)));
        } catch(NumberFormatException nfe) {
            throw new Error("property '" + name.trim() + "' is not of type int");
        }
    }
}
