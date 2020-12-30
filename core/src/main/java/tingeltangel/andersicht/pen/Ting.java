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
public class Ting implements Pen {

    @Override
    public String toString() {
        return("Ting");
    }

    @Override
    public String fromTingId(int id) {
        return(Integer.toString(id));
    }

    @Override
    public int toTingId(String penId) {
        return(Integer.parseInt(penId));
    }

    @Override
    public Collection<Tupel<Integer, String>> getLabelList() {
        LinkedList<Tupel<Integer, String>> list = new LinkedList<Tupel<Integer, String>>();
        for(int i = 15001; i < 16001; i++) {
            list.add(new Tupel<Integer, String>(i, Integer.toString(i)));
        }
        return(list);
    }
    
}
