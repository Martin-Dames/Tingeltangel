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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import tingeltangel.core.Book;
import tingeltangel.core.Codes;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
class GenerateCodes extends CliCmd {
    
    
    @Override
    public String getName() {
        return("generate-codes");
    }

    @Override
    public String getDescription() {
        return("generate-codes 600|1200 <size in mm> <zip file>|<directory>");
    }

    @Override
    public int execute(String[] args) {
        
        if(args.length != 3) {
            return(error("falsche Anzahl von Parametern angegeben"));
        }
        File file = new File(args[2].trim());
        
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
            return(error("ungültige Große angegeben (5-100)"));
        }
        if((size < 5) || (size > 100)) {
            return(error("ungültige Große angegeben (5-100)"));
        }
        
        Book book = CLI.getBook();
        
        try {
            
            File[] entries = null;
            
            book.pngExport(FileEnvironment.getCodesDirectory(book.getID()), size, null);
            entries = FileEnvironment.getCodesDirectory(book.getID()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return(name.toLowerCase().endsWith(".png"));
                }
            });
            
            if(file.isDirectory()) {
                
                for(int i = 0; i < entries.length; i++) {
                    FileEnvironment.copy(entries[i], new File(file, entries[i].getName()));
                }
                
            } else {

                FileOutputStream fos = new FileOutputStream(file);
                ZipOutputStream out = new ZipOutputStream(fos);

                byte[] buffer = new byte[4096];
                for(int i = 0; i < entries.length; i++) {

                    FileInputStream in = new FileInputStream(entries[i]);
                    ZipEntry zipEntry = new ZipEntry(entries[i].getName());
                    out.putNextEntry(zipEntry);

                    int length;
                    while((length = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, length);
                    }

                    out.closeEntry();
                    in.close();

                }
                out.close();
                fos.close();
            }
        } catch(IOException e) {
            return(error("Codes konnten nicht erzeugt werden", e));
        }
        return(ok());
    }

    
}
