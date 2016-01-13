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
import tingeltangel.tools.Binary;

public class MP3Player {

    private Process process = null;
    private static MP3Player player = new MP3Player();
    private Thread pauseThread = null;
    
    private MP3Player() {
        
    }
    
    public static MP3Player getPlayer() {
        return(player);
    }
    

    public void stop() {
        if(process != null) {
            boolean kill = false;
            try {
                process.exitValue();
            } catch(IllegalThreadStateException e) {
                // still running
                kill = true;
            } catch(IllegalStateException e) {
                // still running
                kill = true;
            }
            if(kill) {
                process.destroy();
                process = null;
            }
        } else if(pauseThread != null) {
            pauseThread.interrupt();
        }
    }
    
    public void play(File file) {
        stop();
        File mpg123 = Binary.getBinary(Binary.MPG123);
        
        try {
            if(mpg123 != null) {
                process = new ProcessBuilder(mpg123.getCanonicalPath(), file.getAbsolutePath()).start();
                try {
                    process.waitFor();
                } catch (InterruptedException ex) {
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace(System.out);
        }
    }
    
    public void pause(int ms) {
        stop();
        try {
            pauseThread = Thread.currentThread();
            pauseThread.wait(ms);
        } catch (InterruptedException ex) {
        }
        pauseThread = null;
    }
    
}

