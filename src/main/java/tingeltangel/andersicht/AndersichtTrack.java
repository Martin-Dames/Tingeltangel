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
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class AndersichtTrack implements TreeNode {
    
    private String internalFileName = null;
    private String transcript = "Transkript";
    private final AndersichtObject object;
    private final AndersichtDescriptionLayer descriptionLayer;
    
    AndersichtTrack(AndersichtObject object, AndersichtDescriptionLayer descriptionLayer) {
        this.descriptionLayer = descriptionLayer;
        this.object = object;
    }
    
    @Override
    public String toString() {
        String _t = transcript;
        if(_t.length() > 20) {
            _t = _t.substring(0, 17) + "...";
        }
        return(descriptionLayer + ": " + _t);
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

    @Override
    public TreeNode getChildAt(int childIndex) {
        return(null);
    }

    @Override
    public int getChildCount() {
        return(0);
    }

    @Override
    public TreeNode getParent() {
        return(object);
    }

    @Override
    public int getIndex(TreeNode node) {
       return(-1);
    }

    @Override
    public boolean getAllowsChildren() {
        return(false);
    }

    @Override
    public boolean isLeaf() {
        return(true);
    }

    @Override
    public Enumeration children() {
        return(new Enumeration() {
            @Override
            public boolean hasMoreElements() {
                return(false);
            }

            @Override
            public Object nextElement() {
                return(null);
            }
        });
    }
}
