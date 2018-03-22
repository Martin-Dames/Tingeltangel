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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mdames
 */
public class AndersichtGroup {


    private String name = "Gruppenname";
    private String description = "Gruppenbeschreibung";
    private final AndersichtBook book;
    
    private final List<AndersichtObject> objects = new LinkedList<AndersichtObject>();
    
    AndersichtGroup(String name, String description, AndersichtBook book) {
        this.name = name;
        this.description = description;
        this.book = book;
    }
    
    public AndersichtBook getBook() {
        return(book);
    }
    
    public AndersichtObject addObject(String name, String description) {
        AndersichtObject object = new AndersichtObject(name, description, this);
        objects.add(object);
        book.changeMade();
        return(object);
    }
    
    public void removeObject(AndersichtObject object) {
        objects.remove(object);
        book.changeMade();
    }
    
    public void setName(String name) {
        this.name = name;
        book.changeMade();
    }
    
    public void setDescription(String description) {
        this.description = description;
        book.changeMade();
    }
    
    public String getName() {
        return(name);
    }
    
    public String getDescription() {
        return(description);
    }
    
    public boolean isEmpty() {
        return(objects.isEmpty());
    }
    
    public int getObjectCount() {
        return(objects.size());
    }
    
    public AndersichtObject getObject(int i) {
        return(objects.get(i));
    }
    
    static AndersichtGroup load(AndersichtBook book, DataInputStream in) throws IOException {
        String name = in.readUTF();
        String description = in.readUTF();
        AndersichtGroup group = new AndersichtGroup(name, description, book);
        int size = in.readInt();
        for(int i = 0; i < size; i++) {
            group.objects.add(AndersichtObject.load(group, in));
        }
        return(group);
    }
    
    void save(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(description);
        out.writeInt(objects.size());
        for(int i = 0; i < objects.size(); i++) {
            objects.get(i).save(out);
        }
    }
}
