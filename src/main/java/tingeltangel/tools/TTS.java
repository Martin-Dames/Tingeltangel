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

package tingeltangel.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author mdames
 */
public class TTS {
    
    private final static int UNKNOWN = 0;
    private final static int FEMALE = 1;
    private final static int MALE = 2;
    
    private final static File ESPEAK;
    private final static File LAME;
    private final static boolean ENABLED;
    
    private final static Map<String, Language> voices = new HashMap<String, Language>();
    private final static Map<String, Language> variants = new HashMap<String, Language>();
    private final static SortedSet<String> voiceIDs = new TreeSet<String>();
    private final static SortedSet<String> variantIDs = new TreeSet<String>();
    
    
    public static SortedSet<String> getVoiceIDs() {
        return(voiceIDs);
    }
    
    public static SortedSet<String> getVariantIDs() {
        return(variantIDs);
    }
    
    public static String getVoiceName(String id) {
        return(voices.get(id).name);
    }
    
    public static String getVariantName(String id) {
        return(variants.get(id).name);
    }
    
    public static int getVoiceGender(String id) {
        return(voices.get(id).gender);
    }
    
    public static int getVariantGender(String id) {
        return(variants.get(id).gender);
    }
    
    static {
        try {
            
            ESPEAK = Binary.getBinary(Binary.ESPEAK);
            LAME = Binary.getBinary(Binary.LAME);
            ENABLED = (ESPEAK != null) && (LAME != null);

            if(ENABLED) {

                // collect voices
                String[] cmd1 = {ESPEAK.getCanonicalPath(), "--voices"};
                Process process = new ProcessBuilder(cmd1).start();
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                in.readLine(); // remove heading
                String row;
                while((row = in.readLine()) != null) {
                    String[] r = row.trim().split("[ ]+");
                    Language lang = new Language();
                    lang.id = r[4];
                    lang.name = r[3];
                    if(r[2].endsWith("F")) {
                        lang.gender = FEMALE;
                    } else if(r[2].endsWith("M")) {
                        lang.gender = MALE;
                    } else {
                        lang.gender = UNKNOWN;
                    }
                    voiceIDs.add(lang.id);
                    voices.put(lang.id, lang);
                }
                
                // collect variants
                String[] cmd2 = {ESPEAK.getCanonicalPath(), "--voices=variant"};
                process = new ProcessBuilder(cmd2).start();
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                in.readLine(); // remove heading
                while((row = in.readLine()) != null) {
                    String[] r = row.trim().split("[ ]+");
                    Language lang = new Language();
                    lang.id = r[4].substring(3); // remove "v!\\"
                    lang.name = r[3];
                    if(r[2].endsWith("F")) {
                        lang.gender = FEMALE;
                    } else if(r[2].endsWith("M")) {
                        lang.gender = MALE;
                    } else {
                        lang.gender = UNKNOWN;
                    }
                    variantIDs.add(lang.id);
                    variants.put(lang.id, lang);
                }
            } else {
                System.out.println("WARNING: tts not enabled");
                if(LAME == null) {
                    System.out.println("'lame' not found");
                }
                if(ESPEAK == null) {
                    System.out.println("'espeak' not found");
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
        
        if(ENABLED == false) {
            System.out.println("unable to execute tts request: tts not enabled");
            return;
        }
        
        speed = Math.max(Math.min(speed, 450), 80);
        amplitude = Math.max(Math.min(amplitude, 200), 0);
        pitch = Math.max(Math.min(pitch, 99), 0);

        if((variant == null) || (variant.isEmpty())) {
            variant = "";
        } else {
            variant = "+" + variant;
        }
        
        String[] cmd1 = {
            ESPEAK.getCanonicalPath(),
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
            LAME.getCanonicalPath(),
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
    
}

class MyComparator implements Comparator<Language> {
    @Override
    public int compare(Language lang1, Language lang2) {
        return(lang1.name.compareTo(lang2.name));
    }
}

class Language {
    String id;
    String name;
    int gender;
}
