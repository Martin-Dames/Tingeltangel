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
import java.io.FilenameFilter;
import java.io.IOException;
import tingeltangel.core.Book;
import tingeltangel.core.Translator;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
class ChangeMid extends CliCmd {

    @Override
    String getName() {
        return("change-mid");
    }

    @Override
    String getDescription() {
        return("change-mid <new mid>");
    }

    @Override
    int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }
        int mid;
        try {
            mid = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            return(error("MID keine Zahl zw. 1 und " + Integer.toString(Translator.MAX_MID)));
        }
        if(mid < 1 || mid > Translator.MAX_MID) {
            return(error("MID keine Zahl zw. 1 und " + Integer.toString(Translator.MAX_MID)));
        }
        if(!CLI.bookOpened()) {
            return(error("kein Buch geöffnet"));
        }
        if(CLI.getBook().getID() == mid) {
            return(error("gleiche MID angegeben"));
        }
        if(Book.getBookMIDs().contains(mid)) {
            return(error("MID wird schon von einem anderen Buch genutzt"));
        }
        
        String _id = Integer.toString(mid);
        while(_id.length() < 5) {
            _id = "0" + _id;
        }
        
        try {
            Book book = CLI.getBook();
            int oldID = book.getID();
            book.setID(mid);
            book.save();

            // copy audio
            File[] audios = FileEnvironment.getAudioDirectory(oldID).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return(name.toLowerCase().endsWith(".mp3"));
                }
            });
            File destAudioDir = FileEnvironment.getAudioDirectory(mid);
            for(int i = 0; i < audios.length; i++) {
                FileEnvironment.copy(audios[i], new File(destAudioDir, audios[i].getName()));
            }

            book.clear();
            book.setID(mid);
            Book.loadXML(FileEnvironment.getXML(mid), book, null);
            book.resetChangeMade();
            book.deleteBook(oldID);
        } catch(IOException e) {
            return(error("MID konnte nicht geändert werden", e));
        }
                        
        
        return(ok());
    }

    
}
