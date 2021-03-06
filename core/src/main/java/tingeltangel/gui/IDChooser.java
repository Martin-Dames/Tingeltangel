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

import tingeltangel.tools.Callback;
import java.awt.Dialog;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;
import tingeltangel.core.Tupel;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class IDChooser extends javax.swing.JDialog {

    private final LinkedList<Tupel<Integer, String>> idList = new LinkedList<Tupel<Integer, String>>();
    private MyListModel model = new MyListModel();
    private Callback<Integer> callback;
    
    /**
     * Creates new form IDChooser
     */
    public IDChooser(java.awt.Frame parent, Callback<Integer> callback) {
        super(parent, false);
        initComponents();
        this.callback = callback;
        list.setModel(model);
        niceIDActionPerformed(null);
        setVisible(true);
    }
    
    public IDChooser(Dialog parent, Callback<Integer> callback) {
        super(parent, false);
        initComponents();
        this.callback = callback;
        list.setModel(model);
        niceIDActionPerformed(null);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        niceID = new javax.swing.JRadioButton();
        freeID = new javax.swing.JRadioButton();
        anyID = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        buttonGroup1.add(niceID);
        niceID.setSelected(true);
        niceID.setText("empfohlene & freie MID (empfohlen)");
        niceID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                niceIDActionPerformed(evt);
            }
        });

        buttonGroup1.add(freeID);
        freeID.setText("freie MID");
        freeID.setToolTipText("");
        freeID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freeIDActionPerformed(evt);
            }
        });

        buttonGroup1.add(anyID);
        anyID.setText("beliebige MID");
        anyID.setToolTipText("");
        anyID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anyIDActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Bitte wähle eine Option aus");
        jLabel1.setToolTipText("");

        list.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(list);

        button.setText("OK");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(freeID)
                                    .addComponent(niceID)
                                    .addComponent(anyID))))
                        .addGap(0, 171, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(niceID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(freeID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(anyID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean isNiceID(int id) {
        return((id >= 8000) && (id <= 8500)) || ((id >= 9500) && (id <= Translator.MAX_MID));
    }
    
    private String renderName(int id) {
        String name = Integer.toString(id);
        while(name.length() < 5) {
            name = "0" + name;
        }
        HashMap<String, String> txt = Repository.getBookTxt(id);
        if(txt != null) {
            name += " " + txt.get("Name") + " (" + txt.get("Author") + ")";
        }
        return(name);
    }
    
    private void niceIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_niceIDActionPerformed
        idList.clear();
        
        File[] books = FileEnvironment.getBooksDirectory().listFiles();
        HashSet<Integer> _books = new HashSet<Integer>();
        for(int i = 0; i < books.length; i++) {
            try {
                _books.add(Integer.parseInt(books[i].getName()));
            } catch(NumberFormatException nfe) {
                ;
            }
        }
        
        for(int i = 0; i <= Translator.MAX_MID; i++) {
            if((Translator.ting2code(i) >= 0) && isNiceID(i)) {
                // check if book with mid i already exists
                if(!_books.contains(i)) {
                    idList.add(new Tupel(i, renderName(i)));
                }
            }
        }
        model.refresh();
    }//GEN-LAST:event_niceIDActionPerformed

    private void freeIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freeIDActionPerformed
        idList.clear();
        for(int i = 0; i <= Translator.MAX_MID; i++) {
            if(Repository.getBookTxt(i) == null) {
                idList.add(new Tupel(i, renderName(i)));
            }
        }
        model.refresh();
    }//GEN-LAST:event_freeIDActionPerformed

    private void anyIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anyIDActionPerformed
        idList.clear();
        for(int i = 0; i <= Translator.MAX_MID; i++) {
            idList.add(new Tupel(i, renderName(i)));
        }
        model.refresh();
    }//GEN-LAST:event_anyIDActionPerformed

    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionPerformed
        int index = list.getSelectedIndex();
        if(index != -1) {
            int id = idList.get(index).a;
            callback.callback(id);
            setVisible(false);
        }
    }//GEN-LAST:event_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton anyID;
    private javax.swing.JButton button;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton freeID;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list;
    private javax.swing.JRadioButton niceID;
    // End of variables declaration//GEN-END:variables



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
