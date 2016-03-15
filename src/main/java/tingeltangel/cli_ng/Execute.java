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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author martin
 */
class Execute extends CliCmd {

    @Override
    public String getName() {
        return("execute");
    }

    @Override
    public String getDescription() {
        return("execute <ttcli file>");
    }

    @Override
    public int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }

        File ttcli = new File(args[0]);
        if(!ttcli.canRead()) {
            return(error("die angebebene Datei ist nicht lesbar"));
        }
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(ttcli));
            String row;
            while((row = in.readLine()) != null) {
                CLI.exec(row);
            }
            in.close();
        } catch(IOException ioe) {
            return(error("die datei konnte nicht ausgeführt werden", ioe));
        }
        
        return(ok());
    }
}
