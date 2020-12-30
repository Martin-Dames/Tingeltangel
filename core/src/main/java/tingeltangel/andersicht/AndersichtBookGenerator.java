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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.Script;
import tingeltangel.core.Translator;
import tingeltangel.core.Tripel;
import tingeltangel.core.Tupel;
import tingeltangel.core.scripting.SyntaxError;

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
        
        Book _book = new Book(book.getBookId());
        
        HashMap<Tripel<AndersichtObject,AndersichtLanguageLayer, AndersichtDescriptionLayer>, Integer> objectTracks =
                            new HashMap<Tripel<AndersichtObject,AndersichtLanguageLayer, AndersichtDescriptionLayer>, Integer>();
        HashMap<AndersichtLanguageLayer, Integer> languageTracks = new HashMap<AndersichtLanguageLayer, Integer>();
        
        for(int g = 0; g < book.getGroupCount(); g++) {
            AndersichtGroup group = book.getGroup(g);
            for(int o = 0; o < group.getObjectCount(); o++) {
                AndersichtObject object = group.getObject(o);
                for(int d = 0; d < book.getDescriptionLayerCount(); d++) {
                    for(int l = 0; l < book.getLanguageLayerCount(); l++) {
                        int oid = freeIDs.next();
                        
                        Tripel<AndersichtObject, AndersichtLanguageLayer, AndersichtDescriptionLayer> key =
                                new Tripel<AndersichtObject, AndersichtLanguageLayer, AndersichtDescriptionLayer>(object, book.getLanguageLayer(l), book.getDescriptionLayer(d));
                        
                        objectTracks.put(key, oid);
                        AndersichtTrack aTrack = object.getTrack(book.getLanguageLayer(l), book.getDescriptionLayer(d));
                        _book.addEntry(oid);
                        Entry entry = _book.getEntryByOID(oid);
                        entry.setMP3(aTrack.getInternalMP3());
                    }
                }
            }
        }
                        
        for(int l = 0; l < book.getLanguageLayerCount(); l++) {
            int oid = freeIDs.next();
            languageTracks.put(book.getLanguageLayer(l), oid);
            _book.addEntry(oid);
            Entry entry = _book.getEntryByOID(oid);
            entry.setMP3(book.getLanguageLayer(l).getInternalMP3());
        }

        
        for(int l = 0; l < book.getLanguageLayerCount(); l++) {
            AndersichtLanguageLayer lLayer = book.getLanguageLayer(l);
            _book.addEntry(lLayer.getLabel());
            Entry entry = _book.getEntryByOID(lLayer.getLabel());
            
            String code = "";
            code += "set V0, " + lLayer.getId() + "\n";
            code += "playoid " + languageTracks.get(lLayer) + "\n";
            code += "end\n";
            
            entry.setScript(new Script(code, entry));
        }
        
        // generate main scripts
        
        for(int g = 0; g < book.getGroupCount(); g++) {
            AndersichtGroup group = book.getGroup(g);
            for(int o = 0; o < group.getObjectCount(); o++) {
                AndersichtObject object = group.getObject(o);
                for(int d = 0; d < book.getDescriptionLayerCount(); d++) {
                    AndersichtDescriptionLayer dLayer = book.getDescriptionLayer(d);
                    
                    int oid = object.getLabelAsInt(dLayer);
                    _book.addEntry(oid);
                    Entry entry = _book.getEntryByOID(oid);
                    
                    String code = "";
                    
                    int labelCounter = 0;
                    
                    for(int l = 0; l < book.getLanguageLayerCount(); l++) {
                        AndersichtLanguageLayer lLayer = book.getLanguageLayer(l);
                        
                        
                        Tripel<AndersichtObject, AndersichtLanguageLayer, AndersichtDescriptionLayer> key =
                                new Tripel<AndersichtObject, AndersichtLanguageLayer, AndersichtDescriptionLayer>(object, lLayer, dLayer);
                        
                        
                        int playoid = objectTracks.get(key);
                        
                        code += "cmp v0, " + Integer.toString(lLayer.getId()) + "\n";
                        code += "jne L" + Integer.toString(labelCounter) + "\n";
                        code += "playoid " + Integer.toString(playoid) + "\n";
                        code += "jmp end\n";
                        code += ":L" + Integer.toString(labelCounter) + "\n";
                        
                        labelCounter++;
                    }
                    
                    code += ":end\n";
                    code += "end\n";
                    entry.setScript(new Script(code, entry));
                }
            }
        }
        
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ouf));
        try {
            _book.generateOufFile(out, null);
        } catch(SyntaxError se) {
            throw new IOException(se);
        }
    }
    
}
