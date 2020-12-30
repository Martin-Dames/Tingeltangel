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

/**
 *
 * @author martin
 */
class GetMeta extends CliCmd {

    final static String NAME = "name";
    final static String PUBLISHER = "publisher";
    final static String AUTHOR = "author";
    final static String VERSION = "version";
    final static String URL = "url";
    final static String MAGIC = "magic";
    final static String DATE = "date";
    
    @Override
    String getName() {
        return("get-meta");
    }

    @Override
    String getDescription() {
        return("get-meta name|publisher|author|version|url|magic|date");
    }

    @Override
    int execute(String[] args) {
        
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }
        
        if(!CLI.bookOpened()) {
            return(error("kein Buch geöffnet"));
        }
        
        args[0] = args[0].trim();
        
        if(args[0].equals(NAME)) {
            System.out.println(CLI.getBook().getName());
        } else if(args[0].equals(PUBLISHER)) {
            System.out.println(CLI.getBook().getPublisher());
        } else if(args[0].equals(AUTHOR)) {
            System.out.println(CLI.getBook().getAuthor());
        } else if(args[0].equals(VERSION)) {
            System.out.println(CLI.getBook().getVersion());
        } else if(args[0].equals(URL)) {
            System.out.println(CLI.getBook().getUrl());
        } else if(args[0].equals(MAGIC)) {
            System.out.println(CLI.getBook().getMagicValue());
        } else if(args[0].equals(DATE)) {
            System.out.println(CLI.getBook().getDate());
        } else {
            return(error("unbekanter parameter: " + args[0]));
        }
        return(ok());
    }

}
