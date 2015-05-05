/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 *
 * @author mdames
 */
public class FileEnvironment {

    public static File getHomeDirectory() {
        return new File(System.getProperty("user.home"));
    }

    public static File getBooksDirectory() {
        return getWorkingDirectory("books");
    }

    public static File getBookDirectory(int id) {
        String _id = Integer.toString(id);
        while (_id.length() < 5) {
            _id = "0" + _id;
        }
        File bd = new File(getWorkingDirectory("books"), _id);
        if (bd.exists()) {
            if (!bd.isDirectory()) {
                throw new Error(bd.getAbsolutePath() + " exists but is not a directory");
            }
        } else {
            if (!bd.mkdirs()) {
                throw new Error("can't create directory " + bd.getAbsolutePath());
            }
        }
        return bd;
    }

    public static File getRepositoryDirectory() {
        return getWorkingDirectory("repository");
    }

    public static File getAudioDirectory(int id) {
        File audio = new File(getBookDirectory(id), "audio");
        if (audio.exists()) {
            if (!audio.isDirectory()) {
                throw new Error(audio.getAbsolutePath() + " exists but is not a directory");
            }
        } else {
            if (!audio.mkdirs()) {
                throw new Error("can't create directory " + audio.getAbsolutePath());
            }
        }
        return audio;
    }
    
    public static File getTBU(int id) {
        return(new File(getBookDirectory(id), "book.tbu"));
    }
    
    private static File getWorkingDirectory(String subDirectory) {
        File wd = new File(getWorkingDirectoryRoot(), subDirectory);
        if (wd.exists()) {
            if (!wd.isDirectory()) {
                throw new Error(wd.getAbsolutePath() + " exists but is not a directory");
            }
        } else {
            if (!wd.mkdirs()) {
                throw new Error("can't create directory " + wd.getAbsolutePath());
            }
        }
        return wd;
    }

    public static File getWorkingDirectoryRoot() {
        File wd = null;
        if (OS.isWindows()) {
            String myDocuments = null;
            try {
                Process p = Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
                p.waitFor();
                InputStream in = p.getInputStream();
                byte[] b = new byte[in.available()];
                in.read(b);
                in.close();
                myDocuments = new String(b);
                myDocuments = myDocuments.split("\\s\\s+")[4];
            } catch (Exception e) {
                throw new Error(e);
            }
            wd = new File(myDocuments, "tingeltangel");
        } else {
            wd = new File(getHomeDirectory(), ".tingeltangel");
        }
        if (wd.exists()) {
            if (!wd.isDirectory()) {
                throw new Error(wd.getAbsolutePath() + " exists but is not a directory");
            }
        } else {
            if (!wd.mkdirs()) {
                throw new Error("can't create directory " + wd.getAbsolutePath());
            }
        }
        return wd;
    }

    public static void copy(File source, File destination) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(destination).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
        }
    }
    
}
