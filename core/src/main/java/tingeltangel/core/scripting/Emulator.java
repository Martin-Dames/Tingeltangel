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

package tingeltangel.core.scripting;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.MP3Player;
import tingeltangel.tools.Callback;


public class Emulator {
    
    private final LinkedList<RegisterListener> listeners = new LinkedList<RegisterListener>();
    
    private int lastOID = 0;
    
    public final static int REGISTERS = 99;
    
    private final Random rnd = new Random();
    
    private final static int[] register = new int[REGISTERS];
    private final static String[] hints = new String[REGISTERS];
    
    
    private final static Logger log = LogManager.getLogger(Emulator.class);
    
    static {
        for(int i = 0; i < hints.length; i++) {
            hints[i] = "";
        }
    }
    
    private int leftValue = 0;
    private int rightValue = 0;
    private final Book book;
    
    public Emulator(Book book) {
        this.book = book;
    }
    
    /**
     * 
     * @return left value from last comparison
     */
    public int getLeftValue() {
        return(leftValue);
    }
    
    /**
     * 
     * @return right value from last comparison
     */
    public int getRightValue() {
        return(rightValue);
    }
    
    /**
     * set hint of register i
     * @param i the register
     * @param hint some text
     */
    public void setHint(int i, String hint) {
        hints[i] = hint;
    }
    
    public String getHint(int i) {
        return(hints[i]);
    }
    
    public void setLeftValue(int value) {
        leftValue = value;
    }
    
    public void setRightValue(int value) {
        rightValue = value;
    }
    
    /**
     *
     * @return highest usable register
     */
    public int getMaxRegister() {
        return(REGISTERS - 1);
    }
    
    public static int getMaxBasicRegister() {
        return(91);
    }
    
    public int getRegister(int i) {
        if(i == 93) {
            // language code
            return(20); // always "en"
        } else if(i == 95) {
            // last oid
            return(lastOID);
        } else if(i == 98) {
            // random
            return(rnd.nextInt(0x8000));
        }
        return(register[i]);
    }
    
    public void setRegister(int i, int value) {
        int oval = register[i];
        register[i] = value;
        if(oval != value) {
            Iterator<RegisterListener> it = listeners.iterator();
            while(it.hasNext()) {
                it.next().registerChanged(i, value);
            }
        }
        register[i] = value;
    }
    
    public void play(int oid) {
        Entry entry = book.getEntryByOID(oid);
        if(entry == null) {
            log.warn("NOT playing " + oid + ". Track not Found.");
            return;
        }
        if((entry.isMP3() || entry.isTTS()) && (entry.getMP3() != null)) {
            try {
                log.debug("playing " + entry.getMP3().getAbsolutePath());
                MP3Player.getPlayer().play(entry.getMP3(), new Callback<Exception>() {
                    @Override
                    public void callback(Exception t) {
                        t.printStackTrace();
                    }
                });
                log.debug("done");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void pause(int ms) {
        MP3Player.getPlayer().pause(ms);
    }

    public void addRegisterListener(RegisterListener listener) {
        listeners.add(listener);
    }

    public void setLastOID(int tingID) {
        lastOID = tingID;
    }
}
