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

import tingeltangel.core.Translator;

/**
 *
 * @author martin
 */
class CodeRawToTing extends CliCmd {

    @Override
    String getName() {
        return("code-raw-to-ting");
    }

    @Override
    String getDescription() {
        return("code-raw-to-ting <raw id>");
    }

    @Override
    int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }
        int rid;
        try {
            rid = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            return(error("keine Zahl zw. 0 und 65535"));
        }
        if(rid < 0 || rid > 0xffff) {
            return(error("keine Zahl zw. 0 und 65535"));
        }
        int tid = Translator.code2ting(rid);
        if(tid == -1) {
            return(error("Ting-ID für diese Code-ID nicht bekannt"));
        }
        System.out.println(Integer.toString(tid));
        return(ok());
    }

}
