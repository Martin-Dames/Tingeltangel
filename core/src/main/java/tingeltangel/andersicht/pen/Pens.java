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
package tingeltangel.andersicht.pen;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author mdames
 */
public class Pens {
    
    private final static Pen[] PENS = {
        new Ting(),
        new Penfriend()  
    };
    
    private final static HashMap<String, Pen> PEN_MAP = new HashMap<String, Pen>();
    
    static {
        for(int i = 0; i < PENS.length; i++) {
            PEN_MAP.put(PENS[i].toString(), PENS[i]);
        }
    }
    
    public static Iterator<String> getPenNames() {
        return(PEN_MAP.keySet().iterator());
    }
    
    public static Pen getPen(String name) {
        return(PEN_MAP.get(name));
    }
    
    public static Pen[] getPenArray() {
        return(PENS);
    }
}
