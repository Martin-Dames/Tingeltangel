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
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.andersicht.AndersichtBook;
import tingeltangel.andersicht.AndersichtLanguageLayer;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
public class AndersichtConfigureLanguageLayer extends javax.swing.JDialog {

    private final static Logger log = LogManager.getLogger(AndersichtConfigureLanguageLayer.class);
    
    private JList languageList = new JList();
    private JScrollPane jScrollPane = new JScrollPane();
    
    private JTextField name = new JTextField();
    private JTextField mp3 = new JTextField();
    private JTextArea description = new JTextArea();
    private JButton label = new JButton();
    private final AndersichtBook book;
    
    
    public AndersichtConfigureLanguageLayer(final AndersichtMainFrame parent, final AndersichtBook book) {
        super(parent, false);
        
        this.book = book;
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        
        JButton setMp3Button = new JButton("MP3 auswählen");
        setMp3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AndersichtLanguageLayer lLayer = (AndersichtLanguageLayer)languageList.getSelectedValue();
                final JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("MP3 (*.mp3)", "mp3"));
                if(fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                    int bookId = book.getBookId();
                    File source = fc.getSelectedFile();
                    File targetDir = FileEnvironment.getAndersichtAudioDirectory(Integer.toString(bookId));
                    String uid = Integer.toString(targetDir.list().length);
                    File target = new File(targetDir, uid + "-" + source.getName());
                    try {
                        FileEnvironment.copy(source, target);
                        lLayer.setMP3(target);
                        if(lLayer.getInternalMP3() == null) {
                            mp3.setText("<kein MP3>");
                        } else {
                            mp3.setText(lLayer.getInternalMP3().getName());
                        }
                    } catch(IOException ioe) {
                        JOptionPane.showMessageDialog(parent, "Fehler importieren des MP3: " + ioe.getMessage());
                    }
                }
            }
        });
        
        panel.add(new JLabel("Name"));
        panel.add(name);
        panel.add(new JLabel("Beschreibung"));
        panel.add(description);
        panel.add(new JLabel("Label"));
        panel.add(label);
        panel.add(new JLabel(""));
        panel.add(setMp3Button);
        panel.add(new JLabel("MP3"));
        panel.add(mp3);
        
        final MyListModel model = new MyListModel();
        languageList.setModel(model);
        
        name.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                AndersichtLanguageLayer lLayer = (AndersichtLanguageLayer)languageList.getSelectedValue();
                if(lLayer != null) {
                    lLayer.setName(name.getText());
                    model.refresh();
                }
            }
        });
        description.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                AndersichtLanguageLayer lLayer = (AndersichtLanguageLayer)languageList.getSelectedValue();
                if(lLayer != null) {
                    lLayer.setDescription(description.getText());
                    model.refresh();
                }
            }
        });
        label.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AndersichtChooseLabel(parent, book.getPen(), new Callback<Integer>() {
                    @Override
                    public void callback(Integer _label) {
                        AndersichtLanguageLayer lLayer = (AndersichtLanguageLayer)languageList.getSelectedValue();
                        if(lLayer != null) {
                            lLayer.setLabel(_label);
                            label.setText(Integer.toString(_label));
                            model.refresh();
                        }
                    }
                });
            }
        });
        
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
                AndersichtConfigureLanguageLayer.this.dispose();
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                book.addLanguageLayer("Name", "Beschreibung");
                model.refresh();
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AndersichtLanguageLayer lLayer = (AndersichtLanguageLayer)languageList.getSelectedValue();
                if((lLayer != null) && (model.getSize() > 1)) {
                    book.removeLanguageLayer(lLayer);
                    model.refresh();
                }
            }
        });
        
        // init components here
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jScrollPane, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.EAST);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        jScrollPane.setViewportView(languageList);
        
        
        languageList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                AndersichtLanguageLayer lLayer = (AndersichtLanguageLayer)languageList.getSelectedValue();
                label.setText(book.getPen().fromTingId(lLayer.getLabel()));
                name.setText(lLayer.getName());
                description.setText(lLayer.getDescription());
                if(lLayer.getInternalMP3() == null) {
                    mp3.setText("<kein MP3>");
                } else {
                    mp3.setText(lLayer.getInternalMP3().getName());
                }
            }
        });
        
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        AndersichtLanguageLayer lLayer = book.getLanguageLayer(0);
        
        languageList.setSelectedIndex(0);
        label.setText(book.getPen().fromTingId(lLayer.getLabel()));
        name.setText(lLayer.getName());
        description.setText(lLayer.getDescription());
        
        if(lLayer.getInternalMP3() == null) {
            mp3.setText("<kein MP3>");
        } else {
            mp3.setText(lLayer.getInternalMP3().getName());
        }
        
        model.refresh();
        
        setFocusableWindowState(false);
        setFocusable(false);

        pack();
        setVisible(true);
    }

    class MyListModel implements ListModel {

        private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();

        @Override
        public int getSize() {
            return(book.getLanguageLayerCount());
        }

        @Override
        public Object getElementAt(int index) {
            return(book.getLanguageLayer(index));
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
