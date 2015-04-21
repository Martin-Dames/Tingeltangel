/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class CliGenerateCode extends CliCommand {

    @Override
    public String getName() {
        return("generate-code");
    }

    @Override
    public String getDescription() {
        return("Erzeugt ein png oder eps von der gegebenen Ting-ID");
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
                    return("f");
                }

                @Override
                public String getDescription() {
                    return("setzt das Ausgabedateiformat. png oder eps.");
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
                    return("Dateiformat");
                }

                @Override
                public String getDefault() {
                    return("png");
                }

                @Override
                public boolean acceptValue(String value) {
                    return(value.equals("png") || value.equals("eps"));
                }
            },
            new CliSwitch() {
                @Override
                public String getName() {
                    return("o");
                }

                @Override
                public String getDescription() {
                    return("setzt den Namen der Ausgabedatei (default: <Ting-ID>_<Auflösung>.<Dateiformat>)");
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
        
        Map<String, CliSwitch> switches = new HashMap<String, CliSwitch>();
        for(int i = 0; i < list.length; i++) {
            switches.put(list[i].getName(), list[i]);
        }
        return(switches);
        
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        if(!args.containsKey("o")) {
            args.put("o", args.get("t") + "_" + args.get("r") + "." + args.get("f"));
        }
        
        String fileFormat = args.get("f");
        String tingID = args.get("t");
        String resolution = args.get("r");
        String outputFile = args.get("o");
        
        int codeID = Translator.ting2code(Integer.parseInt(tingID));
        
        if(resolution.equals("600")) {
            Codes.setResolution(Codes.DPI600);
        } else {
            Codes.setResolution(Codes.DPI1200);
        }
    
        int width = Integer.parseInt(args.get("w"));
        int height = Integer.parseInt(args.get("h"));
        
        if(fileFormat.equals("png")) {
            OutputStream out = new FileOutputStream(outputFile);
            Codes.drawPng(codeID, width, height, out);
            out.close();
        } else {
            PrintWriter out = new PrintWriter(new FileWriter(outputFile));
            Codes.drawEps(codeID, width, height, out);
            out.close();
        }
    }
    
}
