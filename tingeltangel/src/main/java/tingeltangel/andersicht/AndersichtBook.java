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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.TreeNode;
import tingeltangel.andersicht.gui.AndersichtBookDefinition;
import tingeltangel.andersicht.pen.Pen;
import tingeltangel.andersicht.pen.Pens;
import tingeltangel.andersicht.pen.Ting;
import tingeltangel.core.Translator;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class AndersichtBook implements TreeNode {

    private String description = "Beschreibung";
    private String name;
    private int bookId = 2303;
    private final List<AndersichtLanguageLayer> languageLayers = new LinkedList<AndersichtLanguageLayer>();
    private final List<AndersichtDescriptionLayer> descriptionLayers = new LinkedList<AndersichtDescriptionLayer>();
    
    private final List<AndersichtGroup> groups = new LinkedList<AndersichtGroup>();
 
    private boolean unsaved = true;
    
    private AndersichtLanguageLayer activeLanguageLayer;
    private final Pen pen;
    
    private AndersichtBook(int bookId, String name, String description, Pen pen) {
        this.name = name;
        this.description = description;
        this.pen = pen;
        this.bookId = bookId;
    }
    
    public void setActiveLanguageLayer(AndersichtLanguageLayer lLayer) {
        activeLanguageLayer = lLayer;
    }
    
    public AndersichtLanguageLayer getActiveLanguageLayer() {
        return(activeLanguageLayer);
    }
    
    @Override
    public String toString() {
        return(name);
    }
    
    public boolean unsaved() {
        return(unsaved);
    }
    
    void changeMade() {
        unsaved = true;
    }
    
    public void changeMade(boolean x) {
        unsaved = x;
    }
    
    public static AndersichtBook newBook(AndersichtBookDefinition def) {
        AndersichtBook book = new AndersichtBook(def.bookId, def.name, def.description, def.pen);
        return(book);
    }
    
    public int getBookId() {
        return(bookId);
    }
    
    public void setBookId(int id) throws IllegalArgumentException {
        if((id < 1) || (id > 9999)) {
            throw new IllegalArgumentException();
        }
        bookId = id;
        changeMade();
    }
    
    public String getName() {
        return(name);
    }
    
    public String getDescription() {
        return(description);
    }
    
    public void setDescription(String description) {
        if(!this.description.equals(description)) {
            this.description = description;
            changeMade();
        }
    }  
    
    public void setName(String name) {
        if(!this.name.equals(name)) {
            this.name = name;
            changeMade();
        }
    }    
    
    public AndersichtGroup addGroup(String name, String description) {
        AndersichtGroup group = new AndersichtGroup(name, description, this);
        groups.add(group);
        changeMade();
        return(group);
    }
    
    public void removeGroup(AndersichtGroup group) {
        groups.remove(group);
        changeMade();
    }
    
    public int getGroupCount() {
        return(groups.size());
    }
    
    public AndersichtGroup getGroup(int i) {
        return(groups.get(i));
    }
    
    public AndersichtLanguageLayer addLanguageLayer(String name, String description) {
        AndersichtLanguageLayer layer = new AndersichtLanguageLayer(name, description, this);
        if(!languageLayers.isEmpty()) {
            layer.setId(languageLayers.get(languageLayers.size() - 1).getId() + 1);
        }
        languageLayers.add(layer);
        changeMade();
        return(layer);
    }
    
    /*
        returns object if layer is used somewhere, else return null 
    */
    public AndersichtObject removeLanguageLayer(AndersichtLanguageLayer layer) {
        // crawl if layer ist still used
        Iterator<AndersichtGroup> groupIterator = groups.iterator();
        while(groupIterator.hasNext()) {
            AndersichtGroup group = groupIterator.next();
            for(int g = 0; g < group.getObjectCount(); g++) {
                AndersichtObject object = group.getObject(g);
                if(object.usesLanguageLayer(layer)) {
                    return(object);
                }
            }
        }
        // remove layer
        changeMade();
        languageLayers.remove(layer);
        return(null);
    }
    
    
    public int getLanguageLayerCount() {
        return(languageLayers.size());
    }
    
    public AndersichtLanguageLayer getLanguageLayer(int i) {
        return(languageLayers.get(i));
    }
    
    public AndersichtDescriptionLayer addDescriptionLayer(String name, String description) {
        AndersichtDescriptionLayer layer = new AndersichtDescriptionLayer(name, description, this);
        if(!descriptionLayers.isEmpty()) {
            layer.setId(descriptionLayers.get(descriptionLayers.size() - 1).getId() + 1);
        }
        descriptionLayers.add(layer);
        changeMade();
        return(layer);
    }
    
    
    /*
        returns object if layer is used somewhere, else return null 
    */
    public AndersichtObject removeDescriptionLayer(AndersichtDescriptionLayer layer) {
        // crawl if layer ist still used
        Iterator<AndersichtGroup> groupIterator = groups.iterator();
        while(groupIterator.hasNext()) {
            AndersichtGroup group = groupIterator.next();
            for(int g = 0; g < group.getObjectCount(); g++) {
                AndersichtObject object = group.getObject(g);
                if(object.usesDescriptionLayer(layer)) {
                    return(object);
                }
            }
        }
        // remove layer
        changeMade();
        descriptionLayers.remove(layer);
        return(null);
    }

    public int getDescriptionLayerCount() {
        return(descriptionLayers.size());
    }
    
    public AndersichtDescriptionLayer getDescriptionLayer(int i) {
        return(descriptionLayers.get(i));
    }
    
    public static AndersichtBook load(int bookId) throws IOException {
        File file = FileEnvironment.getAndersichtBookFile(Integer.toString(bookId));
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        
        String name = in.readUTF();
        String description = in.readUTF();
        int _bookId = in.readInt();
        
        if(_bookId != bookId) {
            throw new IOException("bad book id found");
        }
        
        String penName = in.readUTF();
        
        AndersichtBook book = new AndersichtBook(bookId, name, description, Pens.getPen(penName));

        
        int size = in.readInt();
        for(int i = 0; i < size; i++) {
            book.languageLayers.add(AndersichtLanguageLayer.load(book, in));
        }
        size = in.readInt();
        for(int i = 0; i < size; i++) {
            book.descriptionLayers.add(AndersichtDescriptionLayer.load(book, in));
        }
        size = in.readInt();
        for(int i = 0; i < size; i++) {
            book.groups.add(AndersichtGroup.load(book, in));
        }
        
        in.close();
        
        book.unsaved = false;
        return(book);
    }
    
    
    public void save() throws IOException {
        File file = FileEnvironment.getAndersichtBookFile(Integer.toString(bookId));
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.writeUTF(name);
        out.writeUTF(description);
        out.writeInt(bookId);
        out.writeUTF(pen.toString());
        out.writeInt(languageLayers.size());
        for(int i = 0; i < languageLayers.size(); i++) {
            languageLayers.get(i).save(out);
        }
        out.writeInt(descriptionLayers.size());
        for(int i = 0; i < descriptionLayers.size(); i++) {
            descriptionLayers.get(i).save(out);
        }
        out.writeInt(groups.size());
        for(int i = 0; i < groups.size(); i++) {
            groups.get(i).save(out);
        }
        
        unsaved = false;
        out.close();
    }

    AndersichtDescriptionLayer getDescriptionLayerById(int descriptionId) throws IOException {
        Iterator<AndersichtDescriptionLayer> i = descriptionLayers.iterator();
        while(i.hasNext()) {
            AndersichtDescriptionLayer layer = i.next();
            if(layer.getId() == descriptionId) {
                return(layer);
            }
        }
        throw new IOException("corrupted file format");
    }

    AndersichtLanguageLayer getLanguageLayerById(int languageId) throws IOException {
        Iterator<AndersichtLanguageLayer> i = languageLayers.iterator();
        while(i.hasNext()) {
            AndersichtLanguageLayer layer = i.next();
            if(layer.getId() == languageId) {
                return(layer);
            }
        }
        throw new IOException("corrupted file format");
    }

    public int getGroupIndex(AndersichtGroup group) {
        int i = 0;
        return(groups.indexOf(group));
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return(getGroup(childIndex));
    }

    @Override
    public int getChildCount() {
        return(getGroupCount());
    }

    @Override
    public TreeNode getParent() {
        return(null);
    }

    @Override
    public int getIndex(TreeNode node) {
        if(!(node instanceof AndersichtGroup)) return(-1);
        return(getGroupIndex((AndersichtGroup)node));
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
                return(pos < getGroupCount());
            }

            @Override
            public Object nextElement() {
                pos++;
                return(getGroup(pos));
            }
        });
    }
    
    public Pen getPen() {
        return(pen);
    }

    private LinkedList<String> searchForMissingMp3s() {
        LinkedList<String> errors = new LinkedList<String>();
        Iterator<AndersichtLanguageLayer> ill = languageLayers.iterator();
        while(ill.hasNext()) {
            AndersichtLanguageLayer ll = ill.next();
            if(ll.getInternalMP3() == null) {
                errors.add("MP3 für die Sprache '" + ll.toString() + "' fehlt");
            }
        }
        
        Iterator<AndersichtGroup> ig = groups.iterator();
        while(ig.hasNext()) {
            AndersichtGroup group = ig.next();
            for(int i = 0; i < group.getObjectCount(); i++) {
                AndersichtObject object = group.getObject(i);
                ill = languageLayers.iterator();
                while(ill.hasNext()) {
                    AndersichtLanguageLayer ll = ill.next();
                    Iterator<AndersichtDescriptionLayer> idl = descriptionLayers.iterator();
                    while(idl.hasNext()) {
                        AndersichtDescriptionLayer dl = idl.next();
                        AndersichtTrack track = object.getTrack(ll, dl);
                        if(track.getInternalMP3() == null) {
                            errors.add("MP3 für '" + group.getName() + "/" + object.getName() + "/" + ll.getName() + "/" + dl.getName() + "' fehlt");
                        }
                    }
                }
            }
        }
        return(errors);
    }
    
    private LinkedList<String> searchForLabelConflicts() {
        HashMap<Integer, String> labels = new HashMap<Integer, String>();
        
        LinkedList<String> errors = new LinkedList<String>();
        
        Iterator<AndersichtLanguageLayer> ill = languageLayers.iterator();
        while(ill.hasNext()) {
            AndersichtLanguageLayer ll = ill.next();
            String oldLabel = labels.get(ll.getLabel());
            if(oldLabel != null) {
                errors.add("Sprache '" + ll.toString() + "' hat das selbe Label wie '" + oldLabel + "' zugeordnet");
            }
            labels.put(ll.getLabel(), ll.toString());
        }
        
        
        Iterator<AndersichtGroup> ig = groups.iterator();
        while(ig.hasNext()) {
            AndersichtGroup group = ig.next();
            for(int i = 0; i < group.getObjectCount(); i++) {
                AndersichtObject object = group.getObject(i);
                try {
                    object.hasAllTracks();
                } catch(IllegalArgumentException iae) {
                    errors.add(iae.getMessage());
                }
                Iterator<AndersichtDescriptionLayer> idl = descriptionLayers.iterator();
                while(idl.hasNext()) {
                    AndersichtDescriptionLayer dl = idl.next();
                    String oldLabel = labels.get(object.getLabelAsInt(dl));
                    if(oldLabel != null) {
                        errors.add("Objekt '" + object.toString() + "' hat das selbe Label wie '" + oldLabel + "' zugeordnet");
                    }
                    labels.put(object.getLabelAsInt(dl), object.toString());
                }
            }
        }
        Iterator<Integer> labelIterator = labels.keySet().iterator();
        while(labelIterator.hasNext()) {
            int label = labelIterator.next();
            if(label < Translator.getMinObjectCode()) {
                errors.add("ungültiges Label für '" + labels.get(label) + "'");
            } else if(Translator.ting2code(label) < 0) {
                errors.add("unbekanntes Label für '" + labels.get(label) + "'");
            }
        }
        
        return(errors);
    }
    
    public void generate(File target) throws IOException, IllegalArgumentException {
        
        LinkedList<String> labelErrors = searchForLabelConflicts();
        if(labelErrors.size() > 0) {
            throw new IllegalArgumentException(labelErrors.getFirst());
        }
        
        AndersichtBookGenerator.generate(this, target);
    }

    public StringBuffer checkLabels() {
        
        StringBuffer sb = new StringBuffer();
        
        LinkedList<String> labelErrors = searchForLabelConflicts();
        if(labelErrors.isEmpty()) {
            return(null);
        } else {
            Iterator<String> i = labelErrors.iterator();
            while(i.hasNext()) {
                sb.append(i.next()).append("\n");
            }
        }
        
        return(sb);
    }
    
    public StringBuffer checkMp3s() {
        
        StringBuffer sb = new StringBuffer();
        
        LinkedList<String> mp3Errors = searchForMissingMp3s();
        if(mp3Errors.isEmpty()) {
            return(null);
        } else {
            Iterator<String> i = mp3Errors.iterator();
            while(i.hasNext()) {
                sb.append(i.next()).append("\n");
            }
        }
        
        return(sb);
    }

    private String toLength(String s, int l) {
        if(s.length() > l) {
            s = s.substring(0, l - 3) + "...";
        }
        while(s.length() < l) {
            s += " ";
        }
        return(s);
    }
    
    private String tab(int i) {
        String s = "";
        for(int j = 0; j < i; j++) {
            for(int k = 0; k < 6; k++) {
                s += " ";
            }
        }
        return(s);
    } 
    
    public void generateLabelReport(PrintWriter out) throws IOException {
        out.println("Buchname : " + getName());
        out.println("Buch ID  : " + getBookId());
        out.println("Pen      : " + getPen().toString());
        out.println();
        out.println();
        out.println("Sprachlayer:");
        for(int ll = 0; ll < getLanguageLayerCount(); ll++) {
            int tingId = getLanguageLayer(ll).getLabel();
            String label = Integer.toString(tingId);
            if(!(getPen() instanceof Ting)) {
                label = getPen().fromTingId(tingId) + " (" + label + ")";
            }
            out.println(tab(2) + toLength(getLanguageLayer(ll).getName(), 25) + " : " + label);
        }
        out.println();
        out.println();
        for(int g = 0; g < getGroupCount(); g++) {
            AndersichtGroup group = getGroup(g);
            out.println(group.getName());
            for(int o = 0; o < group.getObjectCount(); o++) {
                AndersichtObject object = group.getObject(o);
                out.println(tab(1) + object.getName());
                for(int l = 0; l < getDescriptionLayerCount(); l++) {
                    AndersichtDescriptionLayer dLayer = getDescriptionLayer(l);
                    int tingId = object.getLabelAsInt(dLayer);
                    String label = Integer.toString(tingId);
                    if(!(getPen() instanceof Ting)) {
                        label = getPen().fromTingId(tingId) + " (" + label + ")";
                    }
                    out.println(tab(2) + toLength(dLayer.getName(), 25) + " : " + label);
                }
                out.println();
            }
            out.println();
        }
    }
    
}
