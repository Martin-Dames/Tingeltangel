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

public class Tripel<T1, T2, T3> {
    
    public Tripel(T1 a, T2 b, T3 c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public T1 a;
    public T2 b;
    public T3 c;
 
    @Override
    public String toString() {
        return("(" + a.toString() + "|" + b.toString() + "|" + c.toString() + ")");
    }
    
    @Override
    public int hashCode() {
        return((int)((((long)a.hashCode()) + b.hashCode() + c.hashCode()) % (long)Integer.MAX_VALUE));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tripel<?, ?, ?> other = (Tripel<?, ?, ?>) obj;
        if (this.a != other.a && (this.a == null || !this.a.equals(other.a))) {
            return false;
        }
        if (this.b != other.b && (this.b == null || !this.b.equals(other.b))) {
            return false;
        }
        if (this.c != other.c && (this.c == null || !this.c.equals(other.c))) {
            return false;
        }
        return true;
    }
    
}
