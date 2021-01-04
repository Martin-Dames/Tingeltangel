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

/**
 *
 * @author mdames
 */
class GetEntryMp3Length extends CliCmd {

    @Override
    String getName() {
        return("get-entry-mp3-length");
    }

    @Override
    String getDescription() {
        return("get-entry-mp3-length <oid>");
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
        if(!CLI.getBook().getEntryByOID(oid).isMP3()) {
            return(error("OID ist nicht vom Typ MP3"));
        }
        float seconds = CLI.getBook().getEntryByOID(oid).getLength();
        int hours = (int)(seconds / 3600f);
        seconds -= hours * 3600;
        int minutes = (int)(seconds / 60f);
        seconds -= minutes * 60;
        String l = "";
        if(hours > 0) {
            l += Integer.toString(hours) + ":";
        }
        if(minutes < 10) {
            l += "0";
        }
        l += Integer.toString(minutes) + ":";
        int fs = (int)seconds;
        seconds -= fs;
        if(fs < 10) {
            l += "0";
        }
        l += Integer.toString(fs);
        l += ".";
        int hs = (int)(seconds * 100);
        if(hs < 10) {
            l += "0";
        }
        l += Integer.toString(hs);
        System.out.println(l);
        return(ok());
    }

}
