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
package tingeltangel.core.scripting;

import org.junit.Test;
import tingeltangel.core.Entry;
import tingeltangel.core.Script;

import static org.junit.Assert.assertEquals;

public class DisassemblerTest {

    private Entry entry = new Entry(null, 15001);

    private Disassembler d = new Disassembler();

    @Test
    public void testDisassemble() throws Exception {
        String expectedCode = "set v3,1\n" +
                "playoid 15001\n" +
                "end\n";

        Script script = new Script(expectedCode, entry);

        byte[] binary = script.compile();

        String result = d.disassemble(binary);

        assertEquals("Correctly disassembled code", expectedCode, result);
    }

    @Test
    public void testDisassemble_2() throws Exception {
        String expectedCode = "cmp v3,0\n" +
                "je l1\n" +
                "cmp v3,1\n" +
                "je l2\n" +
                "cmp v3,2\n" +
                "je l3\n" +
                "end\n" +
                "\n" +
                ":l1\n" +
                "playoid 15001\n" +
                "end\n" +
                "\n" +
                ":l2\n" +
                "playoid 15002\n" +
                "end\n" +
                "\n" +
                ":l3\n" +
                "playoid 15004\n" +
                "end\n";

        Script script = new Script(expectedCode, entry);

        byte[] binary = script.compile();

        String result = d.disassemble(binary);

        assertEquals("Correctly disassembled code", expectedCode, result);
    }

    @Test
    public void testDisassemble_3() {
        byte[] binary = new byte[] {0x02,0x01,0x00,0x01,0x00,30,0x16,0x01,58,-103,0x17,0x02,0,1,0x16,0x01,58,-102,0,0,0};
        String result = d.disassemble(binary);
    }
}
