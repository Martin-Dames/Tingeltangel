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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.Tingeltangel;
import tingeltangel.andersicht.AndersichtBook;
import tingeltangel.andersicht.AndersichtLanguageLayer;
import tingeltangel.tools.Callback;
import tingeltangel.tools.Progress;
import tingeltangel.tools.ProgressDialog;

/**
 *
 * @author mdames
 */
public class AndersichtMainFrame extends JFrame {
    
    private final AndersichtPanel mainPanel;
    private AndersichtBook book = null;
    private final AndersichtLanguageLayer languageLayer = null;
    
    private final static Logger LOG = LogManager.getLogger(AndersichtMainFrame.class);
    
    public AndersichtMainFrame() {
        super(Tingeltangel.ANDERSICHT_FRAME_TITLE + Tingeltangel.MAIN_FRAME_VERSION);
        
        
        mainPanel = new AndersichtPanel(this);
        
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        setBounds(
                    Tingeltangel.MAIN_FRAME_POS_X,
                    Tingeltangel.MAIN_FRAME_POS_Y,
                    Tingeltangel.MAIN_FRAME_WIDTH + getInsets().left + getInsets().right,
                    Tingeltangel.MAIN_FRAME_HEIGHT + getInsets().top + getInsets().bottom
        );

        setJMenuBar(getAndersichtMenuBar());
        
        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if((book != null) && book.unsaved()) {
                    int value =  JOptionPane.showConfirmDialog(AndersichtMainFrame.this, "Das aktuelle Buch ist nicht gespeichert. wollen sie das aktuelle buch speichern?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (value == JOptionPane.YES_OPTION) {
                        try {
                            book.save();
                        } catch(Exception ex) {
                            JOptionPane.showMessageDialog(AndersichtMainFrame.this, "Das Buch konnte nicht gespeichert werden");
                            LOG.error("unable to save book (" + book.getName() + ")", ex);
                        }
                    }
                }
                System.exit(0);
            }
        });
        setVisible(true);
        
        
        setContentPane(mainPanel);
        
        // use alt to enter menu
        
        Action menuAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getRootPane().getJMenuBar().getMenu(0).doClick();
            }
        };
        JRootPane rPane = mainPanel.getRootPane();
        final String MENU_ACTION_KEY = "expand_that_first_menu_please";
        rPane.getActionMap().put(MENU_ACTION_KEY, menuAction);
        InputMap inputMap = rPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true), MENU_ACTION_KEY);
        
    }

    AndersichtPanel getAndersichtPanel() {
        return(mainPanel);
    } 
    
    AndersichtBook getBook() {
        return(book);
    }
    
    AndersichtLanguageLayer getCurrentLanguageLayer() {
        return(languageLayer);
    }
    
    
    private final HashSet<JMenuItem> bookNeeded = new HashSet<JMenuItem>();
    
    private JMenuItem newMenuItem(final String action, String label, boolean needBook) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuAction(action);
            }
        });
        if(needBook) {
            item.setEnabled(false);
            bookNeeded.add(item);
        }
        return(item);
    }
    
    private JMenuBar getAndersichtMenuBar() {
        
        JMenuBar bar = new JMenuBar();
        
        JMenu file = new JMenu("Buch");
        file.add(newMenuItem("new", "Neu", false));
        file.add(newMenuItem("load", "Laden", false));
        file.add(newMenuItem("save", "Speichern", true));
        file.add(newMenuItem("generate", "Generieren", true));
        file.add(newMenuItem("exit", "Beenden", false));
        bar.add(file);
        
        JMenu config = new JMenu("Einstellungen");
        config.add(newMenuItem("configure_languages", "Sprachlayer", true));
        config.add(newMenuItem("configure_descriptions", "Beschreibungslayer", true));
        bar.add(config);
        
        JMenu reports = new JMenu("Checks / Reports");
        reports.add(newMenuItem("check_ids", "Label Check", true));
        reports.add(newMenuItem("check_mp3s", "MP3 Check", true));
        reports.add(newMenuItem("report_labels", "Label Report", true));
        bar.add(reports);
        
        return(bar);
    }
    
    private void bookOpened() {
        Iterator<JMenuItem> i = bookNeeded.iterator();
        while(i.hasNext()) {
            i.next().setEnabled(true);
        }
    }
    
    void menuAction(String action) {
        if(action.equals("new")) {
            AndersichtNewBook nb = new AndersichtNewBook(this, new Callback<AndersichtBookDefinition>() {
                @Override
                public void callback(final AndersichtBookDefinition bookDefinition) {
                    Progress pr = new Progress(AndersichtMainFrame.this, "lade Buch") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            book = AndersichtBook.newBook(bookDefinition);
                            book.addLanguageLayer("Standardsprache", "Standardsprache");
                            book.addDescriptionLayer("Standardbeschreibung", "Standardbeschreibung");
                            bookOpened();
                            mainPanel.refresh();
                        }
                    };
                }
            });
        } else if(action.equals("generate")) {
            try {
                book.save();
                
                final JFileChooser fc = new JFileChooser();
                //fc.setFileFilter(new FileNameExtensionFilter("OUT (*.ouf)", "ouf"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                boolean errorOccured = false;
                if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File target = fc.getSelectedFile();
                    String fileName = Integer.toString(book.getBookId());
                    while(fileName.length() < 5) {
                        fileName = "0" + fileName;
                    }
                    fileName += "_en.ouf";
                    try {
                        book.generate(new File(target, fileName));
                    } catch(Exception e) {
                        JOptionPane.showMessageDialog(this, "Fehler beim Generieren des Buches: " + e.getMessage());
                        LOG.error("error generating book", e);
                        errorOccured = true;
                    }
                }
                if(!errorOccured) {
                    JOptionPane.showMessageDialog(this, "Buch erstellt");
                }
            } catch(IOException ioe) {
                JOptionPane.showMessageDialog(this, "Fehler beim speichern des Buches: " + ioe.getMessage());
            }
        } else if(action.equals("configure_languages")) {
            new AndersichtConfigureLanguageLayer(this, book);
        } else if(action.equals("configure_descriptions")) {
            new AndersichtConfigureDescriptionLayer(this, book);
        } else if(action.equals("save")) {
            try {
                book.save();
            } catch(IOException ioe) {
                JOptionPane.showMessageDialog(this, "Fehler beim speichern des Buches: " + ioe.getMessage());
            }
        } else if(action.equals("check_ids")) {
            StringBuffer sb = book.checkLabels();
            if(sb == null) {
                JOptionPane.showMessageDialog(this, "Keine Konflikte oder Fehler gefunden");
            } else {
                JOptionPane.showMessageDialog(this, "Fehler oder Konflikte gefunden:\n" + sb.toString());
            }
        } else if(action.equals("check_mp3s")) {
            StringBuffer sb = book.checkMp3s();
            if(sb == null) {
                JOptionPane.showMessageDialog(this, "Alle MP3s vorhanden");
            } else {
                JOptionPane.showMessageDialog(this, "Fehlende MP3s gefunden:\n" + sb.toString());
            }
        } else if(action.equals("report_labels")) {
            final JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("Label-Report (*.txt)", "txt"));
                //fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
                boolean errorOccured = false;
                if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File target = fc.getSelectedFile();
                    try {
                        PrintWriter out = new PrintWriter(new FileWriter(target));
                        book.generateLabelReport(out);
                        out.close();
                    } catch(Exception e) {
                        JOptionPane.showMessageDialog(this, "Fehler beim Generieren des Label-Reports: " + e.getMessage());
                        LOG.error("error generating label report", e);
                        errorOccured = true;
                    }
                }
                if(!errorOccured) {
                    JOptionPane.showMessageDialog(this, "Label-Report erstellt");
                }
        } else if(action.equals("load")) {
            AndersichtChooseBook cb = new AndersichtChooseBook(this, new Callback<Integer>() {
                @Override
                public void callback(final Integer _id) {
                    try {
                        book = AndersichtBook.load(_id);
                        bookOpened();
                        mainPanel.refresh();
                        LOG.info("book loaded");
                    } catch (IOException ex) {
                        LOG.error("unable to load book", ex);
                    }
                }
            });
        }
    }
    
}
