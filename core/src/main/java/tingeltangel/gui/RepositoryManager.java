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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.core.Repository;
import tingeltangel.core.constants.TxtFile;
import tingeltangel.tools.Progress;
import tingeltangel.tools.ProgressDialog;

/**
 *
 * @author mdames
 */
public class RepositoryManager extends javax.swing.JInternalFrame {

    private final MyListModel model = new MyListModel();
    private final EditorFrame masterFrame;
    
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JMenuBar jMenuBar1 = new JMenuBar();
    private final JMenu jMenu1 = new JMenu();
    private final JMenuItem update = new JMenuItem();
    private final JMenuItem search = new JMenuItem();
    
    private final JList list = new JList();
    
    private final static Logger log = LogManager.getLogger(RepositoryManager.class);
    
    /**
     * Creates new form RepositoryManager
     */
    public RepositoryManager(EditorFrame frame) {
        // initComponents();
        super("", true, true, true, true);
        
        
        list.setModel(model);
        
        jMenu1.add(update);
        jMenu1.add(search);
        jMenuBar1.add(jMenu1);
        setJMenuBar(jMenuBar1);
        
        jScrollPane1.setViewportView(list);
        setContentPane(jScrollPane1);
        

        jMenu1.setText("Aktionen");
        update.setText("aktualisieren");
        search.setText("neue Bücher suchen");
        
        update.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });

        search.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        
        
        
        this.masterFrame = frame;
        
        list.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                
                Map<String, String> txt = (Map<String, String>)value;
                
                JLabel label = new JLabel(txt.get("ID") + ": " + txt.get(TxtFile.KEY_NAME) + " (" + txt.get(TxtFile.KEY_AUTHOR) + ")");
                                
                if(Repository.exists(Integer.parseInt(txt.get("ID")))) {
                    label.setBackground(Color.green);
                    label.setOpaque(true);
                }
                return(label);
            }
        });
        
        final JList _list = list;
        
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    
                    new Progress(masterFrame, "Suche nach Aktualisierungen") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                int index = _list.locationToIndex(evt.getPoint());
                                Map<String, String> txt = (Map<String, String>)model.getElementAt(index);
                                int id = Integer.parseInt(txt.get("ID"));
                                if(Repository.exists(id)) {
                                    Repository.update(id, progressDialog);
                                } else {
                                    Repository.download(id, progressDialog);
                                }
                            } catch(IOException e) {
                                JOptionPane.showMessageDialog(RepositoryManager.this, "Download fehlgeschlagen: " + e.toString());
                                log.error("download failed", e);
                            }
                            update();
                        }
                    };
                    
                    
                    
                    int index = list.locationToIndex(evt.getPoint());
                    Map<String, String> txt = (Map<String, String>)model.getElementAt(index);
                    int id = Integer.parseInt(txt.get("ID"));
                    try {
                        if(Repository.exists(id)) {
                            Repository.download(id, null);
                        } else {
                            Repository.update(id, null);
                        }
                    } catch(IOException ioe) {
                        JOptionPane.showMessageDialog(RepositoryManager.this, "Download fehlgeschlagen: " + ioe.getMessage());
                    }
                }
            }
        });
        update();
        setBounds(100, 100, 400, 600);
    }
    
    private void updateActionPerformed(java.awt.event.ActionEvent evt) {                                       
        new Progress(masterFrame, "Suche nach Aktualisierungen") {
            @Override
            public void action(ProgressDialog progressDialog) {
                try {
                    Repository.update(progressDialog);
                } catch(IOException e) {
                    JOptionPane.showMessageDialog(RepositoryManager.this, "Suche nach Aktualisierungen fehlgeschlagen");
                    log.error("unable to search for updates", e);
                } catch(IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(RepositoryManager.this, "Suche nach Aktualisierungen fehlgeschlagen: " + e.getMessage());
                    log.error("unable to search for updates", e);
                }
                update();
            }
        };
        
    }                                      

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {                                       
        new Progress(masterFrame, "Suche nach neuen Büchern") {
            @Override
            public void action(ProgressDialog progressDialog) {
                try {
                    Repository.search(progressDialog);
                } catch(IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(RepositoryManager.this, "Suche nach neuen Büchern fehlgeschlagen: " + e.getMessage());
                }
                update();
            }
        };
    }                                      


    private void update() {
        model.update();
    }
    
    class MyListModel implements ListModel {
            
        private Set<ListDataListener> listeners = new HashSet<ListDataListener>();

        @Override
        public int getSize() {
            Integer[] ids = Repository.getIDs();
            return(ids.length);
        }

        @Override
        public Object getElementAt(int index) {
            int id = Repository.getIDs()[index];
            Map<String, String> txt = Repository.getBookTxt(id);

            String _id = Integer.toString(id);
            while(_id.length() < 5) {
                _id = "0" + _id;
            }

            txt.put("ID", _id);
            
            return(txt);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        void update() {
            Iterator<ListDataListener> i = listeners.iterator();
            while(i.hasNext()) {
                i.next().contentsChanged(null);
            }
        }

    };
}
