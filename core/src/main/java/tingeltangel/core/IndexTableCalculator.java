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


public class IndexTableCalculator {
    static final int[] E = {578, 562, 546, 530, 514, 498, 482, 466, 322, 306, 290, 274, 258, 242, 226, 210, -446, -462, -478, -494, -510, -526, -542, -558, -702, -718, -734, -750, -766, -782, -798, -814};

    
    /**
     *
     * @param code Positionscode in der Indextabelle (1. Feld)
     * @param n Position in der Indextablelle (startet bei 0)
     * @return Position in der Datei
     */
    public static int getPositionInFileFromCode(int code, int n) {
        if (((code & 255) != 0) | (n < 0)) {
            return -1;
        }
        n--;
        code = code >> 8;
        int c = ((code >> 3) & 1) | (((code >> 4) & 1) << 1) | (((code >> 5) & 1) << 2) | (((code >> 7) & 1) << 3) | (((code >> 9) & 1) << 4);
        code -= n * 26 - E[c];
        return code << 8;
    }

    /**
     *
     * @param position Position in der Datei
     * @param n Position in der Indextablelle (startet bei 0)
     * @return Positionscode in der Indextabelle (1. Feld)
     */
    static int getCodeFromPositionInFile(int position, int n) {
        if (((position & 255) != 0) | (n < 0)) {
            return Integer.MIN_VALUE;
        }
        n--;
        int b = (position >> 8) + n * 26;
        for (int k = 0; k < E.length; k++) {
            int v = (b - E[k]) << 8;
            if (IndexTableCalculator.getPositionInFileFromCode(v, n + 1) == position) {
                return v;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    
}
