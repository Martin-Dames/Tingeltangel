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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mdames
 */
public class AndersichtBook {

    private String description = "Beschreibung";
    private String name = "Name";
    private int bookId = 2303;
    private final List<AndersichtLanguageLayer> languageLayers = new LinkedList<AndersichtLanguageLayer>();
    private final List<AndersichtDescriptionLayer> descriptionLayers = new LinkedList<AndersichtDescriptionLayer>();
    
    private final List<AndersichtGroup> groups = new LinkedList<AndersichtGroup>();
 
    private boolean unsaved = true;
    
    private AndersichtBook() {
        
    }
    
    public boolean unsaved() {
        return(unsaved);
    }
    
    void changeMade() {
        unsaved = true;
    }
    
    public static AndersichtBook newBook(String name, String description) {
        AndersichtBook book = new AndersichtBook();
        book.name = name;
        book.description = description;
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
    
    public void setName(String name) {
        this.name = name;
        changeMade();
    } 
    
    public String getDescription() {
        return(description);
    }
    
    public void setDescription(String description) {
        this.description = description;
        changeMade();
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
    
    public static AndersichtBook load(File file) throws IOException {
        AndersichtBook book = new AndersichtBook();
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        
        book.name = in.readUTF();
        book.description = in.readUTF();
        book.bookId = in.readInt();
        
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
    
    public void save(File file) throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.writeUTF(name);
        out.writeUTF(description);
        out.writeInt(bookId);
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
        
        unsaved = true;
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
    
}
