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

import tingeltangel.core.Book;
import tingeltangel.core.Translator;

/**
 *
 * @author martin
 */
class DeleteBook extends CliCmd {

    @Override
    String getName() {
        return("delete-book");
    }

    @Override
    String getDescription() {
        return("delete-book <mid>");
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
                return(error("keine Zahl (1-" + Translator.MAX_MID + ") als Parameter angegeben"));
            }
            if((mid < 1) || (mid > Translator.MAX_MID)) {
                return(error("ungültige MID angegeben (1-" + Translator.MAX_MID + ")"));
            }
            Book current = CLI.getBook();
            if(current.getID() == mid) {
                return(error("Buch ist geöffnet. Ein geöffnetes Buch kann nicht gelöscht werden."));
            }
            if(!current.deleteBook(mid)) {
                return(error("das Buch konnte nicht gelöscht werden."));
            }
            return(ok());
    }

}
