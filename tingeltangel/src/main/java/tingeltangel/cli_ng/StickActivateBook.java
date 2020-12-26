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
import tingeltangel.core.TingStick;
import tingeltangel.core.Translator;

/**
 *
 * @author martin
 */
class StickActivateBook extends CliCmd {

    @Override
    String getName() {
        return("stick-activate-book");
    }

    @Override
    String getDescription() {
        return("stick-activate-book <mid>");
    }

    @Override
    int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }
        int mid;
        try {
            mid = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            return(error("MID keine Zahl zw. 1 und " + Integer.toString(Translator.MAX_MID)));
        }
        if(mid < 1 || mid > Translator.MAX_MID) {
            return(error("MID keine Zahl zw. 1 und " + Integer.toString(Translator.MAX_MID)));
        }
        try {
            TingStick stick = TingStick.getStick();
            if(stick == null) {
                return(error("kein Stift gefunden"));
            }
            stick.activateBook(mid);
        } catch(IOException e) {
            return(error("Buch konnte nicht aktiviert werden", e));
        }
        return(ok());
    }

    
}
