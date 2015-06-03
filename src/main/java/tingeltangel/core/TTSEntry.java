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
    
    public int amplitude = 10;
    public int pitch = 50;
    public int speed = 160;
    

    public void generateTTS(Entry entry) {

    }

}
