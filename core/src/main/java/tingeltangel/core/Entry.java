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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.TTS;

public class Entry {

    private final static int MP3 = 1;
    private final static int CODE = 2;
    private final static int SUB = 3;
    private final static int T2S = 4;
    
    private File mp3 = null;
    private Script script = null;
    private TTSEntry tts = null;
    private float mp3length = -1;
    private int size = -1;
    private final Book book;
    private String hint = "";
    private int type = MP3;
    private int tingID = -1;
    private boolean hasCode;
    private String name;
    
    public Entry(Book book, int tingID) {
        this.book = book;
        this.tingID = tingID;
        name = Integer.toString(tingID);
    }
    
    public void setName(String name) {
        if(!this.name.equals(name)) {
            changeMade();
        }
        this.name = name;
    }
    
    public String getName() {
        return(name);
    }
    
    public Book getBook() {
        return(book);
    }
    
    public int getTingID() {
        return(tingID);
    }
    
    
    public int getSize() throws SyntaxError {
        if(isMP3() || isTTS()) {
            return(size);
        } else if((isCode() || isSub()) && (script != null)) {
            return(script.getSize(false));
        }
        return(0);
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    
    public String getHint() {
        return(hint);
    }
    
    public void setHint(String hint) {
        this.hint = hint.trim();
        changeMade();
    }

    public int getType(){
        return  type;
    }
    
    
    void changeMade() {
        book.changeMade();
    }
            
    
    
    public File getMP3() {
        if(type == MP3) {
            return(mp3);
        }
        // tts
        File ttsMp3 = new File(FileEnvironment.getAudioDirectory(book.getID()), "tts_" + tingID + ".mp3");
        
        if(!ttsMp3.canRead()) {
            try {
                TTS.generate(tts.text, tts.amplitude, tts.pitch, tts.speed, tts.voice, tts.variant, ttsMp3);
                
                if(!ttsMp3.canRead()) {
                    return(null);
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
                return(null);
            }
        }
        size = (int)ttsMp3.length();
        return(ttsMp3);
    }
    
    public boolean isMP3() {
        return(type == MP3);
    }
    
    public boolean isTTS() {
        return(type == T2S);
    }
    
    public boolean isCode() {
        return(type == CODE);
    }
    
    public boolean isSub() {
        return(type == SUB);
    }
    
    public void setMP3() {
        type = MP3;
        changeMade();
    }
    
    public void setTTS() {
        type = T2S;
        tts = new TTSEntry("");
        changeMade();
    }
    
    public void setCode() {
        type = CODE;
        if(script == null) {
            script = new Script("", this);
        }
        changeMade();
    }
    
    public void setSub() {
        type = SUB;
        if(script == null) {
            script = new Script("", this);
        }
        changeMade();
    }
    
    public float getLength() {
        return(mp3length);
    }
    
    public void setScript(Script script) {
        this.script = script;
        mp3 = null;
        tts = null;
        type = CODE;
        changeMade();
    }
    
    public void setTTS(TTSEntry tts) {
        this.tts = tts;
        mp3 = null;
        script = null;
        type = T2S;
        
        // prefetch track length
        getMP3();
        
        changeMade();
    }
    
    public TTSEntry getTTS() {
        return(tts);
    }
    
    public Script getScript() {
        return(script);
    }
    
    public void setMP3(File mp3) throws IOException {
        changeMade();
        String name = mp3.getAbsolutePath();
        if(!mp3.isFile()) {
            this.mp3 = null;
            mp3length = -1;
            throw new FileNotFoundException(name);
        }
        if(!mp3.canRead()) {
            this.mp3 = null;
            mp3length = -1;
            throw new FileNotFoundException(name);
        }
        
        // copy to book/audio dir if it is not already there
        File target = new File(FileEnvironment.getAudioDirectory(book.getID()), mp3.getName());
        if(!mp3.equals(target)) {
        	FileEnvironment.copy(mp3, target);
        }

        this.mp3 = target;
        try {
            mp3length = Mp3Utils.getDuration(this.mp3) / 1000.f;
        } catch(IOException e) {
            this.mp3 = null;
            throw e;
        }
        size = (int)mp3.length();
        script = null;
        tts = null;
        type = MP3;
    }

    public void setHasCode(boolean hasCode) {
        if(this.hasCode != hasCode) {
            changeMade();
        }
        this.hasCode = hasCode;
    }
    
    public boolean hasCode() {
        if(type == SUB) {
            return(false);
        }
        return(hasCode);
    }
}
