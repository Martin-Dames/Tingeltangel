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
package tingeltangel;

import org.junit.Before;
import org.junit.Test;
import tiptoi_reveng.lexer.LexerException;
import tiptoi_reveng.parser.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ReadYamlFileTest {

    private ReadYamlFile reader = new ReadYamlFile();

    @Before
    public void setUp() {
        reader.ignoreAudioFiles = true;
    }

    @Test
    public void testParser() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/example.yaml").getFile()))   ;
    }

    @Test
    public void testParser2() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/vokabeltrainer.yaml").getFile()));
    }

    @Test
    public void testParser5() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/WWW_Weltatlas.yaml").getFile()));
    }

    /**
     * This YAML file contains a fix mapping of identifiers to codes.
     */
    @Test
    public void testParser_FixedCodes() throws ParserException, IOException, LexerException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/vokabeltrainer-codes.yaml").getFile()));

        assertEquals("teddy", reader.getUsedOidAndIdentifiers().get(16001));
        assertEquals("wichtel", reader.getUsedOidAndIdentifiers().get(16002));
        assertEquals("fragezeichen", reader.getUsedOidAndIdentifiers().get(16010));
        assertEquals("franzoesisch", reader.getUsedOidAndIdentifiers().get(16011));
        assertEquals("deutsch", reader.getUsedOidAndIdentifiers().get(16012));
        assertEquals("ball", reader.getUsedOidAndIdentifiers().get(16004));
    }

    @Test
    public void testGetUsedOidAndIdentifiers() throws LexerException, ParserException, IOException {
        reader.read(new File(getClass().getResource("/tip-toi-reveng/vokabeltrainer.yaml").getFile()));

        Map<Integer, String> result = reader.getUsedOidAndIdentifiers();

        assertEquals(6, result.size());
    }


}
