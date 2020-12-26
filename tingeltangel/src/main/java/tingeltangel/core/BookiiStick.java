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

import tingeltangel.core.constants.KiiFile;
import tingeltangel.core.constants.OufFile;
import tingeltangel.core.constants.PngFile;
import tingeltangel.core.constants.ScriptFile;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.tools.OS;
import tingeltangel.tools.ProgressDialog;


/**
 * the central class for managing a bookii stick
 * @author martin dames
 */
public class BookiiStick extends Stick {

    private final static String BOOK_DIR = "BOOK";
    private final static String CONFIG_DIR = "CONFIGURE";
    private final static String SETTINGS_FILE = "SETTINGS.INI";
    private final static String TBD_FILE = "TBD.TXT";
    
    private final File path;
    
    private File tbdFile = null; 
    private File settingsFile = null; 
    private File bookDir = null;
    
    private final static Logger LOG = LogManager.getLogger(BookiiStick.class);
    
    @Override
    public String getType() {
        return("Bookii");
    }
    
    @Override
    public boolean isBookii() {
        return(true);
    }
    
    private BookiiStick(File path) {
        this.path = path;
        File[] content = path.listFiles();
        File configDir = null;
        for(int i = 0; i < content.length; i++) {
            if(content[i].isDirectory() && (content[i].getName().toUpperCase().equals(BOOK_DIR))) {
                bookDir = content[i];
            }
            if(content[i].isDirectory() && (content[i].getName().toUpperCase().equals(CONFIG_DIR))) {
                configDir = content[i];
            }
        }
        File[] configContent = configDir.listFiles();
        for(int i = 0; i < content.length; i++) {
            if(configContent[i].isFile() && (configContent[i].getName().toUpperCase().equals(SETTINGS_FILE))) {
                settingsFile = configContent[i];
            }
            if(configContent[i].isFile() && (configContent[i].getName().toUpperCase().equals(TBD_FILE))) {
                tbdFile = configContent[i];
            }
        }
    }
    
    private static boolean checkForStick(File path) {
        File[] content = path.listFiles();
        if(content != null) {
            File bookDir = null;
            File configDir = null;
            for(int i = 0; i < content.length; i++) {
                if(content[i].isDirectory() && (content[i].getName().toUpperCase().equals(BOOK_DIR))) {
                    bookDir = content[i];
                }
                if(content[i].isDirectory() && (content[i].getName().toUpperCase().equals(CONFIG_DIR))) {
                    configDir = content[i];
                }
            }
            if((configDir == null) || (bookDir == null)) {
                return(false);
            }
            File[] configContent = configDir.listFiles();
            boolean foundSettings = false;
            boolean foundTbd = false;
            for(int i = 0; i < content.length; i++) {
                if(configContent[i].isFile() && (configContent[i].getName().toUpperCase().equals(SETTINGS_FILE))) {
                    foundSettings = true;
                }
                if(configContent[i].isFile() && (configContent[i].getName().toUpperCase().equals(TBD_FILE))) {
                    foundTbd = true;
                }
            }
            if(!foundSettings) {
                return(false);
            }
            if(!foundTbd) {
                return(false);
            }
            return(true);
        }
        return(false);
    }
    
    /**
     * retrieves size of free space on stick
     * @return free space in bytes
     */
    @Override
    public long getFreeSpace() {
        return(path.getUsableSpace());
    }
    
    /**
     * retrieves the file TBD.TXT from stick
     * @return the tbd file
     */
    public File getTBDFile() {
        return(tbdFile);
    }
    
    /**
     * retrieves the content of TBD.TXT from stick
     * @return content from TBD.TXT as HashSet&lt;Integer&gt;
     * @throws IOException
     */
    public HashSet<Integer> getTBD() throws IOException {
        if(!tbdFile.canRead()) {
            throw new FileNotFoundException(tbdFile.getAbsolutePath());
        }
        HashSet<Integer> tbd = new HashSet<>();
        BufferedReader in = new BufferedReader(new FileReader(tbdFile));
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(!row.isEmpty()) {
                try {
                    tbd.add(Integer.parseInt(row));
                } catch(NumberFormatException nfe) {
                    LOG.warn("invalid row in " + tbdFile.getAbsolutePath() + ": " + row);
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
        if(!bookDir.canRead()) {
            throw new FileNotFoundException(bookDir.getAbsolutePath());
        }
        LinkedList<Integer> books = new LinkedList<>();
        File[] files = bookDir.listFiles();
        for(int i = 0; i < files.length; i++) {
            if(files[i].getName().endsWith(KiiFile._EN_KII) && (files[i].getName().length() == KiiFile._EN_KII.length() + 5)) {
                books.add(Integer.parseInt(files[i].getName().substring(0, 5)));
            }
        }
        return(books);
    }
 
 
    /**
     * gets the directory on stick where most data resists
     * @return the book directory
     */
    public File getBookDir() {
        return(bookDir);
    }
    
    /**
     * sets the stick settings
     * @param settings
     * @throws IOException
     */
    public void setSettings(HashMap<String, String> settings) throws IOException {
        
        if(!settingsFile.canWrite()) {
            throw new FileNotFoundException(settingsFile.getAbsolutePath());
        }
        
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(settingsFile), "UTF-16");
        
        
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
    public HashMap<String, String> getSettings() throws IOException {
        if(!settingsFile.canRead()) {
            throw new FileNotFoundException(settingsFile.getAbsolutePath());
        }
        
        HashMap<String, String> settings = new HashMap<String, String>();
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFile), Charset.forName("UTF-16")));
        String row;
        while((row = in.readLine()) != null) {
            row = row.trim();
            if(!row.isEmpty()) {
                int p = row.indexOf("=");
                if(p == -1) {
                    throw new IOException("bad row in " + settingsFile.getAbsolutePath() + ": " + row);
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
    public void delete(int id) {
        String _id = Integer.toString(id);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        File txt = new File(getBookDir(), _id + TxtFile._EN_TXT);
        File png = new File(getBookDir(), _id + PngFile._EN_PNG);
        File ouf = new File(getBookDir(), _id + KiiFile._EN_KII);
        File src = new File(getBookDir(), _id + ScriptFile._EN_SRC);
        
        txt.delete();
        png.delete();
        ouf.delete();
        if(src.exists()) {
            src.delete();
        }
        
    }
    
    /*
    public static int getOnlineBookVersion(int id) throws IOException {
        if(Repository.getBookTxt(id) == null) {
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
    */

    
    /**
     * copies a book from the repository to the stick
     * @param mid the book id
     * @throws IOException
     */
    @Override
    public void copyFromRepositoryToStick(int mid) throws IOException {
        String _id = Integer.toString(mid);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        
        
        File txt = new File(getBookDir(), _id + TxtFile._EN_TXT);
        File png = new File(getBookDir(), _id + PngFile._EN_PNG);
        File kii = new File(getBookDir(), _id + KiiFile._EN_KII);
        File src = new File(getBookDir(), _id + ScriptFile._EN_SRC);
        
        File srcKii = Repository.getBookKii(mid);
        LOG.info("srcKii=" + srcKii);
        if((srcKii == null) || (!srcKii.canRead())) {
            LOG.info("kii not found. try ouf...");
            srcKii = Repository.getBookOuf(mid);
        }
        
        LOG.info("copy kii...");
        fileCopy(srcKii, kii);
        LOG.info("copy png...");
        fileCopy(Repository.getBookPng(mid), png);
        File rSrc = Repository.getBookSrc(mid);
        if((rSrc != null) && (rSrc.exists())) {
            LOG.info("copy src...");
            fileCopy(rSrc, src);
        }
        LOG.info("copy txt...");
        fileCopy(Repository.getBookTxtFile(mid), txt);
    }

    /**
     * sets the TBD.TXT
     * @param newTbd the new list of books in TBD.TXT
     * @throws IOException
     */
    public void setTBD(LinkedList<Integer> newTbd) throws IOException {
        if(!tbdFile.canWrite()) {
            throw new FileNotFoundException(tbdFile.getAbsolutePath());
        }
        PrintWriter out = new PrintWriter(new FileWriter(tbdFile));
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
    public static BookiiStick getStick() throws IOException {
        Set<BookiiStick> sticks = getSticks();
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
    public static Set<BookiiStick> getSticks() throws IOException {
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
        HashSet<BookiiStick> sticks = new HashSet<>();
        for (File mount : mounts) {
            if (BookiiStick.checkForStick(mount)) {
                sticks.add(new BookiiStick(mount));
            }
        }
        return(sticks);
    }
    
    @Override
    public void update(ProgressDialog progress) {
        update(null, progress);
    }
    
    @Override
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
                LOG.info("checking id=" + id + "...");
                try {
                    Repository.update(id, null);
                    LOG.info("repository update done");
                    HashMap<String, String> bookTxt = Repository.getBookTxt(id);
                    if(bookTxt == null) {
                        LOG.info("skipping book " + id + ". not found in repository");
                    } else {
                        int repositoryVersion = Integer.parseInt(Repository.getBookTxt(id).get(TxtFile.KEY_VERSION));
                        LOG.info("repository version=" + repositoryVersion);
                        int stickVersion = -1;
                        try {
                            stickVersion = getBookVersion(id);
                        } catch(IOException ioe) {
                            LOG.warn("failed to get book version from stick (mid=" + id + ")", ioe);
                        }
                        LOG.info("stick version=" + stickVersion);
                        if(stickVersion < repositoryVersion) {
                            try {
                                LOG.info("copyFromRepositoryToStick...");
                                copyFromRepositoryToStick(id);
                                LOG.info("copy done");
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
                    LOG.info("book not found in repository (mid=" + id + "): perhaps a bookii only file", fnfe);
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
        LOG.warn("update on bookii stick done");
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
        return(new File(getBookDir(), _id + "_en.kii"));
    }
    
}
