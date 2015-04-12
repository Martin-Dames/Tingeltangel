
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
    
    private final static HashMap<String, String> properties = new HashMap<String, String>();
    
    static {
        try {
            File propertyFile = new File(PROPERTY_FILE);
            propertyFile.createNewFile();
            BufferedReader in = new BufferedReader(new FileReader(propertyFile));
            String row;
            while((row = in.readLine()) != null) {
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("#"))) {
                    int p = row.indexOf("=");
                    if(p == -1) {
                        throw new IOException();
                    }
                    properties.put(row.substring(0, p).trim(), row.substring(p + 1).trim());
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
            properties.remove(name);
        } else {
            properties.put(name, value.trim());
        }
        try {
            PrintWriter out = new PrintWriter(new FileWriter(PROPERTY_FILE));
            Iterator<String> keys = properties.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
                out.println(key + " = " + properties.get(key));
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
    
    public static String getProperty(String name) {
        return(properties.get(name.trim()));
    }
    
    public static int getPropertyAsInteger(String name) {
        try {
            return(Integer.parseInt(properties.get(name)));
        } catch(NumberFormatException nfe) {
            throw new Error("property '" + name.trim() + "' is not of type int");
        }
    }
}
