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
package tingeltangel.tools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author martin
 */
public class Permutate {
    
    private final List<int[]> set = new LinkedList<int[]>();
    
    private Permutate(int n) {
        int[] a = new int[n];
        for(int i = 0; i < n; i++) {
            a[i] = i;
        }
        perm(a, n);
    }
    
    public static Iterator<int[]> perms(int n) {
        return(new Permutate(n).set.iterator());
    }
    
    private void perm(int[] a, int n) {
        if(n == 1) {
            int[] b = new int[a.length];
            System.arraycopy(a, 0, b, 0, a.length);
            set.add(b);
            return;
        }
        for(int i = 0; i < n; i++) {
            swap(a, i, n-1);
            perm(a, n-1);
            swap(a, i, n-1);
        }
    }  

    private static void swap(int[] a, int i, int j) {
        int c = a[i];
        a[i] = a[j];
        a[j] = c;
    }

}
