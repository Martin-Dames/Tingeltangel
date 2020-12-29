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
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import tingeltangel.Tingeltangel;
import tingeltangel.core.Tupel;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class AndersichtChooseBook extends javax.swing.JDialog {

    
    private final LinkedList<Tupel<Integer, String>> idList = new LinkedList<Tupel<Integer, String>>();
    private final static Logger LOG = LogManager.getLogger(AndersichtChooseBook.class);
    private JList bookList = new JList();
    private JButton button = new JButton();
    private JScrollPane jScrollPane = new JScrollPane();
    
    public AndersichtChooseBook(java.awt.Frame parent, final Callback<Integer> callback) {
        super(parent, false);
        
        setTitle(Tingeltangel.MAIN_FRAME_TITLE + " - Buch laden");
        
        setSize(300, 400);
        
        // init components here
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jScrollPane, BorderLayout.CENTER);
        getContentPane().add(button, BorderLayout.SOUTH);
        jScrollPane.setViewportView(bookList);
        
        MyListModel model = new MyListModel();
        bookList.setModel(model);
        
        button.setText("OK");
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int index = bookList.getSelectedIndex();
                if(index != -1) {
                    int id = idList.get(index).a;
                    callback.callback(id);
                    AndersichtChooseBook.this.dispose();
                }
            }
        });
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        File[] books = FileEnvironment.getAndersichtDirectory().listFiles();
        for(int i = 0; i < books.length; i++) {
            if(books[i].isDirectory()) {
                try {
                    int id = Integer.parseInt(books[i].getName());
                    if(id != 15000) {
                        idList.add(new Tupel(id, getLabel(id)));
                    }
                } catch(NumberFormatException nfe) {
                    LOG.warn("unable to parse book", nfe);
                } catch(IOException ioe) {
                    LOG.warn("unable to parse book", ioe);
                }
            }
        }
        
        model.refresh();
        
        setFocusable(false);
        
        //pack();
        setVisible(true);
    }
    
    private String getLabel(int bookId) throws IOException {
        File file = FileEnvironment.getAndersichtBookFile(Integer.toString(bookId));
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        String label = in.readUTF();
        in.close();
        return(label);
    }
    
    class MyListModel implements ListModel {

        private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();

        @Override
        public int getSize() {
            return(idList.size());
        }

        @Override
        public Object getElementAt(int index) {
            return(idList.get(index).b);
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
