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
import java.io.IOException;
import static tingeltangel.gui.TTSPreferences.PROPERTY_DEFAULT_VOICE;
import static tingeltangel.gui.TTSPreferences.PROPERTY_DEFAULT_VARIANT;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.TTS;

/**
 *
 * @author martin
 */
public class TTSEntry {
    
    public TTSEntry(String text) {
        this.text = text;
    }
    
    public String text = "";
    public String voice = "de";
    public String variant = null;
    
    public int amplitude = 200;
    public int pitch = 50;
    public int speed = 160;
    
    private static File getMP3(Entry entry) {
        return(new File(FileEnvironment.getAudioDirectory(entry.getBook().getID()), "tts_" + entry.getTingID() + ".mp3"));
    }
    
    public File generateTTS(Entry entry) throws IOException {
        File mp3 = getMP3(entry);
        TTS.generate(text, amplitude, pitch, speed, Properties.getStringProperty(PROPERTY_DEFAULT_VOICE), Properties.getStringProperty(PROPERTY_DEFAULT_VARIANT), mp3);
        entry.setSize((int)mp3.length());
        return(mp3);
    }

    public void invalidate(Entry entry) {
        getMP3(entry).delete();
    }

}
