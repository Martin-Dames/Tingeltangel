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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import tingeltangel.tools.Callback;


public class MP3Player {

    private static final MP3Player mp3Player = new MP3Player();
    
    private Player player = null;
    private final Object pause;
    
    private MP3Player() {
        this.pause = new Object();
    }
    
    public static MP3Player getPlayer() {
        return(mp3Player);
    }
    
    public void play(File file, Callback<Exception> onError) throws FileNotFoundException {
        try {
            stop();
            player = new Player(new FileInputStream(file));
            player.play();
        } catch(JavaLayerException e) {
            onError.callback(e);
        }
    }
    
    public void pause(int ms) {
        stop();
        try {
            synchronized(pause) {
                pause.wait(ms);
            }
        } catch(InterruptedException ex) {
        }
    }
    
    public void stop() {
        if((player != null) && !player.isComplete()) {
            player.close();
        } else {
            synchronized(pause) {
                pause.notify();
            }
        }
    }
    
}

