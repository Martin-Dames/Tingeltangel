
package tingeltangel.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import tingeltangel.core.scripting.Emulator;
import tingeltangel.core.scripting.RegisterListener;
import tingeltangel.core.scripting.SyntaxError;

public class Book {

    private final static long DEFAULT_MAGIC_VALUE = 0x0000000a;
    
    private SortedIntList indexIDs = new SortedIntList();
    private HashMap<Integer, Entry> indexEntries = new HashMap<Integer, Entry>();
        
    private boolean changed = false;
    
    private int id = 8000;
    private String name;
    private String publisher;
    private String author;
    private int version;
    private String url;
    
    private long date = new Date().getTime() / 1000;
    private long magicValue = DEFAULT_MAGIC_VALUE;
    
    private File dir;
    
    
    private Emulator emulator;
    
    public File getMP3Path() throws NoBookException {
        
        if(dir == null) {
            throw new NoBookException();
        }
        
        File mp3Path = new File(dir, "audio");
        if(mp3Path.isDirectory()) {
            mp3Path.mkdir();
        }
        return(mp3Path);
        
    }
    
    public final void clear() {
        id = Translator.getRandomBookCode();
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
    
    public void setURL(String url) {
        this.url = url;
        changeMade();
    }
    
    public Book(MP3Player player, File dir) {
        this.dir = dir;
        checkDir();
        clear();
        emulator = new Emulator(this, player);
    }
    
    public void addEntry(int tingID) {
        if((tingID < 15000) || (tingID > 0x10000)) {
            throw new Error("TingID=" + tingID + " out of range");
        }
        indexIDs.add(tingID);
        indexEntries.put(tingID, new Entry(this, tingID));
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
    
    public void setID(int id) {
        this.id = id;
        changeMade();
    }
    
    public int getID() {
        return(id);
    }
    
    public int getSize() {
        return(indexIDs.size());
    }
    
    public boolean hasDirectory() {
        return(dir != null);
    }
    
    public int getLastID() {
        if(indexIDs.size() == 0) {
            return(15000);
        }
        int lastFound = -1;
        Iterator<Integer> i = indexIDs.iterator();
        while(i.hasNext()) {
            int id = i.next();
            Entry e = indexEntries.get(id);
            if(!e.isEmpty()) {
                lastFound = id;
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

    public void save() throws IOException {
        save(new File(dir, "book.tbu"));
    }
    
    public void save(File file) throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        
        // version
        int fileFormatVersion = 1;
        out.writeInt(fileFormatVersion + 15000);
        
        out.writeInt(id);
        out.writeUTF(name);
        out.writeUTF(publisher);
        out.writeUTF(author);
        out.writeInt(version);
        out.writeUTF(url);
        
        out.writeLong(magicValue);
        out.writeLong(date);
        
        out.writeInt(indexIDs.size());
        Iterator<Integer> iterator = indexIDs.iterator();
        while(iterator.hasNext()) {
            indexEntries.get(iterator.next()).save(out);
        }
        
        out.writeInt(emulator.getMaxRegister() + 1);
        for(int i = 0; i <= emulator.getMaxRegister(); i++) {
            out.writeUTF(emulator.getHint(i));
        }
        
        
        out.close();
        changed = false;
    }
    
    public static void load(File file, Book book) throws FileNotFoundException, IOException, NoBookException {
        load(new FileInputStream(file), book);
    }

    public static void load(InputStream inputStream, Book book) throws IOException, NoBookException {
        DataInputStream in = new DataInputStream(inputStream);
        
        book.clear();
        
        // compatibility hack
        int fileFormatVersion = in.readInt();
        if(fileFormatVersion <= 15000) {
            book.id = fileFormatVersion;
            fileFormatVersion = 0;
        } else {
            book.id = in.readInt();
            fileFormatVersion -= 15000;
        }
        book.name = in.readUTF();
        book.publisher = in.readUTF();
        book.author = in.readUTF();
        book.version = in.readInt();
        book.url = in.readUTF();
        if(fileFormatVersion > 0) {
            book.magicValue = in.readLong();
            book.date = in.readLong();
        }
        
        int size = in.readInt();
        for(int i = 0; i < size; i++) {
            Entry entry = Entry.load(in, book);
            book.addEntry(entry.getTingID());
            book.indexEntries.put(entry.getTingID(), entry);
        }
        
        size = in.readInt();
        for(int i = 0; i < size; i++) {
            book.emulator.setHint(i, in.readUTF());
        }
                
        
        book.changed = false;
    }
    
    public void setDirectory(File dir) {
        this.dir = dir;
        checkDir();
    }

    private void checkDir() {
        if(dir == null) {
            return;
        }
        File audioDir = new File(dir, "audio");
        if(!audioDir.isDirectory()) {
            audioDir.mkdir();
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
    
    
    
    private void generateOufFile(DataOutputStream out) throws IOException, SyntaxError {
        
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
                out.writeInt(Tools.getCodeFromPositionInFile(pos, i));
                out.writeInt(entry.getSize());
                if(entry.isMP3()) {
                    out.writeInt(0x0001);
                } else {
                    out.writeInt(0x0002);
                }
                pos += entry.getSize();
            }
        }
        
        pos = startOfIndexTable + 12 * size;
        // write data
        byte[] buffer = new byte[4096];
        for(int t = 0; t < size; t++) {
            Entry e = getEntryFromTingID(t + 15001);
            
            if(!e.isEmpty()) {
                int pad = 0x100 - (pos & 0xff);
                pos += pad;
                for(int i = 0; i < pad; i++) {
                    out.write(0x0);
                }
                if(e.isMP3()) {
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
    
    public void epsExport(File dir) throws IOException, IllegalArgumentException {
        
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
        Codes.drawEps(Translator.ting2code(id), 200, 200, out);
        out.close();
        
        for(int i = 0; i < size; i++) {
            if(getEntryFromTingID(i + 15001).hasCode()) {
                out = new PrintWriter(new FileWriter(new File(dir, (i + 15001) + ".eps")));
                Codes.drawEps(Translator.ting2code(i + 15001), 200, 200, out);
                out.close();
            }
        }
    }
    
    public void pngExport(File dir) throws IOException, IllegalArgumentException {
        
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
        Codes.drawPng(Translator.ting2code(id), 200, 200, out);
        out.close();
                
        for(int i = 0; i < size; i++) {
            if(getEntryFromTingID(i + 15001).hasCode()) {
                out = new FileOutputStream(new File(dir, (i + 15001) + ".png"));
                Codes.drawPng(Translator.ting2code(i + 15001), 200, 200, out);
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
    
    public void export(File dir) throws IOException, IllegalArgumentException, SyntaxError {
        
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
        boolean needScriptFile = false;
        for(int i = 0; i < size; i++) {
            Entry entry = getEntryFromTingID(i + 15001);
            if(entry.isMP3() && (entry.getMP3() != null)) {
                if(!entry.getMP3().canRead()) {
                    throw new IllegalArgumentException("Die Datei '" + entry.getMP3().getAbsolutePath() + "' konnte nicht gelesen werden.");
                }
            } else if(entry.isCode() || entry.isSub()) {
                needScriptFile = true;
            }
        }
        
        String idS = "" + id;
        while(idS.length() < 5) {
            idS = "0" + idS;
        }
        
        File ouf = new File(dir, idS + "_en.ouf");
        File png = new File(dir, idS + "_en.png");
        File src = new File(dir, idS + "_en.src");
        
        // TODO use propert png file
        InputStream fci = new FileInputStream("sample.png");
        OutputStream fco = new FileOutputStream(png);
        int b;
        byte[] buffer = new byte[4096];
        while((b = fci.read(buffer)) != -1) {
            fco.write(buffer, 0, b);
        }
        fco.close();
        fci.close();
              
        if(needScriptFile) {
            PrintWriter srcOut = new PrintWriter(new FileWriter(src));
            generateScriptFile(srcOut);
            srcOut.close();
        }
                
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ouf));
        generateOufFile(out);
        out.close();
        
        PrintWriter txt = new PrintWriter(new FileWriter(new File(dir, idS + "_en.txt")));
        
        
        txt.println("Name: " + name.trim());
        txt.println("Publisher: " + publisher.trim());
        txt.println("Author: " + author.trim());
        txt.println("Book Version: " + version);
        txt.println("URL: " + url);
        txt.println("ThumbMD5: " + md5(png));
        txt.println("FileMD5: " + md5(ouf));
        if(needScriptFile) {
            txt.println("ScriptMD5: " + md5(src));
        }
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
        int pathLength = this.dir.getCanonicalPath().length() +1 ;
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
    
    public void importFromScriptFile(InputStream scriptFile) throws IOException, NoBookException {
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
                    entry.setMP3(new File(this.dir, line));
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
