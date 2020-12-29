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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultComboBoxModel;
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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.andersicht.AndersichtBook;
import tingeltangel.andersicht.AndersichtDescriptionLayer;
import tingeltangel.andersicht.AndersichtGroup;
import tingeltangel.andersicht.AndersichtLanguageLayer;
import tingeltangel.andersicht.AndersichtObject;
import tingeltangel.andersicht.AndersichtTrack;
import tingeltangel.core.TingStick;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;

/**
 *
 * @author mdames
 */
class AndersichtPanel extends JPanel {
    
    private final AndersichtMainFrame mainFrame;
    
    
    private final static Logger LOG = LogManager.getLogger(AndersichtPanel.class);
        
    private final JComboBox languageChooser;
    private final DefaultComboBoxModel languageChooserModel = new DefaultComboBoxModel();
    
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
    
    private JButton deploymentButton = new JButton();
    
    private File lastMp3Dir = null;
    
    private final int ACTION_ADD_GROUP = 1;
    private final int ACTION_ADD_OBJECT = 2;
    private final int ACTION_ADD_MP3 = 3;
    
    
    private boolean online = false;
    
    
    AndersichtPanel(final AndersichtMainFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
                
        bookDescriptionField.setRows(10);
        groupDescriptionField.setRows(10);
        objectDescriptionField.setRows(10);
        trackTranscriptField.setRows(10);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        bookOptionPanel.setLayout(new GridBagLayout());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        bookOptionPanel.add(newActionButton(ACTION_ADD_GROUP, "Gruppe hinzufügen"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        bookOptionPanel.add(new JLabel("Buch ID"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 2;
        bookOptionPanel.add(bookIdLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        bookOptionPanel.add(new JLabel("Buchname"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 2;
        bookOptionPanel.add(bookNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        bookOptionPanel.add(new JLabel("Buchbeschreibung"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 2;
        bookOptionPanel.add(bookDescriptionField, gbc);
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
        
        
        groupOptionPanel.setLayout(new GridBagLayout());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        groupOptionPanel.add(newActionButton(ACTION_ADD_OBJECT, "Objekt hinzufügen"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        groupOptionPanel.add(new JLabel("Guppenname"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 2;
        groupOptionPanel.add(groupNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        groupOptionPanel.add(new JLabel("Guppenbeschreibung"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 2;
        groupOptionPanel.add(groupDescriptionField, gbc);
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
        
        
        objectOptionPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        objectOptionPanel.add(new JLabel("Objektname"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 2;
        objectOptionPanel.add(objectNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        objectOptionPanel.add(new JLabel("Objektbeschreibung"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 2;
        objectOptionPanel.add(objectDescriptionField, gbc);
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
        
        
        trackOptionPanel.setLayout(new GridBagLayout());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 2;
        trackOptionPanel.add(newActionButton(ACTION_ADD_MP3, "MP3 auswählen"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        trackOptionPanel.add(new JLabel("Transkript"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 2;
        trackOptionPanel.add(trackTranscriptField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        trackOptionPanel.add(new JLabel("MP3"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 2;
        trackOptionPanel.add(trackMp3Label, gbc);
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
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        trackOptionPanel.add(new JLabel("Label"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 2;
        trackOptionPanel.add(trackLabelButton, gbc);
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
        
        languageChooser = new JComboBox(languageChooserModel);
        languageChooser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    AndersichtBook book = mainFrame.getBook();
                    if(book != null) {
                        AndersichtLanguageLayer lLayer = (AndersichtLanguageLayer)e.getItem();
                        book.setActiveLanguageLayer(lLayer);
                        
                        TreePath path = tree.getSelectionPath();
                        if(path != null) {
                            if(path.getLastPathComponent() instanceof AndersichtTrack) {
                                
                                AndersichtTrack track = (AndersichtTrack)path.getLastPathComponent();
                                path = path.getParentPath();
                                AndersichtObject object = (AndersichtObject)path.getLastPathComponent();
                                AndersichtDescriptionLayer dLayer = object.getDescriptionLayer(track);
                                path = path.pathByAddingChild(object.getTrack(lLayer, dLayer));
                                
                            }
                        }
                        
                        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                        model.setRoot(book);
                        if(path != null) {
                            tree.setSelectionPath(path);
                        }
                    }
                }
            }
        });
        
        
        
        optionPanel = new JPanel();
        optionPanel.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, optionPanel);
        
        add(splitPane, BorderLayout.CENTER);
        splitPane.setDividerLocation(500);
        
        deploymentButton.setText("keinen Stift gefunden");
        deploymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    AndersichtBook book = mainFrame.getBook();
                    book.save();
                    try {
                        String fileName = Integer.toString(book.getBookId());
                        while(fileName.length() < 5) {
                            fileName = "0" + fileName;
                        }
                        fileName += "_en.ouf";
                        File file = new File(FileEnvironment.getAndersichtBookDirectory(Integer.toString(book.getBookId())), fileName);
                        book.generate(file);
                        try {
                            // copy to stick
                            TingStick stick = TingStick.getStick();
                            if(stick != null) {
                                File dest = stick.getBookDir();
                                if(!dest.getAbsolutePath().contains("$ting")) {
                                    dest = new File(stick.getBookDir(), "$ting");
                                }
                                FileEnvironment.copy(file, new File(dest, file.getName()));
                                stick.activateBook(book.getBookId());
                                JOptionPane.showMessageDialog(mainFrame, "Buch auf den Stift kopiert");
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, "Stift nicht gefunden");
                            }
                        } catch(IOException e3) {
                            JOptionPane.showMessageDialog(mainFrame, "Fehler beim Kopieren des Buches auf den Stift: " + e3.getMessage());
                        }
                    } catch(IOException e2) {
                        JOptionPane.showMessageDialog(mainFrame, "Fehler beim Generieren des Buches: " + e2.getMessage());
                    }
                } catch(IOException e1) {
                    JOptionPane.showMessageDialog(mainFrame, "Fehler beim Speichern des Buches: " + e1.getMessage());
                }
            }
        });
        
        Runnable task = new TimerTask() {
            @Override
            public void run() {
                try {
                    TingStick stick = TingStick.getStick();
                    if(online && (stick == null)) {
                        // go offline
                        online = false;
                        deploymentButton.setEnabled(false);
                        deploymentButton.setText("keinen Stift gefunden");
                    } else if((!online) && (stick != null) && (mainFrame.getBook() != null)) {
                        // go online
                        online = true;
                        deploymentButton.setEnabled(true);
                        deploymentButton.setText("auf den Stift kopieren");
                    }
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(task, 3, 3, TimeUnit.SECONDS);
        deploymentButton.setEnabled(false);
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridy = 0;
        
        gbc.gridx = 0;
        topPanel.add(languageChooser, gbc);
        gbc.gridx = 1;
        topPanel.add(deploymentButton, gbc);
        
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    public DefaultComboBoxModel getLanguageChooserModel() {
        return(languageChooserModel);
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
                if((lastMp3Dir != null) && lastMp3Dir.isDirectory()) {
                    fc.setCurrentDirectory(lastMp3Dir);
                }
                if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    int bookId = track.getObject().getGroup().getBook().getBookId();
                    File source = fc.getSelectedFile();
                    lastMp3Dir = source.getParentFile();
                    File targetDir = FileEnvironment.getAndersichtAudioDirectory(Integer.toString(bookId));
                    String uid = Integer.toString(targetDir.list().length);
                    File target = new File(targetDir, uid + "-" + source.getName());
                    try {
                        FileEnvironment.copy(source, target);
                        track.setMP3(target);
                        trackMp3Label.setText(track.getInternalMP3().getName());
                    } catch(IOException ioe) {
                        JOptionPane.showMessageDialog(this, "Fehler beim Importieren des MP3: " + ioe.getMessage());
                    }
                }
                break;
        }
    }
    
    public void refresh() {
        AndersichtBook book = mainFrame.getBook();
        if(book != null) {
            LOG.info("refresh()");
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            model.setRoot(book);
            languageChooserModel.removeAllElements();
            for(int i = 0; i < book.getLanguageLayerCount(); i++) {
                languageChooserModel.addElement(book.getLanguageLayer(i));
            }
            LOG.info("refresh finished");
        }
    }
    
}

