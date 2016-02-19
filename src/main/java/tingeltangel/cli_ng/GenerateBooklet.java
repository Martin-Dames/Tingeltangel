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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author martin
 */
class GenerateBooklet extends CliCmd {

    @Override
    public String getName() {
        return("generate-booklet");
    }

    @Override
    public String getDescription() {
        return("generate-booklet <ps file>");
    }

    @Override
    public int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern angegeben"));
        }
        File out = new File(args[0].trim());
        if(!out.canWrite()) {
            System.err.println("");
            return(error("Die Datei " + out.getAbsolutePath() + " kann nicht geschrieben werden"));
        }
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(out));
            CLI.getBook().generateTestBooklet(pw);
            pw.close();
        } catch(IOException e) {
            return(error("Die Datei " + out.getAbsolutePath() + " konnte nicht geschrieben werden", e));
        }
        return(ok());
    }

}
