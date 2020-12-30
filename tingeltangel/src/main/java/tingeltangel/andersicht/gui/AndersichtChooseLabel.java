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
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import tingeltangel.andersicht.pen.Pen;
import tingeltangel.core.Tupel;
import tingeltangel.tools.Callback;

/**
 *
 * @author mdames
 */
public class AndersichtChooseLabel extends javax.swing.JDialog {

    
    private final LinkedList<Tupel<Integer, String>> idList = new LinkedList<Tupel<Integer, String>>();
    private final static Logger log = LogManager.getLogger(AndersichtChooseLabel.class);
    private JList labelList = new JList();
    private JButton button = new JButton();
    private JScrollPane jScrollPane = new JScrollPane();
    
    public AndersichtChooseLabel(java.awt.Frame parent, Pen pen, final Callback<Integer> callback) {
        super(parent, false);
        
        // init components here
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jScrollPane, BorderLayout.CENTER);
        getContentPane().add(button, BorderLayout.SOUTH);
        jScrollPane.setViewportView(labelList);
        
        MyListModel model = new MyListModel();
        labelList.setModel(model);
        
        button.setText("OK");
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int index = labelList.getSelectedIndex();
                if(index != -1) {
                    int id = idList.get(index).a;
                    callback.callback(id);
                    setVisible(false);
                }
            }
        });
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        idList.addAll(pen.getLabelList());
        
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
