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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author mdames
 */
class SetEntryScript extends CliCmd {

    @Override
    String getName() {
        return("set-entry-script");
    }

    @Override
    String getDescription() {
        return("set-entry-script <oid> <script-file>");
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
        File scriptFile = new File(args[1]);
        if(!scriptFile.canRead()) {
            return(error("angebebene Skript-Datei ist nicht lesbar"));
        }
        if(!CLI.bookOpened()) {
            return(error("kein Buch geöffnet"));
        }
        if(!CLI.getBook().entryForTingIDExists(oid)) {
            CLI.getBook().addEntry(oid);
        }
        try {
            // store content of scriptFile to script
            BufferedReader in = new BufferedReader(new FileReader(scriptFile));
            StringBuilder sb = new StringBuilder();
            String row;
            while((row = in.readLine()) != null) {
                sb.append(row).append("\n");
            }
            in.close();
            CLI.getBook().getEntryByOID(oid).setCode();
            CLI.getBook().getEntryByOID(oid).getScript().setCode(sb.toString());
        } catch (IOException ex) {
            return(error("Script konnte nicht hinzugefügt werden", ex));
        }
        return(ok());
    }

}
