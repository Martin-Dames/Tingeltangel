/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import tingeltangel.core.Tupel;

/**
 *
 * @author mdames
 */
public class TTS {
    
    private final static String ESPEAK_DATA;
    private final static String ESPEAK_BIN;
    private final static String LAME;
    
    private final static SortedSet<Tupel<String, String>> eVoices = new TreeSet<Tupel<String, String>>(new MyComparator());
    private final static SortedSet<Tupel<String, String>> mVoices = new TreeSet<Tupel<String, String>>(new MyComparator());
    private final static SortedSet<Tupel<String, String>> variants = new TreeSet<Tupel<String, String>>(new MyComparator());
    
    private static String getLangName(File langFile) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(langFile));
        String r;
        while((r = in.readLine()) != null) {
            if(r.trim().toLowerCase().startsWith("name ")) {
                in.close();
                return(r.trim().substring("name ".length()).trim());
            }
        }
        in.close();
        return("unknown");
    }
    
    private static void addLanguageFiles(File dir, String prefix, SortedSet<Tupel<String, String>> set) throws IOException {
        File[] files = dir.listFiles();
        for(int i = 0; i < files.length; i++) {
            if(!files[i].getName().startsWith(".")) {
                set.add(new Tupel(prefix + files[i].getName(), getLangName(files[i])));
            }
        }
    }
    
    static {
        try {
            if(System.getProperty("os.name").startsWith("Windows")) {
                ESPEAK_BIN = "c:\\Program Files (x86)\\eSpeak\\command_line\\espeak.exe";
                ESPEAK_DATA = "c:\\Program Files (x86)\\eSpeak\\espeak-data";
                LAME = "c:\\Users\\mdames\\Desktop\\lame3.99.5\\lame.exe";
            } else {
                ESPEAK_BIN = "espeak";
                ESPEAK_DATA = "/usr/share/espeak-data";
                LAME = "lame";
            }

            // collect voices and variants
            File voices = new File(new File(ESPEAK_DATA), "voices");

            File[] files = voices.listFiles();
            for(int i = 0; i < files.length; i++) {
                if(!files[i].getName().startsWith(".")) {
                    if(files[i].isDirectory()) {
                        String dir = files[i].getName();
                        if(dir.equals("!v")) {
                            // variant dir
                            addLanguageFiles(files[i], "", variants);
                        } else if(dir.equals("mb")) {
                            // mbrola dir
                            addLanguageFiles(files[i], dir + "/", mVoices);
                        } else {
                            // voice dir
                            addLanguageFiles(files[i], dir + "/", eVoices);
                        }
                    } else {
                        // top level language
                        eVoices.add(new Tupel(files[i].getName(), getLangName(files[i])));
                    }
                }
            }

            // check for mbrola language packs
            Iterator<Tupel<String, String>> i = mVoices.iterator();
            File mbrola = new File(new File(ESPEAK_DATA), "mbrola");
            while(i.hasNext()) {
                String langName = i.next().a.substring("mb/mb-".length());
                if(!new File(mbrola, langName).exists()) {
                    i.remove();
                }
            }
        } catch(IOException ioe) {
            throw new Error(ioe);
        }
    }
    
    /**
     *
     * @param text The text to read
     * @param amplitude The amplitude (0-200)
     * @param pitch The pitch (0-99)
     * @param speed word per minute (80-450)
     * @param voice The voice
     * @param variant The variant (use null or empty string for no variant)
     * @param mp3 The MP§ to generate
     */
    public static void generate(final String text, int amplitude, int pitch, int speed, String voice, String variant, final File mp3) throws IOException {
        speed = Math.max(Math.min(speed, 450), 80);
        amplitude = Math.max(Math.min(amplitude, 200), 0);
        pitch = Math.max(Math.min(pitch, 99), 0);

        if((variant == null) || (variant.isEmpty())) {
            variant = "";
        } else {
            variant = "+" + variant;
        }
        
        String[] cmd1 = {
            ESPEAK_BIN,
            "--stdin",
            "--stdout",
            "-b",
            "1",
            "-z",
            "-a",
            Integer.toString(amplitude),
            "-p",
            Integer.toString(pitch),
            "-s",
            Integer.toString(speed),
            "-v",
            voice + variant
        };
        
        String[] cmd2 = {
            LAME,
            "-",
            "-"
        };
        
        final Process p1 = new ProcessBuilder(cmd1).start();
        final Process p2 = new ProcessBuilder(cmd2).start();
        
        // write mp3
        new Thread() {
            @Override
            public void run() {
                try {
                    InputStream in = p2.getInputStream();
                    InputStream err = p2.getErrorStream();
                    OutputStream out = new FileOutputStream(mp3);
                    copyStream(in, err, out);
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }.start();
        
        // copy data from p1 to p2
        new Thread() {
            @Override
            public void run() {
                try {
                    InputStream in = p1.getInputStream();    
                    InputStream err = p1.getErrorStream();                
                    OutputStream out = p2.getOutputStream();
                    copyStream(in, err, out);
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }.start();
        
        // copy text to p1
        new Thread() {
            @Override
            public void run() {
                try {
                    InputStream in = new ByteArrayInputStream(text.getBytes(Charset.forName("UTF-8")));
                    OutputStream out = p1.getOutputStream();
                    copyStream(in, null, out);
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }.start();
        
        try {
            p2.waitFor();
        } catch (InterruptedException ex) {
        }
    }
    
    private static void copyStream(InputStream in, final InputStream err, OutputStream out) throws IOException {
        
        if(err != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        byte[] b = new byte[4096];
                        while(true) {
                            int i = err.read(b);
                            if(i < 0) {
                                return;
                            }
                        }
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }.start();
        }
        
        byte[] buffer = new byte[4096];
        int c = 0;
        while(true) {
            int i = in.read(buffer);
            if(i < 0) {
                out.close();
                return;
            }
            c += i;
            out.write(buffer, 0, i);
            out.flush();
        }
    }
    
    public static void main(String[] args) throws IOException {
        Iterator<Tupel<String, String>> i;
        
        System.out.println("eSpeak voices:");
        i = eVoices.iterator();
        while(i.hasNext()) {
            Tupel<String, String> t = i.next();
            System.out.println("\t" + t.a + "\t" + t.b);
        }
        
        System.out.println("mbrola voices:");
        i = mVoices.iterator();
        while(i.hasNext()) {
            Tupel<String, String> t = i.next();
            System.out.println("\t" + t.a + "\t" + t.b);
        }
        
        System.out.println("variants:");
        i = variants.iterator();
        while(i.hasNext()) {
            Tupel<String, String> t = i.next();
            System.out.println("\t" + t.a + "\t" + t.b);
        }
        
        generate("Hallo, ich bin Tingeltangel.", 100, 50, 100, "mb/mb-de2", "", new File("/home/martin/test.mp3"));
        
    }
    
    
}

class MyComparator implements Comparator<Tupel<String, String>> {
    @Override
    public int compare(Tupel<String, String> o1, Tupel<String, String> o2) {
        return(o1.a.compareTo(o2.a));
    }
}