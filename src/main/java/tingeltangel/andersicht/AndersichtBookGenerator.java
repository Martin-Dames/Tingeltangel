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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import tingeltangel.core.Translator;

/**
 *
 * @author mdames
 */
public class AndersichtBookGenerator {

    private final AndersichtBook book;
    private final File ouf;
    
    private AndersichtBookGenerator(AndersichtBook book, File ouf) {
        this.book = book;
        this.ouf = ouf;
    }
    
    static void generate(AndersichtBook book, File ouf) throws IOException {
        new AndersichtBookGenerator(book, ouf).generate();
    }
    
    private void generate() throws IOException {
        // collect free ids
        TreeSet<Integer> freeIDsSet = new TreeSet<Integer>();
        for(int i = Translator.getMinObjectCode(); i < 0x10000; i++) {
            freeIDsSet.add(i);
        }
        for(int i = 0; i < book.getLanguageLayerCount(); i++) {
            freeIDsSet.remove(book.getLanguageLayer(i).getLabel());
        }
        for(int g = 0; g < book.getGroupCount(); g++) {
            AndersichtGroup group = book.getGroup(g);
            for(int o = 0; o < group.getObjectCount(); o++) {
                AndersichtObject object = group.getObject(o);
                for(int l = 0; l < book.getDescriptionLayerCount(); l++) {
                    freeIDsSet.remove(object.getLabelAsInt(book.getDescriptionLayer(l)));
                }
            }
        }
        Iterator<Integer> freeIDs = freeIDsSet.iterator();
        
        
        
        
        
    }
    
}
