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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import tingeltangel.Tingeltangel;
import tingeltangel.core.constants.KiiFile;
import tingeltangel.core.constants.OufFile;
import tingeltangel.core.constants.PngFile;
import tingeltangel.core.constants.ScriptFile;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.ProgressDialog;

public class Repository {
    
    private final static String KNOWN_BOOKS_FILE = "/known_books.txt";
    
    private final static HashMap<Integer, HashMap<String, String>> BOOKS = new HashMap<Integer, HashMap<String, String>>();
    
    static {
        init();
    }
    
    private static boolean testTxtFile(File txt) {
        try {
            HashMap<String, String> txtMap = readTxt(txt);
            if(!txtMap.containsKey("Name")) {
                return(false);
            }
        } catch(IOException e) {
            return(false);
        }
        return(true);
    }
    
    private static HashMap<String, String> readTxt(File txt) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(txt), "UTF-8"));
        HashMap<String, String> data = new HashMap<String, String>();
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(!row.isEmpty()) {
                int p = row.indexOf(":");
                if(p>0) {
                        String key = row.substring(0, p).trim();
                        String value = row.substring(p + 1).trim();
                        data.put(key, value);
                } else {
                    throw new IOException("txt file broken");
                }
            }
        }
        in.close();
        return(data);
    }
    
    private static void init() {
        try {
            BOOKS.clear();
            
            File booksDir = FileEnvironment.getRepositoryDirectory();
            if(!booksDir.canRead()) {
                throw new Error("can't read from books directory");
            }
            if(!booksDir.canWrite()) {
                throw new Error("can't write to books directory");
            }
            List<File> bookFiles = Arrays.asList(booksDir.listFiles());
            for(File bookFile: bookFiles) {
            	String name = bookFile.getName();
                if(name.endsWith(".txt")) {
                    int id = Integer.parseInt(name.substring(0, name.indexOf("_")));
                    HashMap<String, String> data = readTxt(bookFile);
                    if(data.containsKey("Name")) {
                        // System.out.println(String.format("Imported book %s (" + id + ")", data.get("Name"), id));
                        BOOKS.put(id, data);
                    } else {
                        // delete broken txt-file
                        System.out.println(String.format("Deleted broken file %s", data.get(TxtFile.KEY_NAME)));
                        bookFile.delete();
                    }
                }
            }
            
            /*
            File booksDir = new File("books");
            if(!booksDir.canRead()) {
                throw new Error("can't read from books directory");
            }
            if(!booksDir.canWrite()) {
                throw new Error("can't write to books directory");
            }
            List<File> bookFiles = Arrays.asList(booksDir.listFiles());
            for(File bookFile: bookFiles) {
            	String name = bookFile.getName();
                if(name.endsWith(".txt")) {
                    int id = Integer.parseInt(name.substring(0, name.indexOf("_")));
                    
                    HashMap<String, String> data = readTxt(bookFile);
                    
                    // check if txt-file is valid
                    if(data.containsKey("Name")) {
                    	// System.out.println(String.format("Imported book %s", data.get("Name")));
                    	BOOKS.put(id, data);
                    } else {
                    	// delete broken txt-file
                    	System.out.println(String.format("Deleted broken file %s", name));
                    	bookFile.delete();
                    }
                }
            }
            */
        } catch(IOException ioe) {
            throw new Error(ioe);
        }
    }
    
    /**
     * 
     * @return all ids of known books in repository
     */
    public static Integer[] getIDs() {
        Integer[] bks = BOOKS.keySet().toArray(new Integer[0]);
        Arrays.sort(bks);
        return(bks);
    }
    
    public static HashMap<String, String> getBookTxt(int id) {
        return(BOOKS.get(id));
    }
    
    private static File getBookFile(int id, String type) {
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File file = new File(FileEnvironment.getRepositoryDirectory(), _id + type);
        if(!file.exists()) {
            return(null);
        }
        return(file);
    }
    
    public static boolean exists(int id) {
        return(getBookFile(id, OufFile._EN_OUF) != null);
    }
    
    public static boolean txtExists(int id) {
        return(getBookFile(id, TxtFile._EN_TXT) != null);
    }
    
    public static File getBookOuf(int id) {
        return(getBookFile(id, OufFile._EN_OUF));
    }
    
    public static File getBookKii(int id) {
        return(getBookFile(id, KiiFile._EN_KII));
    }
    
    public static File getBookPng(int id) {
        return(getBookFile(id, PngFile._EN_PNG));
    }
    
    public static File getBookSrc(int id) {
        return(getBookFile(id, ScriptFile._EN_SRC));
    }
    
    public static File getBookTxtFile(int id) {
        return(getBookFile(id, TxtFile._EN_TXT));
    }

    private static void download(String url, File dest, ProgressDialog progress) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[4096];
        
        URLConnection connection = new URL(url).openConnection();
        int size = connection.getContentLength();
        if(progress != null) {
            progress.setMax(size);
        }
        InputStream in = connection.getInputStream();
        OutputStream out = new FileOutputStream(dest);
        int k;
        int s = 0;
        while((k = in.read(buffer)) != -1) {
            out.write(buffer, 0, k);
            s += k;
            if(progress != null) {
                progress.setVal(s);
            }
        }

        in.close();
        out.close();
    }
    
    public static void download(int id, ProgressDialog progress) throws IOException {
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File txtFile = new File(FileEnvironment.getRepositoryDirectory(), _id + TxtFile._EN_TXT);
        download(Tingeltangel.BASE_URL + "/get-description/id/" + Integer.toString(id) + "/area/en/sn/5497559973888/", txtFile, null);
        
        if(!checkTxtForPlausibility(txtFile)) {
            txtFile.delete();
            throw new IOException("txt file for " + id + " seems to be broken");
        }
        
        BOOKS.put(id, readTxt(txtFile));
        File pngFile = new File(FileEnvironment.getRepositoryDirectory(), _id + PngFile._EN_PNG);
        download(Tingeltangel.BASE_URL + "/get/id/" + Integer.toString(id) + "/type/thumb/area/en/sn/5497559973888/", pngFile, null);
        
        
        if(!checkPngForPlausibility(pngFile)) {
            pngFile.delete();
            throw new IOException("png file for " + id + " seems to be broken");
        }
        
        File oufFile = new File(FileEnvironment.getRepositoryDirectory(), _id + OufFile._EN_OUF);
        download(Tingeltangel.BASE_URL + "/get/id/" + Integer.toString(id) + "/area/en/type/archive/sn/5497559973888/", oufFile, progress);
        
        
        if(!checkOufForPlausibility(oufFile)) {
            pngFile.delete();
            oufFile.delete();
            throw new IOException("ouf file for " + id + " seems to be broken");
        }
        /*
        if(getBookTxt(id).containsKey("ScriptMD5")) {
            File scriptFile = new File(FileEnvironment.getRepositoryDirectory(), _id + ScriptFile._EN_SRC);
            try {
                download(Tingeltangel.BASE_URL + "/get/id/" + _id + "/area/en/type/script", scriptFile, null);
            } catch(FileNotFoundException fnfe) {
                // ignore this
            }
        }
        */
    }
    
    public static void update(int id, ProgressDialog progress) throws FileNotFoundException, IOException {
        // update txt file
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        
        HashMap<String, String> bookTxt = getBookTxt(id);
        if(bookTxt == null) {
            search(id);
            bookTxt = getBookTxt(id);
            if(bookTxt == null) {
                throw new FileNotFoundException();
            }
        }
        int version = Integer.parseInt(getBookTxt(id).get(TxtFile.KEY_VERSION));
        File txtFile = new File(FileEnvironment.getRepositoryDirectory(), _id + TxtFile._EN_TXT);
        download(Tingeltangel.BASE_URL + "/get-description/id/" + Integer.toString(id) + "/area/en/sn/5497559973888/", txtFile, null);
        BOOKS.put(id, readTxt(txtFile));
        int version2 = Integer.parseInt(getBookTxt(id).get(TxtFile.KEY_VERSION));
        if((version2 > version) || !exists(id)) {
            File pngFile = new File(FileEnvironment.getRepositoryDirectory(), _id + PngFile._EN_PNG);
            download(Tingeltangel.BASE_URL + "/get/id/" + Integer.toString(id) + "/type/thumb/area/en/sn/5497559973888/", pngFile, null);
            File oufFile = new File(FileEnvironment.getRepositoryDirectory(), _id + OufFile._EN_OUF);
            download(Tingeltangel.BASE_URL + "/get/id/" + Integer.toString(id) + "/area/en/type/archive/sn/5497559973888/", oufFile, progress);
            /*
            if(getBookTxt(id).containsKey("ScriptMD5")) {
                File scriptFile = new File(FileEnvironment.getRepositoryDirectory(), _id + ScriptFile._EN_SRC);
                try {
                    download(Tingeltangel.BASE_URL + "/get/id/" + _id + "/area/en/type/script", scriptFile, null);
                } catch(FileNotFoundException fnfe) {
                    // ignore this
                }
            }
            */
        }
        if(progress != null) {
            progress.done();
        }
    }
    
    public static void search(int id) {
        byte[] buffer = new byte[4096];
        InputStream in = null;
        OutputStream out = null;
        try {
            String _id = Integer.toString(id);
            while(_id.length() < 5) {
                _id = "0" + _id;
            }
            in = new URL(Tingeltangel.BASE_URL + "/get-description/id/" + Integer.toString(id) + "/area/en/sn/5497559973888/").openStream();
            out = new FileOutputStream(new File(FileEnvironment.getRepositoryDirectory(), _id + TxtFile._EN_TXT));

            int k;
            while((k = in.read(buffer)) != -1) {
                out.write(buffer, 0, k);
            }

            in.close();
            out.close();
        } catch(IOException ioe) {
            if(in != null) {
                try {
                    in.close();
                } catch(Exception e) {
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch(Exception e) {
                }
            }
            System.out.println("id=" + id + ": " + ioe.getMessage());
        }
    }
    
    public static void search(ProgressDialog progress) {
        
        if(progress != null) {
            progress.setMax(Translator.MAX_MID);
        }
        
        
        for(int id = 0; id <= Translator.MAX_MID; id++) {
            if(progress != null) {
                progress.setVal(id);
            }
            search(id);
        }
        init();
        progress.done();
    }

    public static void initialUpdate(final Thread done) throws IOException {
        final HashSet<String> toDownload = new HashSet<>();
        BufferedReader bin = new BufferedReader(new InputStreamReader(Repository.class.getResourceAsStream(KNOWN_BOOKS_FILE)));
        String row;
        while((row = bin.readLine()) != null) {
            row = row.trim();
            if((!row.isEmpty()) && (!row.startsWith("#"))) {
                if(!txtExists(Integer.parseInt(row))) {
                    toDownload.add(row);
                }
            }
        }
        bin.close();
        
        final JFrame splash = new JFrame("initialisieren...");
        splash.setResizable(false);
        splash.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        splash.setBounds(100, 100, 300, 100);
        final JProgressBar bar = new JProgressBar();
        bar.setMaximum(toDownload.size());
        bar.setValue(0);
        bar.setBounds(10, 160, 280, 20);
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Bücher-Index herunterladen...");
        label.setBounds(30, 30, 100, 20);
        panel.add(label);
        panel.add(bar);
        splash.getContentPane().add(panel);
        splash.setVisible(true);
        
        System.out.println("need to download " + toDownload.size() + " book txt files...");
        
        new Thread() {
            @Override
            public void run() {
        
                byte[] buffer = new byte[4096];
                Iterator<String> ids = toDownload.iterator();
                int c = 0;
                while(ids.hasNext()) {
                    String row = ids.next();

                    bar.setValue(c++);

                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        
                        File txt = new File(FileEnvironment.getRepositoryDirectory() , row + TxtFile._EN_TXT);
                        
                        in = new URL(Tingeltangel.BASE_URL + "/get-description/id/" + Integer.toString(Integer.parseInt(row)) + "/sn/5497559973888/").openStream();
                        out = new FileOutputStream(txt);

                        int k;
                        while((k = in.read(buffer)) != -1) {
                            out.write(buffer, 0, k);
                        }

                        in.close();
                        out.close();

                        if(!testTxtFile(txt)) {
                            throw new IOException("txt file broken");
                        }
                        
                    } catch(IOException ioe) {
                        if(in != null) {
                            try {
                                in.close();
                            } catch(IOException e) {
                            }
                        }
                        if(out != null) {
                            try {
                                out.close();
                            } catch(IOException e) {
                            }
                        }
                        new File(FileEnvironment.getRepositoryDirectory(), row + TxtFile._EN_TXT).delete();
                        System.out.println("failed to load book " + row + ": " + ioe.getMessage());
                    }
                }
                System.out.println("got " + toDownload.size() + " book txt files");
                splash.dispose();
                init();
                done.start();
            }
        }.start();
    }

    public static void update(ProgressDialog progress) throws IOException {
        Integer[] ids = getIDs();
        List<Integer> _ids = new LinkedList<>();
        for(Integer id : ids) {
            if(Repository.exists(id)) {
                _ids.add(id);
            }
        }
        if(progress != null) {
            progress.setMax(_ids.size());
        }
        for(int r = 0; r < _ids.size(); r++) {
            if(progress != null) {
                progress.setVal(r);
            }
            update(_ids.get(r), null);
        }
    }
    
    public static void cleanup() {
        File[] toDelete = FileEnvironment.getRepositoryDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return(name.toLowerCase().endsWith(".ouf"));
            }
        });
        for(int i = 0; i < toDelete.length; i++) {
            toDelete[i].delete();
        }
    }
    
    public static void main(String[] args) throws Exception {
        Integer[] ids = getIDs();
        
        String header = "\n^ Buch ID ^ Name ^ Herausgeber ^ Autor ^ Version ^ Downloads ^^^";
        
        File f = new File("bookList.txt");
        PrintWriter out = new PrintWriter(new FileWriter(f));
        out.print("\n====== Offizielle Bücher ======\n");
        
        
        for(int i = 0; i < ids.length; i++) {
            
            if((i % 10 == 0) && (i < ids.length - 1)) {
                out.println(header);
            }
            
            HashMap<String, String> book = Repository.getBookTxt(ids[i]);
            String id_short = Integer.toString(ids[i]);
            String id_long = id_short;
            while(id_long.length() < 5) {
                id_long = "0" + id_long;
            }
            out.print("| " + id_long + " | " + book.get("Name") + " | ");
            out.print(book.get("Publisher") + " | " + book.get("Author") + " | ");
            out.print(book.get("Book Version") + " | ");
            out.print("[[http://13.80.138.170/book-files/get-description/id/" + id_short + "/area/en/sn/5497559973888/|txt-Datei]]" + " | ");
            out.print("[[http://13.80.138.170/book-files/get/id/" + id_short + "/type/thumb/area/en/sn/5497559973888/|png-Datei]]" + " | ");
            out.print("[[http://13.80.138.170/book-files/get/id/" + id_short + "/area/en/type/archive/sn/5497559973888/|ouf-Datei]]" + " |\n");
            
          
            
        }
        out.close();
    }

    public static HashMap<String, String> getBook(File file) throws IOException {
        if(file == null) {
            return(null);
        }
        return(readTxt(file));
    }

    private static boolean checkTxtForPlausibility(File txtFile) {
        return(true);
    }

    private static boolean checkPngForPlausibility(File pngFile) {
    
        return(true);}

    private static boolean checkOufForPlausibility(File oufFile) {
        
        return(true);
    }
    
}
