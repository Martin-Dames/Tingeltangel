/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.wimmelbuch;

import java.util.HashSet;
import java.util.Iterator;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.TTSEntry;

/**
 *
 * @author mdames
 */
public class Wimmelbuch {

    private HashSet<Event> events = new HashSet<Event>();
    private HashSet<Item> items = new HashSet<Item>();
    private HashSet<Constraint> constraints = new HashSet<Constraint>();
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public void addEvent(Event event) {
        events.add(event);
    }
    
    public void generate(Book book) throws Exception {
        
        // collect constraints
        constraints.clear();
        Iterator<Event> iEvents = events.iterator();
        int audioCounter = 0;
        while(iEvents.hasNext()) {
            Event event = iEvents.next();
            constraints.addAll(event.getAllConstraints());
            event.setAudioTrack(audioCounter++);
            
            Iterator<Item> iItems = event.getItems().iterator();
            while(iItems.hasNext()) {
                Item item = iItems.next();
                item.addEvent(event);
            }
        }
        Iterator<Constraint> iConstraints = constraints.iterator();
        int registerCounter = 0;
        int bitCounter = 0;
        while(iConstraints.hasNext()) {
            Constraint constraint = iConstraints.next();
            constraint.setRegister(registerCounter);
            constraint.setBit(bitCounter);
            bitCounter++;
            if(bitCounter == 0x10) {
                bitCounter = 0;
                registerCounter++;
            }
        }
        Iterator<Item> iItems = items.iterator();
        while(iItems.hasNext()) {
            iItems.next().setAudioTrack(audioCounter++);
        }
        
        int offset = 15001;
        boolean done = false;
        while(!done) {
            while(book.entryForTingIDExists(offset)) {
                offset++;
            }
            done = true;
            for(int k = 1; k < audioCounter; k++) {
                if(book.entryForTingIDExists(offset + k)) {
                    offset = offset + k;
                    done = false;
                    break;
                }
            }
        }
        if(!done) {
            throw new Exception("keine freien IDs gefunden");
        }
        
        // add audio tracks
        iItems = items.iterator();
        while(iItems.hasNext()) {
            Item item = iItems.next();
            int tid = item.getAudioTrack() + offset;
            book.addEntry(tid);
            Entry entry = book.getEntryByOID(tid);
            entry.setTTS(new TTSEntry(item.getTTS()));
        }
        iEvents = events.iterator();
        while(iEvents.hasNext()) {
            Event event = iEvents.next();
            int tid = event.getAudioTrack() + offset;
            book.addEntry(tid);
            Entry entry = book.getEntryByOID(tid);
            entry.setTTS(new TTSEntry(event.getTTS()));
        }
        
        // add item scripts
        iItems = items.iterator();
        while(iItems.hasNext()) {
            Item item = iItems.next();
            
            StringBuilder sb = new StringBuilder();
            sb.append("// wimmelbuch item " + item.getName() + "\n");
            // check for events
            iEvents = item.getEvents().iterator();
            while(iEvents.hasNext()) {
                Event event = iEvents.next();
                // check for items
                Item[][] eventItems = new Item[1][event.getItems().size()];
                Iterator<Item> iEventItems = event.getItems().iterator();
                for(int c = 0; c < eventItems[0].length; c++) {
                    eventItems[0][c] = iEventItems.next();
                }
                if(event.getMode() == Event.PERMUTATE) {
                    eventItems = permutations(eventItems[0]);
                }
                
                for(int p = 0; p < eventItems.length; p++) {
                    for(int i = 0; i < eventItems[p].length; i++) {
                        // use v10,v11,v12
                        sb.append("cmp v1").append(i).append(",").append(eventItems[p][i].getID()).append("\n");
                        sb.append("jne not_match_").append(event.getAudioTrack()).append("_").append(p).append("\n");
                    }
                    sb.append("jmp match_").append(event.getAudioTrack()).append("\n");
                    sb.append(":not_match_").append(event.getAudioTrack()).append("_").append(p).append("\n");
                }
                
                sb.append(":match_").append(event.getAudioTrack()).append("\n");
                
                // check for constraints
                iConstraints = event.getSetConstraints().iterator();
                while(iConstraints.hasNext()) {
                    Constraint c = iConstraints.next();
                    sb.append("getbit v16,v").append(c.getRegister() + 20).append(",v").append(c.getBit()).append("\n");
                    sb.append("cmp v16,0\n");
                    sb.append("je not_match_").append(event.getAudioTrack()).append("\n");
                }
                iConstraints = event.getUnsetConstraints().iterator();
                while(iConstraints.hasNext()) {
                    Constraint c = iConstraints.next();
                    sb.append("getbit v16,v").append(c.getRegister() + 20).append(",v").append(c.getBit()).append("\n");
                    sb.append("cmp v16,1\n");
                    sb.append("je not_match_").append(event.getAudioTrack()).append("\n");
                }
                // set resulting constraints
                
                // play event track
                
                // unset v10,v11,v12 if no further event exists
                
                // end
                sb.append(":not_match_").append(event.getAudioTrack()).append("\n");
            }
            sb.append("playoid ").append(item.getAudioTrack()).append("\n");
            sb.append("end\n");
            
            // create script entry with sb at item.getID()
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Wimmelbuch wimmelbuch = new Wimmelbuch();
        
        Item feuerzeug = new Item(15001, "feuerzeug", "ein feuerzeug");
        wimmelbuch.addItem(feuerzeug);
        
        Item holz = new Item(15002, "holz", "etwas holz");
        wimmelbuch.addItem(holz);
        
        Item ofen = new Item(15003, "ofen", "ein ofen");
        wimmelbuch.addItem(ofen);
        
        Item katze = new Item(15004, "katze", "eine kleine katze");
        wimmelbuch.addItem(katze);
        
        Item pfanne = new Item(15005, "pfanne", "eine bratpfanne");
        wimmelbuch.addItem(pfanne);
        
        Item kettensaege = new Item(15006, "kettensaege", "eine kettensaege");
        wimmelbuch.addItem(kettensaege);
        
        Constraint ofen_warm = new Constraint();
        Constraint katze_matsch = new Constraint();
        
        Event e1 = new Event("du hast den ofen angemacht. nun wird es warm");
        e1.addItem(feuerzeug);
        e1.addItem(holz);
        e1.addItem(ofen);
        e1.setMode(Event.PERMUTATE);
        e1.addUnsetConstraint(ofen_warm);
        e1.addResultingSetConstraint(ofen_warm);
        wimmelbuch.addEvent(e1);
        
        Event e2 = new Event("der ofen ist schon warm");
        e2.addItem(feuerzeug);
        e2.addItem(holz);
        e2.addItem(ofen);
        e2.setMode(Event.PERMUTATE);
        e2.addSetConstraint(ofen_warm);
        wimmelbuch.addEvent(e2);
        
        Event e3 = new Event("nun ist die kleine katze matsch");
        e3.addItem(katze);
        e3.addItem(pfanne);
        e3.setMode(Event.PERMUTATE);
        e3.addUnsetConstraint(katze_matsch);
        e3.addResultingSetConstraint(katze_matsch);
        wimmelbuch.addEvent(e3);
        
        Event e4 = new Event("die katze ist nun noch etwas matschiger");
        e4.addItem(katze);
        e4.addItem(pfanne);
        e4.setMode(Event.PERMUTATE);
        e4.addSetConstraint(katze_matsch);
        wimmelbuch.addEvent(e4);
        
        Event e5  = new Event("eine matschige katze");
        e5.addItem(katze);
        e5.addSetConstraint(katze_matsch);
        wimmelbuch.addEvent(e5);
        
        wimmelbuch.generate(null);
    }

    private Item[][] permutations(Item[] itemA) throws Exception {
        switch(itemA.length) {
            case 0:
                throw new Exception("event with no item found");
            case 1:
                Item[][] r1 = new Item[1][1];
                r1[0][0] = itemA[0];
                return(r1);
            case 2:
                Item[][] r2 = new Item[2][2];
                r2[0][0] = itemA[0];
                r2[0][1] = itemA[1];
                r2[1][0] = itemA[1];
                r2[1][1] = itemA[0];
                return(r2);
            case 3:
                Item[][] r3 = new Item[6][3];
                r3[0][0] = itemA[0];
                r3[0][1] = itemA[1];
                r3[0][2] = itemA[2];
                
                r3[1][0] = itemA[0];
                r3[1][1] = itemA[2];
                r3[1][2] = itemA[1];
                
                r3[2][0] = itemA[1];
                r3[2][1] = itemA[0];
                r3[2][2] = itemA[2];
                
                r3[3][0] = itemA[1];
                r3[3][1] = itemA[2];
                r3[3][2] = itemA[0];
                
                r3[4][0] = itemA[2];
                r3[4][1] = itemA[0];
                r3[4][2] = itemA[1];
                
                r3[5][0] = itemA[2];
                r3[5][1] = itemA[1];
                r3[5][2] = itemA[0];
                
                return(r3);
        }
        throw new Exception("found more than 3 items in event");
    }
}
