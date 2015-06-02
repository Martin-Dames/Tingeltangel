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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import tingeltangel.core.Book;
import tingeltangel.core.scripting.SyntaxError;

public class CompileTingBook {
    public static void main(String[] args) throws IOException, SyntaxError {
        File srcFile = new File("/tmp/zahlen/08071_en.src");


        File inputDir = srcFile.getParentFile();

        Book book = new Book(8071);
        book.importFromScriptFile(new FileInputStream(srcFile));
        if( srcFile.getName().matches("[0-9][0-9][0-9][0-9][0-9]_en.src")) {
            int id = Integer.parseInt(srcFile.getName().substring(0, 5));
            
        }  else {
            throw   new RuntimeException("Need book id");

        }


        book.export(inputDir, null);
    }
}
