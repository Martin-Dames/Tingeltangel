
package tingeltangel.core.scripting;

import java.util.Iterator;
import java.util.LinkedList;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.MP3Player;


public class Emulator {
    
    private final LinkedList<RegisterListener> listeners = new LinkedList<RegisterListener>();
    
    public final static int REGISTERS = 98;
    
    private final static int[] register = new int[REGISTERS];
    private final static String[] hints = new String[REGISTERS];
    
    static {
        for(int i = 0; i < hints.length; i++) {
            hints[i] = "";
        }
    }
    
    private int leftValue = 0;
    private int rightValue = 0;
    private final MP3Player player;
    private final Book book;
    
    public Emulator(Book book, MP3Player player) {
        this.player = player;
        this.book = book;
    }
    
    public int getLeftValue() {
        return(leftValue);
    }
    
    public int getRightValue() {
        return(rightValue);
    }
    
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
    
    public int getMaxRegister() {
        return(REGISTERS - 1);
    }
    
    public int getRegister(int i) {
        return(register[i]);
    }
    
    public void setRegister(int i, int value) {
        if(register[i] != value) {
            Iterator<RegisterListener> it = listeners.iterator();
            while(it.hasNext()) {
                it.next().registerChanged(i, value);
            }
        }
        register[i] = value;
    }
    
    public void play(int oid) {
        Entry entry = book.getEntryFromTingID(oid);
        if(entry.isMP3() && (entry.getMP3() != null)) {
            String hint = Integer.toString(oid);
            String indexHint = entry.getHint();
            if(!indexHint.isEmpty()) {
                hint += " (" + indexHint + ")";
            }
            player.add(entry.getMP3(), hint);
        }
    }
    
    public void pause(int ms) {
        player.addPause(ms);
    }

    public void addRegisterListener(RegisterListener listener) {
        listeners.add(listener);
    }
}
