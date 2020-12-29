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

import java.util.Collection;
import java.util.LinkedList;
import tingeltangel.core.Tupel;

/**
 *
 * @author mdames
 */
public class Penfriend implements Pen {

    @Override
    public String toString() {
        return("Penfriend");
    }

    @Override
    public String fromTingId(int id) {
        if((39185 <= id) && (id <= 39311)) return("CS" + (id - 39184));
        if((39312 <= id) && (id <= 39729)) return("ID" + (id - 39311));
        if(id == -1) {
            return("X");
        }
        throw new Error();
    }

    @Override
    public int toTingId(String penId) {
        if(penId.startsWith("CS")) {
            return(Integer.parseInt(penId.substring(2)) + 39184);
        } else if(penId.startsWith("ID")) {
            return(Integer.parseInt(penId.substring(2)) + 39311);
        } else if(penId.equals("X")) {
            return(-1);
        }
        throw new Error();
    }

    @Override
    public Collection<Tupel<Integer, String>> getLabelList() {
        LinkedList<Tupel<Integer, String>> list = new LinkedList<>();
        for(int i = 39185; i <= 39729; i++) {
            list.add(new Tupel<>(i, fromTingId(i)));
        }
        return(list);
    }
    
}
