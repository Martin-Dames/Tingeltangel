
package tingeltangel.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

import tingeltangel.core.constants.OufFile;
import tingeltangel.core.constants.PngFile;
import tingeltangel.core.constants.ScriptFile;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.gui.ProgressDialog;


public class Importer {

    /**
     * 
     * @param oufFile
     * @param txt (could be null)
     * @param scriptFile (could be null)
     * @param book
     * @throws Exception 
     */
    public static void importOuf(File oufFile, HashMap<String, String> txt, File scriptFile, Book book, ProgressDialog progress) throws IOException {
        
        DataInputStream ouf = new DataInputStream(new FileInputStream(oufFile));
        
        if(txt != null) {
            book.setAuthor(txt.get(TxtFile.KEY_AUTHOR));
            book.setName(txt.get(TxtFile.KEY_NAME));
            book.setPublisher(txt.get(TxtFile.KEY_PUBLISHER));
            book.setURL(txt.get(TxtFile.KEY_URL));
            book.setVersion(Integer.parseInt(txt.get(TxtFile.KEY_VERSION)));
        } else {
            book.setAuthor("unknown");
            book.setName("unknown");
            book.setPublisher("unknown");
            book.setURL("unknown");
            book.setVersion(1);
        }
        
        
        int startOfIndex = ouf.readInt();
        if(startOfIndex != 0x66)
        ouf.readInt(); // 2
        int firstTingID = ouf.readInt();
        int lastTingID = ouf.readInt();
        int tingIDCount = ouf.readInt();
        System.out.println("first id = " + firstTingID);
        System.out.println("last id  = " + lastTingID);
        System.out.println("id count = " + tingIDCount);
        
        book.setID(ouf.readInt());
        book.setMagicValue(ouf.readInt());
        book.setDate(ouf.readInt());
        ouf.readInt(); // 0
        ouf.readInt(); // 0xffff
        
        // load script file
        
        HashMap<Integer, String> scripts = new HashMap<Integer, String>();
        HashMap<Integer, String> notes = new HashMap<Integer, String>();
        if(scriptFile != null) {
        
            BufferedReader in = new BufferedReader(new FileReader(scriptFile));
            String row;
            boolean inScript = true;
            boolean inNote = false;
            int precode = -1;
            String script = "";
            String note = "";
            while((row = in.readLine()) != null) {
                System.out.println(row);
                if(inNote) {
                    if(row.startsWith(ScriptFile.CONTENT)) {
                        notes.put(precode, note);
                        note = "";
                        inNote = false;
                        inScript = true;
                    } else if(row.startsWith(ScriptFile.PRECODE)) {
                        notes.put(precode, note);
                        note = "";
                        inNote = false;
                        precode = Integer.parseInt(row.substring(ScriptFile.PRECODE.length()).trim());
                    } else {
                        note += row + ScriptFile.LB;
                    }
                } else if(inScript) {
                    if(row.startsWith(ScriptFile.PRECODE)) {
                        scripts.put(precode, script);
                        script = "";
                        inScript = false;
                        precode = Integer.parseInt(row.substring(ScriptFile.PRECODE.length()).trim());
                    } else {
                        script += row + ScriptFile.LB;
                    }
                } else {
                    if(row.startsWith(ScriptFile.CONTENT)) {
                        inScript = true;
                    } else if(row.startsWith(ScriptFile.NODE)) {
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
        }
        
        
        // read till startOfIndex
        ouf.skipBytes(startOfIndex - 40);
        
        if(firstTingID != 15001) {
            
            if(firstTingID == 15000) {
                System.out.println("WARNING: first ting id is 15000. Trying auto correction...");
                
                ouf.readInt();
                ouf.readInt();
                int type15000 = ouf.readInt();
                if(type15000 == 0) {
                    System.out.println("Auto correction successfull");
                    firstTingID = 15001;
                    tingIDCount--;
                } else {
                    System.out.println("Auto correction failed. The import is expected to fail.");
                }
            } else {
                System.out.println("WARNING: first ting id is neither 15001 nor 15000. The import is expected to fail.");
            }
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
        
        progress.setMax(index.size());
        int counter = 0;
        
        // find first entry
        int pos = 12 * (lastTingID - firstTingID + 1) + startOfIndex;
        int diff = (0x100 - (pos % 0x100)) % 0x100;
        ouf.skipBytes(diff);
        pos += diff;
        
        System.out.println("possible start of first entry: " + Integer.toHexString(pos));
        
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
        buffer = new byte[4096];
        while(indexIterator.hasNext()) {
            int[] e = indexIterator.next();
            
            progress.setVal(counter++);
            
            int epos = Tools.getPositionInFileFromCode(e[0], e[3] - 15001) + entryOffset;
            
            ouf = new DataInputStream(new FileInputStream(oufFile));
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
                try {
                    out = new FileOutputStream(new File(book.getMP3Path(), _eid + ".mp3"));
                } catch(NoBookException nbo) {
                    throw new Error(nbo);
                }
                
                int len = e[1];
                while(len > 0) {
                    k = ouf.read(buffer, 0, Math.min(buffer.length, len));
                    if(k>0) {
                        out.write(buffer, 0, k);
                        len -= k;
                    } else {
                    	// TODO analyze why this error occurs at some books
                    	System.err.println("error reading mp3 with id="+_eid);
                    	len=-1;
                    }
                }
                out.close();
                try {
                    entry.setMP3(new File(book.getMP3Path(), _eid + ".mp3"));
                    entry.setMP3();
                } catch(NoBookException nbo) {
                    throw new Error(nbo);
                }
            } else {
                // script
                
                if(scriptFile != null) {
                    if(scripts.get(e[3])!=null) {
                            Script script = new Script(scripts.get(e[3]), entry);
                            entry.setScript(script);
                            if(script.isSub()) {
                                entry.setSub();
                            } else {
                                entry.setCode();
                            }
                    } else {
                            System.err.println("\tid " + _eid + " not found in script");	
                    }
                } else {

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();

                    int len = e[1];
                    while(len > 0) {
                        k = ouf.read(buffer, 0, Math.min(buffer.length, len));
                        if(k>0) {
                            bout.write(buffer, 0, k);
                            len -= k;
                        } else {
                            // TODO analyze why this error occurs at some books
                            System.err.println("error reading bin with id="+_eid);
                            len=-1;
                        }
                    }

                    
                    Script script = new Script(bout.toByteArray(), entry);
                    entry.setScript(script);
                    if(script.isSub()) {
                        entry.setSub();
                    } else {
                        entry.setCode();
                    }
                }
            }
            
            String note = notes.get(e[3]);
            if(note != null) {
                entry.setHint(note);
            }
            
            ouf.close();
        }
        book.save();
    }
    
    /*
    public static void importOfficial(int id, File path, Book book) throws IOException {
        
        File tmpDir = new File(path, "tmp");
        tmpDir.mkdir();
        
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        
        Stick.downloadOfficial(tmpDir, id);
        
        File src = new File(tmpDir, _id + ScriptFile._EN_SRC);
        if(!src.exists()) {
            src = null;
        }
        
        importOuf(new File(tmpDir, _id + OufFile._EN_OUF), Books.getBook(id), src, book);
        
        
        
        new File(tmpDir, _id + OufFile._EN_OUF).delete();
        new File(tmpDir, _id + TxtFile._EN_TXT).delete();
        new File(tmpDir, _id + PngFile._EN_PNG).delete();
        if(src != null) {
            src.delete();
        }
        tmpDir.delete();
        
        
    }
    */
}
