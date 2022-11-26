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
        return("generate-code 600|1200 <size in mm> <ting id>|<ting id bereich> <directory>");
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
        
        int idStart = -1;
        int idEnd = -1;
        try {
            int p = args[2].indexOf("-");
            
            if(p < 0) {
                idStart = Integer.parseInt(args[2]);
                idEnd = idStart;
            } else {
                idStart = Integer.parseInt(args[2].substring(0, p));
                idEnd = Integer.parseInt(args[2].substring(p + 1));
            }
        } catch(NumberFormatException e) {
            return(error("ungültige id angegeben (0-65535)"));
        }
        
        if(idStart > idEnd) {
            return(error("ungültige id bereich angegeben"));
        }
        
        int[] cIds = new int[idEnd - idStart + 1];
        for(int i = 0; i < cIds.length; i++) {
            cIds[i] = Translator.ting2code(idStart + i);
            if(cIds[i] < 0) {
                return(error("unbekannte id angegeben (" + (idStart + i) + ")"));
            }
        }
        
        try {
            for(int i = 0; i < cIds.length; i++) {
                String filename = Integer.toString(i + idStart);
                while(filename.length() < 5) {
                    filename = "0" + filename;
                }
                filename = "ting_label_" + filename + ".png";
                FileOutputStream out = new FileOutputStream(new File(file, filename));
                Codes.drawPng(cIds[i], size, size, out);
                out.close();
            }
        
        } catch(IOException e) {
            return(error("Code konnten nicht erzeugt werden", e));
        }
        return(ok());
    }

    
}
