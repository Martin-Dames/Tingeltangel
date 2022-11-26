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

import tingeltangel.core.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import tingeltangel.core.scripting.SyntaxError;

public class ExtractOuf {

    public static void main(String[] args) throws Exception {
        ExtractOuf self = new ExtractOuf() ;

        File targetDir = new File("/tmp/dump");

        Book book = self.extract(new File("/tmp/bauernhof/05045_en.ouf"), targetDir);
        book.generateScriptFile( new File(targetDir,"0"+book.getID()+"_en.src"));

    }

    Book extract(File ouf, File targetDir) throws IOException, SyntaxError {
        RandomAccessFile in = new RandomAccessFile(ouf, "r");

        // HEADER
        int startOfIndexTable = in.readInt();
        in.readInt();
        int smallestTingId = in.readInt();
        in.readInt();
        int numberOfTingCodes = in.readInt();
        int bookId = in.readInt();


        in.seek(startOfIndexTable);

        // INDEX TABLE
        Map<Integer, IndexTableEntry> code2entry = new HashMap<Integer, IndexTableEntry>();
        for (int i = 0; i < numberOfTingCodes; i++) {
            IndexTableEntry ite = new IndexTableEntry();
            ite.position = IndexTableCalculator.getPositionInFileFromCode(in.readInt(), i);
            ite.size = in.readInt();
            ite.type = in.readInt();

            code2entry.put(i + smallestTingId, ite);
        }

        Book book = new Book(bookId);

        for (Integer code : code2entry.keySet()) {
            IndexTableEntry ite = code2entry.get(code);
            book.addEntry(code);
            Entry entry = book.getEntryByOID(code);
            if (ite.type == 1) {
                in.seek(ite.position);
                byte[] data = new byte[ite.size];
                in.read(data);

                File mp3file = new File(targetDir, code + ".mp3");
                FileOutputStream out = new FileOutputStream(mp3file);
                out.write(data);
                out.close();

                entry.setMP3();
                entry.setMP3(mp3file);
            } else if (ite.type == 2) {
                in.seek(ite.position);
                byte[] data = new byte[ite.size];
                in.read(data);
                entry.setCode();
                entry.setScript(new Script(data, entry));
            }
        }

        in.close();

        return book;
    }

    private static class IndexTableEntry {
        int position;
        int size;
        int type;

    }

}
