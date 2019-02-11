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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;
import javax.swing.JFrame;
import tingeltangel.tools.ProgressDialog;


/**
 * the central class for managing a bookii stick
 * @author martin dames
 */
public abstract class Stick {

    private final static String UID_FILE = "tt.uid";
    private final static String UNNAMED = "unnamed";
    
    public abstract String getType();
    
    public static Stick getAnyStick() throws IOException {
        Stick stick;
        
        stick = TingStick.getStick();
        if(stick != null) {
            return(stick);
        }
        
        stick = BookiiStick.getStick();
        if(stick != null) {
            return(stick);
        }
        return(null);
    }
    
    public String getUID() throws IOException {
        File uidFile = new File(getBookDir(), UID_FILE);
        if(!uidFile.exists()) {
            String uid = UUID.randomUUID().toString();
            PrintWriter out = new PrintWriter(new FileWriter(uidFile));
            out.println("uid:" + uid);
            out.println("name:" + UNNAMED);
            out.close();
            return(uid);
        }
        BufferedReader in = new BufferedReader(new FileReader(uidFile));
        String row;
        while((row = in.readLine()) != null) {
            int p = row.indexOf(":");
            if(p != -1) {
                String key = row.substring(0, p).trim();
                String value = row.substring(p + 1).trim();
                if(key.equals("uid")) {
                    in.close();
                    return(value);
                }
            }
        }
        in.close();
        // uid file seems to be broken
        uidFile.delete();
        return(getUID());
    }
    
    public String getName() throws IOException {
        File uidFile = new File(getBookDir(), UID_FILE);
        if(!uidFile.exists()) {
            return(UNNAMED);
        }
        BufferedReader in = new BufferedReader(new FileReader(uidFile));
        String row;
        while((row = in.readLine()) != null) {
            int p = row.indexOf(":");
            if(p != -1) {
                String key = row.substring(0, p).trim();
                String value = row.substring(p + 1).trim();
                if(key.equals("name")) {
                    in.close();
                    return(value);
                }
            }
        }
        in.close();
        // uid file seems to be broken
        uidFile.delete();
        return(UNNAMED);
    }
    
    public void setName(String name) throws IOException {
        String uid = getUID();
        File uidFile = new File(getBookDir(), UID_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(uidFile));
        out.println("uid:" + uid);
        out.println("name:" + name);
        out.close();
    }
    
    /**
     * retrieves size of free space on stick
     * @return free space in bytes
     */
    public abstract long getFreeSpace();
    
    /**
     * retrieves the file TBD.TXT from stick
     * @return the tbd file
     */
    public abstract File getTBDFile();
    
    /**
     * retrieves the content of TBD.TXT from stick
     * @return content from TBD.TXT as HashSet&lt;Integer&gt;
     * @throws IOException
     */
    public abstract HashSet<Integer> getTBD() throws IOException;
    
    /**
     * retrieves the books on stick
     * @return list of books on the stick as LinkedList&lt;Integer&gt;
     * @throws IOException
     */
    public abstract LinkedList<Integer> getBooks() throws IOException;
 
 
    /**
     * gets the directory on stick where most data resists
     * @return the book directory
     */
    public abstract File getBookDir();
    
    /**
     * sets the stick settings
     * @param settings
     * @throws IOException
     */
    public abstract void setSettings(HashMap<String, String> settings) throws IOException;
    
    /**
     * gets the stick settings
     * @return the stick settings
     * @throws IOException
     */
    public abstract HashMap<String, String> getSettings() throws IOException;

    /**
     * gets book version on stick
     * @param id the book id
     * @return the book version or -1 if book not found
     * @throws IOException
     */
    public abstract int getBookVersion(int id) throws IOException;
    
    /**
     * deletes a book from the stick
     * @param id the book to delete
     */
    public abstract void delete(int id);
    
    /**
     * copies a book from the repository to the stick
     * @param mid the book id
     * @throws IOException
     */
    public abstract void copyFromRepositoryToStick(int mid) throws IOException;

    /**
     * sets the TBD.TXT
     * @param newTbd the new list of books in TBD.TXT
     * @throws IOException
     */
    public abstract void setTBD(LinkedList<Integer> newTbd) throws IOException;

    /**
     * saves the whole stick to zip file
     * @param target the zip file to write to
     * @throws IOException
     */
    public abstract void saveStick(File target) throws IOException;

    /**
     * restores the whole stick from a zip file
     * @param source the zip file to read from
     * @throws IOException
     */
    public abstract void restoreStick(File source) throws IOException;

    
    public abstract void update(ProgressDialog progress);
    
    public abstract boolean update(JFrame frame, ProgressDialog progress);

    public abstract void activateBook(int id) throws IOException;
}
