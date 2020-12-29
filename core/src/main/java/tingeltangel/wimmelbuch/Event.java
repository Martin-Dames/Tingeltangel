/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.wimmelbuch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mdames
 */
public class Event implements Comparable {

    public final static int IN_ORDER = 0;
    public final static int PERMUTATE = 1;
    
    private final String tts;
    private final LinkedList<Item> items = new LinkedList<Item>();
    private int mode = IN_ORDER;
    private boolean clearQueueAfterEvent = false;
    private int audioTrack = 0;
    
    private final LinkedList<Constraint> setConstraints = new LinkedList<Constraint>();
    private final LinkedList<Constraint> unsetConstraints = new LinkedList<Constraint>();
    private final LinkedList<Constraint> resultingSetConstraints = new LinkedList<Constraint>();
    private final LinkedList<Constraint> resultingUnsetConstraints = new LinkedList<Constraint>();
    
    public Event(String tts) {
        this.tts = tts;
    }

    public void setClearQueueAfterEvent() {
        clearQueueAfterEvent = true;
    }
    
    public boolean clearQueueAfterEvent() {
        return(clearQueueAfterEvent);
    }
    
    public LinkedList<Constraint> getSetConstraints() {
        return(setConstraints);
    }
    
    public LinkedList<Constraint> getUnsetConstraints() {
        return(unsetConstraints);
    }

    public LinkedList<Constraint> getResultingSetConstraints() {
        return(resultingSetConstraints);
    }
    
    public LinkedList<Constraint> getResultingUnsetConstraints() {
        return(resultingUnsetConstraints);
    }
    
    public int getAudioTrack() {
        return(audioTrack);
    }
    
    public String getTTS() {
        return(tts);
    }
    
    public void setAudioTrack(int audioTrack) {
        this.audioTrack = audioTrack;
    }
    
    public void addItem(Item item) throws Exception {
        if(items.size() == Wimmelbuch.MAX_QUEUE_SIZE) {
            throw new Exception();
        }
        items.addFirst(item);
    }
    
    public List<Item> getItems() {
        return(items);
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public int getMode() {
        return(mode);
    }

    public HashSet<Constraint> getAllConstraints() {
        HashSet<Constraint> constraints = new HashSet<Constraint>();
        constraints.addAll(setConstraints);
        constraints.addAll(unsetConstraints);
        constraints.addAll(resultingSetConstraints);
        constraints.addAll(resultingUnsetConstraints);
        return(constraints);
    }
    
    public void addUnsetConstraint(Constraint constraint) {
        unsetConstraints.add(constraint);
    }

    public void addSetConstraint(Constraint constraint) {
        setConstraints.add(constraint);
    }

    public void addResultingSetConstraint(Constraint constraint) {
        resultingSetConstraints.add(constraint);
    }

    public void addResultingUnsetConstraint(Constraint constraint) {
        resultingUnsetConstraints.add(constraint);
    }
    
    @Override
    public int compareTo(Object e) {
        if(e == null) {
            throw new Error();
        }
        if(!(e instanceof Event)) {
            throw new Error();
        }
        if(equals(e)) {
            return(0);
        }
        Event event = (Event)e;
        if(items.size() > event.items.size()) {
            return(-1);
        } else if(items.size() < event.items.size()) {
            return(1);
        }
        int c = getEventString().compareTo(event.getEventString());
        if(c != 0) {
            return(c);
        }
        c = getConstraintString().compareTo(event.getConstraintString());
        if(c != 0) {
            return(c);
        }
        throw new Error("found unique events");
    }
    
    private String getConstraintString() {
        String s = "";
        Iterator<Constraint> i = setConstraints.iterator();
        while(i.hasNext()) {
            Constraint c = i.next();
            s += "," + c.getRegister() + "." + c.getBit();
        }
        i = unsetConstraints.iterator();
        while(i.hasNext()) {
            Constraint c = i.next();
            s += ",!" + c.getRegister() + "." + c.getBit();
        }
        return(s);
    }
    
    private String getEventString() {
        Iterator<Item> i = items.iterator();
        String s = "";
        while(i.hasNext()) {
            s += "," + i.next().getName();
        }
        return(s);
    }
    
}
