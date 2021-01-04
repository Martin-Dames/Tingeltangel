/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.wimmelbuch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.ForeignImporter;
import tingeltangel.core.Script;
import tingeltangel.core.TTSEntry;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.Permutate;

/**
 *
 * @author mdames
 */
public class Wimmelbuch implements ForeignImporter {

    private final HashSet<Event> events = new HashSet<Event>();
    private final HashSet<Item> items = new HashSet<Item>();
    private final HashSet<Constraint> constraints = new HashSet<Constraint>();
    
    final static int MAX_QUEUE_SIZE = 6;
        
    private void addItem(Item item) {
        items.add(item);
    }
    
    private void addEvent(Event event) {
        events.add(event);
    }
    
    private void generate(Book book, boolean useLocking, int startOID) throws Exception {
        
        
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
        
        
        // add audio tracks
        iItems = items.iterator();
        while(iItems.hasNext()) {
            Item item = iItems.next();
            int tid = item.getAudioTrack() + startOID;
            book.addEntry(tid);
            Entry entry = book.getEntryByOID(tid);
            entry.setTTS(new TTSEntry(item.getTTS()));
        }
        iEvents = events.iterator();
        while(iEvents.hasNext()) {
            Event event = iEvents.next();
            int tid = event.getAudioTrack() + startOID;
            book.addEntry(tid);
            Entry entry = book.getEntryByOID(tid);
            entry.setTTS(new TTSEntry(event.getTTS()));
        }
        
        int[] reg_item_queue = new int[MAX_QUEUE_SIZE];
        for(int i = 0; i < MAX_QUEUE_SIZE; i++) {
            reg_item_queue[i] = i;
        }
        int reg_i = MAX_QUEUE_SIZE + 0;
        int usedRegisters = MAX_QUEUE_SIZE + 1;
        
        // add item scripts
        iItems = items.iterator();
        while(iItems.hasNext()) {
            Item item = iItems.next();
            
            StringBuilder sb = new StringBuilder();
            sb.append("// wimmelbuch item ").append(item.getName()).append("\n");
            if(useLocking) {
                sb.append("lock\n");
            }
            
            // check if current selected item is in queue
            
            for(int i = 0; i < reg_item_queue.length; i++) {
                sb.append("cmp v95,v").append(reg_item_queue[i]).append("\n");
                sb.append("je queue_remove_").append(i).append("\n");
            }
            sb.append("jmp insert\n");
            
            for(int i = 0; i < reg_item_queue.length; i++) {
                sb.append(":queue_remove_").append(i).append("\n");
                for(int k = i + 1; k < reg_item_queue.length; k++) {
                    sb.append("set v").append(reg_item_queue[k - 1]).append(",v").append(reg_item_queue[k]).append("\n");
                }
                sb.append("set v").append(reg_item_queue[reg_item_queue.length - 1]).append(",0\n");
                sb.append("jmp insert\n");
            }
            
            // insert into queue
            sb.append(":insert\n");
            for(int k = reg_item_queue.length - 1; k > 0; k--) {
                sb.append("set v").append(reg_item_queue[k]).append(",v").append(reg_item_queue[k - 1]).append("\n");
            }
            sb.append("set v").append(reg_item_queue[0]).append(",v95\n");
            
            // check for events
            iEvents = item.getEvents().iterator();
            while(iEvents.hasNext()) {
                sb.append("// event\n");
                Event event = iEvents.next();
                // check for items
                Item[][] eventItems = new Item[1][event.getItems().size()];
                Iterator<Item> iEventItems = event.getItems().iterator();
                for(int c = 0; c < eventItems[0].length; c++) {
                    eventItems[0][c] = iEventItems.next();
                }
                if(event.getMode() == Event.PERMUTATE) {
                    eventItems = permutations(eventItems[0]);
                    LinkedList<Item[]> il = new LinkedList<Item[]>();
                    for(int p = 0; p < eventItems.length; p++) {
                        if(eventItems[p][0] == item) {
                            il.add(eventItems[p]);
                        }
                    }
                    eventItems = il.toArray(new Item[0][eventItems[0].length]);
                }
                
                for(int p = 0; p < eventItems.length; p++) {
                    
                    
                    for(int i = 0; i < eventItems[p].length; i++) {
                        sb.append("cmp v").append(reg_item_queue[i]).append(",").append(eventItems[p][i].getID()).append("\n");
                        sb.append("jne not_match_").append(event.getAudioTrack()).append("_").append(p).append("\n");
                    }
                    sb.append("jmp match_").append(event.getAudioTrack()).append("\n");
                    sb.append(":not_match_").append(event.getAudioTrack()).append("_").append(p).append("\n");
                }
                sb.append("jmp not_match_").append(event.getAudioTrack()).append("\n");
                
                sb.append(":match_").append(event.getAudioTrack()).append("\n");
                
                // check for constraints
                iConstraints = event.getSetConstraints().iterator();
                while(iConstraints.hasNext()) {
                    Constraint c = iConstraints.next();
                    
                    sb.append("getbit v").append(reg_i).append(",v").append(c.getRegister() + usedRegisters).append(",v").append(c.getBit()).append("\n");
                    //sb.append("set v").append(reg_i).append(",v").append(c.getRegister() * 8 + c.getBit() + usedRegisters).append("\n");

                    sb.append("cmp v").append(reg_i).append(",0\n");
                    sb.append("je not_match_").append(event.getAudioTrack()).append("\n");
                }
                iConstraints = event.getUnsetConstraints().iterator();
                while(iConstraints.hasNext()) {
                    Constraint c = iConstraints.next();
                    
                    sb.append("getbit v").append(reg_i).append(",v").append(c.getRegister() + usedRegisters).append(",v").append(c.getBit()).append("\n");
                    //sb.append("set v").append(reg_i).append(",v").append(c.getRegister() * 8 + c.getBit() + usedRegisters).append("\n");
                    
                    sb.append("cmp v").append(reg_i).append(",1\n");
                    sb.append("je not_match_").append(event.getAudioTrack()).append("\n");
                }
                // set resulting constraints
                iConstraints = event.getResultingSetConstraints().iterator();
                while(iConstraints.hasNext()) {
                    Constraint c = iConstraints.next();
                    
                    sb.append("setbit v").append(c.getRegister() + usedRegisters).append(",v").append(c.getBit()).append("\n");
                    //sb.append("set v").append(c.getRegister() * 8 + c.getBit() + usedRegisters).append(",1\n");
                    
                }
                iConstraints = event.getResultingUnsetConstraints().iterator();
                while(iConstraints.hasNext()) {
                    Constraint c = iConstraints.next();
                    
                    //sb.append("set v").append(c.getRegister() * 8 + c.getBit() + usedRegisters).append(",0\n");
                    sb.append("unsetbit v").append(c.getRegister() + usedRegisters).append(",v").append(c.getBit()).append("\n");
                    
                }
                // play event track
                sb.append("playoid ").append(event.getAudioTrack() + startOID).append("\n");
                                
                if(event.clearQueueAfterEvent()) {
                    for(int i = 0; i < reg_item_queue.length; i++) {
                        sb.append("set v").append(reg_item_queue[i]).append(",0\n");
                    }
                }
                
                // end
                if(useLocking) {
                    sb.append("unlock\n");
                }
                sb.append("end\n");
                sb.append(":not_match_").append(event.getAudioTrack()).append("\n");
            }
            sb.append("playoid ").append(item.getAudioTrack() + startOID).append("\n");
            if(useLocking) {
                sb.append("unlock\n");
            }
            sb.append("end\n");
            
            // create script entry with sb at item.getID()
            book.addEntry(item.getID());
            Entry entry = book.getEntryByOID(item.getID());
            entry.setScript(new Script(sb.toString(), entry));
            entry.setHasCode(true);
            entry.setName(item.getName());
        }
    }
    
    private int fak(int n) {
        if(n == 1) {
            return(1);
        }
        return(n * fak(n - 1));
    }
    
    private Item[][] permutations(Item[] itemA) throws Exception {
        if(itemA.length == 0) {
            throw new Exception("event with no item found");
        }

        Item[][] r4 = new Item[fak(itemA.length)][itemA.length];
        Iterator<int[]> i = Permutate.perms(itemA.length);
        int p = 0;
        while(i.hasNext()) {
            
            int[] a = i.next();
            for(int q = 0; q < a.length; q++) {
                r4[p][q] = itemA[a[q]];
            }
            p++;
        }
        return(r4);
    }

    @Override
    public void importBook(Book book, File file) throws Exception {
        
        File audioDir = FileEnvironment.getAudioDirectory(book.getID());
        if(audioDir.exists()) {
            File[] tracks = audioDir.listFiles();
            for(int i = 0; i < tracks.length; i++) {
                if(tracks[i].isFile()) {
                    tracks[i].delete();
                }
            }
        }
        
        BufferedReader in = new BufferedReader(new FileReader(file));
        String row;
        int n = 0;
        
        int mid = 0;
        String name = "unknown";
        String publisher = "unknown";
        String author = "unknown";
        int version = 1;
        String url = "";
        boolean use_locking = true;
        int start_generated_oids = 20000;
              
        LinkedList<String> itemDefs = new LinkedList<String>();
        LinkedList<String> eventDefs = new LinkedList<String>();
        
        while((row = in.readLine()) != null) {
            n++;
            row = row.trim();
            if(!row.isEmpty() && !row.startsWith("//")) {
                int p = row.indexOf(":");
                String cmd = row.substring(0, p).trim().toLowerCase();
                String args = row.substring(p + 1).trim();
                if(cmd.equals("mid")) {
                    mid = Integer.parseInt(args);
                } else if(cmd.equals("name")) {
                    name = args;
                } else if(cmd.equals("publisher")) {
                    publisher = args;
                } else if(cmd.equals("author")) {
                    author = args;
                } else if(cmd.equals("version")) {
                    version = Integer.parseInt(args);
                } else if(cmd.equals("url")) {
                    url = args;
                } else if(cmd.equals("use-locking")) {
                    use_locking = Boolean.parseBoolean(args);
                } else if(cmd.equals("start-generated-oids")) {
                    start_generated_oids = Integer.parseInt(args);
                } else if(cmd.equals("item")) {
                    itemDefs.add(args);
                } else if(cmd.equals("event")) {
                    eventDefs.add(args);
                } else {
                    throw new Exception("unknown argument: " + cmd + " on row " + n);
                }
            }
        }
        in.close();
        
        if(mid <= 0 || mid >= 10000) {
            throw new Exception("bad mid given");
        }
        
        book.clear();
        book.setID(mid);
        book.setName(name);
        book.setPublisher(publisher);
        book.setAuthor(author);
        book.setVersion(version);
        book.setURL(url);
        
        HashMap<String, Item> _items = new HashMap<String, Item>();
        HashMap<String, Constraint> _constr = new HashMap<String, Constraint>();
        
        Iterator<String> is = itemDefs.iterator();
        while(is.hasNext()) {
            String ide = is.next();
            int p;
            
            p = ide.indexOf(":");
            int oid = Integer.parseInt(ide.substring(0, p).trim());
            ide = ide.substring(p + 1).trim();
            
            p = ide.indexOf(":");
            String iName = ide.substring(0, p).trim().toLowerCase();
            String tts = ide.substring(p + 1).trim();
            
            Item item = new Item(oid, iName, tts);
            _items.put(iName, item);
            addItem(item);
        }
        
        is = eventDefs.iterator();
        while(is.hasNext()) {
            String ide = is.next();
            int p;
            
            p = ide.indexOf(":");
            String queue = ide.substring(0, p).trim().toLowerCase();
            ide = ide.substring(p + 1).trim();
            
            p = ide.indexOf(":");
            String constr = ide.substring(0, p).trim().toLowerCase();
            ide = ide.substring(p + 1).trim();
            
            p = ide.indexOf(":");
            String resConstr = ide.substring(0, p).trim().toLowerCase();
            String tts = ide.substring(p + 1).trim();
            
            boolean permutate = false;
            if(queue.startsWith("*")) {
                permutate = true;
                queue = queue.substring(1).trim();
            }
            
            Event event = new Event(tts);
            
            String[] q = queue.split(",");
            for(int i = 0; i < q.length; i++) {
                String ins = q[i].trim();
                if(!ins.isEmpty()) {
                    event.addItem(_items.get(q[i].trim()));
                    if(permutate) {
                        event.setMode(Event.PERMUTATE);
                    } else {
                        event.setMode(Event.IN_ORDER);
                    }
                }
            }
            
            q = constr.split(",");
            for(int i = 0; i < q.length; i++) {
                String ins = q[i].trim();
                if(!ins.isEmpty()) {
                    
                    boolean not = false;
                    if(ins.startsWith("!")) {
                       not =  true;
                       ins = ins.substring(1).trim();
                    }
                    
                    if(!_constr.containsKey(ins)) {
                        _constr.put(ins, new Constraint());
                    }
                    Constraint c = _constr.get(ins);
                    
                    if(!not) {
                        event.addSetConstraint(c);
                    } else {
                        event.addUnsetConstraint(c);
                    }
                }
            }
            
            q = resConstr.split(",");
            for(int i = 0; i < q.length; i++) {
                String ins = q[i].trim();
                if(!ins.isEmpty()) {
                    
                    if(ins.equals("clear")) {
                        event.setClearQueueAfterEvent();
                    } else {
                    
                        boolean not = false;
                        if(ins.startsWith("!")) {
                           not =  true;
                           ins = ins.substring(1).trim();
                        }

                        if(!_constr.containsKey(ins)) {
                            _constr.put(ins, new Constraint());
                        }
                        Constraint c = _constr.get(ins);

                        if(!not) {
                            event.addResultingSetConstraint(c);
                        } else {
                            event.addResultingUnsetConstraint(c);
                        }
                    }
                }
            }
            
            addEvent(event);
        }
        
        
        generate(book, use_locking, start_generated_oids);
        
        book.save();
    }
    
}
