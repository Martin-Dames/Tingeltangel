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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;

import java.io.File;

public class ExtractOufTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ExtractOuf extractOuf = new ExtractOuf();

    @Test
    public void testExtract() throws Exception {
        File ouf = new File(getClass().getResource("/08091_en.ouf").getFile());

        Book result = extractOuf.extract(ouf, folder.newFolder());

        for (Integer id : result.getIds()) {
            Entry entry = result.getEntryByOID(id);
            System.out.println(entry.getTingID());
            if( entry.isCode()) {
                System.out.println(entry.getScript() );
            } else if( entry.isMP3()) {
                System.out.println(entry.getMP3());
            }
        }
    }
}
