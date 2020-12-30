/*
 * Copyright 2018 mdames.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tingeltangel.andersicht.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import tingeltangel.andersicht.pen.Pen;
import tingeltangel.andersicht.pen.Pens;
import tingeltangel.core.Tupel;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class AndersichtNewBook extends javax.swing.JDialog {

    
    private final LinkedList<Integer> idList = new LinkedList<Integer>();
    private final static Logger log = LogManager.getLogger(AndersichtNewBook.class);
    private JList bookList = new JList();
    private JButton button = new JButton();
    private JScrollPane jScrollPane = new JScrollPane();
    
    private JTextField bookName = new JTextField();
    private JTextField bookDescription = new JTextField();
    
    private JComboBox penChooser;
    
    public AndersichtNewBook(java.awt.Frame parent, final Callback<AndersichtBookDefinition> callback) {
        super(parent, false);
        
        
        
        // init components here
        bookName.setText("Buchname");
        bookDescription.setText("Buchbeschreibung");
        
        penChooser = new JComboBox(Pens.getPenArray());
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(4, 2));
        southPanel.add(new JLabel("Buchname"));
        southPanel.add(bookName);
        southPanel.add(new JLabel("Buchbeschreibung"));
        southPanel.add(bookDescription);
        southPanel.add(new JLabel("Labeltyp"));
        southPanel.add(penChooser);
        southPanel.add(new JLabel(""));
        southPanel.add(button);
        
        
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jScrollPane, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        
        
        jScrollPane.setViewportView(bookList);
        
        MyListModel model = new MyListModel();
        bookList.setModel(model);
        
        button.setText("OK");
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int index = bookList.getSelectedIndex();
                if(index != -1) {
                    AndersichtBookDefinition def = new AndersichtBookDefinition();
                    def.bookId = idList.get(index);
                    def.name = bookName.getText();
                    def.description = bookDescription.getText();
                    def.pen = (Pen)penChooser.getSelectedItem();
                    
                    callback.callback(def);
                    setVisible(false);
                }
            }
        });
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        
        HashSet<Integer> inUse = new HashSet<Integer>();
        File[] books = FileEnvironment.getAndersichtDirectory().listFiles();
        for(int i = 0; i < books.length; i++) {
            if(books[i].isDirectory()) {
                try {
                    int id = Integer.parseInt(books[i].getName());
                    if(id != 15000) {
                        inUse.add(id);
                    }
                } catch(NumberFormatException nfe) {
                    log.warn("unable to parse book", nfe);
                }
            }
        }
        for(int i = 1; i < 10000; i++) {
            if(!inUse.contains(i)) {
                idList.add(i);
            }
        }
        
        model.refresh();
        
        setFocusable(false);
        
        pack();
        setVisible(true);
    }
    
    class MyListModel implements ListModel {

        private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();

        @Override
        public int getSize() {
            return(idList.size());
        }

        @Override
        public Object getElementAt(int index) {
            return(idList.get(index));
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }
        
        public void refresh() {
            Iterator<ListDataListener> i = listeners.iterator();
            while(i.hasNext()) {
                i.next().contentsChanged(null);
            }   
        }
    }

}
