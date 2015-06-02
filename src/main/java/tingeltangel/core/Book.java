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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tingeltangel.core.constants.OufFile;
import tingeltangel.core.constants.PngFile;
import tingeltangel.core.constants.ScriptFile;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.core.scripting.Emulator;
import tingeltangel.core.scripting.RegisterListener;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.ProgressDialog;
import tingeltangel.tools.FileEnvironment;

public class Book {

    private final static long DEFAULT_MAGIC_VALUE = 0x0000000b;
    
    private SortedIntList indexIDs = new SortedIntList();
    private HashMap<Integer, Entry> indexEntries = new HashMap<Integer, Entry>();
        
    private boolean changed = false;
    
    private int id;
    private String name;
    private String publisher;
    private String author;
    private int version;
    private String url;
    
    private long date = new Date().getTime() / 1000;
    private long magicValue = DEFAULT_MAGIC_VALUE;
    
    
    private Emulator emulator;
    
    
    public final void clear() {
        name = "My Book";
        publisher = "Me";
        author = "Me";
        version = 1;
        url = "";
        indexIDs = new SortedIntList();
        indexEntries = new HashMap<Integer, Entry>();
        changed = false;
        date = new Date().getTime() / 1000;
        
        magicValue = DEFAULT_MAGIC_VALUE;
    }
        
    public void generateTestBooklet(PrintWriter out) {
        LinkedList<Tupel<Integer, String>> booklet = new LinkedList<Tupel<Integer, String>>();
        Iterator<Integer> ids = indexIDs.iterator();
        while(ids.hasNext()) {
            int tid = ids.next();
            Entry entry = indexEntries.get(tid);
            if(entry.isMP3() || entry.isCode()) {
                String txt = entry.getHint();
                if(entry.isMP3()) {
                    txt += " (" + entry.getMP3().getName() + ")";
                }
                booklet.add(new Tupel<Integer, String>(tid, txt));
            }
        }
        // generate booklet
        Codes.setResolution(Codes.DPI600);
        Codes.drawBooklet(name, booklet, out);
    }
    
    public long getMagicValue() {
        return(magicValue);
    }
        
    public long getDate() {
        return(date);
    }
    
    public void setMagicValue(long magicValue) {
        this.magicValue = magicValue;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public Emulator getEmulator() {
        return(emulator);
    }
    
    public boolean unsaved() {
        return(changed);
    }
    
    void changeMade() {
        changed = true;
    }
    
    public String getName() {
        return(name);
    }
    
    public String getPublisher() {
        return(publisher);
    }
    
    public String getAuthor() {
        return(author);
    }
    
    public String getUrl() {
        return(url);
    }
    
    public int getVersion() {
        return(version);
    }
    
    public void setName(String name) {
        this.name = name;
        changeMade();
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
        changeMade();
    }
    
    public void setAuthor(String author) {
        this.author = author;
        changeMade();
    }
    
    public void setVersion(int version) {
        this.version = version;
        changeMade();
    }
    
    public void setID(int id) {
        this.id = id;
    }
    
    public void setURL(String url) {
        this.url = url;
        changeMade();
    }
    
    public Book(int id) {
        this.id = id;
        clear();
        emulator = new Emulator(this);
    }
    
    public void addEntry(int tingID) {
        if((tingID < 15000) || (tingID > 0x10000)) {
            throw new Error("TingID=" + tingID + " out of range");
        }
        if(!indexIDs.containsKey(tingID)) {
            indexIDs.add(tingID);
            indexEntries.put(tingID, new Entry(this, tingID));
        }
        changeMade();
    }
    
    public Entry getEntry(int i) {
        return(indexEntries.get(indexIDs.get(i)));
    }
    
    public Entry getEntryByID(int id) {
        return(indexEntries.get(id));
    }
    
    public boolean entryForTingIDExists(int tingID) {
        return(indexEntries.get(tingID) != null);
    }
    
    public Entry getEntryFromTingID(int tingID) {
        Entry e = indexEntries.get(tingID);
        if(e == null) {
            e = new Entry(this, tingID);
        }
        return(e);
    }
    
    public int getID() {
        return(id);
    }
    
    public int getSize() {
        return(indexIDs.size());
    }
    
    public int getLastID() {
        if(indexIDs.size() == 0) {
            return(15000);
        }
        int lastFound = -1;
        Iterator<Integer> i = indexIDs.iterator();
        while(i.hasNext()) {
            int _id = i.next();
            Entry e = indexEntries.get(_id);
            if(!e.isEmpty()) {
                lastFound = _id;
            }
        }
        if(lastFound == -1) {
            return(15000);
        }
        return(lastFound);
    }

    void removeEntry(int row) {
        int tingID = indexIDs.get(row);
        indexIDs.remove(row);
        indexEntries.remove(tingID);
        changeMade();
    }

    private static String encodeAttribute(String v) {
        return(v.replace("&", "&amp;").replace("\"", "&quot;"));
    }
    
    private static String encodeValue(String v) {
        return(v.replace("&", "&amp;").replace("<", "&gt;"));
    }
    
    public void save() throws IOException {
        
        PrintWriter xml = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileEnvironment.getXML(id)), "UTF-8"));
        xml.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.println("<book");
        xml.println("\t\tformat=\"1\"");
        xml.println("\t\tid=\"" + id + "\"");
        xml.println("\t\tversion=\"" + version + "\"");
        xml.println("\t\tdate=\"" + date + "\"");
        xml.println("\t\ttitle=\"" + encodeAttribute(name) + "\"");
        xml.println("\t\tpublisher=\"" + encodeAttribute(publisher) + "\"");
        xml.println("\t\tauthor=\"" + encodeAttribute(author) + "\"");
        xml.println("\t\turl=\"" + encodeAttribute(url) + "\"");
        xml.println("\t\tmagic=\"" + magicValue + "\"");
        xml.println(">");
        xml.println("\t<entries>");
        Iterator<Integer> iterator = indexIDs.iterator();
        while(iterator.hasNext()) {
            Entry entry = indexEntries.get(iterator.next());
            if(!entry.isEmpty()) {
                
                String type = "script";
                if(entry.isSub()) {
                    type = "sub";
                } else if(entry.isMP3()) {
                    type = "mp3";
                } else if(entry.isTTS()) {
                    type = "tts";
                }
                xml.print("\t\t<entry id=\"" + entry.getTingID() + "\" type=\"" + type + "\"");
                if(entry.isMP3()) {
                    String mp3name = "";
                    if(entry.getMP3() != null) {
                        mp3name = entry.getMP3().getName();
                    }
                    xml.print(" mp3=\"" + encodeAttribute(mp3name) + "\"");
                }
                xml.println(">");
                if(entry.isCode() || entry.isSub()) {
                    xml.println("\t\t\t<code>" + encodeValue(entry.getScript().toString()) + "</code>");
                } else if(entry.isTTS()) {
                    xml.println("\t\t\t<tts>" + encodeValue(entry.getScript().toString()) + "</tts>");
                }
                xml.println("\t\t\t<hint>" + encodeValue(entry.getHint()) + "</hint>");
                xml.println("\t\t</entry>");
            }
        }
        xml.println("\t</entries>");
        xml.println("\t<registers>");
        for(int i = 0; i <= emulator.getMaxRegister(); i++) {
            if(!emulator.getHint(i).trim().isEmpty()) {
                xml.println("\t\t<register id=\"" + i + "\">");
                xml.println("\t\t\t<hint>" + encodeValue(emulator.getHint(i)) + "</hint>");
                xml.println("\t\t</register>");
            }
        }
        xml.println("\t</registers>");
        xml.println("</book>");
        xml.close();
        
        changed = false;
        
    }
    
    public static String getLabel(File xmlFile) throws IOException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            
            Element bookElement = doc.getDocumentElement();
            
            int format = Integer.parseInt(bookElement.getAttribute("format")); // sould be 1
            if(format != 1) {
                throw new IOException("unknown file format");
            }
            
            String id = bookElement.getAttribute("id");
            while(id.length() < 5) {
                id = "0" + id;
            }
            
            return(id + ": " + bookElement.getAttribute("title") + " (" + bookElement.getAttribute("author") + ")");
        } catch (SAXException ex) {
            throw new IOException(ex);
        } catch (ParserConfigurationException ex) {
            throw new Error();
        } catch (NumberFormatException ex) {
            throw new IOException(ex);
        }
    }
    
    private static String getTagContent(Node node) {
        NodeList childNodes = node.getChildNodes();
        String content = "";
        if(childNodes.getLength() > 0) {
            content = childNodes.item(0).getNodeValue();
        }
        return(content);
    }
    
    public static void loadXML(File file, Book book) throws IOException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            
            Element bookElement = doc.getDocumentElement();
            
            int format = Integer.parseInt(bookElement.getAttribute("format")); // sould be 1
            if(format != 1) {
                throw new IOException("unknown file format");
            }
            
            if(book.getID() != Integer.parseInt(bookElement.getAttribute("id"))) {
                throw new IOException("book id missmatch");
            };
            
            book.version = Integer.parseInt(bookElement.getAttribute("version"));
            book.date = Integer.parseInt(bookElement.getAttribute("date"));
            book.magicValue = Integer.parseInt(bookElement.getAttribute("magic"));
            book.name = bookElement.getAttribute("title");
            book.publisher = bookElement.getAttribute("publisher");
            book.author = bookElement.getAttribute("author");
            book.url = bookElement.getAttribute("url");
                        
            
            NodeList entries = doc.getElementsByTagName("entry");
            for(int i = 0; i < entries.getLength(); i++) {
                Node entryNode = entries.item(i);
                if(entryNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element)entryNode;
                    int tingID = Integer.parseInt(eElement.getAttribute("id"));
                    String type = eElement.getAttribute("type");
                    String mp3 = eElement.getAttribute("mp3");
                    Entry entry = new Entry(book, tingID);
                    if(type.equals("mp3")) {
                        entry.setMP3(new File(FileEnvironment.getAudioDirectory(book.getID()), mp3));
                    } else if(type.equals("script") || type.equals("sub")) {
                        // get code
                        String code = getTagContent(eElement.getElementsByTagName("code").item(0));
                        Script script = new Script(code, entry);
                        entry.setScript(script);
                        if(type.equals("sub")) {
                            entry.setSub();
                        }
                    } else if(type.equals("tts")) {
                        // get tts
                        String tts = getTagContent(eElement.getElementsByTagName("tts").item(0));
                        entry.setTTS(tts);
                    } else {
                        throw new IOException("unknown type: " + type);
                    }
                    // get hint
                    entry.setHint(getTagContent(eElement.getElementsByTagName("hint").item(0)));
                    
                    book.addEntry(entry.getTingID());
                    book.indexEntries.put(entry.getTingID(), entry);
                    
                }
            }
            
            
            NodeList registers = doc.getElementsByTagName("register");
            for(int i = 0; i < registers.getLength(); i++) {
                Node registerNode = registers.item(i);
                if(registerNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element registerElement = (Element)registerNode;
                    int rID = Integer.parseInt(registerElement.getAttribute("id"));
                    String hint = getTagContent(registerElement.getElementsByTagName("hint").item(0));
                    book.emulator.setHint(rID, hint);
                }
            }  
                    
        } catch (SAXException ex) {
            throw new IOException(ex);
        } catch (ParserConfigurationException ex) {
            throw new Error();
        } catch (NumberFormatException ex) {
            throw new IOException(ex);
        }
    }
    

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    private String md5(File file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream is = new FileInputStream(file);
            DigestInputStream dis = new DigestInputStream(is, md);
            byte[] buffer = new byte[4096];
            while(dis.read(buffer) >= 0) {}
            byte[] bytes = md.digest();
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } catch(NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }
    
    
    
    private void generateOufFile(DataOutputStream out, ProgressDialog progress) throws IOException, SyntaxError {
        
        int startOfIndexTable = 0x0068;
        
        int lastID = getLastID();
        int size = lastID - 15000;
        
        // write header
        out.writeInt(startOfIndexTable);
        out.writeInt(0x0002);
        out.writeInt(15001);
        out.writeInt(lastID);
        out.writeInt(size);
        out.writeInt(id);
        out.writeInt((int)magicValue);
        out.writeInt((int)date);
        out.writeInt(0x0000);
        out.writeInt(0xffff);
        
        // pad with zeros
        for(int i = 40; i < startOfIndexTable; i++) {
            out.writeByte(0x00);
        }
        
        
        // write index table
        int pos = startOfIndexTable + 12 * size;
        for(int i = 0; i < size; i++) {
            Entry entry = getEntryFromTingID(i + 15001);
            if(entry.isEmpty()) {
                out.writeInt(0x0000);
                out.writeInt(0x0000);
                out.writeInt(0x0000);
            } else {
                pos += 0x100 - (pos & 0xff);
                out.writeInt(IndexTableCalculator.getCodeFromPositionInFile(pos, i));
                out.writeInt(entry.getSize());
                if(entry.isMP3() || entry.isTTS()) {
                    out.writeInt(0x0001);
                } else {
                    out.writeInt(0x0002);
                }
                pos += entry.getSize();
            }
        }
        
        if(progress != null) {
            progress.setMax(size);
        }
        
        pos = startOfIndexTable + 12 * size;
        // write data
        byte[] buffer = new byte[4096];
        for(int t = 0; t < size; t++) {
            if(progress != null) {
                progress.setVal(t);
            }
            Entry e = getEntryFromTingID(t + 15001);
            
            if(!e.isEmpty()) {
                int pad = 0x100 - (pos & 0xff);
                pos += pad;
                for(int i = 0; i < pad; i++) {
                    out.write(0x0);
                }
                if(e.isMP3() || e.isTTS()) {
                    InputStream in = new FileInputStream(e.getMP3());
                    int b;
                    while((b = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, b);
                        pos += b;
                    }
                } else {
                    byte[] bin = e.getScript().compile();
                    out.write(bin);
                    pos += bin.length;
                }
            }
            
           
        }
        
        out.close();
    }
    
    public void epsExport(File dir, ProgressDialog progress) throws IOException, IllegalArgumentException {
        
        int size = getLastID() - 15000;
        
        if(id > 15000) {
            throw new IllegalArgumentException("maximale Buch ID: 15000");
        }
        if(id < 0) {
            throw new IllegalArgumentException("minimale Buch ID: 0");
        }
        if(Translator.ting2code(id) < 0) {
            throw new IllegalArgumentException("die Code-ID zur Buch ID " + id + " ist zur Zeit noch unbekannt");
        }
        if(15000 + size > Translator.getMaxObjectCode()) {
            throw new IllegalArgumentException("zu viele OIDs. Maximale zur Zeit unterstüzte OIS: " + Translator.getMaxObjectCode());
        }
        
        
        
        PrintWriter out = new PrintWriter(new FileWriter(new File(dir, "activation.eps")));
        Codes.drawEps(Translator.ting2code(id), 100, 100, out);
        out.close();
        
        if(progress != null) {
            progress.setMax(size);
        }
        
        for(int i = 0; i < size; i++) {
            if(progress != null) {
                progress.setVal(i);
            }
            if(getEntryFromTingID(i + 15001).hasCode()) {
                out = new PrintWriter(new FileWriter(new File(dir, (i + 15001) + ".eps")));
                Codes.drawEps(Translator.ting2code(i + 15001), 100, 100, out);
                out.close();
            }
        }
    }
    
    public void pngExport(File dir, ProgressDialog progress) throws IOException, IllegalArgumentException {
        
        int size = getLastID() - 15000;
        
        if(id > 15000) {
            throw new IllegalArgumentException("maximale Buch ID: 15000");
        }
        if(id < 0) {
            throw new IllegalArgumentException("minimale Buch ID: 0");
        }
        if(Translator.ting2code(id) < 0) {
            throw new IllegalArgumentException("die Code-ID zur Buch ID " + id + " ist zur Zeit noch unbekannt");
        }
        if(15000 + size > Translator.getMaxObjectCode()) {
            throw new IllegalArgumentException("zu viele OIDs. Maximale zur Zeit unterstüzte OIS: " + Translator.getMaxObjectCode());
        }
        
        
        OutputStream out = new FileOutputStream(new File(dir, "activation.png"));
        Codes.drawPng(Translator.ting2code(id), 100, 100, out);
        out.close();
        
        if(progress != null) {
            progress.setMax(size);
        }
        
        for(int i = 0; i < size; i++) {
            if(progress != null) {
                progress.setVal(i);
            }
            if(getEntryFromTingID(i + 15001).hasCode()) {
                out = new FileOutputStream(new File(dir, (i + 15001) + ".png"));
                Codes.drawPng(Translator.ting2code(i + 15001), 100, 100, out);
                out.close();
            }
        }
    }
    
    public void epsSingleExport(File file, int tingID) throws IOException, IllegalArgumentException {
        
        
        if(tingID > 0x10000) {
            throw new IllegalArgumentException("maximale Ting-ID: 65535");
        }
        if(tingID < 0) {
            throw new IllegalArgumentException("minimale Ting-ID: 0");
        }
        if(Translator.ting2code(tingID) < 0) {
            throw new IllegalArgumentException("der Ting-Code zur Buch ID " + id + " ist zur Zeit noch unbekannt");
        }        
        
        PrintWriter out = new PrintWriter(new FileWriter(file));
        Codes.drawEps(Translator.ting2code(tingID), 200, 200, out);
        out.close();
        
    }
    
    public void export(File dir, ProgressDialog progress) throws IOException, IllegalArgumentException, SyntaxError {
        
        int size = getLastID() - 15000;
        
        if(name == null || name.isEmpty() || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Kein Buchname angegeben");
        }        
        if(publisher == null || publisher.isEmpty() || publisher.trim().isEmpty()) {
            throw new IllegalArgumentException("Kein Herausgeber angegeben");
        }
        if(author == null || author.isEmpty() || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Kein Autor angegeben");
        }
        if(version < 1) {
            throw new IllegalArgumentException("Keine gültige Version angegeben");
        }
        if(url == null) {
            url = "";
        }
        
        
        // test if index table is valid
        for(int i = 0; i < size; i++) {
            Entry entry = getEntryFromTingID(i + 15001);
            if(entry.isMP3() && (entry.getMP3() != null)) {
                if(!entry.getMP3().canRead()) {
                    throw new IllegalArgumentException("Die Datei '" + entry.getMP3().getAbsolutePath() + "' konnte nicht gelesen werden.");
                }
            }
        }
        
        String idS = "" + id;
        while(idS.length() < 5) {
            idS = "0" + idS;
        }
        
        File ouf = new File(dir, idS + OufFile._EN_OUF);
        File png = new File(dir, idS + PngFile._EN_PNG);
        File src = new File(dir, idS + ScriptFile._EN_SRC);
        
        // TODO use proper png file
        InputStream fci = new FileInputStream("sample.png");
        OutputStream fco = new FileOutputStream(png);
        int b;
        byte[] buffer = new byte[4096];
        while((b = fci.read(buffer)) != -1) {
            fco.write(buffer, 0, b);
        }
        fco.close();
        fci.close();
              
        PrintWriter srcOut = new PrintWriter(new FileWriter(src));
        generateScriptFile(srcOut);
        srcOut.close();
                
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ouf));
        generateOufFile(out, progress);
        out.close();
        
        PrintWriter txt = new PrintWriter(new FileWriter(new File(dir, idS + TxtFile._EN_TXT)));
        
        
        txt.println("Name: " + name.trim());
        txt.println("Publisher: " + publisher.trim());
        txt.println("Author: " + author.trim());
        txt.println("Book Version: " + version);
        txt.println("URL: " + url);
        txt.println("ThumbMD5: " + md5(png));
        txt.println("FileMD5: " + md5(ouf));
        txt.println("ScriptMD5: " + md5(src));
        txt.println("Book Area Code: en");
        txt.close();
    }

    public void addRegisterListener(RegisterListener listener) {
        emulator.addRegisterListener(listener);
    }

    public void generateScriptFile( File srcFile )  throws IOException {
        this.generateScriptFile(new PrintWriter(new FileWriter(srcFile)) );
    }

    void generateScriptFile(PrintWriter out) throws IOException {
        Iterator<Integer> ids = indexIDs.iterator();
        int pathLength = FileEnvironment.getBookDirectory(id).getCanonicalPath().length() +1 ;
        while(ids.hasNext()) {
            Entry entry = indexEntries.get(ids.next());
            if(entry.isMP3()) {
                out.print("Precode=" + entry.getTingID() + "\r\n");
                out.print("TYPE=1\r\n");
                out.print("[Note]\r\n");
                out.print(entry.getHint() + "\r\n");
                out.print("[Content]\r\n");
                out.print(entry.getMP3().getAbsolutePath().substring(pathLength).replace('\\', '/') + "\r\n");
                out.print("\r\n");
            } else if(entry.isCode() || entry.isSub()) {
                out.print("Precode=" + entry.getTingID() + "\r\n");
                out.print("TYPE=0\r\n");
                out.print("[Note]\r\n");
                out.print(entry.getHint() + "\r\n");
                out.print("[Content]\r\n");
                out.print(entry.getScript().toString().replaceAll("\n", "\r\n"));
                out.print("\r\n");
            }
        }

        out.close();
    }
    
    /**
     * Return a set of all OIDs used in the book.
     *
     * @return a set of all OIDs used in the book.
     */
    public Set<Integer> getIds() {
        return this.indexEntries.keySet();
    }
    
    public void importFromScriptFile(InputStream scriptFile) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(scriptFile));

        Entry entry = null;
        int type = -1;
        boolean inNote = false;
        boolean inContent = false;
        String line = in.readLine();
        while (line != null) {
            if (line.startsWith("Precode=")) {
                inContent = false;
                inNote = false;
                int tingID = Integer.parseInt( line.substring(line.indexOf('=')+1)   );
                this.addEntry(tingID);
                entry = this.getEntryFromTingID(tingID);
            } else if( entry != null && line.startsWith("TYPE="))  {
                type = Integer.parseInt(line.substring(line.indexOf('=')+1));
                if( type == 0 ) {
                    entry.setScript(new Script("",entry));
                } else if( type == 1) {
                    entry.setMP3();
                } else {
                    throw new RuntimeException("Unknown type "+type);
                }
            } else if( line.startsWith("[Note]")) {
                inNote = true;
                inContent = false;
            } else if( line.startsWith("[Content]")) {
                inNote = false;
                inContent = true;
            } else if( type == 1 && inContent) {
                if( !line.trim().isEmpty()) {
                    entry.setMP3(new File(FileEnvironment.getAudioDirectory(id), line));
                } else {
                    inContent = false;
                }
            } else if( type == 0 && inContent) {
                Script script = entry.getScript();
                script.setCode(script.toString() + line + "\n" );
            } else if( inNote) {
                entry.setHint(entry.getHint() + line +"\n" );
            }

            line = in.readLine();
        }
    }
   
}
