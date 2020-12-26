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

import org.junit.Before;
import org.junit.Test;
import tiptoi_reveng.lexer.LexerException;
import tiptoi_reveng.parser.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ReadYamlFileTest {

    private ReadYamlFile reader = new ReadYamlFile();

    @Before
    public void setUp() {
        reader.ignoreAudioFiles = true;
    }

    @Test
    public void testParser() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/example.yaml").getFile()), null);
    }

    @Test
    public void testParser2() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/vokabeltrainer.yaml").getFile()), null);
    }

    @Test
    public void testParser5() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/WWW_Weltatlas.yaml").getFile()), null);
    }

    /**
     * This YAML file contains a fix mapping of identifiers to codes.
     */
    @Test
    public void testParser_FixedCodes() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/vokabeltrainer-codes.yaml").getFile()), null);

        assertEquals("teddy", reader.getUsedOidAndIdentifiers().get(16001));
        assertEquals("wichtel", reader.getUsedOidAndIdentifiers().get(16002));
        assertEquals("fragezeichen", reader.getUsedOidAndIdentifiers().get(16010));
        assertEquals("franzoesisch", reader.getUsedOidAndIdentifiers().get(16011));
        assertEquals("deutsch", reader.getUsedOidAndIdentifiers().get(16012));
        assertEquals("ball", reader.getUsedOidAndIdentifiers().get(16004));
    }

    @Test
    public void testGetUsedOidAndIdentifiers() throws LexerException, ParserException, IOException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/vokabeltrainer.yaml").getFile()), null);

        Map<Integer, String> result = reader.getUsedOidAndIdentifiers();

        assertEquals(6, result.size());
    }

    /**
     * Read a simple YAML file with only three MP3 actions and check the resulting book.
     * @throws LexerException
     * @throws ParserException
     * @throws IOException
     */
    @Test
    public void testParser_Simple() throws LexerException, ParserException, IOException {
        Book book = reader.read(new File(getClass().getResource("/tip-toi-reveng/simple.yaml").getFile()), null);

        Map<Integer, String> oid2label = reader.getUsedOidAndIdentifiers();
        Map<String,Integer> label2oid = new HashMap<String, Integer>();
        for (Integer key : oid2label.keySet()) {
            label2oid.put(oid2label.get(key),key);
        }

        assertEquals(3, oid2label.size());
        for (Integer oid : oid2label.keySet()) {
            assertTrue(oid > 15000);
        }
        assertNotNull("OID for label a", label2oid.get("a"));
        assertNotNull("OID for label b", label2oid.get("b"));
        assertNotNull("OID for label c", label2oid.get("c"));

        Entry entryA = book.getEntryByOID(label2oid.get("a"));
        assertNotNull("entry for label a", entryA);
        Entry entryB = book.getEntryByOID(label2oid.get("b"));
        assertNotNull("entry for label b", entryB);
        Entry entryC = book.getEntryByOID(label2oid.get("c"));
        assertNotNull("entry for label c", entryC);

        // Change these tests if you optimize the result for MP3 files that are only used once.
        assertFalse("no direct MP3 file for label a", entryA.isMP3());
        assertNotNull("a script for label", entryA.getScript());
        String scriptA = entryA.getScript().toString();
        assertTrue("script plays one file and ends" ,scriptA.matches("playoid [0-9]+\nend\n"));

        String scriptB = entryB.getScript().toString();
        assertTrue("script plays one file and ends" ,scriptB.matches("playoid [0-9]+\nend\n"));

        String scriptC = entryC.getScript().toString();
        assertTrue("script plays one file and ends" ,scriptC.matches("playoid [0-9]+\nend\n"));
    }

}
