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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import tingeltangel.andersicht.AndersichtBook;
import tingeltangel.andersicht.AndersichtDescriptionLayer;
import tingeltangel.andersicht.AndersichtGroup;
import tingeltangel.andersicht.AndersichtObject;
import tingeltangel.andersicht.AndersichtTrack;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
class AndersichtPanel extends JPanel {
    
    private final AndersichtMainFrame mainFrame;
        
    private final JComboBox languageChooser = new JComboBox();
    private JTree tree;
    private final JSplitPane splitPane;
    private JPanel optionPanel;
    
    private JPanel bookOptionPanel = new JPanel();
    private JLabel bookIdLabel = new JLabel();
    private JTextField bookNameField = new JTextField();
    private JTextArea bookDescriptionField = new JTextArea();
    
    private JPanel groupOptionPanel = new JPanel();
    private JTextField groupNameField = new JTextField();
    private JTextArea groupDescriptionField = new JTextArea();
    
    private JPanel objectOptionPanel = new JPanel();
    private JTextField objectNameField = new JTextField();
    private JTextArea objectDescriptionField = new JTextArea();
    
    private JPanel trackOptionPanel = new JPanel();
    private JTextArea trackTranscriptField = new JTextArea();
    private JLabel trackMp3Label = new JLabel();
    private JButton trackLabelButton = new JButton();
    
    
    private final int ACTION_ADD_GROUP = 1;
    private final int ACTION_ADD_OBJECT = 2;
    private final int ACTION_ADD_MP3 = 3;
    
    
    AndersichtPanel(final AndersichtMainFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
                
        bookDescriptionField.setRows(4);
        groupDescriptionField.setRows(4);
        objectDescriptionField.setRows(4);
        trackTranscriptField.setRows(4);
        
        bookOptionPanel.setLayout(new GridLayout(4, 2));
        bookOptionPanel.add(new JLabel(""));
        bookOptionPanel.add(newActionButton(ACTION_ADD_GROUP, "Gruppe hinzufügen"));
        bookOptionPanel.add(new JLabel("Buch ID"));
        bookOptionPanel.add(bookIdLabel);
        bookOptionPanel.add(new JLabel("Buchname"));
        bookOptionPanel.add(bookNameField);
        bookOptionPanel.add(new JLabel("Buchbeschreibung"));
        bookOptionPanel.add(bookDescriptionField);
        bookNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                TreePath selectionPath = tree.getSelectionPath();
                if(selectionPath != null) {
                    AndersichtBook book = (AndersichtBook)selectionPath.getLastPathComponent();
                    book.setName(bookNameField.getText());
                    ((DefaultTreeModel)tree.getModel()).nodeChanged(book);
                }
            }
        });
        
        groupOptionPanel.setLayout(new GridLayout(3, 2));
        groupOptionPanel.add(new JLabel(""));
        groupOptionPanel.add(newActionButton(ACTION_ADD_OBJECT, "Objekt hinzufügen"));
        groupOptionPanel.add(new JLabel("Guppenname"));
        groupOptionPanel.add(groupNameField);
        groupOptionPanel.add(new JLabel("Guppenbeschreibung"));
        groupOptionPanel.add(groupDescriptionField);
        groupNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                TreePath selectionPath = tree.getSelectionPath();
                if(selectionPath != null) {
                    AndersichtGroup group = (AndersichtGroup)selectionPath.getLastPathComponent();
                    group.setName(groupNameField.getText());
                    ((DefaultTreeModel)tree.getModel()).nodeChanged(group);
                }
            }
        });
        groupDescriptionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                TreePath selectionPath = tree.getSelectionPath();
                if(selectionPath != null) {
                    AndersichtGroup group = (AndersichtGroup)selectionPath.getLastPathComponent();
                    group.setDescription(groupDescriptionField.getText());
                }
            }
        });
        
        objectOptionPanel.setLayout(new GridLayout(2, 2));
        objectOptionPanel.add(new JLabel("Objektname"));
        objectOptionPanel.add(objectNameField);
        objectOptionPanel.add(new JLabel("Objektbeschreibung"));
        objectOptionPanel.add(objectDescriptionField);
        
        objectNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                TreePath selectionPath = tree.getSelectionPath();
                if(selectionPath != null) {
                    AndersichtObject object = (AndersichtObject)selectionPath.getLastPathComponent();
                    object.setName(objectNameField.getText());
                    ((DefaultTreeModel)tree.getModel()).nodeChanged(object);
                }
            }
        });
        objectDescriptionField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                TreePath selectionPath = tree.getSelectionPath();
                if(selectionPath != null) {
                    AndersichtObject object = (AndersichtObject)selectionPath.getLastPathComponent();
                    object.setDescription(objectDescriptionField.getText());
                }
            }
        });
        
        
        trackOptionPanel.setLayout(new GridLayout(4, 2));
        trackOptionPanel.add(new JLabel(""));
        trackOptionPanel.add(newActionButton(ACTION_ADD_MP3, "MP3 auswählen"));
        trackOptionPanel.add(new JLabel("Transkript"));
        trackOptionPanel.add(trackTranscriptField);
        trackOptionPanel.add(new JLabel("MP3"));
        trackOptionPanel.add(trackMp3Label);
        trackTranscriptField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void change() {
                TreePath selectionPath = tree.getSelectionPath();
                if(selectionPath != null) {
                    AndersichtTrack track = (AndersichtTrack)selectionPath.getLastPathComponent();
                    track.setTranscript(trackTranscriptField.getText());
                }
            }
        });
        objectOptionPanel.add(new JLabel("Label"));
        objectOptionPanel.add(trackLabelButton);
        trackLabelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // choose new label
                new AndersichtChooseLabel(mainFrame, mainFrame.getBook().getPen(), new Callback<Integer>() {
                    @Override
                    public void callback(Integer label) {
                        TreePath selectionPath = tree.getSelectionPath();
                        if(selectionPath != null) {
                            AndersichtTrack track = (AndersichtTrack)selectionPath.getLastPathComponent();
                            AndersichtDescriptionLayer dl = track.getObject().getDescriptionLayer(track);
                            track.getObject().setLabel(dl, label);
                            trackLabelButton.setText(track.getObject().getLabelAsString(dl));
                        }
                    }
                });
            }
        });
        
        
        setLayout(new BorderLayout());
        
        tree = new JTree(new DefaultTreeModel(mainFrame.getBook()));
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object selectedObject = e.getPath().getLastPathComponent();
                optionPanel.removeAll();
                if(selectedObject instanceof AndersichtBook) {
                    AndersichtBook book = (AndersichtBook)selectedObject;
                    optionPanel.add(bookOptionPanel, BorderLayout.NORTH);
                    bookIdLabel.setText(Integer.toString(book.getBookId()));
                    bookNameField.setText(book.getName());
                    bookDescriptionField.setText(book.getDescription());
                } else if(selectedObject instanceof AndersichtGroup) {
                    AndersichtGroup group = (AndersichtGroup)selectedObject;
                    optionPanel.add(groupOptionPanel, BorderLayout.NORTH);
                    groupNameField.setText(group.getName());
                    groupDescriptionField.setText(group.getDescription());
                } else if(selectedObject instanceof AndersichtObject) {
                    AndersichtObject object = (AndersichtObject)selectedObject;
                    optionPanel.add(objectOptionPanel, BorderLayout.NORTH);
                    objectNameField.setText(object.getName());
                    objectDescriptionField.setText(object.getDescription());
                } else if(selectedObject instanceof AndersichtTrack) {
                    AndersichtTrack track = (AndersichtTrack)selectedObject;
                    optionPanel.add(trackOptionPanel, BorderLayout.NORTH);
                    trackTranscriptField.setText(track.getTranscript());
                    File mp3 = track.getInternalMP3();
                    if(mp3 == null) {
                        trackMp3Label.setText("<kein MP3>");
                    } else {
                        trackMp3Label.setText(mp3.getName());
                    }
                    trackLabelButton.setText(track.getObject().getLabelAsString(track.getObject().getDescriptionLayer(track)));
                }
                optionPanel.revalidate();
                optionPanel.repaint();
            }
        });
        
        JPanel languageChooserPanel = new JPanel();
        
        languageChooserPanel.add(languageChooser);
        
        add(languageChooserPanel, BorderLayout.NORTH);
        
        optionPanel = new JPanel();
        optionPanel.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, optionPanel);
        
        add(splitPane, BorderLayout.CENTER);
        splitPane.setDividerLocation(500);
        
    }
    
    private JButton newActionButton(final int action, String label) {
        JButton button = new JButton(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(action);
            }
        });
        return(button);
    }

    void action(int action) {
        TreePath path = tree.getSelectionPath();
        Object selectedObject = path.getLastPathComponent();
        int[] childIndices = new int[1];
        switch(action) {
            case ACTION_ADD_GROUP:
                AndersichtBook book = (AndersichtBook)selectedObject;
                book.addGroup("neue Gruppe", "");
                childIndices[0] = book.getChildCount() - 1;
                ((DefaultTreeModel)tree.getModel()).nodesWereInserted(book, childIndices);
                break;
            case ACTION_ADD_OBJECT:
                AndersichtGroup group = (AndersichtGroup)selectedObject;
                group.addObject("neues Objekt", "");
                childIndices[0] = group.getChildCount() - 1;
                ((DefaultTreeModel)tree.getModel()).nodesWereInserted(group, childIndices);
                break;
            case ACTION_ADD_MP3:
                AndersichtTrack track = (AndersichtTrack)selectedObject;
                final JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("MP3 (*.mp3)", "mp3"));
                if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    int bookId = track.getObject().getGroup().getBook().getBookId();
                    File source = fc.getSelectedFile();
                    File targetDir = FileEnvironment.getAndersichtAudioDirectory(Integer.toString(bookId));
                    String uid = Integer.toString(targetDir.list().length);
                    File target = new File(targetDir, uid + "-" + source.getName());
                    try {
                        FileEnvironment.copy(source, target);
                        track.setMP3(target);
                    } catch(IOException ioe) {
                        JOptionPane.showMessageDialog(this, "Fehler beim Importieren des MP3: " + ioe.getMessage());
                    }
                }
                break;
        }
    }
    
    void refresh() {
        AndersichtBook book = mainFrame.getBook();
        if(book != null) {
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            model.setRoot(book);
            languageChooser.removeAllItems();
            for(int i = 0; i < book.getLanguageLayerCount(); i++) {
                languageChooser.addItem(book.getLanguageLayer(i));
            }
        }
    }
    
}

