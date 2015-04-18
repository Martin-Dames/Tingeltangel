
package tingeltangel.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import tingeltangel.Tingeltangel;
import tingeltangel.core.constants.TxtFile;


public class Stick {

    private final static String STICK_DIR = "$ting";
    private final static String SETTINGS_FILE = "SETTINGS.INI";
    private final static String TBD_FILE = "TBD.TXT";
    
    private final static String[] STICK_FILES = {
        "TBD.TXT", "SETTINGS.INI", "SETTING.DAT", "BOOK.SYS"
    };
    
    public static boolean checkForStick(File path) {
        File[] content = path.listFiles();
        if(content != null) {
            for(int i = 0; i < content.length; i++) {
                if(content[i].isDirectory() && (content[i].getName().equals(STICK_DIR))) {
                    content = content[i].listFiles();
                    // check for basic files
                    for(int f = 0; f < STICK_FILES.length; f++) {
                        boolean found = false;
                        for(int j = 0; j < content.length; j++) {
                            if(content[j].getName().equals(STICK_FILES[f])) {
                                found = true;
                                break;
                            }
                        }
                        if(!found) {
                            return(false);
                        }
                    }
                    return(true);
                }
            }
        }
        return(false);
    }
    
    public static long getFreeSpace(File path) {
        return(path.getUsableSpace());
    }
    
    public static LinkedList<Integer> getTBD(File path) throws IOException {
        File file = new File(new File(path, STICK_DIR), TBD_FILE);
        if(!file.canRead()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        LinkedList<Integer> tbd = new LinkedList<Integer>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(!row.isEmpty()) {
                try {
                    tbd.add(Integer.parseInt(row));
                } catch(NumberFormatException nfe) {
                    System.out.println("invalid row in " + file.getAbsolutePath() + ": " + row);
                }
            }
        }
        in.close();
        return(tbd);
    }
    
    public static LinkedList<Integer> getBooks(File path) throws IOException {
        File file = new File(path, STICK_DIR);
        if(!file.canRead()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        LinkedList<Integer> books = new LinkedList<Integer>();
        File[] files = file.listFiles();
        for(int i = 0; i < files.length; i++) {
            if(files[i].getName().endsWith("_en.ouf") && (files[i].getName().length() == "_en.ouf".length() + 5)) {
                books.add(Integer.parseInt(files[i].getName().substring(0, 5)));
            }
        }
        return(books);
    }
    
    public static String getBookContent(File path) throws IOException {
        StringBuilder buffer = new StringBuilder();
        
        Iterator<Integer> i = getBooks(path).iterator();
        while(i.hasNext()) {
            int id = i.next();
            String bookID = Integer.toString(id);
            while(bookID.length() < 5) {
                bookID = "0" + bookID;
            }
            HashMap<String, String> book = Books.getBook(id);
            if(book == null) {
                buffer.append(bookID).append("\n");
            } else {
                buffer.append(bookID).append(" (").append(book.get("Name")).append(")\n");
            }
        }
        
        return(buffer.toString());
    }    
    
    public static String getTBDContent(File path) throws IOException {
        StringBuilder buffer = new StringBuilder();

        Iterator<Integer> i = getTBD(path).iterator();
        while(i.hasNext()) {
            int id = i.next();
            String bookID = Integer.toString(id);
            while(bookID.length() < 5) {
                bookID = "0" + bookID;
            }
            HashMap<String, String> book = Books.getBook(id);
            if(book == null) {
                buffer.append(bookID).append("\n");
            } else {
                buffer.append(bookID).append(" (").append(book.get("Name")).append(")\n");
            }
        }
        
        return(buffer.toString());
    }
    
    public static File getBookDir(File path) {
        return(new File(path, STICK_DIR));
    }
    
    public static void setSettings(File path, HashMap<String, String> settings) throws IOException {
        File file = new File(new File(path, STICK_DIR), SETTINGS_FILE);
        if(!file.canWrite()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "UTF-16");
        
        
        TreeSet<String> sortedKeys = new TreeSet<String>();
        sortedKeys.addAll(settings.keySet());
        Iterator<String> keys = sortedKeys.iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            out.append(key + "=" + settings.get(key) + "\r\n");
        }
        
        out.close();
        
    }
    
    public static HashMap<String, String> getSettings(File path) throws IOException {
        File file = new File(new File(path, STICK_DIR), SETTINGS_FILE);
        if(!file.canRead()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        
        HashMap<String, String> settings = new HashMap<String, String>();
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-16")));
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(!row.isEmpty()) {
                int p = row.indexOf("=");
                if(p == -1) {
                    throw new IOException("bad row in " + file.getAbsolutePath() + ": " + row);
                }
                String key = row.substring(0, p).trim();
                String value = row.substring(p + 1).trim();
                settings.put(key, value);
            }
        }
        in.close();
        
        return(settings);
    }

    public static int getBookVersion(File path, int id) throws IOException {
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File txt = new File(Stick.getBookDir(path), _id + TxtFile._EN_TXT);
        BufferedReader in = new BufferedReader(new FileReader(txt));
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(row.startsWith("Book Version:")) {
                row = row.substring("Book Version:".length()).trim();
                in.close();
                return(Integer.parseInt(row));
            }
        }
        in.close();
        return(-1);
    }
    
    public static void delete(File path, int id) {
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File txt = new File(Stick.getBookDir(path), _id + TxtFile._EN_TXT);
        File png = new File(Stick.getBookDir(path), _id + "_en.png");
        File ouf = new File(Stick.getBookDir(path), _id + "_en.ouf");
        File src = new File(Stick.getBookDir(path), _id + "_en.src");
        
        txt.delete();
        png.delete();
        ouf.delete();
        if(src.exists()) {
            src.delete();
        }
        
    }
    
    public static int getOnlineBookVersion(int id) throws IOException {
        if(Books.getBook(id) == null) {
            return(-1);
        }
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(Tingeltangel.BASE_URL + "/get-description/id/" + _id + "/area/en").openStream()));
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(row.startsWith("Book Version:")) {
                row = row.substring("Book Version:".length()).trim();
                in.close();
                return(Integer.parseInt(row));
            }
        }
        in.close();
        return(-1);
    }
    
    private static void fileCopy(File source, File target) throws IOException {
        System.out.println("copy file from: " + source.getAbsolutePath() + " to " + target.getAbsolutePath());
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        byte[] buffer = new byte[4096];
        int k;
        while((k = in.read(buffer)) != -1) {
            out.write(buffer, 0, k);
        }
        out.close();
        in.close();
    }
    
    private static void downloadBookLocally(String url, File target) throws IOException {
        System.out.println("opening url: " + Tingeltangel.BASE_URL + url);
        
        URLConnection connection = new URL(Tingeltangel.BASE_URL + url).openConnection();
        
        InputStream in = connection.getInputStream();
        OutputStream out = new FileOutputStream(target);
        byte[] buffer = new byte[4096];
        int k;
        int s = 0;
        while((k = in.read(buffer)) != -1) {
            out.write(buffer, 0, k);
            s += k;
        }
        out.close();
        in.close();
    }
    
    public static void downloadOfficial(File path, int id) throws IOException {
        
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        
        File txtOut = File.createTempFile("ting_txt_", null);
        downloadBookLocally("/get-description/id/" + _id + "/area/en", txtOut);
        
        File pngOut = File.createTempFile("ting_png_", null);
        downloadBookLocally("/get/id/" + _id + "/area/en/type/thumb", pngOut);
        
        File oufOut = File.createTempFile("ting_ouf_", null);
        downloadBookLocally("/get/id/" + _id + "/area/en/type/archive", oufOut);
        
        File srcOut = null;
        if(Books.getBook(id).containsKey("ScriptMD5")) {
            srcOut = File.createTempFile("ting_src_", null);
            downloadBookLocally("/get/id/" + _id + "/area/en/type/script", srcOut);
        }
        
        getBookDir(path).mkdir();
        
        fileCopy(txtOut, new File(path, _id + TxtFile._EN_TXT));
        fileCopy(pngOut, new File(path, _id + "_en.png"));
        fileCopy(oufOut, new File(path, _id + "_en.ouf"));
        if(srcOut != null) {
            fileCopy(srcOut, new File(path, _id + "_en.src"));
        }
        
        txtOut.delete();
        pngOut.delete();
        oufOut.delete();
        if(srcOut != null) {
            srcOut.delete();
        }
    }

    public static void setTBD(File path, LinkedList<Integer> newTbd) throws IOException {
        File file = new File(new File(path, STICK_DIR), TBD_FILE);
        if(!file.canWrite()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        PrintWriter out = new PrintWriter(new FileWriter(file));
        Iterator<Integer> i = newTbd.iterator();
        while(i.hasNext()) {
            String id = Integer.toString(i.next());
            while(id.length() < 5) {
                id = "0" + id;
            }
            out.println(id);
        }
        out.close();
    }

    private static void zip(ZipOutputStream zos, File file, LinkedList<String> stack) throws IOException {
        FileInputStream fis = null;
        if(file.isDirectory()) {
            stack.addLast(file.getName());
            File[] childs = file.listFiles();
            for(int i = 0; i < childs.length; i++) {
                zip(zos, childs[i], stack);
            }
            stack.pollLast();
        } else {
            try {
                String prefix = "";
                Iterator<String> stackIt = stack.iterator();
                stackIt.next();
                while(stackIt.hasNext()) {
                    prefix += stackIt.next() + "/";
                }
                zos.putNextEntry(new ZipEntry(prefix + file.getName()));
                int len;
                byte[] buffer = new byte[4096];
                fis = new FileInputStream(file);
                while ((len = fis.read(buffer, 0, buffer.length)) > 0) {
                    zos.write(buffer, 0, len);
                }
            } finally {
                try {
                    if(fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {}
            }
        }
    }
    
    public static void saveStick(File stick, File target) throws IOException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(target));
            LinkedList<String> stack = new LinkedList<String>();
            zip(zos, stick, stack);
        } finally {
            if(zos != null){
                try {
                    zos.closeEntry();
                    zos.close();
                } catch (IOException e) {}
            }
        } 
    }

    public static void restoreStick(File stick, File source) throws IOException {
        ZipFile zipFile = new ZipFile(source);
        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        TreeSet<String> files = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1.length() < o2.length()) {
                    return(-1);
                }
                if(o1.length() > o2.length()) {
                    return(1);
                }
                return(o1.compareTo(o2));
            }
        });
        while(zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();
            files.add(zipEntry.getName());
        }
        Iterator<String> i = files.iterator();
        while(i.hasNext()) {
            String fileName = i.next();
            int p = fileName.lastIndexOf("/");
            if(p != -1) {
                new File(stick, fileName.substring(0, p)).mkdirs();
            }
            OutputStream out = new FileOutputStream(new File(stick, fileName));
            InputStream in = zipFile.getInputStream(zipFile.getEntry(fileName));
            // copy in to out
            int len;
            byte[] buffer = new byte[4096];
            while ((len = in.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
        }
        zipFile.close();
    }
}
