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
import tingeltangel.core.Tupel;

/**
 *
 * @author mdames
 */
public interface Pen {
    
    @Override
    public String toString();
    public String fromTingId(int id);
    public int toTingId(String penId);

    public Collection<Tupel<Integer, String>> getLabelList();
    
}
