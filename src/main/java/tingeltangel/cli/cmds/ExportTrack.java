/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
*/
package tingeltangel.cli.cmds;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Book;
import tingeltangel.core.Importer;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class ExportTrack extends CliCommand {

    @Override
    public String getName() {
        return("export-track");
    }

    @Override
    public String getDescription() {
        return("exportiert einen einzelnen Track als MP3 oder Binärdatei");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        CliSwitch[] list = {
            new CliSwitch() {
                @Override
                public String getName() {
                    return("t");
                }

                @Override
                public String getLabel() {
                    return("OID");
                }

                @Override
                public String getDescription() {
                    return("Track ID (OID)");
                }

                @Override
                public boolean isOptional() {
                    return(false);
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    int id = Integer.parseInt(value);
                    return(id > 15000);
                }
            },            
            new CliSwitch() {
                @Override
                public String getName() {
                    return("b");
                }

                @Override
                public String getLabel() {
                    return("Book");
                }

                @Override
                public String getDescription() {
                    return("ouf-Datei");
                }

                @Override
                public boolean isOptional() {
                    return(false);
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    File f = new File(value);
                    return(f.canRead() && f.isFile());
                }
            },
            new CliSwitch() {
                @Override
                public String getName() {
                    return("o");
                }

                @Override
                public String getLabel() {
                    return("Ausgabedatei");
                }

                @Override
                public String getDescription() {
                    return("Ausgabedatei");
                }

                @Override
                public boolean isOptional() {
                    return(false);
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    File file = new File(value);
                    if(!file.getParentFile().isDirectory()) {
                        return(false);
                    }
                    if(file.exists() && !file.canWrite()) {
                        return(false);
                    }
                    return(true);
                }
            }
        };
        return(list2map(list));
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        File ouf = new File(args.get("b"));
        int oid = Integer.parseInt(args.get("t"));
        File file = new File(args.get("o"));
        
        Importer.extractTrack(ouf, oid, file);
        
    }
    
    /*
    public static void main(String[] args) throws IOException, SyntaxError {
        
        File ouf1 = new File("C:\\Users\\mdames\\Documents\\tingeltangel\\repository\\05100_en.ouf");
        File out1 = new File("C:\\Users\\mdames\\Desktop\\05100_15001_repository.bin");
        
        File ouf2 = new File("C:\\Users\\mdames\\Documents\\tingeltangel\\books\\05100\\dist\\05100_en.ouf");
        File out2 = new File("C:\\Users\\mdames\\Desktop\\05100_15001_dist.bin");
        
        Book book = new Book(5100);
        Book.loadXML(FileEnvironment.getXML(5100), book);
        book.export(ouf2.getParentFile(), null);
        
        Importer.extractTrack(ouf1, 15001, out1);
        Importer.extractTrack(ouf2, 15001, out2);
        
    }
    */
    
}
