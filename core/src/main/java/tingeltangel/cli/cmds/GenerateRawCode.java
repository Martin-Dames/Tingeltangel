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

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Codes;

/**
 *
 * @author mdames
 */
public class GenerateRawCode extends CliCommand {

    @Override
    public String getName() {
        return("generate-raw-code");
    }

    @Override
    public String getDescription() {
        return("Erzeugt ein png oder eps von der gegebenen Code-ID");
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        
        CliSwitch[] list = {
            new CliSwitch() {
                @Override
                public String getName() {
                    return("r");
                }

                @Override
                public String getDescription() {
                    return("setzt die Ausgabeauflösung. 600 für 600dpi oder 1200 für 1200dpi.");
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public boolean isOptional() {
                    return(true);
                }

                @Override
                public String getLabel() {
                    return("Auflösung");
                }

                @Override
                public String getDefault() {
                    return("1200");
                }

                @Override
                public boolean acceptValue(String value) {
                    return(value.equals("1200") || value.equals("600"));
                }
            },
            new CliSwitch() {
                @Override
                public String getName() {
                    return("o");
                }

                @Override
                public String getDescription() {
                    return("setzt den Namen der Ausgabedatei (default: <Code-ID>_raw.png)");
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public boolean isOptional() {
                    return(true);
                }

                @Override
                public String getLabel() {
                    return("Ausgabedatei");
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    return(!value.isEmpty());
                }
            },
            new CliSwitch() {

                @Override
                public String getName() {
                    return("c");
                }

                @Override
                public String getDescription() {
                    return("die Code-ID");
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
                public String getLabel() {
                    return("Code-ID");
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    try {
                        int v = Integer.parseInt(value);
                        return((v >= 0) || (v <= 0xffff));
                    } catch(NumberFormatException nfe) {
                        return(false);
                    }
                }
            },
            new CliSwitch() {

                @Override
                public String getName() {
                    return("w");
                }

                @Override
                public String getDescription() {
                    return("Breite im mm");
                }

                @Override
                public boolean isOptional() {
                    return(true);
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public String getLabel() {
                    return("Breite");
                }

                @Override
                public String getDefault() {
                    return("100");
                }

                @Override
                public boolean acceptValue(String value) {
                    try {
                        int v = Integer.parseInt(value);
                        if((v < 10) || (v > 500)) {
                            return(false);
                        }
                        return(true);
                    } catch(NumberFormatException nfe) {
                        return(false);
                    }
                }
            },
            new CliSwitch() {

                @Override
                public String getName() {
                    return("h");
                }

                @Override
                public String getDescription() {
                    return("Höhe im mm");
                }

                @Override
                public boolean isOptional() {
                    return(true);
                }

                @Override
                public boolean hasArgument() {
                    return(true);
                }

                @Override
                public String getLabel() {
                    return("Höhe");
                }

                @Override
                public String getDefault() {
                    return("100");
                }

                @Override
                public boolean acceptValue(String value) {
                    try {
                        int v = Integer.parseInt(value);
                        if((v < 10) || (v > 500)) {
                            return(false);
                        }
                        return(true);
                    } catch(NumberFormatException nfe) {
                        return(false);
                    }
                }
            }
        };
        
        return list2map(list);
        
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        if(!args.containsKey("o")) {
            args.put("o", args.get("t") + "_raw.png");
        }
        
        String _codeID = args.get("t");
        String resolution = args.get("r");
        String outputFile = args.get("o");
        
        int codeID = Integer.parseInt(_codeID);
        
        if(resolution.equals("600")) {
            Codes.setResolution(Codes.DPI600);
        } else {
            Codes.setResolution(Codes.DPI1200);
        }
    
        int width = Integer.parseInt(args.get("w"));
        int height = Integer.parseInt(args.get("h"));
        
        OutputStream out = new FileOutputStream(outputFile);
        Codes.drawPng(codeID, width, height, out);
        out.close();
        
    }
    
}
