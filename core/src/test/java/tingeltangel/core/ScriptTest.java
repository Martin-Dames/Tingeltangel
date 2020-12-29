/*
    Copyright (C) 2015   Jesper Zedlitz <jesper@zedlitz.de>
  
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

import org.junit.Test;
import tingeltangel.core.scripting.SyntaxError;

import static org.junit.Assert.*;

public class ScriptTest {

    private Entry entry = new Entry(null, 15001);

    @Test
    public void testCompile_ValidCode() throws SyntaxError {
        Script script = new Script("set v3, 1\n" +
                "playoid 15001\n" +
                "end", entry);

        byte[] expected = new byte[]{
                0x02, 0x01, 0x00, 0x03, 0x00, 0x01,  // set V3, 1
                0x16, 0x01, 0x3a, (byte) 0x99,       // playoid 15001 (= 0x3a99)
                0x00, 0x00,                          // end
                0x00
        };

        byte[] binary = script.compile();
        assertArrayEquals("expected byte code", expected, binary);
    }

    @Test
    public void testCompile_SyntaxError() {
        Script script = new Script("foobar", entry);

        try {
            script.compile();
            fail("The code is invalid.");
        } catch (SyntaxError syntaxError) {
            // ok
        }
    }

    @Test
    public void testGetSize() throws SyntaxError {
        Script script = new Script("set v3, 1\n" +
                "playoid 15001\n" +
                "end", entry);


        assertEquals(13, script.getSize(false));
    }

}
