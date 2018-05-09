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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import tingeltangel.andersicht.AndersichtBook;
import tingeltangel.andersicht.AndersichtGroup;
import tingeltangel.andersicht.AndersichtObject;
import tingeltangel.andersicht.AndersichtTrack;

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
    private JTextField bookIdField = new JTextField();
    private JTextField bookNameField = new JTextField();
    private JTextField bookDescriptionField = new JTextField();
    
    private JPanel groupOptionPanel = new JPanel();
    private JTextField groupNameField = new JTextField();
    private JTextField groupDescriptionField = new JTextField();
    
    private JPanel objectOptionPanel = new JPanel();
    private JTextField objectNameField = new JTextField();
    private JTextField objectDescriptionField = new JTextField();
    
    private JPanel trackOptionPanel = new JPanel();
    private JTextField trackTranscriptField = new JTextField();
    
    
    private final int ACTION_ADD_GROUP = 1;
    private final int ACTION_ADD_OBJECT = 2;
    
    
    AndersichtPanel(AndersichtMainFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
                
        bookOptionPanel.add(newActionButton(ACTION_ADD_GROUP, "Gruppe hinzufügen"));
        bookOptionPanel.add(bookIdField);
        bookOptionPanel.add(bookNameField);
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
        
        groupOptionPanel.add(newActionButton(ACTION_ADD_OBJECT, "Objekt hinzufügen"));
        groupOptionPanel.add(groupNameField);
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
        
        objectOptionPanel.add(objectNameField);
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
        
        trackOptionPanel.add(trackTranscriptField);
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
        
        
        setLayout(new BorderLayout());
        
        tree = new JTree(new DefaultTreeModel(mainFrame.getBook()));
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object selectedObject = e.getPath().getLastPathComponent();
                optionPanel.removeAll();
                if(selectedObject instanceof AndersichtBook) {
                    AndersichtBook book = (AndersichtBook)selectedObject;
                    optionPanel.add(bookOptionPanel);
                    bookIdField.setText(Integer.toString(book.getBookId()));
                    bookNameField.setText(book.getName());
                    bookDescriptionField.setText(book.getDescription());
                } else if(selectedObject instanceof AndersichtGroup) {
                    AndersichtGroup group = (AndersichtGroup)selectedObject;
                    optionPanel.add(groupOptionPanel);
                    groupNameField.setText(group.getName());
                    groupDescriptionField.setText(group.getDescription());
                } else if(selectedObject instanceof AndersichtObject) {
                    AndersichtObject object = (AndersichtObject)selectedObject;
                    optionPanel.add(objectOptionPanel);
                    objectNameField.setText(object.getName());
                    objectDescriptionField.setText(object.getDescription());
                } else if(selectedObject instanceof AndersichtTrack) {
                    AndersichtTrack track = (AndersichtTrack)selectedObject;
                    optionPanel.add(trackOptionPanel);
                    trackTranscriptField.setText(track.getTranscript());
                }
                optionPanel.revalidate();
                optionPanel.repaint();
            }
        });
        
        JPanel languageChooserPanel = new JPanel();
        
        languageChooserPanel.add(languageChooser);
        
        add(languageChooserPanel, BorderLayout.NORTH);
        
        optionPanel = new JPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, optionPanel);
        
        add(splitPane, BorderLayout.CENTER);
        splitPane.setDividerLocation(300);
        
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

