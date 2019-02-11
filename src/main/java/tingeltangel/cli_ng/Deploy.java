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
import java.io.FilenameFilter;
import java.io.IOException;
import tingeltangel.core.Book;
import tingeltangel.core.TingStick;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author martin
 */
class Deploy extends CliCmd {

    @Override
    String getName() {
        return("deploy");
    }

    @Override
    String getDescription() {
        return("deploy");
    }

    @Override
    int execute(String[] args) {
        try {
            TingStick stick = TingStick.getStick();
            if(stick == null) {
                return(error("kein Stift gefunden"));
            }
            if(!CLI.bookOpened()) {
                return(error("kein Buch geöffnet"));
            }
            Book book = CLI.getBook();
            book.generateTTS(null);
            book.export(FileEnvironment.getDistDirectory(book.getID()), null);
            
            File dest = stick.getBookDir();
            if(!dest.getAbsolutePath().contains("$ting")) {
                dest = new File(stick.getBookDir(), "$ting");
            }
            File[] files = FileEnvironment.getDistDirectory(book.getID()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    name = name.toLowerCase();
                    return(name.endsWith(".ouf") || name.endsWith(".src") || name.endsWith(".png") || name.endsWith(".txt"));
                }
            });
            for(int i = 0; i < files.length; i++) {
                FileEnvironment.copy(files[i], new File(dest, files[i].getName()));
            }
            
        } catch(IOException e) {
            return(error("Buch konnte nicht deployed werden", e));
        } catch(SyntaxError e) {
            return(error("Buch konnte nicht deployed werden", e));
        }
        
        return(ok());
    }

    
}
