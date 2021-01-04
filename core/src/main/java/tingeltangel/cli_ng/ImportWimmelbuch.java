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
import tingeltangel.wimmelbuch.Wimmelbuch;

/**
 *
 * @author martin
 */
class ImportWimmelbuch extends CliCmd {

    @Override
    public String getName() {
        return("import-wimmelbuch");
    }

    @Override
    public String getDescription() {
        return("import-wimmelbuch <wimmelbuch file>");
    }

    @Override
    public int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }
        try {
            new Wimmelbuch().importBook(CLI.getBook(), new File(args[0]));
        } catch(Exception e) {
            return(error("Das Buch konnte nicht importiert werden: " + e.getMessage(), e));
        }
        return(ok());
    }

}
