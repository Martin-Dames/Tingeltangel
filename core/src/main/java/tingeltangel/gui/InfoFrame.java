/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
<<<<<<< HEAD:tingeltangel/src/main/java/tingeltangel/gui/InfoFrame.java

=======

>>>>>>> feature/update:core/src/main/java/tingeltangel/gui/InfoFrame.java
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
<<<<<<< HEAD:tingeltangel/src/main/java/tingeltangel/gui/InfoFrame.java

=======

>>>>>>> feature/update:core/src/main/java/tingeltangel/gui/InfoFrame.java
*/

package tingeltangel.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class InfoFrame extends JFrame {

    private JEditorPane text = new JEditorPane();

    public InfoFrame(String title, String file) {
        super(title);
        setTitle(title);
        setVisible(false);
        setBounds(315, 5, 600, 400);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        text.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        text.setEditable(false);
        setContentPane(new JScrollPane(text));

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(InfoFrame.class.getClassLoader().getResourceAsStream(file)));
            String row;
            StringBuilder s = new StringBuilder();
            while((row = in.readLine()) != null) {
                int p0 = row.indexOf("%%");
                int p1 = row.lastIndexOf("%%");
                if((p0 != -1) && (p1 != -1) && (p0 != p1)) {
                    String x0 = row.substring(0, p0);
                    String x1 = row.substring(p0 + 2, p1);
                    String x2 = row.substring(p1 + 2);
                    s.append(x0);
                    s.append(InfoFrame.class.getClassLoader().getSystemResource("icons/" + x1).toString());
                    s.append(x2);
                } else {
                    s.append(row);
                }
            }
            in.close();
            text.setText(s.toString());
            text.setCaretPosition(0);
        } catch(IOException e) {
            throw new Error(e);
        }
    }
}
