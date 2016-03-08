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
import java.io.FileOutputStream;
import java.io.IOException;
import tingeltangel.core.Book;
import tingeltangel.core.Codes;
import tingeltangel.core.Translator;

/**
 *
 * @author martin
 */
class GenerateCode extends CliCmd {
    
    
    @Override
    public String getName() {
        return("generate-code");
    }

    @Override
    public String getDescription() {
        return("generate-code 600|1200 <size in mm> <ting id> <png file>");
    }

    @Override
    public int execute(String[] args) {
        
        if(args.length != 4) {
            return(error("falsche Anzahl von Parametern angegeben"));
        }
        File file = new File(args[3].trim());
        
        if(args[0].toLowerCase().equals("600")) {
            Codes.setResolution(Codes.DPI600);
        } else if(args[0].toLowerCase().equals("1200")) {
            Codes.setResolution(Codes.DPI1200);
        } else {
            return(error("Syntax Fehler"));
        }
        
        int size = -1;
        try {
            size = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            return(error("ungültige Große angegeben (5-500)"));
        }
        if((size < 5) || (size > 500)) {
            return(error("ungültige Große angegeben (5-500)"));
        }
        
        int id = -1;
        try {
            id = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            return(error("ungültige id angegeben (0-65535)"));
        }
        
        id = Translator.ting2code(id);
        if(id < 0) {
            return(error("unbekannte id angegeben"));
        }
        
        
        Book book = CLI.getBook();
        
        try {
            
            FileOutputStream out = new FileOutputStream(file);
            Codes.drawPng(id, size, size, out);
            out.close();
        
        } catch(IOException e) {
            return(error("Code konnten nicht erzeugt werden", e));
        }
        return(ok());
    }

    
}
