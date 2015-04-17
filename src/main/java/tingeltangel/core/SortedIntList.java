
package tingeltangel.core;

import java.util.Iterator;
import java.util.LinkedList;

public class SortedIntList {
    
    LinkedList<Integer> list = new LinkedList<Integer>();
    
    public void add(Integer element) {
        Iterator<Integer> i = list.iterator();
        int p = 0;
        while(i.hasNext()) {
            if(i.next().compareTo(element) > 0) {
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
    
}
