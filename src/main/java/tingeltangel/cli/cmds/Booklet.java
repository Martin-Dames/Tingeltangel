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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Book;
import tingeltangel.tools.FileEnvironment;

public class Booklet extends CliCommand {

    private Book book = null;
    private File file = null;
    
    @Override
    public String getName() {
        return("booklet");
    }

    @Override
    public String getDescription() {
        return("generiert ein Booklet (ps) aus einem Buch");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        CliSwitch[] list = {
            new CliSwitch() {
                @Override
                public String getName() {
                    return("b");
                }

                @Override
                public String getLabel() {
                    return("MID");
                }

                @Override
                public String getDescription() {
                    return("Buch ID (MID)");
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
                    try {
                        int id = Integer.parseInt(value);
                        if((id < 0) || (id >= 15000)) {
                            return(false);
                        }
                        book = new Book(id);
                        Book.loadXML(FileEnvironment.getXML(id), book);
                    } catch (IOException ex) {
                        return(false);
                    } catch(NumberFormatException e) {
                        return(false);
                    }
                    return(true);
                }
            },
            new CliSwitch() {
                @Override
                public String getName() {
                    return("o");
                }

                @Override
                public String getLabel() {
                    return("PS Ausgabe");
                }

                @Override
                public String getDescription() {
                    return("PS Ausgabedatei");
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
                    file = new File(value);
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
        PrintWriter out = new PrintWriter(new FileWriter(file));
        book.generateTestBooklet(out);
        out.close();
    }
    
}
