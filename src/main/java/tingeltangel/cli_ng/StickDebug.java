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

import java.io.IOException;
import java.util.HashMap;
import tingeltangel.core.TingStick;

/**
 *
 * @author martin
 */
class StickDebug extends CliCmd {

    @Override
    String getName() {
        return("stick-debug");
    }

    @Override
    String getDescription() {
        return("stick-debug on|off|state");
    }

    @Override
    int execute(String[] args) {
        if(args.length != 1) {
            return(error("Falsche Anzahl von Parametern"));
        }
        
        TingStick stick = null;
        try {
            stick = TingStick.getStick();
            if(stick == null) {
                return(error("Kein Stift gefunden"));
            }
        } catch(IOException e) {
            return(error("Fehler beim erkennen des Stifts", e));
        }
        
        if(args[0].toLowerCase().trim().equals("state")) {
            try {
                HashMap<String, String> settings = stick.getSettings();
                String debug = settings.get("testpen").toLowerCase().trim();
                if(debug.equals("yes")) {
                    System.out.println("debug modus ist aktiv");
                } else if(debug.equals("no")) {
                    System.out.println("debug modus ist inaktiv");
                } else {
                    return(error("Debugmodus ermittelt werden"));
                }
            } catch(IOException e) {
                return(error("Debugmodus ermittelt werden", e));
            }
        } else {
        
            String debug = null;
            if(args[0].toLowerCase().trim().equals("on")) {
                debug = "yes";
            } else if(args[0].toLowerCase().trim().equals("off")) {
                debug = "no";
            } else {
                return(error("Syntax Fehler"));
            }
            try {
                HashMap<String, String> settings = stick.getSettings();
                settings.put("testpen", debug);
                stick.setSettings(settings);
            } catch(IOException e) {
                return(error("Debugmodus konnte nicht eingestellt werden", e));
            }
        }
        return(ok());
    }

}
