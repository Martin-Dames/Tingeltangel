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
class AppendEntrySubScript extends CliCmd {

    @Override
    String getName() {
        return("append-entry-sub-script");
    }

    @Override
    String getDescription() {
        return("append-entry-sub-script <oid> <script row>");
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
                
        if(!CLI.bookOpened()) {
            return(error("kein Buch geöffnet"));
        }
        if(!CLI.getBook().entryForTingIDExists(oid)) {
            return(error("oid existiert nicht"));
        }
        if(!CLI.getBook().getEntryByOID(oid).isSub()) {
            return(error("entry ist nicht von type sub-skript"));
        }
        
        String code = CLI.getBook().getEntryByOID(oid).getScript().toString();
        code += "\n" + args[1];
        CLI.getBook().getEntryByOID(oid).getScript().setCode(code);
        return(ok());
    }

}
