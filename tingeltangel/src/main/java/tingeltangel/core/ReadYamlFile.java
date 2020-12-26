/*
    Copyright (C) 2015   Jesper Zedlitz <jesper@zedlitz.de>
  
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


import org.yaml.snakeyaml.Yaml;
import tingeltangel.tiptoireveng.Interpreter;
import tiptoi_reveng.lexer.Lexer;
import tiptoi_reveng.lexer.LexerException;
import tiptoi_reveng.node.Start;
import tiptoi_reveng.parser.Parser;
import tiptoi_reveng.parser.ParserException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tingeltangel.tools.ProgressDialog;

/**
 * Jeder Identifikator (YAML-Datei) muss eine OID bekommen.  Dabei aufpassen, welche OIDs manuell über ein zusätzliches Mapping vorgegeben wurde.
 * Außerdem muss jeder Dateiname eine OID bekommen. Die werden aber nicht nach außen angezeigt.
 * Jede Variable bekommt einen Register zugewiesen. Dabei muss man aufpassen, welche Register manuell verwendet wurden.
 */
public class ReadYamlFile {
    public static final int MINIMAL_OID = 15001;

    boolean ignoreAudioFiles = false;

    private Interpreter interpreter = new Interpreter();
    private Map<Integer, String> usedOidAndIdentifiers = new HashMap<Integer, String>();
    
    

    private void convertOgg2Mp3(File oggFile, File mp3File) throws IOException {
        Process p = Runtime.getRuntime().exec("/usr/bin/avconv -i " + oggFile.getCanonicalPath() + " " + mp3File.getCanonicalPath());
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, String> getUsedOidAndIdentifiers() {
        return usedOidAndIdentifiers;
    }

    public Book read(File yamlFile, ProgressDialog progress) throws ParserException, IOException, LexerException {
        Yaml yaml = new Yaml();
        Map data = (Map) yaml.load(new FileInputStream(yamlFile));

        Map scripts = (Map) data.get("scripts");

        File dir = yamlFile.getParentFile();
        Book book = new Book(8000 + ((Integer) data.get("product-id")));

        Map scriptcodes = (Map) data.get("scriptcodes");
        if (scriptcodes != null) {
            for (Object identifier : scriptcodes.keySet()) {
                int oid = (Integer) scriptcodes.get(identifier);
                if(oid <= MINIMAL_OID) {
                    oid += 8000;
                }
                interpreter.getIdentifier2oid().put(identifier.toString(), oid);
            }
        }

        for (Object identifier : scripts.keySet()) {
            if (identifier instanceof Integer) {
                interpreter.getOids().add((Integer) identifier);
            } else {
                interpreter.addIdentifier(identifier.toString());
            }

            @SuppressWarnings("unchecked")
            List<String> commands = (List<String>) scripts.get(identifier);
            for (String command : commands) {
                PushbackReader reader = new PushbackReader(new StringReader(command));

                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                try {
                    Start start = parser.parse();
                    start.apply(interpreter);
                } catch (ParserException pe) {
                    System.err.println("Could not parse command " + command);
                    pe.printStackTrace();
                    throw pe;
                }
            }
        }

        interpreter.startSecondPhase();

        // Create Entry objects for sound files.
        for (String filename : interpreter.getFilename2oid().keySet()) {
            int oid = interpreter.getFilename2oid().get(filename);
            book.addEntry(oid);
            Entry entry = book.getEntryByOID(oid);

            if (!ignoreAudioFiles) {
                // Since the TipToi pen uses OggVorbis files we might have to convert the audio files to mp3.
                File mp3File = new File(dir, "audio_files/" + filename + ".mp3");
                if (!mp3File.exists()) {
                    File oggFile = new File(dir, "audio_files/" + filename + ".ogg");
                    if (!oggFile.exists()) {
                        throw new RuntimeException("Could not find audio file " + filename);
                    }
                    convertOgg2Mp3(oggFile, mp3File);
                }

                entry.setMP3(mp3File);
            }
        }


        for (Object identifier : scripts.keySet()) {
            interpreter.getScript().setLength(0);
            int oid;
            if (identifier instanceof Integer) {
                oid = (Integer) identifier;
                if (oid < MINIMAL_OID) {
                    oid += 7000;
                }

                usedOidAndIdentifiers.put(oid, null);

            } else {
                oid = interpreter.getIdentifier2oid().get(identifier.toString());
                usedOidAndIdentifiers.put(oid, identifier.toString());
            }

            @SuppressWarnings("unchecked")
            List<String> commands = (List<String>) scripts.get(identifier);
            for (String command : commands) {
                PushbackReader reader = new PushbackReader(new StringReader(command));

                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                try {
                    Start start = parser.parse();
                    start.apply(interpreter);
                } catch (ParserException pe) {
                    System.err.println("Could not parse command " + command);
                    pe.printStackTrace();
                    throw pe;
                }
            }

            interpreter.getScript().append("end\n");

            book.addEntry(oid);
            Entry entry = book.getEntryByOID(oid);
            Script script = new Script(interpreter.getScript().toString(), entry);
            entry.setScript(script);
            entry.setHint(identifier.toString());
        }

        return book;
    }

    public static void main(String[] args) throws Exception {
        File yaml = new File("C:\\Users\\mdames\\Desktop\\Das-verlorene-Schaf-Rallye-master\\Das-verlorene-Schaf-Rallye-master\\gme\\verlorenes_schaf.yaml");
        Book book = new ReadYamlFile().read(yaml, null);
        System.out.println("Imported yaml book (mid=" + book.getID() + ")");
        book.save();
    }
    
}
