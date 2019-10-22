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

import tingeltangel.core.Codes;

/**
 *
 * @author martin
 */
class SetCodeConfig extends CliCmd {

    final static String PATTERN = "pattern";
    final static String DOT = "dot";
    final static String DELTA = "delta";
    
    final static String DPI_600 = "600";
    final static String DPI_1200 = "1200";
    
    @Override
    String getName() {
        return("set-code-config");
    }

    @Override
    String getDescription() {
        return("set-code-config 600|1200 pattern|dot|delta <value>");
    }

    @Override
    int execute(String[] args) {
        
        if(args.length != 3) {
            return(error("falsche Anzahl von Parametern"));
        }
        
        try {
            args[0] = args[0].trim();
            args[1] = args[1].trim();

            int value = Integer.parseInt(args[2].trim());
            
            if(args[0].equals(DPI_600) && args[1].equals(PATTERN)) {
                Codes.setPatternSize600(value);
            } else if(args[0].equals(DPI_600) && args[1].equals(DOT)) {
                Codes.setDotSize600(value);
            } else if(args[0].equals(DPI_600) && args[1].equals(DELTA)) {
                Codes.setDeltaSize600(value);
            } else if(args[0].equals(DPI_1200) && args[1].equals(PATTERN)) {
                Codes.setPatternSize1200(value);
            } else if(args[0].equals(DPI_1200) && args[1].equals(DOT)) {
                Codes.setDotSize1200(value);
            } else if(args[0].equals(DPI_1200) && args[1].equals(DELTA)) {
                Codes.setDeltaSize1200(value);
            } else {
                return(error("unbekante Parameterkombination: " + args[0] + " " + args[1]));
            }
            Codes.saveProperties();
        } catch(NumberFormatException e) {
            return(error("keine Zahl als dritten Parameter angegeben"));
        }
        return(ok());
    }

    
}
