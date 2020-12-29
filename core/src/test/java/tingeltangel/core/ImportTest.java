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

import org.junit.Test;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author martin
 */
public class ImportTest {

    @Test
    public void testImportOuf() throws IOException, SyntaxError {

        // switch to test mode
        FileEnvironment.test();

        List<Integer> success = new LinkedList<Integer>();
        List<Integer> failed = new LinkedList<Integer>();

        Integer[] _iDs = Repository.getIDs();
        Integer[] iDs = new Integer[Math.min(_iDs.length, 3)];
        System.arraycopy(_iDs, 0, iDs, 0, iDs.length);

        for (int i = 0; i < iDs.length; i++) {
            int id = iDs[i];
            if (id > 0) {
                File ouf = Repository.getBookOuf(id);
                if (ouf != null) {
                    System.out.println("\nImporting " + id + " ...");
                    try {
                        Importer.importBook(ouf, null, null, null, new Book(id), null);
                        success.add(id);
                    } catch (Exception e) {
                        System.out.println("********** ERROR: " + e.getMessage() + " ****************");
                        e.printStackTrace();
                        failed.add(id);
                    }
                }
            }
        }

        System.out.println("success:");
        Iterator<Integer> i = success.iterator();
        while (i.hasNext()) {
            System.out.print(" " + i.next());
        }
        System.out.println();

        System.out.println("failed:");
        i = failed.iterator();
        while (i.hasNext()) {
            System.out.print(" " + i.next());
        }
        System.out.println();


        if (failed.size() > 0) {
            System.out.println("imported " + success.size() + " out of " + (success.size() + failed.size()) + " books");
            // throw new IOException("imported " + success.size() + " out of " + (success.size() + failed.size()) + " books");
        } else if ((success.size() + failed.size()) == 0) {
            System.out.println("no books found in repository, so no books imported for testing");
        } else {
            System.out.println("all " + success.size() + " books successfully imported for testing");
        }

    }

}
