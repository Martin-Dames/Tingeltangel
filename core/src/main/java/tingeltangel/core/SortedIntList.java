/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
*/

package tingeltangel.core;

import java.util.Iterator;
import java.util.LinkedList;

public class SortedIntList {

    LinkedList<Integer> list = new LinkedList<Integer>();
    
    public void add(Integer element) {
        Iterator<Integer> i = list.iterator();
        int p = 0;
        while(i.hasNext()) {
            Integer n = i.next();
            if(n.compareTo(element) == 0) {
                i.remove();
                list.add(p, element);
                return;
            } else if(n.compareTo(element) > 0) {
                list.add(p, element);
                return;
            }
            p++;
        }
        list.add(element);
    }
    
    public void remove(int i) {
        list.remove(i);
    }
    
    public Iterator<Integer> iterator() {
        return(list.iterator());
    }
    
    public int get(int i) {
        return(list.get(i));
    }
    
    public int size() {
        return(list.size());
    }

    boolean containsKey(int tingID) {
        return(list.contains(tingID));
    }

    void removeByTingID(int tingID) {
        list.remove(Integer.valueOf(tingID));
    }
    
    
}
