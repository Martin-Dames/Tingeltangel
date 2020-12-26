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

import static tingeltangel.cli_ng.GetMeta.AUTHOR;
import static tingeltangel.cli_ng.GetMeta.DATE;
import static tingeltangel.cli_ng.GetMeta.MAGIC;
import static tingeltangel.cli_ng.GetMeta.NAME;
import static tingeltangel.cli_ng.GetMeta.PUBLISHER;
import static tingeltangel.cli_ng.GetMeta.URL;
import static tingeltangel.cli_ng.GetMeta.VERSION;

/**
 *
 * @author martin
 */
class SetMeta extends CliCmd {

    @Override
    String getName() {
        return("set-meta");
    }

    @Override
    String getDescription() {
        return("set-meta name|publisher|author|version|url|magic|date <value>");
    }

    @Override
    int execute(String[] args) {
        
        if(args.length != 2) {
            return(error("falsche Anzahl von Parametern"));
        }
        
        if(!CLI.bookOpened()) {
            return(error("kein Buch geöffnet"));
        }
        
        try {
            args[0] = args[0].trim();

            if(args[0].equals(NAME)) {
                CLI.getBook().setName(args[1]);
            } else if(args[0].equals(PUBLISHER)) {
                CLI.getBook().setPublisher(args[1]);
            } else if(args[0].equals(AUTHOR)) {
                CLI.getBook().setAuthor(args[1]);
            } else if(args[0].equals(VERSION)) {
                CLI.getBook().setVersion(Integer.parseInt(args[1]));
            } else if(args[0].equals(URL)) {
                CLI.getBook().setURL(args[1]);
            } else if(args[0].equals(MAGIC)) {
                CLI.getBook().setMagicValue(Long.parseLong(args[1]));
            } else if(args[0].equals(DATE)) {
                CLI.getBook().setDate(Long.parseLong(args[1]));
            } else {
                return(error("unbekanter parameter: " + args[0]));
            }
        } catch(NumberFormatException e) {
            return(error("keine Zahl als zweiten Parameter angegeben"));
        }
        return(ok());
    }

    
}
