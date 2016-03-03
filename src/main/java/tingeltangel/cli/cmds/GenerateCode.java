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
import java.util.HashMap;
import java.util.Map;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Codes;
import tingeltangel.core.Translator;

/**
 *
 * @author mdames
 */
public class GenerateCode extends CliCommand {

    @Override
    public String getName() {
        return("generate-code");
    }

    @Override
    public String getDescription() {
        return("Erzeugt ein png von der gegebenen Ting-ID");
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
                    return("setzt den Namen der Ausgabedatei (default: <Ting-ID>.png)");
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
                    return("t");
                }

                @Override
                public String getDescription() {
                    return("die Ting-ID");
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
                    return("Ting-ID");
                }

                @Override
                public String getDefault() {
                    return(null);
                }

                @Override
                public boolean acceptValue(String value) {
                    try {
                        int v = Integer.parseInt(value);
                        if((v < 0) || (v > 0xffff)) {
                            return(false);
                        }
                        return(Translator.ting2code(v) >= 0);
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
            args.put("o", args.get("t") + ".png");
        }
        
        String tingID = args.get("t");
        String resolution = args.get("r");
        String outputFile = args.get("o");
        
        int codeID = Translator.ting2code(Integer.parseInt(tingID));
        
        if("600".equals(resolution)) {
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
