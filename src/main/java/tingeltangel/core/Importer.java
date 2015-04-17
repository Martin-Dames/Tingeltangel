
package tingeltangel.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


public class Importer {

    
    public static void importOfficial(int id, File path, MP3Player player, Book book) throws IOException {
        
        File tmpDir = new File(path, "tmp");
        tmpDir.mkdir();
        
        File mp3Dir = new File(path, "audio");
        //File hexDir = new File(path, "hex");
        //hexDir.mkdir();
        
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        
        Stick.downloadOfficial(tmpDir, id);
        DataInputStream ouf = new DataInputStream(new FileInputStream(new File(tmpDir, _id + "_en.ouf")));
        File scriptFile = new File(tmpDir, _id + "_en.src");
        
        
        HashMap<String, String> txt = Books.getBook(id);
        
        book.setAuthor(txt.get("Author"));
        book.setName(txt.get("Name"));
        book.setPublisher(txt.get("Publisher"));
        book.setURL(txt.get("URL"));
        book.setVersion(Integer.parseInt(txt.get("Book Version")));
        
        // load script file
        HashMap<Integer, String> scripts = new HashMap<Integer, String>();
        HashMap<Integer, String> notes = new HashMap<Integer, String>();
        BufferedReader in = new BufferedReader(new FileReader(scriptFile));
        String row;
        boolean inScript = false;
        boolean inNote = false;
        int precode = -1;
        String script = "";
        String note = "";
        while((row = in.readLine()) != null) {
            System.out.println(row);
            if(inNote) {
                if(row.startsWith("[Content]")) {
                    notes.put(precode, note);
                    note = "";
                    inNote = false;
                    inScript = true;
                } else if(row.startsWith("Precode=")) {
                    notes.put(precode, note);
                    note = "";
                    inNote = false;
                    precode = Integer.parseInt(row.substring("Precode=".length()).trim());
                } else {
                    note += row + "\n";
                }
            } else if(inScript) {
                if(row.startsWith("Precode=")) {
                    scripts.put(precode, script);
                    script = "";
                    inScript = false;
                    precode = Integer.parseInt(row.substring("Precode=".length()).trim());
                } else {
                    script += row + "\n";
                }
            } else {
                if(row.startsWith("[Content]")) {
                    inScript = true;
                } else if(row.startsWith("[Note]")) {
                    inNote = true;
                }
            }
        }
        in.close();
        if(inNote) {
            notes.put(precode, note);
        } else if(inScript) {
            scripts.put(precode, script);
        }
        
        
        int startOfIndex = ouf.readInt();
        ouf.readInt(); // 2
        int firstTingID = ouf.readInt();
        int lastTingID = ouf.readInt();
        int tingIDCount = ouf.readInt();
        
        book.setID(ouf.readInt());
        book.setMagicValue(ouf.readInt());
        book.setDate(ouf.readInt());
        ouf.readInt(); // 0
        ouf.readInt(); // 0xffff
        
        // read till startOfIndex
        ouf.skipBytes(startOfIndex - 40);
        
        if(firstTingID != 15001) {
            System.out.println("WARNING: first ting id is " + firstTingID);
        }
        if(tingIDCount != lastTingID - firstTingID + 1) {
            System.out.println("WARNING: index count missmatch (first=" + firstTingID + ", last=" + lastTingID + ", count=" + tingIDCount + ")");
        }
        
        LinkedList<int[]> index = new LinkedList<int[]>();
        int firstEntryCode = -1;
        
        // read index table
        for(int i = firstTingID; i <= lastTingID; i++) {
            int[] e = new int[4];
            for(int k = 0; k < 3; k++) {
                e[k] = ouf.readInt();
            }
            if(e[2] != 0) {
                e[3] = i;
                if(firstEntryCode < 0) {
                    firstEntryCode = e[0];
                }
                index.add(e);
            }
        }
        
        // find first entry
        int pos = 12 * (lastTingID - firstTingID + 1) + startOfIndex;
        int diff = (0x100 - (pos % 0x100)) % 0x100;
        ouf.skipBytes(diff);
        pos += diff;
        
        byte[] buffer = new byte[12];
        
        int k = 0;
        while(k != buffer.length) {
            k += ouf.read(buffer, k, buffer.length - k);
        }
        boolean isempty = true;
        for(k = 0; k < buffer.length; k++) {
            if(buffer[k] != 0) {
                isempty = false;
                break;
            }
        }
        while(isempty) {
            pos += 0x100;
            ouf.skipBytes(0x100 - buffer.length);
            k = 0;
            while(k != buffer.length) {
                k += ouf.read(buffer, k, buffer.length - k);
            }
            for(k = 0; k < buffer.length; k++) {
                if(buffer[k] != 0) {
                    isempty = false;
                    break;
                }
            }
        }
        int entryOffset = pos - Tools.getPositionInFileFromCode(firstEntryCode, 0);
        if(entryOffset != 0) {
            System.out.println("INFO: entryOffest=" + entryOffset);
        }
        
        ouf.close();
        
        Iterator<int[]> indexIterator = index.iterator();
        int c = 0;
        buffer = new byte[4096];
        while(indexIterator.hasNext()) {
            int[] e = indexIterator.next();
            
            int epos = Tools.getPositionInFileFromCode(e[0], c++) + entryOffset;
            
            
            ouf = new DataInputStream(new FileInputStream(new File(tmpDir, _id + "_en.ouf")));
            ouf.skipBytes(epos);
            
            OutputStream out;
            
            String _eid = Integer.toString(e[3]);
            while(_eid.length() < 5) {
                _eid = "0" + _eid;
            }
            
            // book.add e to index table
            book.addEntry(e[3]);
            
            
            Entry entry = book.getEntryByID(e[3]);
            
            if(e[2] == 1) {
                // mp3
                //System.out.println("extracting mp3 @" + epos + " (id=" + _eid + ") ...");
                out = new FileOutputStream(new File(mp3Dir, _eid + ".mp3"));
                
                int len = e[1];
                while(len > 0) {
                    k = ouf.read(buffer, 0, Math.min(buffer.length, len));
                    out.write(buffer, 0, k);
                    len -= k;
                }
                out.close();
                try {
                    entry.setMP3(new File(mp3Dir, _eid + ".mp3"));
                    entry.setMP3();
                } catch(NoBookException nbo) {
                    throw new Error(nbo);
                }
            } else {
                // script
                //System.out.println("extracting bin @" + epos + " (id=" + _eid + ") ...");
                
                entry.setScript(new Script(scripts.get(e[3]), entry));
                entry.setCode();
            }
            
            note = notes.get(e[3]);
            if(note != null) {
                entry.setHint(note);
            }
            
            ouf.close();
        }
        
        new File(tmpDir, _id + "_en.ouf").delete();
        
        
    }
    
    public static void main(String[] args) throws IOException {
        //importOfficial(15, new File("import_test"), new MP3Player());
    }
    
}
