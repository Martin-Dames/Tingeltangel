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

package tingeltangel.core.scripting;

public class SyntaxError extends Exception {
    
    private int row = -1;
    private int tingID = -1;
    
    public SyntaxError(String message) {
        super(message);
    }
    
    public void setRow(int row) {
        this.row = row;
    }
    
    public void setTingID(int tingID) {
        this.tingID = tingID;
    }
    
    public int getRow() {
        return(row);
    }
    
    public int getTingID() {
        return(tingID);
    }
    
}
