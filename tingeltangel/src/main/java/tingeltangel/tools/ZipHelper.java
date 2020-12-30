/*
    Copyright (C) 2016   Martin Dames <martin@bastionbytes.de>
  
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
package tingeltangel.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import tingeltangel.core.Book;

/**
 *
 * @author mdames
 */
public class ZipHelper {
    
    public static void zip(File output, final File[] input, ProgressDialog progressDialog, final JFrame frame, final Book book, String title, final String error) throws IOException {
        
        final FileOutputStream fos = new FileOutputStream(output);
        final ZipOutputStream out = new ZipOutputStream(fos);

        new Progress(frame, title) {
            @Override
            public void action(ProgressDialog progressDialog) {
                
                byte[] buffer = new byte[4096];
                progressDialog.setMax(input.length);
                try {
                    for(int i = 0; i < input.length; i++) {

                        FileInputStream in = new FileInputStream(input[i]);
                        ZipEntry zipEntry = new ZipEntry(input[i].getName());
                        out.putNextEntry(zipEntry);

                        int length;
                        while((length = in.read(buffer)) >= 0) {
                            out.write(buffer, 0, length);
                        }

                        out.closeEntry();
                        in.close();

                        progressDialog.setVal(i);
                    }
                    out.close();
                    fos.close();
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(frame, error + ": " + ioe.getMessage());
                }
                progressDialog.done();
            }

        };
    }
    
}
