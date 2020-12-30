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
import java.util.Map;
import tingeltangel.core.Book;
import tingeltangel.core.Importer;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
class ImportFromRepository extends CliCmd {

    @Override
    public String getName() {
        return("import-from-repository");
    }

    @Override
    public String getDescription() {
        return("import-from-repository <mid>");
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

            if(new File(FileEnvironment.getBooksDirectory(), _mid).exists()) {
                return(error("Dieses Buch existiert schon"));
            }

            if(!Repository.exists(mid)) {
                return(error("Das Buch wurde nicht gefunden"));
            }
            
            Book book = CLI.getBook();
            
            book.setID(mid);
            book.clear();
            
            
            File ouf = Repository.getBookOuf(mid);
            Map<String, String> txt = Repository.getBookTxt(mid);
            File src = Repository.getBookSrc(mid);
            File png = Repository.getBookPng(mid);
            Importer.importBook(ouf, txt, src, png, book, null);
            
        } catch(Exception ex) {
            return(error("das Buch konnte nicht gelesen werden", ex));
        }
        return(ok());
    }
}
