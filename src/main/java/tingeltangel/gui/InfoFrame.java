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

package tingeltangel.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

public class InfoFrame extends JInternalFrame {
    
    private JEditorPane text = new JEditorPane();
    
    public InfoFrame(String title, String file) {
        super("", true, true, true, true);
        setTitle(title);
        setVisible(false);
        setBounds(315, 5, 600, 400);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        text.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        text.setEditable(false);
        setContentPane(new JScrollPane(text));
                
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String row;
            StringBuilder s = new StringBuilder();
            while((row = in.readLine()) != null) {
                s.append(row);
            }
            in.close();
            text.setText(s.toString());
            text.setCaretPosition(0);
        } catch(IOException e) {
            throw new Error(e);
        }
    }
    
}