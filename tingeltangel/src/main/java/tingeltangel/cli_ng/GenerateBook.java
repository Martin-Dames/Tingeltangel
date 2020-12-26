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
import javax.swing.JOptionPane;
import tingeltangel.core.Book;
import tingeltangel.gui.EditorFrame;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.Progress;
import tingeltangel.tools.ProgressDialog;

/**
 *
 * @author martin
 */
class GenerateBook extends CliCmd {

    @Override
    public String getName() {
        return("generate-book");
    }

    @Override
    public String getDescription() {
        return("generate-book <zip file>|<directory>");
    }

    @Override
    public int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern angegeben"));
        }
        File file = new File(args[0].trim());
        if(!file.canWrite()) {
            return(error("Die Datei oder das Verzeichnis " + file.getAbsolutePath() + " kann nicht geschrieben werden"));
        }
        
        try {
            Book book = CLI.getBook();
            book.generateTTS(null);
            book.export(FileEnvironment.getDistDirectory(book.getID()), null);

            File[] entries = FileEnvironment.getDistDirectory(book.getID()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return(
                            name.toLowerCase().endsWith(".ouf") ||
                            name.toLowerCase().endsWith(".png") ||
                            name.toLowerCase().endsWith(".txt") ||
                            name.toLowerCase().endsWith(".src")
                    );
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
        } catch(Exception e) {
            return(error("das Buch konnte nicht generiert werden", e));
        }
        return(ok());
    }
}
