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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import tingeltangel.core.constants.ScriptFile;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.core.scripting.Command;
import tingeltangel.core.scripting.Commands;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.ProgressDialog;


public class Importer {

    
    /**
     * 
     * @param oufFile
     * @param txt (could be null)
     * @param scriptFile (could be null)
     * @param book
     * @throws Exception 
     */
    public static void importBook(File oufFile, Map<String, String> txt, File scriptFile, Book book, ProgressDialog progress) throws IOException, SyntaxError {

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
        
        // load script file
        HashMap<Integer, String> scripts = new HashMap<Integer, String>();
        HashMap<Integer, String> notes = new HashMap<Integer, String>();
        HashMap<Integer, Boolean> scriptEntryIsMP3 = new HashMap<Integer, Boolean>();
        if(scriptFile != null) {
        
            BufferedReader in = new BufferedReader(new FileReader(scriptFile));
            String row;
            boolean inScript = true;
            boolean inNote = false;
            int precode = -1;
            String script = "";
            String note = "";
            while((row = in.readLine()) != null) {
                //System.out.println(row);
                if(inNote) {
                    if(row.startsWith(ScriptFile.CONTENT)) {
                        notes.put(precode, note);
                        note = "";
                        inNote = false;
                        inScript = true;
                    } else if(row.startsWith(ScriptFile.PRECODE)) {
                        if(precode != -1) {
                            notes.put(precode, note);
                        }
                        note = "";
                        inNote = false;
                        precode = Integer.parseInt(row.substring(ScriptFile.PRECODE.length()).trim());
                    } else {
                        note += row + ScriptFile.LB;
                    }
                } else if(inScript) {
                    if(row.startsWith(ScriptFile.PRECODE)) {
                        if(precode != -1) {
                            scripts.put(precode, script);
                        }
                        script = "";
                        inScript = false;
                        precode = Integer.parseInt(row.substring(ScriptFile.PRECODE.length()).trim());
                    } else {
                        script += row + ScriptFile.LB;
                    }
                } else {
                    if(row.startsWith(ScriptFile.CONTENT)) {
                        inScript = true;
                    } else if(row.toUpperCase().startsWith("TYPE=")) {
                        String type = row.substring("TYPE=".length()).trim();
                        if(type.equals("1")) {
                            scriptEntryIsMP3.put(precode, true);
                        } else {
                            scriptEntryIsMP3.put(precode, false);
                        }
                    } else if(row.startsWith(ScriptFile.NOTE)) {
                        inNote = true;
                    }
                }
                if(inNote) {
                    notes.put(precode, note);
                } else if(inScript) {
                    if(precode != -1) {
                        scripts.put(precode, script);
                    }
                }
            }
            in.close();
        }
        
        if((oufFile == null) && (scriptFile != null)) {
            Iterator<Integer> ids = scripts.keySet().iterator();
            while(ids.hasNext()) {
                int id = ids.next();
                
                book.addEntry(id);
                Entry entry = book.getEntryByID(id);
                
                if(scriptEntryIsMP3.get(id)) {
                    entry.setMP3();
                } else {
                    Script script = new Script(scripts.get(id), entry);
                    entry.setScript(script);
                    if(script.isSub()) {
                        entry.setSub();
                    } else {
                        entry.setCode();
                    }
                }
                String note = notes.get(id);
                if(note != null) {
                    entry.setHint(note);
                }

            }
            book.save();
            return;
        }
        
        
        DataInputStream ouf = new DataInputStream(new FileInputStream(oufFile));
        
        int startOfIndex = ouf.readInt();
        if(startOfIndex != 0x66) { // why ?
            ouf.readInt(); // 2
        } 
        int firstTingID = ouf.readInt();
        int lastTingID = ouf.readInt();
        int tingIDCount = ouf.readInt();
        /*
        System.out.println("first id = " + firstTingID);
        System.out.println("last id  = " + lastTingID);
        System.out.println("id count = " + tingIDCount);
        */
        ouf.readInt(); // book id
        
        book.setMagicValue(ouf.readInt());
        book.setDate(ouf.readInt());
        ouf.readInt(); // 0
        ouf.readInt(); // 0xffff
        
        
        
        boolean firstTingIdCorrected = false;
        
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
                    firstTingIdCorrected = true;
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
        int firstEntryCode = 0;
        int firstEntryN = 0;
        int firstEntryLength = 0;
        boolean firstEntryTypeIsScript = false;
        boolean foundFirstEntryCode = false;
        
        // read index table
        for(int i = firstTingID; i <= lastTingID; i++) {
            int[] e = new int[4];
            for(int k = 0; k < 3; k++) {
                e[k] = ouf.readInt();
            }
            System.out.println(e[0] + " " + e[1] + " " + e[2]);
            if(e[2] != 0) {
                e[3] = i;
                if(!foundFirstEntryCode) {
                    if(e[1] > 0) { // entry must not be empty
                        firstEntryCode = e[0];
                        firstEntryN = i - 15001;
                        if(e[2] == 2) {
                            firstEntryTypeIsScript = true;
                        }
                        firstEntryLength = e[1];
                        foundFirstEntryCode = true;
                    }
                }
                index.add(e);
            }
        }
        
        if(progress != null) {
            progress.setMax(index.size());
        }
        int counter = 0;
        
        // find first entry
        int pos = 12 * (lastTingID - firstTingID + 1) + startOfIndex;
        System.out.println("end of index table: 0x" + Integer.toHexString(pos));
        int diff = (0x100 - (pos % 0x100)) % 0x100;
        pos += diff;
        if(firstTingIdCorrected) {
            diff -= 12;
        }
        ouf.skipBytes(diff);
        
        System.out.println("possible start of first entry: 0x" + Integer.toHexString(pos));
        System.out.println("firstEntryCode: " + firstEntryCode);
        System.out.println("firstEntryLength: " + firstEntryLength);
        
        
        // try to find first entry starting at pos
        
        // reopen file (why this? should work without)
        /*
        ouf.close();
        ouf = new DataInputStream(new FileInputStream(oufFile));
        ouf.skipBytes(pos);
        */
        
        byte[] buffer = new byte[Math.min(firstEntryLength, 50)];
        
        int k = 0;
        while(k != buffer.length) {
            k += ouf.read(buffer, k, buffer.length - k);
        }
        
        
        if(firstEntryTypeIsScript) {
            System.out.println("searching for script...");
            while(!isScriptData(buffer)) {
                pos += 0x100;
                ouf.skipBytes(0x100 - buffer.length);
                k = 0;
                while(k != buffer.length) {
                    k += ouf.read(buffer, k, buffer.length - k);
                }
            }
        } else {
            System.out.println("searching for mp3...");
            while(!isMp3Data(buffer)) {
                pos += 0x100;
                ouf.skipBytes(0x100 - buffer.length);
                k = 0;
                while(k != buffer.length) {
                    k += ouf.read(buffer, k, buffer.length - k);
                }
            }
        }
        System.out.println("start of first entry: 0x" + Integer.toHexString(pos));

        int entryOffset = pos - IndexTableCalculator.getPositionInFileFromCode(firstEntryCode, firstEntryN);
        
        
        System.out.println("offset: " + entryOffset);
        
        ouf.close();
        
        Iterator<int[]> indexIterator = index.iterator();
        buffer = new byte[4096];
        while(indexIterator.hasNext()) {
            int[] e = indexIterator.next();
            
            if(progress != null) {
                progress.setVal(counter++);
            }
            
            int epos = IndexTableCalculator.getPositionInFileFromCode(e[0], e[3] - 15001) + entryOffset;
            
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
                //System.out.println("@0x" + Integer.toHexString(epos) + " importing oid " + e[3] + " (mp3 len=" + e[1] + ") ...");
                // mp3
                out = new FileOutputStream(new File(FileEnvironment.getAudioDirectory(book.getID()), _eid + ".mp3"));
                
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
			throw new IOException("error reading mp3 with id=" + _eid);
                    }
                }
                out.close();

		// check if it's really a mp3
                
		InputStream in = new FileInputStream(new File(FileEnvironment.getAudioDirectory(book.getID()), _eid + ".mp3"));
		byte[] _buffer = new byte[Math.min(10, e[1])];
		int j = 0;
		while(j < _buffer.length) {
                    j += in.read(_buffer, j, _buffer.length - j);
		}
		in.close();
		if(!isMp3Data(_buffer)) {
                    if(e[1] == 0) {
                        System.err.println("extracted no data for oid=" + _eid + " (file is empty)");
                    } else {
                        System.err.println("extracted data for oid=" + _eid + " seems to be not an mp3 or is corrupted");
                    }
		}
                

                entry.setMP3(new File(FileEnvironment.getAudioDirectory(book.getID()), _eid + ".mp3"));
                entry.setMP3();
            } else {
                //System.out.println("@0x" + Integer.toHexString(epos) + " importing oid " + e[3] + " (script len=" + e[1] + ") ...");
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
                            throw new IOException("id " + _eid + " not found in script");
                    }
                } else {

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    OutputStream bin = new FileOutputStream(FileEnvironment.getBinObjectFile(book.getID(), entry.getTingID()));

                    int len = e[1];
                    while(len > 0) {
                        k = ouf.read(buffer, 0, Math.min(buffer.length, len));
                        if(k > 0) {
                            bout.write(buffer, 0, k);
                            bin.write(buffer, 0, k);
                            len -= k;
                        } else {
                            // TODO analyze why this error occurs at some books
                            System.err.println("error reading bin with id="+_eid);
                            len=-1;
                            throw new IOException("error reading bin with id="+_eid);
                        }
                    }
                    bin.close();
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
    
    private static boolean isMp3Data(byte[] data) {
        if(data.length <= 3) {
            return(false);
        }
        // check for id3
        if(data[0] == 'I' && data[1] == 'D' && data[2] == '3') {
            return(true);
        }
        
        // check for mp3
        if(
                            (data[0] == 'I' && data[1] == 'D' && data[2] == '3')   || // id3
                            ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xF2) || // mpeg v2 layer 3 (crc)
                            ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xF3) || // mpeg v2 layer 3
                            ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xFA) || // mpeg v1 layer 3 (crc)
                            ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xFB) || // mpeg v1 layer 3
                            ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0x00)) { // ? (seems to be valid)
            return(true);
        }
        return(false);
    }
    
    private static boolean isScriptData(byte[] data) {
        if((data.length > 3) && (data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 0)) {
            return(false);
        }
        int p = 0;
        while(p + 1 < data.length) {
            int opcode = (data[p + 1] & 0xff) | ((data[p] & 0xff) << 8);
            Command command = Commands.getCommand(opcode);
            if(command == null) {
                return(false);
            }
            p += command.getNumberOfArguments() * 2 + 2;
        }
        return(true);
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
