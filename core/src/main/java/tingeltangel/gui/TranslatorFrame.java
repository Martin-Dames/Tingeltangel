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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import tingeltangel.core.Translator;

public class TranslatorFrame extends JInternalFrame implements ActionListener {
    
    private JTextField tingID = new JTextField();
    private JTextField codeID = new JTextField();
    private JButton code2ting = new JButton("Ting ID berechnen");
    private JButton ting2code = new JButton("Code ID berechnen");
    private final EditorFrame frame;
    
    public TranslatorFrame(EditorFrame frame) {
        super("", true, true, true, true);
        this.frame = frame;
        setTitle("ID Rechner");
        setVisible(false);
        setBounds(325, 15, 300, 300);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2, 2));
        gridPanel.add(new JLabel("Ting ID"));
        gridPanel.add(tingID);
        gridPanel.add(new JLabel("Code ID"));
        gridPanel.add(codeID);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(ting2code, BorderLayout.WEST);
        bottomPanel.add(code2ting, BorderLayout.EAST);
        
        ting2code.setActionCommand("ting2code");
        ting2code.addActionListener(this);
        code2ting.setActionCommand("code2ting");
        code2ting.addActionListener(this);
        
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(gridPanel, BorderLayout.CENTER);
        main.add(bottomPanel, BorderLayout.SOUTH);
        
        
        
        setContentPane(main);
          
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if(action.equals("ting2code")) {
            try {
                int ting = Integer.parseInt(tingID.getText().trim());
                if((ting < 0) || (ting >= 0x10000)) {
                    throw new NumberFormatException();
                }
                int code = Translator.ting2code(ting);
                codeID.setText(Integer.toString(code));
                if(code < 0) {
                    JOptionPane.showMessageDialog(frame, "Die Code-ID zur Ting-ID " + ting + " ist unbekannt", "Warnung", JOptionPane.WARNING_MESSAGE);
                }
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(frame, "Keine gültige Ting-ID angegeben", "Fehler", JOptionPane.WARNING_MESSAGE);
            }
        } else if(action.equals("code2ting")) {
            try {
                int code = Integer.parseInt(codeID.getText().trim());
                if((code < 0) || (code >= 0x10000)) {
                    throw new NumberFormatException();
                }
                int ting = Translator.code2ting(code);
                tingID.setText(Integer.toString(ting));
                if(ting < 0) {
                    JOptionPane.showMessageDialog(frame, "Die Ting-ID zur Code-ID " + code + " ist unbekannt", "Warnung", JOptionPane.WARNING_MESSAGE);
                }
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(frame, "Keine gültige Code-ID angegeben", "Fehler", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
}