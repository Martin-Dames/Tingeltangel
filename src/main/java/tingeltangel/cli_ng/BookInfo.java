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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import tingeltangel.core.Book;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
class BookInfo extends CliCmd {

    @Override
    public String getName() {
        return("book-info");
    }

    @Override
    public String getDescription() {
        return("book-info <mid>");
    }

    @Override
    public int execute(String[] args) {
        try {
            if(args.length != 1) {
                return(error("falsche Anzahl von Parametern"));
            }
            int mid;
            try {
                mid = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                return(error("keine Zahl (1-" + Translator.MAX_MID + ") als Parameter angegeben"));
            }
            if((mid < 1) || (mid > Translator.MAX_MID)) {
                return(error("ungültige MID angegeben (1-" + Translator.MAX_MID + ")"));
            }
            String _mid = Integer.toString(mid);
            while(_mid.length() < 5) {
                _mid = "0" + _mid;
            }

            boolean existsInBooks = new File(FileEnvironment.getBooksDirectory(), _mid).exists();
            boolean existsInRepository = Repository.txtExists(mid);
            
            if((!existsInBooks) && (!existsInRepository)) {
                return(error("Dieses Buch existiert nicht"));
            }

            if(existsInBooks) {
                Book book = new Book(15000);
                book.setID(mid);
                Book.loadXML(FileEnvironment.getXML(mid), book, null);
                if(existsInRepository) {
                    System.out.println("BOOK:");
                }
                System.out.println("Name     : " + book.getName());
                System.out.println("Verleger : " + book.getPublisher());
                System.out.println("Author   : " + book.getAuthor());
                System.out.println("Version  : " + book.getVersion());
                System.out.println("URL      : " + book.getUrl());
                System.out.println("?        : " + book.getMagicValue());
                System.out.println("Datum    : " + new SimpleDateFormat("dd.MM.yyyy").format(new Date(book.getDate() * 1000)));
            }
            if(existsInRepository) {
                Map<String, String> txt = Repository.getBookTxt(mid);
                if(existsInBooks) {
                    System.out.println("REPOSITORY:");
                }
                System.out.println("Name     : " + txt.get(TxtFile.KEY_NAME));
                System.out.println("Verleger : " + txt.get(TxtFile.KEY_PUBLISHER));
                System.out.println("Author   : " + txt.get(TxtFile.KEY_AUTHOR));
                System.out.println("Version  : " + txt.get(TxtFile.KEY_VERSION));
                System.out.println("URL      : " + txt.get(TxtFile.KEY_URL));
            }
        } catch(IOException ex) {
            return(error("das Buch konnte nicht gelesen werden", ex));
        }
        return(ok());
    }
}
