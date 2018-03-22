/*
 * Copyright 2018 mdames.
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
package tingeltangel.andersicht;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class AndersichtTrack {
    
    private String internalFileName = null;
    private String transcript = "Transkript";
    private final AndersichtObject object;
    
    AndersichtTrack(AndersichtObject object) {
        this.object = object;
    }
    
    public String getTranscript() {
        return(transcript);
    }
    
    public void setTranscript(String transcript) {
        this.transcript = transcript;
        object.getGroup().getBook().changeMade();
    }

    void load(DataInputStream in) throws IOException {
        internalFileName = in.readUTF();
        transcript = in.readUTF();
    }
    
    void save(DataOutputStream out) throws IOException {
        out.writeUTF(internalFileName);
        out.writeUTF(transcript);
    }
    
    private AndersichtBook getBook() {
        return(object.getGroup().getBook());
    }
    
    public void setMP3(File file) throws IOException {
        File f = FileEnvironment.getFreeFileName(FileEnvironment.getAndersichtBookDirectory(getBook().getName()), ".mp3");
        FileEnvironment.copy(file, f);
        internalFileName = f.getName();
        object.getGroup().getBook().changeMade();
    }
    
    public File getInternalMP3() {
        return(new File(FileEnvironment.getAndersichtBookDirectory(getBook().getName()), internalFileName));
    }
}
