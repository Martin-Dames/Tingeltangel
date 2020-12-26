
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tingeltangel.Tingeltangel;
import tingeltangel.andersicht.AndersichtBook;
import tingeltangel.andersicht.AndersichtDescriptionLayer;

/**
 *
 * @author mdames
 */
public class AndersichtConfigureDescriptionLayer extends javax.swing.JDialog {

    private final static Logger log = LogManager.getLogger(AndersichtConfigureDescriptionLayer.class);
    
    private JList descriptionList = new JList();
    private final JScrollPane jScrollPane = new JScrollPane();
    
    private JTextField name = new JTextField();
    private JTextArea description = new JTextArea();
    private final AndersichtBook book;
    
    
    public AndersichtConfigureDescriptionLayer(final AndersichtMainFrame parent, final AndersichtBook book) {
        super(parent, false);
        
        setTitle(Tingeltangel.MAIN_FRAME_TITLE + " - Beschreibungslayer");
        
        this.book = book;
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 2;
        panel.add(name, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Beschreibung"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 2;
        panel.add(description, gbc);
        
        final MyListModel model = new MyListModel();
        descriptionList.setModel(model);
        
        name.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                AndersichtDescriptionLayer dLayer = (AndersichtDescriptionLayer)descriptionList.getSelectedValue();
                if(dLayer != null) {
                    dLayer.setName(name.getText());
                    model.refresh();
                }
            }
        });
        description.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                AndersichtDescriptionLayer dLayer = (AndersichtDescriptionLayer)descriptionList.getSelectedValue();
                if(dLayer != null) {
                    dLayer.setDescription(description.getText());
                    model.refresh();
                }
            }
        });
        
        description.setRows(10);
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("hinzufügen");
        JButton removeButton = new JButton("entfernen");
        JButton okButton = new JButton("OK");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(okButton);
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getAndersichtPanel().refresh();
                AndersichtConfigureDescriptionLayer.this.dispose();
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                book.addDescriptionLayer("Name", "Beschreibung");
                model.refresh();
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AndersichtDescriptionLayer dLayer = (AndersichtDescriptionLayer)descriptionList.getSelectedValue();
                if((dLayer != null) && (model.getSize() > 1)) {
                    book.removeDescriptionLayer(dLayer);
                    model.refresh();
                }
            }
        });
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        centerPanel.add(jScrollPane);
        centerPanel.add(panel);
        
        // init components here
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        jScrollPane.setViewportView(descriptionList);
        
        
        descriptionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                AndersichtDescriptionLayer dLayer = (AndersichtDescriptionLayer)descriptionList.getSelectedValue();
                name.setText(dLayer.getName());
                description.setText(dLayer.getDescription());
            }
        });
        
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        AndersichtDescriptionLayer dLayer = book.getDescriptionLayer(0);
        
        descriptionList.setSelectedIndex(0);
        name.setText(dLayer.getName());
        description.setText(dLayer.getDescription());
        
        
        model.refresh();
        
        setFocusable(false);
        
        setSize(500, 400);
        //pack();
        setVisible(true);
    }

    class MyListModel implements ListModel {

        private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();

        @Override
        public int getSize() {
            return(book.getDescriptionLayerCount());
        }

        @Override
        public Object getElementAt(int index) {
            return(book.getDescriptionLayer(index));
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
