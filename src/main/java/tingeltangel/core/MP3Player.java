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

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import tingeltangel.tools.Binary;

public class MP3Player {

    private Process process = null;
    private LinkedList<PlaylistEntry> playlist = new LinkedList<PlaylistEntry>();
    private PlaylistEntry current = null;
    private HashSet<ActionListener> listeners = new HashSet<ActionListener>();
    
    private static MP3Player _player = new MP3Player();
    
    private MP3Player() {
        
    }
    
    public static MP3Player getPlayer() {
        return(_player);
    }
    
    private synchronized void _add(PlaylistEntry x) {
        playlist.add(x);
    }
    
    private synchronized void _clear() {
        playlist.clear();
    }
    
    private synchronized PlaylistEntry _poll() {
        return(playlist.poll());
    }
    
    private synchronized boolean _isEmpty() {
        return(playlist.isEmpty());
    }
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    private void playlistChanged() {
        Iterator<ActionListener> i = listeners.iterator();
        while(i.hasNext()) {
            i.next().actionPerformed(null);
        }
    }
    
    public void add(File file, String hint, float length) {
        System.out.println("mp3 player: add to playlist " + file.getAbsolutePath());
        _add(new PlaylistEntry(file, hint, length));
        playlistChanged();
        new Thread() {
            @Override
            public void run() {
                play();
            }
        }.start();
    }
    
    public void addPause(int ms) {
        _add(new PlaylistEntry(ms));
        playlistChanged();
        new Thread() {
            @Override
            public void run() {
                play();
            }
        }.start();
    }
    
    public void stopAndClean() {
        if(process != null) {
            process.destroy();
            _clear();
            playlistChanged();
        }
    }
    
    public String getCurrent() {
        if(current == null) {
            return("");
        }
        return(current.toString());
    }
    
    public String getPlaylist() {
        String s = "";
        Iterator<PlaylistEntry> iterator = playlist.iterator();
        while(iterator.hasNext()) {
            s += iterator.next().toString();
            if(iterator.hasNext()) {
                s += "\n";
            }
        }
        return(s);
    }
    
    private void play() {
        if(process != null) {
            try {
                process.exitValue();
            } catch(IllegalThreadStateException e) {
                // still running
                return;
            } catch(IllegalStateException e) {
                // still running
                return;
            }
        }
        
        
        File mpg123 = Binary.getBinary(Binary.MPG123);
        
        current = _poll();
        playlistChanged();
        if(current != null) {
            if(current.isPause()) {
                synchronized(this) {
                    try {
                        System.out.println("mp3 player: pause " + current.getPause() + "ms");
                        wait(current.getPause());
                    } catch (InterruptedException ex) {
                    }
                }
            } else {
                try {
                    System.out.println("mp3 player: " + current.getMP3().getAbsolutePath());
                    if(mpg123 != null) {
                        process = new ProcessBuilder(mpg123.getCanonicalPath(), current.getMP3().getAbsolutePath()).start();
                        try {
                            process.waitFor();
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        // pause for length of track
                        synchronized(this) {
                            try {
                                System.out.println("pause " + ((int)(current.getLength() * 1000)) + "ms");
                                wait((int)(current.getLength() * 1000));
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                } catch(IOException ioe) {
                    ioe.printStackTrace(System.out);
                }
            }
        }
        current = null;
        if(!_isEmpty()) {
            play();
        } else {
            playlistChanged();
        }
    }
    
    
}

class PlaylistEntry {
    
    private File mp3 = null;
    private int pause = 0;
    private String hint = null;
    private float length;
    
    public PlaylistEntry(File mp3, String hint, float length) {
        this.mp3 = mp3;
        this.hint = hint;
        this.length = length;
    }
    
    public PlaylistEntry(int pause) {
        this.pause = pause;
    }
    
    public boolean isPause() {
        return(mp3 == null);
    }
    
    public int getPause() {
        return(pause);
    }
    
    public File getMP3() {
        return(mp3);
    }
    
    public float getLength() {
        return(length);
    }
    
    public String getHint() {
        return(hint);
    }
    
    @Override
    public String toString() {
        if(isPause()) {
            return("Pause " + getPause() + "ms");
        } else {
            return("Track " + getHint());
        }
    }
}
