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

// this test was replaced by ImportTest

package tingeltangel.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    
    /*
    
    @Test
    public void testLoad() throws IOException {
        File dir = new File(getClass().getResource("/book.tbu").getFile()).getParentFile();

        Book book = new Book(8091, null);
        Book.load(getClass().getResourceAsStream("/book.tbu"), book);

        assertEquals("Me", book.getAuthor());
        assertEquals("Me", book.getPublisher());
        assertEquals("My Book", book.getName());
        assertEquals(8091, book.getID());

        List<Integer> expectedIds = Arrays.asList(15001, 15002, 15003, 15004, 15005, 15006, 15007, 15009, 15010);
        assertEquals("The book contains 9 OIDs.", 9, book.getIds().size());
        assertTrue(book.getIds().containsAll(expectedIds));

        assertTrue(book.getEntryFromTingID(15001).isMP3());
        assertTrue(book.getEntryFromTingID(15002).isMP3());
        assertTrue(book.getEntryFromTingID(15004).isMP3());

        assertTrue(book.getEntryFromTingID(15006).getScript().toString().startsWith("set v3, 2\n" +
                "playoid 15002\n" +
                "end"));
    }

    @Test
    public void testGenerateScriptFile() throws Exception {

        Book book = new Book(8091, null);
        Book.load(getClass().getResourceAsStream("/book.tbu"), book);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        book.generateScriptFile(pw);
        String result = baos.toString();

        assertTrue("mp3 entry for code 15001", result.startsWith("Precode=15001\r\n" +
                "TYPE=1\r\n" +
                "[Note]\r\n" +
                "Track 1\r\n" +
                "[Content]\r\n" +
                "audio/Track_1.mp3\r\n"));

        assertTrue("script for code 15005", result.contains("Precode=15005\r\n" +
                "TYPE=0\r\n" +
                "[Note]\r\n" +
                "Track 1\r\n" +
                "[Content]\r\n" +
                "set v3, 1\r\n" +
                "playoid 15001\r\n" +
                "end"));
    }

    @Test
    public void testImportFromScriptFile() throws IOException {
        //File dir = new File(getClass().getResource("/08091_en.src").getFile()).getParentFile();

        Book book = new Book(8091, null);
        book.importFromScriptFile(getClass().getResourceAsStream("/08091_en.src"));

        List<Integer> expectedIds = Arrays.asList(15001, 15002, 15004, 15005, 15006, 15007, 15009, 15010);
        assertEquals("The book contains 8 OIDs.", 8, book.getIds().size());
        assertTrue(book.getIds().containsAll(expectedIds));

        assertTrue(book.getEntryFromTingID(15001).isMP3());
        assertTrue(book.getEntryFromTingID(15002).isMP3());
        assertTrue(book.getEntryFromTingID(15004).isMP3());

        assertTrue(book.getEntryFromTingID(15006).getScript().toString().startsWith("set v3, 2\n" +
                "playoid 15002\n" +
                "end"));
    }
    
    **/

    @Test
    public void testDummy() {
    }

}
