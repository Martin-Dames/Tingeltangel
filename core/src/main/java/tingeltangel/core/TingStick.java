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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tingeltangel.core.constants.OufFile;
import tingeltangel.core.constants.PngFile;
import tingeltangel.core.constants.ScriptFile;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.tools.OS;
import tingeltangel.tools.ProgressDialog;


/**
 * the central class for managing a ting stick
 * @author martin dames
 */
public class TingStick extends Stick {

    private final static String STICK_DIR = "$ting";
    private final static String SETTINGS_FILE = "SETTINGS.INI";
    private final static String TBD_FILE = "TBD.TXT";
    
    private final static String[] STICK_FILES = {SETTINGS_FILE, TBD_FILE};

    
    private final File path;
    
    
    private final static Logger LOG = LogManager.getLogger(TingStick.class);
    
    
    @Override
    public String getType() {
        return("Ting");
    }
    
    @Override
    public boolean isBookii() {
        return(false);
    }
    
    private TingStick(File path) {
        this.path = path;
    }
    
    private static boolean checkForStick(File path) {
        File[] content = path.listFiles();
        if(content != null) {
            for(int i = 0; i < content.length; i++) {
                if(content[i].isDirectory() && (content[i].getName().equals(STICK_DIR))) {
                    
                    content = content[i].listFiles();
                    // check for basic files
                    for(int f = 0; f < STICK_FILES.length; f++) {
                        boolean found = false;
                        for(int j = 0; j < content.length; j++) {
                            if(content[j].getName().toUpperCase().equals(STICK_FILES[f]) && content[j].canWrite()) {
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
    
    /**
     * retrieves size of free space on stick
     * @return free space in bytes
     */
    public long getFreeSpace() {
        return(path.getUsableSpace());
    }
    
    /**
     * retrieves the file TBD.TXT from stick
     * @return the tbd file
     */
    public File getTBDFile() {
        return(new File(new File(path, STICK_DIR), TBD_FILE));
    }
    
    /**
     * retrieves the content of TBD.TXT from stick
     * @return content from TBD.TXT as HashSet&lt;Integer&gt;
     * @throws IOException
     */
    public HashSet<Integer> getTBD() throws IOException {
        File file = new File(new File(path, STICK_DIR), TBD_FILE);
        if(!file.canRead()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        HashSet<Integer> tbd = new HashSet<>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(!row.isEmpty()) {
                try {
                    tbd.add(Integer.parseInt(row));
                } catch(NumberFormatException nfe) {
                    LOG.warn("invalid row in " + file.getAbsolutePath() + ": " + row);
                }
            }
        }
        in.close();
        return(tbd);
    }
    
    /**
     * retrieves the books on stick
     * @return list of books on the stick as LinkedList&lt;Integer&gt;
     * @throws IOException
     */
    public LinkedList<Integer> getBooks() throws IOException {
        File file = new File(path, STICK_DIR);
        if(!file.canRead()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        LinkedList<Integer> books = new LinkedList<>();
        File[] files = file.listFiles();
        for(int i = 0; i < files.length; i++) {
            if(files[i].getName().endsWith(OufFile._EN_OUF) && (files[i].getName().length() == OufFile._EN_OUF.length() + 5)) {
                books.add(Integer.parseInt(files[i].getName().substring(0, 5)));
            }
        }
        return(books);
    }
 
 
    /**
     * gets the directory on stick where most data resists
     * @return the $ting directory
     */
    public File getBookDir() {
        return(new File(path, STICK_DIR));
    }
    
    /**
     * sets the stick settings
     * @param settings
     * @throws IOException
     */
    public void setSettings(HashMap<String, String> settings) throws IOException {
        File file = new File(new File(path, STICK_DIR), SETTINGS_FILE);
        if(!file.canWrite()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "UTF-16");
        
        
        TreeSet<String> sortedKeys = new TreeSet<>();
        sortedKeys.addAll(settings.keySet());
        Iterator<String> keys = sortedKeys.iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            out.append(key + "=" + settings.get(key) + "\r\n");
        }
        
        out.close();
        
    }
    
    /**
     * gets the stick settings
     * @return the stick settings
     * @throws IOException
     */
    @Override
    public HashMap<String, String> getSettings() throws IOException {
        File file = new File(new File(path, STICK_DIR), SETTINGS_FILE);
        if(!file.canRead()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        
        HashMap<String, String> settings = new HashMap<>();
        
        
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

    /**
     * gets book version on stick
     * @param id the book id
     * @return the book version or -1 if book not found
     * @throws IOException
     */
    @Override
    public int getBookVersion(int id) throws IOException {
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File txt = new File(getBookDir(), _id + TxtFile._EN_TXT);
        if(!txt.exists()) {
            return(-1);
        }
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
    
    /**
     * deletes a book from the stick
     * @param id the book to delete
     */
    @Override
    public void delete(int id) {
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File txt = new File(getBookDir(), _id + TxtFile._EN_TXT);
        File png = new File(getBookDir(), _id + PngFile._EN_PNG);
        File ouf = new File(getBookDir(), _id + OufFile._EN_OUF);
        File src = new File(getBookDir(), _id + ScriptFile._EN_SRC);
        
        txt.delete();
        png.delete();
        ouf.delete();
        if(src.exists()) {
            src.delete();
        }
        
    }
        
    
    /**
     * copies a book from the repository to the stick
     * @param mid the book id
     * @throws IOException
     */
    public void copyFromRepositoryToStick(int mid) throws IOException {
        
        String _id = Integer.toString(mid);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File txt = new File(getBookDir(), _id + TxtFile._EN_TXT);
        File png = new File(getBookDir(), _id + PngFile._EN_PNG);
        File ouf = new File(getBookDir(), _id + OufFile._EN_OUF);
        File src = new File(getBookDir(), _id + ScriptFile._EN_SRC);
        
        fileCopy(Repository.getBookOuf(mid), ouf);
        fileCopy(Repository.getBookPng(mid), png);
        File rSrc = Repository.getBookSrc(mid);
        if((rSrc != null) && (rSrc.exists())) {
            fileCopy(rSrc, src);
        }
        fileCopy(Repository.getBookTxtFile(mid), txt);
    }

    /**
     * sets the TBD.TXT
     * @param newTbd the new list of books in TBD.TXT
     * @throws IOException
     */
    public void setTBD(LinkedList<Integer> newTbd) throws IOException {
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
    
    /**
     * saves the whole stick to zip file
     * @param target the zip file to write to
     * @throws IOException
     */
    public void saveStick(File target) throws IOException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(target));
            LinkedList<String> stack = new LinkedList<String>();
            zip(zos, path, stack);
        } finally {
            if(zos != null){
                try {
                    zos.closeEntry();
                    zos.close();
                } catch (IOException e) {}
            }
        } 
    }

    /**
     * restores the whole stick from a zip file
     * @param source the zip file to read from
     * @throws IOException
     */
    public void restoreStick(File source) throws IOException {
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
                new File(path, fileName.substring(0, p)).mkdirs();
            }
            OutputStream out = new FileOutputStream(new File(path, fileName));
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

    /**
     * get the stick
     * @return the stick object or null
     * @throws IOException
     */
    public static TingStick getStick() throws IOException {
        Set<TingStick> sticks = getSticks();
        if(sticks.isEmpty()) {
            return(null);
        }
        return(sticks.iterator().next());
    }
    
    /**
     * get all the sticks
     * @return set of stick objects
     * @throws IOException
     */
    public static Set<TingStick> getSticks() throws IOException {
        File[] mounts;
        if(OS.isWindows()) {
            mounts = File.listRoots();
        } else {
            LinkedList<File> mountList = new LinkedList<File>();
            Process process = new ProcessBuilder(OS.getMountCommand()).start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String row;
            while ((row = in.readLine()) != null) {
                row = row.trim();
                if (row.startsWith("/dev/")) {
                    int p = row.indexOf(" on ");
                    row = row.substring(p + " on ".length());
                    p = row.indexOf(" ");
                    row = row.substring(0, p);
                    mountList.add(new File(row));
                }
            }
            mounts = mountList.toArray(new File[0]);
        }
        HashSet<TingStick> sticks = new HashSet<>();
        for (File mount : mounts) {
            if (TingStick.checkForStick(mount)) {
                sticks.add(new TingStick(mount));
            }
        }
        return(sticks);
    }
    
    public void update(ProgressDialog progress) {
        update(null, progress);
    }
    
    public boolean update(JFrame frame, ProgressDialog progress) {
        boolean result = true;
        try {
            LinkedList<Integer> books = getBooks();
            if(progress != null) {
                progress.setMax(books.size());
            }
            Iterator<Integer> ids = books.iterator();
            int c = 0;
            while(ids.hasNext()) {
                if(progress != null) {
                    progress.setVal(c++);
                }
                int id = ids.next();
                try {
                    Repository.update(id, null);
                    HashMap<String, String> bookTxt = Repository.getBookTxt(id);
                    if(bookTxt == null) {
                        LOG.info("skipping book " + id + ". not found in repository");
                    } else {
                        int repositoryVersion = Integer.parseInt(Repository.getBookTxt(id).get(TxtFile.KEY_VERSION));
                        int stickVersion = -1;
                        try {
                            stickVersion = getBookVersion(id);
                        } catch(IOException ioe) {
                            LOG.warn("failed to get book version from stick (mid=" + id + ")", ioe);
                        }
                        if(stickVersion < repositoryVersion) {
                            try {
                                copyFromRepositoryToStick(id);
                            } catch(IOException ioe) {
                                LOG.warn("failed to copy book " + id + " from repository to stick", ioe);
                                if(frame != null) {
                                    JOptionPane.showMessageDialog(frame, "Das Buch " + id + " konnte nicht auf den Stift kopiert werden");
                                }
                                result = false;
                            }
                        }
                    }
                } catch(IOException fnfe) {
                    LOG.info("book not found in repository (mid=" + id + ")", fnfe);
                }
            }
        } catch(IOException ioe) {
            LOG.warn("failed to get stick content", ioe);
            if(frame != null) {
                JOptionPane.showMessageDialog(frame, "Auf den Stift konnte nicht zugegriffen werden");
            }
            result = false;
        }
        // process tbd
        try {
            HashSet<Integer> tbdSet = getTBD();
            Iterator<Integer> tbds = tbdSet.iterator();
            if(progress != null) {
                progress.restart("Verarbeite TBD.TXT");
                progress.setMax(tbdSet.size());
            }
            LinkedList<Integer> newTbds = new LinkedList<Integer>();
            int c = 0;
            while(tbds.hasNext()) {
                if(progress != null) {
                    progress.setVal(c++);
                }
                int id = tbds.next();
                if(!Repository.txtExists(id)) {
                    Repository.search(id);
                }
                if(!Repository.txtExists(id)) {
                    newTbds.add(id);
                } else {
                    try {
                        Repository.update(id, null);
                        copyFromRepositoryToStick(id);
                    } catch(IOException ioe) {
                        LOG.warn("failed to copy book " + id + " from repository to stick", ioe);
                        if(frame != null) {
                            JOptionPane.showMessageDialog(frame, "Das Buch " + id + " konnte nicht auf den Stift kopiert werden");
                        }
                        result = false;
                        newTbds.add(id);
                    }
                }
            }
            try {
                setTBD(newTbds);
            } catch(IOException ioe) {
                LOG.warn("failed to write TBD.TXT", ioe);
            }
        } catch(IOException ioe) {
            LOG.warn("unable to read TBD.TXT", ioe);
            if(frame != null) {
                JOptionPane.showMessageDialog(frame, "Die Datei TBD.TXT auf dem Stift kann nicht gelesen werden");
                result = false;
            }
        }
        if(progress != null) {
            progress.done();
        }
        return(result);
    }

    public void activateBook(int id) throws IOException {
        
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        
        HashMap<String, String> settings = getSettings();
        settings.put("book", _id);
        setSettings(settings);
        
    }
    
    
    @Override
    public File getBookOufOrKii(int mid) {
        String _id = Integer.toString(mid);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        return(new File(getBookDir(), _id + "_en.ouf"));
    }
}
