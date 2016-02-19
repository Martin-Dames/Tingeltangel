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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import tingeltangel.core.Book;
import tingeltangel.core.Importer;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;
import tingeltangel.core.scripting.SyntaxError;

/**
 *
 * @author martin
 */
class ImportManual extends CliCmd {

    @Override
    public String getName() {
        return("import-manual");
    }

    @Override
    public String getDescription() {
        return("import-manual [src <src file>] [ouf <ouf file>] [txt <txt file>] [png <png-file>] [mid <mid>]");
    }

    @Override
    public int execute(String[] args) {
        
        int p = 0;
        
        String src = null;
        String ouf = null;
        String txt = null;
        String png = null;
        String mid = null;
        
        while(p < args.length - 1) {
            String a = args[p].trim().toLowerCase();
            if(a.equals("src")) {
                src = args[p + 1].trim();
            } else if(a.equals("ouf")) {
                ouf = args[p + 1].trim();
            } else if(a.equals("txt")) {
                txt = args[p + 1].trim();
            } else if(a.equals("png")) {
                png = args[p + 1].trim();
            } else if(a.equals("mid")) {
                mid = args[p + 1].trim();
            } else {
                return(error("syntax error"));
            }  
            p += 2;
        }
        
        if((src != null) && (!new File(src).canRead())) {
            return(error("Die Datei " + src + " konnte nicht gelesen werden"));
        }
        if((ouf != null) && (!new File(ouf).canRead())) {
            return(error("Die Datei " + ouf + " konnte nicht gelesen werden"));
        }
        if((txt != null) && (!new File(txt).canRead())) {
            return(error("Die Datei " + txt + " konnte nicht gelesen werden"));
        }
        if((png != null) && (!new File(png).canRead())) {
            return(error("Die Datei " + png + " konnte nicht gelesen werden"));
        }
        
        int id = -1;
        if(mid != null) {
            try {
                id = Integer.parseInt(mid);
            } catch(NumberFormatException e) {
                return(error("Fehlerhafte mid angegeben"));
            }
            if((id < 1) || (id > Translator.MAX_MID)) {
                return(error("Fehlerhafte mid angegeben (1-" + Translator.MAX_MID + ")"));
            }
        }
        if((id < 0) && (ouf != null)) {
            try {
                // prefetch id
                DataInputStream in = new DataInputStream(new FileInputStream(ouf));
                in.skipBytes(20);
                id = in.readInt();
                in.close();
            } catch(IOException e) {
                return(error("Fehler beim Lesen der ouf-Datei", e));
            }
        }
        if(id < 0) {
            return(error("MID konnte nicht ermittelt werden"));
        }
        
        if((ouf == null) && (src == null)) {
            return(error("Es muss mindestens eine ouf Datei oder eine src Datei angegeben werden"));
        }
        
        Book book = CLI.getBook();
        book.setID(id);
        book.clear();
        
        try {
            Importer.importBook(new File(ouf), Repository.getBook(new File(txt)), new File(src), new File(png), book, null);
        } catch(Exception e) {
            return(error("Das Buch konnte nicht importiert werden.", e));
        }
           
        return(ok());
        
    }

}
