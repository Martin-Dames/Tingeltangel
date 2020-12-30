/*
 * Copyright 2016 mdames.
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
import java.io.IOException;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
class ExportMp3 extends CliCmd {

    @Override
    String getName() {
        return("export-mp3 <oid> <mp3 file>");
    }

    @Override
    String getDescription() {
        return("export-mp3 <oid>");
    }

    @Override
    int execute(String[] args) {
        if(args.length != 2) {
            return(error("falsche Anzahl von Parametern"));
        }
        int oid;
        try {
            oid = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            return(error("OID keine Zahl zw. 15001 und 65535"));
        }
        
        File mp3File = new File(args[1]);
        if(!mp3File.canWrite()) {
            return(error("angebebene MP3-Datei ist nicht lesbar"));
        }
        
        if(!CLI.bookOpened()) {
            return(error("kein Buch geöffnet"));
        }
        if(!CLI.getBook().entryForTingIDExists(oid)) {
            return(error("OID nicht gefunden"));
        }
        if(!CLI.getBook().getEntryByOID(oid).isMP3()) {
            return(error("OID ist nicht vom Typ MP3"));
        }
        File audioFile = CLI.getBook().getEntryByOID(oid).getMP3();
        if(audioFile == null) {
            return(error("OID enthält kein MP3"));
        }
        try {
            FileEnvironment.copy(audioFile, mp3File);
        } catch (IOException ex) {
            return(error("MP3 konnte nicht extrahiert werden", ex));
        }
        return(ok());
    }

}
