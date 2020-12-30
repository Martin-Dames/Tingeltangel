/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.wimmelbuch;

import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author mdames
 */
public class Item {

    private int id;
    private String tts;
    private int audioTrack = 0;
    private String name;
    private TreeSet<Event> events = new TreeSet<Event>();
    
    public Item(int id, String name, String tts) {
        this.id = id;
        this.tts = tts;
        this.name = name;
    }
    
    public String getName() {
        return(name);
    }
    
    public int getID() {
        return(id);
    }
    
    public String getTTS() {
        return(tts);
    }
    
    public Set<Event> getEvents() {
        return(events);
    }
    
    public void setAudioTrack(int audioTrack) {
        this.audioTrack = audioTrack;
    }
    
    public int getAudioTrack() {
        return(audioTrack);
    }
    
    void addEvent(Event event) {
        if((event.getMode() == Event.IN_ORDER) && (event.getItems().get(0) != this)) {
            return;
        }
        events.add(event);
    }
}
