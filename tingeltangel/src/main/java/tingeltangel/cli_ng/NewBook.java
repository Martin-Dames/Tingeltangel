/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli_ng;

import java.io.File;
import tingeltangel.core.Book;
import tingeltangel.core.Translator;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
class NewBook extends CliCmd {

    @Override
    public String getName() {
        return("new-book");
    }

    @Override
    public String getDescription() {
        return("new-book <mid>");
    }

    @Override
    public int execute(String[] args) {
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

        // check if there is already a book with this id
        if(new File(FileEnvironment.getBooksDirectory(), _mid).exists()) {
            return(error("Dieses Buch existiert schon"));
        }

        Book book = CLI.getBook();
        book.clear();
        book.setID(mid);
        
        return(ok());
    }
    
}
