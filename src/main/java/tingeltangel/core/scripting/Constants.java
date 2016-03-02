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
package tingeltangel.core.scripting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author martin
 */
public class Constants {
    
    private final HashMap<String, String> constants = new HashMap<String, String>();
    private final LinkedList<String> keys = new LinkedList<String>();
              
    public String get(String name) {
        return(constants.get(name));
    }
    
    public void set(String name, String value) {
        if(!constants.containsKey(name)) {
            keys.add(name);
        }
        constants.put(name, value);
    }
    
    public void setUsedKeys(Set<String> usedKeys) {
        Iterator<String> i = usedKeys.iterator();
        while(i.hasNext()) {
            String key = i.next();
            if(!constants.containsKey(key)) {
                set(key, "");
            }
        }
        
    }
    
    public void unset(String name) {
        constants.remove(name);
    }
    
    public Set<String> getNames() {
        return(constants.keySet());
    }
    
    public static Constants loadFromString(String def) {
        Constants v = new Constants();
        String[] defs = def.split(";");
        for(int i = 0; i < defs.length; i++) {
            int p = defs[i].indexOf("=");
            v.set(defs[i].substring(0, p), defs[i].substring(p + 1));
        }
        return(v);
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        Iterator<String> keys = constants.keySet().iterator();
        while(keys.hasNext()) {
            if(s.length() > 0) {
                s.append(";");
            }
            String key = keys.next();
            s.append(key).append("=").append(constants.get(key));
        }
        return(s.toString());
    }

    public String getNameAt(int i) {
        return(keys.get(i));
    }

    public void changeName(int i, String newName) {
        String oldName = getNameAt(i);
        
        // change in linkedlist
        keys.set(i, newName);
        
        // change hashmap
        String value = get(oldName);
        unset(oldName);
        constants.put(newName, value);
        
    }
    
}
