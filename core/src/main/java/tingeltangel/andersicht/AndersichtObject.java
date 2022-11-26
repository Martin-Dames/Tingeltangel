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
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.tree.TreeNode;

/**
 *
 * @author mdames
 */
public class AndersichtObject implements TreeNode {


    private String name = "Objektname";
    private String description = "Objektbeschreibung";
    private final AndersichtGroup group;
    
    private final HashMap<AndersichtDescriptionLayer, Integer> labels = new HashMap<AndersichtDescriptionLayer, Integer>();
    
    Map<AndersichtLanguageLayer, Map<AndersichtDescriptionLayer, AndersichtTrack>> trackMap
                = new HashMap<AndersichtLanguageLayer, Map<AndersichtDescriptionLayer, AndersichtTrack>>();

    AndersichtObject(String name, String description, AndersichtGroup group) {
        this.name = name;
        this.description = description;
        this.group = group;
    }
    
    @Override
    public String toString() {
        return(name);
    }
    
    public int getLabelAsInt(AndersichtDescriptionLayer dLayer) {
        if(!labels.containsKey(dLayer)) {
            labels.put(dLayer, -1);
        }
        return(labels.get(dLayer));
    }
    
    private AndersichtBook getBook() {
        return(group.getBook());
    }
    
    public String getLabelAsString(AndersichtDescriptionLayer dLayer) {
        return(getBook().getPen().fromTingId(getLabelAsInt(dLayer)));
    }
    
    public void setLabel(AndersichtDescriptionLayer dLayer, int label) {
        int oldLabel = getLabelAsInt(dLayer);
        if(oldLabel != label) {
            labels.put(dLayer, label);
            getBook().changeMade();
        }
    }
    
    public void setName(String name) {
        if(!this.name.equals(name)) {
            this.name = name;
            group.getBook().changeMade();
        }
    }
    
    public void setDescription(String description) {
        if(!this.description.equals(description)) {
            this.description = description;
            group.getBook().changeMade();
        }
    }
    
    public String getName() {
        return(name);
    }
    
    public String getDescription() {
        return(description);
    }
    
    public AndersichtGroup getGroup() {
        return(group);
    }
    
    public AndersichtTrack getTrack(AndersichtLanguageLayer languageLayer, AndersichtDescriptionLayer descriptionLayer) {
        Map<AndersichtDescriptionLayer, AndersichtTrack> map = trackMap.get(languageLayer);
        if(map == null) {
            map = new HashMap<AndersichtDescriptionLayer, AndersichtTrack>();
            trackMap.put(languageLayer, map);
        }
        AndersichtTrack track = map.get(descriptionLayer);
        if(track == null) {
            track = new AndersichtTrack(this, descriptionLayer);
            map.put(descriptionLayer, track);
            group.getBook().changeMade();
        }
        return(track);
    }
    
    public void removeTrack(AndersichtLanguageLayer languageLayer, AndersichtDescriptionLayer descriptionLayer) {
        Map<AndersichtDescriptionLayer, AndersichtTrack> map = trackMap.get(languageLayer);
        if(map != null) {
            AndersichtTrack track = map.get(descriptionLayer);
            if(track != null) {
                map.remove(descriptionLayer);
                group.getBook().changeMade();
                if(map.isEmpty()) {
                    trackMap.remove(languageLayer);
                }
            }
        }
    }
    
    public boolean usesLanguageLayer(AndersichtLanguageLayer layer) {
        Iterator<AndersichtLanguageLayer> i = trackMap.keySet().iterator();
        while(i.hasNext()) {
            if(i.next() == layer) {
                return(true);
            }
        }
        return(false);
    }
    
    public boolean usesDescriptionLayer(AndersichtDescriptionLayer layer) {
        Iterator<AndersichtLanguageLayer> i1 = trackMap.keySet().iterator();
        while(i1.hasNext()) {
            Iterator<AndersichtDescriptionLayer> i2 = trackMap.get(i1.next()).keySet().iterator();
            while(i2.hasNext()) {
                if(i2.next() == layer) {
                    return(true);
                }
            }
        }
        return(false);
    }
    
    
    static AndersichtObject load(AndersichtGroup group, DataInputStream in) throws IOException {
        String name = in.readUTF();
        String description = in.readUTF();
        
        AndersichtObject object = new AndersichtObject(name, description, group);
        
        int s = in.readInt();
        for(int i = 0; i < s; i++) {
            int dId = in.readInt();
            int label = in.readInt();
            AndersichtDescriptionLayer dLayer = group.getBook().getDescriptionLayerById(dId);
            object.labels.put(dLayer, label);
        }
        
        int size = in.readInt();
        for(int i = 0; i < size; i++) {
            int descriptionId = in.readInt();
            int languageId = in.readInt();
            AndersichtDescriptionLayer dLayer = group.getBook().getDescriptionLayerById(descriptionId);
            AndersichtLanguageLayer lLayer = group.getBook().getLanguageLayerById(languageId);
            AndersichtTrack track = object.getTrack(lLayer, dLayer);
            track.load(in, group.getBook().getBookId());
        }
        return(object);
    }
    
    void save(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(description);
        
        AndersichtBook book = getGroup().getBook();
        
        out.writeInt(book.getDescriptionLayerCount());
        for(int dl = 0; dl < book.getDescriptionLayerCount(); dl++) {
            AndersichtDescriptionLayer descriptionLayer = book.getDescriptionLayer(dl);
            out.writeInt(descriptionLayer.getId());
            out.writeInt(getLabelAsInt(descriptionLayer));
        }
        
        List<SaveObject> list = new LinkedList<SaveObject>();
        for(int ll = 0; ll < book.getLanguageLayerCount(); ll++) {
            AndersichtLanguageLayer languageLayer = book.getLanguageLayer(ll);
            for(int dl = 0; dl < book.getDescriptionLayerCount(); dl++) {
                AndersichtDescriptionLayer descriptionLayer = book.getDescriptionLayer(dl);
                SaveObject so = new SaveObject();
                so.descriptionId = descriptionLayer.getId();
                so.languageId = languageLayer.getId();
                so.track = getTrack(languageLayer, descriptionLayer);
                list.add(so);
            }
        }
        out.writeInt(list.size());
        Iterator<SaveObject> i = list.iterator();
        while(i.hasNext()) {
            i.next().save(out);
        }
        
    }

    public int getDescriptionTrackIndex(AndersichtLanguageLayer currentLanguageLayer, AndersichtTrack track) {
        AndersichtBook book = group.getBook();
        for(int i = 0; i < book.getDescriptionLayerCount(); i++) {
            if(getTrack(currentLanguageLayer, book.getDescriptionLayer(i)) == track) {
                return(i);
            }
        }
        return(-1);
    }


    
    
    @Override
    public TreeNode getChildAt(int childIndex) {
         AndersichtLanguageLayer lLayer = group.getBook().getActiveLanguageLayer();
         AndersichtDescriptionLayer dLayer = group.getBook().getDescriptionLayer(childIndex);
         return(getTrack(lLayer, dLayer));
    }

    @Override
    public int getChildCount() {
        return(group.getBook().getDescriptionLayerCount());
    }

    @Override
    public TreeNode getParent() {
        return(group);
    }

    @Override
    public int getIndex(TreeNode node) {
        if(!(node instanceof AndersichtTrack)) return(-1);
        AndersichtLanguageLayer lLayer = group.getBook().getActiveLanguageLayer();
        return(getDescriptionTrackIndex(lLayer, (AndersichtTrack)node));
    }

    @Override
    public boolean getAllowsChildren() {
        return(true);
    }

    @Override
    public boolean isLeaf() {
        return(false);
    }

    @Override
    public Enumeration children() {
        return(new Enumeration() {
            int pos = -1;
            
            @Override
            public boolean hasMoreElements() {
                return(pos < group.getBook().getDescriptionLayerCount());
            }

            @Override
            public Object nextElement() {
                pos++;
                return(getChildAt(pos));
            }
        });
    }

    void hasAllTracks() throws IllegalArgumentException {
        AndersichtBook book = getGroup().getBook();
        for(int ll = 0; ll < book.getLanguageLayerCount(); ll++) {
            AndersichtLanguageLayer languageLayer = book.getLanguageLayer(ll);
            for(int dl = 0; dl < book.getDescriptionLayerCount(); dl++) {
                AndersichtDescriptionLayer descriptionLayer = book.getDescriptionLayer(dl);
                AndersichtTrack track = getTrack(languageLayer, descriptionLayer);
                if((track.getInternalMP3() == null) || !track.getInternalMP3().canRead()) {
                    throw new IllegalArgumentException("Kann das MP3 für Objekt '" + toString() + "' (Sprache: '" + languageLayer.toString() +
                                "', Beschreibung: '" + descriptionLayer.toString() + "') nicht finden.");
                }
            }
        }
    }

    public AndersichtDescriptionLayer getDescriptionLayer(AndersichtTrack track) {
        AndersichtBook book = getGroup().getBook();
        for(int ll = 0; ll < book.getLanguageLayerCount(); ll++) {
            AndersichtLanguageLayer languageLayer = book.getLanguageLayer(ll);
            for(int dl = 0; dl < book.getDescriptionLayerCount(); dl++) {
                AndersichtDescriptionLayer descriptionLayer = book.getDescriptionLayer(dl);
                AndersichtTrack _track = getTrack(languageLayer, descriptionLayer);
                if(_track == track) {
                    return(descriptionLayer);
                }
            }
        }
        return(null);
    }
    
}

class SaveObject {
    int languageId;
    int descriptionId;
    AndersichtTrack track;
    
    void save(DataOutputStream out) throws IOException {
        out.writeInt(descriptionId);
        out.writeInt(languageId);
        track.save(out);
    }
}
