
package tingeltangel.core;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class MP3Player {

    private Process process = null;
    private LinkedList<PlaylistEntry> playlist = new LinkedList<PlaylistEntry>();
    private PlaylistEntry current = null;
    private HashSet<ActionListener> listeners = new HashSet<ActionListener>();
    
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
    
    public void add(File file, String hint) {
        System.out.println("mp3 player: add to playlist " + file.getAbsolutePath());
        _add(new PlaylistEntry(file, hint));
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
            } catch(IllegalStateException e) {
                // still running
                return;
            }
        }
        
        
        String mpg123Binary = "mpg123";
        
        if(System.getProperty("os.name").startsWith("Windows")) {
            mpg123Binary = Properties.getProperty(Properties.WIN_MPG123+Properties._PATH);
        }
        
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
                    process = new ProcessBuilder(mpg123Binary, current.getMP3().getAbsolutePath()).start();
                    try {
                        process.waitFor();
                    } catch (InterruptedException ex) {
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
    
    public PlaylistEntry(File mp3, String hint) {
        this.mp3 = mp3;
        this.hint = hint;
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