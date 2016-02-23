/*
 * Copyright 2016 martin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tingeltangel.cli_ng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import tingeltangel.core.Entry;
import tingeltangel.core.MP3Player;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.Callback;

/**
 *
 * @author martin
 */
class Play extends CliCmd {

    @Override
    String getName() {
        return("play");
    }

    @Override
    String getDescription() {
        return("play <oid>");
    }

    @Override
    int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }
        int oid;
        try {
            oid = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            return(error("OID keine Zahl zw. 15001 und 65535"));
        }
        if(oid < 15001 || oid > 0xffff) {
            return(error("OID keine Zahl zw. 15001 und 65535"));
        }
        
        if(!CLI.bookOpened()) {
            return(error("kein Buch geöffnet"));
        }
        if(!CLI.getBook().entryForTingIDExists(oid)) {
            return(error("OID nicht gefunden"));
        }
        
        final Entry entry = CLI.getBook().getEntryByOID(oid);
        
        if(entry.isSub()) {
            return(error("OID ist ein Sub-Skript"));
        }
        
        if(entry.isMP3() && (entry.getMP3() != null)) {
            try {
                MP3Player.getPlayer().play(entry.getMP3(), new Callback<Exception>() {
                    @Override
                    public void callback(Exception t) {
                        System.err.println("Fehler bein Abspielen des MP3 (" + entry.getMP3().getAbsolutePath() + "): " + t.getMessage());
                    }
                });
            } catch (FileNotFoundException ex) {
                return(error("konnte MP3 nicht abspielen", ex));
            }
        } else if(entry.isCode()) {
            try {
                entry.getScript().execute();
            } catch(SyntaxError se) {
                return(error("Syntax Fehler (OID " + se.getTingID() + " Zeile " + se.getRow() + "): " + se.getMessage()));
            }
        } else if(entry.isTTS()) {
            try {
                final File tts = entry.getTTS().generateTTS(entry);
                MP3Player.getPlayer().play(tts, new Callback<Exception>() {
                    @Override
                    public void callback(Exception t) {
                        System.err.println("Fehler bein Abspielen des MP3 (" + tts.getAbsolutePath() + "): " + t.getMessage());
                    }
                });
            } catch(IOException ioe) {
                return(error("Es ist ein Fehelr aufgetreten: " + ioe.getMessage(), ioe));
            }
        }
        return(ok());
    }

    
}
