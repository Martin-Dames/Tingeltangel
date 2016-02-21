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

import tingeltangel.core.Properties;
import static tingeltangel.gui.TTSPreferences.PROPERTY_DEFAULT_VOICE;
import tingeltangel.tools.TTS;

/**
 *
 * @author martin
 */
class TtsSetVoice extends CliCmd {

    @Override
    String getName() {
        return("tts-set-voice");
    }

    @Override
    String getDescription() {
        return("tts-set-voice <voice>");
    }

    @Override
    int execute(String[] args) {
        if(args.length != 1) {
            return(error("falsche Anzahl von Parametern"));
        }
        if(!TTS.getVoiceIDs().contains(args[0])) {
            return(error("unbekannte stimme"));
        }
        Properties.setProperty(PROPERTY_DEFAULT_VOICE, args[0]);
        return(ok());
    }

}
