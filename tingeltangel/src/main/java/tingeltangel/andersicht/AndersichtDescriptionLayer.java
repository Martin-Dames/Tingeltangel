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

/**
 *
 * @author mdames
 */
public class AndersichtDescriptionLayer {
    
    private String name = "Name";
    private String description = "Beschreibung";
    private int id = 0;
    private final AndersichtBook book;
    
    AndersichtDescriptionLayer(String name, String description, AndersichtBook book) {
        this.name = name;
        this.description = description;
        this.book = book;
    }
    
    @Override
    public String toString() {
        return(name);
    }
    
    public AndersichtBook getBook() {
        return(book);
    }
    
    public String getName() {
        return(name);
    }
    
    public void setName(String name) {
        this.name = name;
        book.changeMade();
    }
    
    public String getDescription() {
        return(description);
    }
    
    public void setDescription(String description) {
        this.description = description;
        book.changeMade();
    }
    
    int getId() {
        return(id);
    }
    
    void setId(int id) {
        this.id = id;
    }
    
    static AndersichtDescriptionLayer load(AndersichtBook book, DataInputStream in) throws IOException {
        String name = in.readUTF();
        String description = in.readUTF();
        AndersichtDescriptionLayer layer = new AndersichtDescriptionLayer(name, description, book);
        layer.id = in.readInt();
        return(layer);
    }
    
    void save(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(description);
        out.writeInt(id);
    }
}
