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
import java.io.OutputStream;
import java.util.LinkedList;
import tingeltangel.core.Codes;

/**
 *
 * @author martin
 */
class GenerateCodesPage extends CliCmd {

    @Override
    public String getName() {
        return("generate-codes-page");
    }

    @Override
    public String getDescription() {
        return("generate-codes-page 600|1200 <code width in mm> <code height in mm> <zip file>|<directory> <list of codes>");
    }
    // generate-codes-page 600 10 10 /home/martin/Desktop/test.png 15001-15200,8000-8020,8000-8020

    @Override
    public int execute(String[] args) {
        
        if(args.length != 5) {
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
        
        int width;
        try {
            width = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            return(error("ungültige Große angegeben (5-100)"));
        }
        if((width < 5) || (width > 100)) {
            return(error("ungültige Große angegeben (5-100)"));
        }
        
        int height;
        try {
            height = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            return(error("ungültige Große angegeben (5-100)"));
        }
        if((height < 5) || (height > 100)) {
            return(error("ungültige Große angegeben (5-100)"));
        }
        
        LinkedList<Integer> _codes = new LinkedList<Integer>();
        
        String[] elements = args[4].split(",");
        for(String element : elements) {
            int p = element.indexOf("-");
            if (p == -1) {
                int c = Integer.parseInt(element.trim());
                _codes.add(c);
            } else {
                int s = Integer.parseInt(element.substring(0, p).trim());
                int e = Integer.parseInt(element.substring(p + 1).trim());
                for(int k = s; k <= e; k++) {
                    _codes.add(k);
                }
            }
        }
        int[] codes = new int[_codes.size()];
        for(int i = 0; i < codes.length; i++) {
            codes[i] = _codes.get(i);
        }
        
        try {
            
            OutputStream out = new FileOutputStream(file);
            Codes.drawPagePNG(codes, width, height, out);
            out.close();
            
            
        } catch(IOException e) {
            return(error("Codes konnten nicht erzeugt werden", e));
        }
        return(ok());
    }

    
}
