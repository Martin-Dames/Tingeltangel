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

import java.util.Iterator;
import tingeltangel.tools.TTS;

/**
 *
 * @author martin
 */
class TtsGetVoices extends CliCmd {

    @Override
    String getName() {
        return("tts-get-voices");
    }

    @Override
    String getDescription() {
        return("tts-get-voices");
    }

    @Override
    int execute(String[] args) {
        Iterator<String> voices = TTS.getVoiceIDs().iterator();
        while(voices.hasNext()) {
            String voice = voices.next();
            String gender = "unbekannt";
            if(TTS.getVoiceGender(voice) == TTS.FEMALE) {
                gender = "weiblich";
            } else if(TTS.getVoiceGender(voice) == TTS.MALE) {
                gender = "männlich";
            }
            System.out.println(voice + ": " + TTS.getVoiceName(voice) + " (" + gender + ")");
        }
        return(ok());
    }

}
