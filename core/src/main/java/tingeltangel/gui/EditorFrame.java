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

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
import tingeltangel.cli_ng.CLI;
import tingeltangel.core.Book;
import tingeltangel.core.Codes;
import tingeltangel.core.Entry;
import tingeltangel.core.Importer;
import tingeltangel.core.ReadYamlFile;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.Progress;
import tingeltangel.tools.ProgressDialog;
import tingeltangel.tools.ZipHelper;
import tingeltangel.wimmelbuch.Wimmelbuch;
import tiptoi_reveng.lexer.LexerException;
import tiptoi_reveng.parser.ParserException;

public class EditorFrame extends JFrame implements Callback<String> {

    private Book book = new Book(15000);
    
    private final EditorPanel indexPanel;
    private final InfoFrame contactFrame = new InfoFrame("Kontakt", "html/contact.html");
    private final InfoFrame licenseFrame = new InfoFrame("Lizenz", "html/license.html");
    private final InfoFrame manualFrame = new InfoFrame("Handbuch", "html/manual.html");
    
    private final LinkedList<EntryListener> listeners = new LinkedList<EntryListener>();
    
    private final static Logger log = LogManager.getLogger(EditorFrame.class);
    
    public EditorFrame() {
        super(Tingeltangel.MAIN_FRAME_TITLE + Tingeltangel.MAIN_FRAME_VERSION);
        
        
        indexPanel = new EditorPanel(this);
        
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        setBounds(
                    Tingeltangel.MAIN_FRAME_POS_X,
                    Tingeltangel.MAIN_FRAME_POS_Y,
                    Tingeltangel.MAIN_FRAME_WIDTH + getInsets().left + getInsets().right,
                    Tingeltangel.MAIN_FRAME_HEIGHT + getInsets().top + getInsets().bottom
        );

        MasterFrameMenu.setMenuCallback(this);
        setJMenuBar(MasterFrameMenu.getMenuBar());
        
        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeMasterFrame();
            }
        });
        setVisible(true);
        
        
        setContentPane(indexPanel);
        
        // use alt to enter menu
        
        Action menuAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getRootPane().getJMenuBar().getMenu(0).doClick();
            }
        };
        JRootPane rPane = indexPanel.getRootPane();
        final String MENU_ACTION_KEY = "expand_that_first_menu_please";
        rPane.getActionMap().put(MENU_ACTION_KEY, menuAction);
        InputMap inputMap = rPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true), MENU_ACTION_KEY);
        
        
        book.resetChangeMade();
        indexPanel.setVisible(false);
    }
    
    public void setBookOpened() {
        indexPanel.setVisible(true);
        JMenuBar bar = getJMenuBar();
        if(bar != null) {
            for(int i = 0; i < bar.getMenuCount(); i++) {
                enableMenu(bar.getMenu(i));
            }
        }
    }
    
    private void enableMenu(JMenu menu) {
        menu.setEnabled(true);
        for(int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if(item instanceof JMenu) {
                enableMenu((JMenu)item);
            } else {
                item.setEnabled(true);
            }
        }
    }
    
    public Book getBook() {
        return(book);
    }
    
    
    void addEntryListener(EntryListener listener) {
        listeners.add(listener);
    }
    
    void entrySelected(int i) {
        Entry entry = book.getEntry(i);
        Iterator<EntryListener> it = listeners.iterator();
        while(it.hasNext()) {
            it.next().entrySelected(entry);
        }
    }
    
    private void closeMasterFrame() {
        if(book.unsaved()) {
            int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie das aktuelle buch speichern?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (value == JOptionPane.YES_OPTION) {
                try {
                    book.save();
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "Das Buch konnte nicht gespeichert werden");
                    log.error("unable to save book (" + book.getID() + ")", e);
                }
            }
        }
        System.exit(0);
    }

    @Override
    public void callback(String id) {
        if(id.equals("buch.exit")) {
            closeMasterFrame();
        } else if(id.equals("buch.new")) {
            boolean newBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein neues Buch erstellen?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    newBook = true;
                }
            } else {
                newBook = true;
            }
            if(newBook) {
                
                IDChooser ic = new IDChooser(this, new Callback<Integer>() {

                    @Override
                    public void callback(Integer id) {
                        String _id = Integer.toString(id);
                        while(_id.length() < 5) {
                            _id = "0" + _id;
                        }
                        
                        // check if there is already a book with this id
                        if(new File(FileEnvironment.getBooksDirectory(), _id).exists()) {
                            JOptionPane.showMessageDialog(EditorFrame.this, "Dieses Buch existiert schon");
                            return;
                        }
                        
                        book.clear();
                        book.setID(id);
                        indexPanel.updateList(null);
                        indexPanel.refresh();
                        setBookOpened();
                    }
                });
                
            }
                
        } else if(id.equals("buch.import.repo")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch importieren?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {

                BookIDChooser bidc = new BookIDChooser(this, new Callback<Integer>() {
                    @Override
                    public void callback(final Integer id) {
                        // check repository
                        if(!Repository.exists(id)) {
                            new Progress(EditorFrame.this, "Buch wird heruntergeladen") {
                                @Override
                                public void action(ProgressDialog progressDialog) {
                                    try {
                                        Repository.download(id, progressDialog);
                                        new Progress(EditorFrame.this, "Buch wird importiert") {
                                            @Override
                                            public void action(ProgressDialog progressDialog) {
                                                try {
                                                    book = new Book(id);
                                                    File ouf = Repository.getBookOuf(id);
                                                    Map<String, String> txt = Repository.getBookTxt(id);
                                                    File src = Repository.getBookSrc(id);
                                                    File png = Repository.getBookPng(id);
                                                    Importer.importBook(ouf, txt, src, png, book, progressDialog);
                                                    progressDialog.restart("aktualisiere Liste");
                                                    indexPanel.updateList(progressDialog);
                                                    indexPanel.refresh();
                                                    setBookOpened();
                                                } catch (SyntaxError ex) {
                                                    JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                                    log.error("unable to import book", ex);
                                                } catch (IOException ex) {
                                                    JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                                    log.error("unable to import book", ex);
                                                }
                                            }
                                        };
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Herunterladen des Buches");
                                        log.error("unable to download book", ex);
                                    }
                                }
                            };
                        } else {
                            new Progress(EditorFrame.this, "Buch wird importiert") {
                                @Override
                                public void action(ProgressDialog progressDialog) {
                                    try {
                                        book = new Book(id);
                                        File ouf = Repository.getBookOuf(id);
                                        Map<String, String> txt = Repository.getBookTxt(id);
                                        File src = Repository.getBookSrc(id);
                                        File png = Repository.getBookPng(id);
                                        Importer.importBook(ouf, txt, src, png, book, progressDialog);
                                        progressDialog.restart("aktualisiere Liste");
                                        indexPanel.updateList(progressDialog);
                                        indexPanel.refresh();
                                        setBookOpened();
                                    } catch (SyntaxError ex) {
                                        JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                        log.error("unable to import book", ex);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                        log.error("unable to import book", ex);
                                    }
                                }
                            };
                        }
                    }
                });

            }
        } else if(id.equals("buch.import.yaml")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch importieren?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {
                final JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("tiptoi Buch (*.yaml)", "yaml"));
                if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    
                    Progress pr = new Progress(EditorFrame.this, "importiere Buch") {
                        @Override
                        public void action(ProgressDialog progressDialog) {

                            try {
                                new ReadYamlFile().read(fc.getSelectedFile(), progressDialog).save();
                                indexPanel.updateList(progressDialog);
                                indexPanel.refresh();
                                setBookOpened();
                            } catch(ParserException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "Die yaml Datei konnte nicht importiert werden");
                                log.error("unable to import yaml file", e);
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "Die yaml Datei konnte nicht importiert werden");
                                log.error("unable to import yaml file", e);
                            } catch (LexerException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "Die yaml Datei konnte nicht importiert werden");
                                log.error("unable to import yaml file", e);
                            }
                        }
                    };
                }
            }
        } else if(id.equals("buch.import.ouf")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch importieren?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {
                
                
                final Callback<Map> callback = new Callback<Map>() {
                    @Override
                    public void callback(final Map data) {
                        int id = -1;
                        if(data.get("id") != null) {
                            id = (Integer)data.get("id");
                        }
                        if(id < 0) {
                            try {
                                // prefetch id
                                DataInputStream in = new DataInputStream(new FileInputStream((File)data.get("ouf")));
                                in.skipBytes(20);
                                id = in.readInt();
                                in.close();
                            } catch(IOException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim lesen der ouf Datei");
                                log.error("unable to read ouf", e);
                                return;
                            }
                        }
                        
                        final int _id = id;
                        EditorFrame.this.setEnabled(false);
                        
                        
                        Progress pr = new Progress(EditorFrame.this, "importiere Buch") {
                            @Override
                            public void action(ProgressDialog progressDialog) {
                                try {
                                    book = new Book(_id);
                                    Importer.importBook((File)data.get("ouf"), Repository.getBook((File)data.get("txt")), (File)data.get("src"), (File)data.get("png"), book, progressDialog);
                                    
                                    setBookOpened();
                                } catch(IOException e) {
                                    JOptionPane.showMessageDialog(EditorFrame.this, "Import ist fehlgeschlagen");
                                    log.error("unable to import book", e);
                                } catch(SyntaxError se) {
                                    JOptionPane.showMessageDialog(EditorFrame.this, "Import ist fehlgeschlagen");
                                    log.error("unable to import book", se);
                                }
                                progressDialog.restart("aktualisiere Liste");
                                indexPanel.updateList(progressDialog);
                                indexPanel.refresh();
                            }
                        };
                        
                    }
                };
                new ImportDialog(EditorFrame.this, true, callback).setVisible(true);
                
                
            }
        } else if(id.equals("buch.load")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch laden?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {
                
                ChooseBook cb = new ChooseBook(this, new Callback<Integer>() {
                    @Override
                    public void callback(final Integer _id) {
                        Progress pr = new Progress(EditorFrame.this, "lade Buch") {
                            @Override
                            public void action(ProgressDialog progressDialog) {
                                try {
                                    book.clear();
                                    book.setID(_id);
                                    Book.loadXML(FileEnvironment.getXML(_id), book, progressDialog);
                                    indexPanel.refresh();
                                    progressDialog.restart("aktualisiere Liste");
                                    indexPanel.updateList(progressDialog);
                                    book.resetChangeMade();
                                    setBookOpened();
                                } catch (IOException ex) {
                                    log.error("unable to load book", ex);
                                }
                            }
                        };
                    }
                });
            }
        } else if(id.equals("buch.save")) {
            
            
            try {
                book.save();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Das Buch konnte nicht gespeichert werden");
                log.error("unable to save book", e);
            }
          
        } else if(id.equals("buch.generate")) {
            
            
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Ting Archiv (*.zip)", "zip"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".zip")) {
                        file = file + ".zip";
                    }
                    final File output = new File(file);
                    new Progress(EditorFrame.this, "erzeuge Buch") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                book.generateTTS(progressDialog);
                                
                                new Progress(EditorFrame.this, "erzeuge Buch") {
                                    @Override
                                    public void action(ProgressDialog progressDialog) {
                                        try {
                                            book.export(FileEnvironment.getDistDirectory(book.getID()), progressDialog);
                                            
                                            // create zip to output
                                            final FileOutputStream fos = new FileOutputStream(output);
                                            final ZipOutputStream out = new ZipOutputStream(fos);

                                            new Progress(EditorFrame.this, "erzeuge zip") {
                                                @Override
                                                public void action(ProgressDialog progressDialog) {
                                                    File[] entries = FileEnvironment.getDistDirectory(book.getID()).listFiles(new FilenameFilter() {
                                                        @Override
                                                        public boolean accept(File dir, String name) {
                                                            return(
                                                                    name.toLowerCase().endsWith(".ouf") ||
                                                                    name.toLowerCase().endsWith(".png") ||
                                                                    name.toLowerCase().endsWith(".txt") ||
                                                                    name.toLowerCase().endsWith(".src")
                                                            );
                                                        }
                                                    });
                                                    byte[] buffer = new byte[4096];
                                                    progressDialog.setMax(entries.length);
                                                    try {
                                                        for(int i = 0; i < entries.length; i++) {

                                                            FileInputStream in = new FileInputStream(entries[i]);
                                                            ZipEntry zipEntry = new ZipEntry(entries[i].getName());
                                                            out.putNextEntry(zipEntry);

                                                            int length;
                                                            while((length = in.read(buffer)) >= 0) {
                                                                out.write(buffer, 0, length);
                                                            }

                                                            out.closeEntry();
                                                            in.close();

                                                            progressDialog.setVal(i);
                                                        }
                                                        out.close();
                                                        fos.close();
                                                    } catch(IOException ioe) {
                                                        JOptionPane.showMessageDialog(EditorFrame.this, "Ting Archiv konnte nicht erstellt werden: " + ioe.getMessage());
                                                    }
                                                    progressDialog.done();
                                                }

                                            };
                                        } catch(IOException e) {
                                            log.error("unable to generate book", e);
                                            JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen");
                                        } catch(IllegalArgumentException e) {
                                            log.error("unable to generate book", e);
                                            JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen: " + e.getMessage());
                                        } catch(SyntaxError e) {
                                            log.error("unable to generate book", e);
                                            JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen: Syntax Error in Skript " + e.getTingID() + " in Zeile " + e.getRow() + " (" + e.getMessage() + ")");
                                        }
                                    }

                                };
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "TTS Generierung fehlgeschlagen");
                            }
                        }
                    };
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen: " + ioe.getMessage());
                }
            }
        } else if(id.equals("buch.generateMp3")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("MP3 archiv (*.zip)", "zip"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".zip")) {
                        file = file + ".zip";
                    }
                    final String _file = file;
                    new Progress(EditorFrame.this, "erzeuge Buch") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                book.generateTTS(progressDialog);
                    
                                final FileOutputStream fos = new FileOutputStream(_file);
                                final ZipOutputStream out = new ZipOutputStream(fos);

                                new Progress(EditorFrame.this, "erzeuge Buch") {
                                    @Override
                                    public void action(ProgressDialog progressDialog) {
                                        File[] entries = FileEnvironment.getAudioDirectory(book.getID()).listFiles(new FilenameFilter() {
                                            @Override
                                            public boolean accept(File dir, String name) {
                                                return(name.toLowerCase().endsWith(".mp3"));
                                            }
                                        });
                                        byte[] buffer = new byte[4096];
                                        progressDialog.setMax(entries.length);
                                        try {
                                            for(int i = 0; i < entries.length; i++) {

                                                FileInputStream in = new FileInputStream(entries[i]);
                                                ZipEntry zipEntry = new ZipEntry(entries[i].getName());
                                                out.putNextEntry(zipEntry);

                                                int length;
                                                while((length = in.read(buffer)) >= 0) {
                                                    out.write(buffer, 0, length);
                                                }

                                                out.closeEntry();
                                                in.close();

                                                progressDialog.setVal(i);
                                            }
                                            out.close();
                                            fos.close();
                                        } catch(IOException ioe) {
                                            JOptionPane.showMessageDialog(EditorFrame.this, "MP3 Archiv konnte nicht erstellt werden: " + ioe.getMessage());
                                        }
                                        progressDialog.done();
                                    }

                                };
                            } catch(IOException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "MP3 Archiv konnte nicht erstellt werden: " + e.getMessage());
                            }
                        }
                    };
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "MP3 Archiv konnte nicht gespeichert werden");
                    log.error("unable to save mp3 archive", e);
                }
            }
        } else if(id.equals("actions.cliscript")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("CLI Skript (*.ttcli)", "ttcli"));
            
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                
                try {
                    BufferedReader in = new BufferedReader(new FileReader(fc.getSelectedFile()));
                    String row;
                    CLI.init();
                    CLI.setBook(book);
                    while((row = in.readLine()) != null) {
                        System.out.println(row);
                        CLI.exec(row);
                    }
                } catch(IOException e) {
                    JOptionPane.showMessageDialog(EditorFrame.this, "CLI Skript konnte nicht geladen werden");
                    log.error("unable to load cli script", e);
                }
                
            }
            
        } else if(id.equals("buch.import.wimmelbuch")) {
            JFileChooser fc2 = new JFileChooser();
            fc2.setFileFilter(new FileNameExtensionFilter("Wimmelbuch (*.wb)", "wb"));
            if(fc2.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc2.getSelectedFile();
                    new Wimmelbuch().importBook(book, file);
                } catch(Exception ioe) {
                    JOptionPane.showMessageDialog(EditorFrame.this, "Wimmelbuch Import fehlgeschlagen");
                    log.error("unable to import wimmelbuch", ioe);
                }
                indexPanel.updateList(null);
                indexPanel.refresh();
                setBookOpened();
            }
        } else if(id.equals("buch.generatePngCodes")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("PNG Codes (*.zip)", "zip"));
            
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".zip")) {
                        file = file + ".zip";
                    }
                    final File output = new File(file);


                    new Progress(EditorFrame.this, "erzeuge Codes") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                book.pngExport(FileEnvironment.getCodesDirectory(book.getID()), progressDialog);
                                
                                File[] input = FileEnvironment.getCodesDirectory(book.getID()).listFiles(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return(name.toLowerCase().endsWith(".png"));
                                    }
                                });
                                ZipHelper.zip(output, input, progressDialog, EditorFrame.this, book, "erzeuge ZIP", "ZIP konnte nicht erstellt werden");
                            } catch(IOException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "Code-Generierung fehlgeschlagen");
                                log.error("unable to generate codes", e);
                            }
                        }
                    };
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(EditorFrame.this, "Code-Generierung fehlgeschlagen");
                    log.error("unable to generate codes", ioe);
                }
            }
        } else if(id.equals("prefs.binary")) {
            new BinaryLocationsDialog(this, true).setVisible(true);
        } else if(id.equals("prefs.tts")) {
            new TTSPreferences().setVisible(true);
        } else if(id.equals("prefs.codes")) {
            new CodePreferences().setVisible(true);
    /*    } else if(id.equals("windows.stick")) {
            stickFrame.setVisible(true); */
        } else if(id.equals("windows.index")) {
            indexPanel.setVisible(true);
    /*    } else if(id.equals("windows.reference")) {
            referenceFrame.setVisible(true);
        } else if(id.equals("windows.translator")) {
            translatorFrame.setVisible(true);
        } else if(id.equals("windows.repository")) {
            repositoryFrame.setVisible(true);
        } else if(id.equals("windows.gfx")) {
            gfxEditFrame.setVisible(true);
            gfxEditFrame.update(); */
        } else if(id.startsWith("codes.raw.")) {
            id = id.substring("codes.raw.".length());
            int start = Integer.parseInt(id.substring(0, 1)) * 10000 + Integer.parseInt(id.substring(2)) * 1000;
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Code Tabelle (*.png)", "png"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".ps")) {
                        file = file + ".ps";
                    }
                    OutputStream out = new FileOutputStream(file);
                    Codes.drawPagePNG(start, out);
                    out.close();
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "Die Codetabelle konnte nicht gespeichert werden");
                    log.error("unable to save code tabular", e);
                }
            }
        } else if(id.startsWith("codes.ting.")) {
            if(!genTingCodes(Integer.parseInt(id.substring(id.lastIndexOf(".") + 1)))) {
                JOptionPane.showMessageDialog(this, "Die Ting-Codetabelle konnte nicht erstellt werden, da die gewählten Codes noch unbekannt sind.");
            }
        } else if(id.equals("codes.tabular.ting2code")) {
            generateTabular(true);
        } else if(id.equals("codes.tabular.code2ting")) {
            generateTabular(false);
        } else if(id.equals("actions.searchForNewBooks")) {
            new Progress(this, "Buchliste aktualisieren") {
                @Override
                public void action(ProgressDialog progressDialog) {
                    Repository.search(progressDialog);
                }
            };
        } else if(id.equals("actions.updateBooks")) {
            new Progress(this, "Bücher aktualisieren") {
                @Override
                public void action(ProgressDialog progressDialog) {
                    try {
                        Repository.update(progressDialog);
                    } catch(IOException ioe) {
                        log.error("unable to update books", ioe);
                        JOptionPane.showMessageDialog(EditorFrame.this, "Update der bekannten Bücher fehlgeschlagen: " + ioe.getMessage());
                    }
                }
            };
        } else if(id.equals("actions.deleteBook")) {
            ChooseBook cb = new ChooseBook(this, new Callback<Integer>() {
                @Override
                public void callback(Integer _id) {
                    if(_id == book.getID()) {
                        JOptionPane.showMessageDialog(EditorFrame.this, "Das Buch wird gerade bearbeitet und kann nicht gelöscht werden.");
                    } else if(!book.deleteBook(_id)) {
                        JOptionPane.showMessageDialog(EditorFrame.this, "Das Buch konnte nicht gelöscht werden.");
                    } else {
                        JOptionPane.showMessageDialog(EditorFrame.this, "Das Buch wurde gelöscht.");
                    }
                }
            });
        } else if(id.equals("actions.cleanupRepository")) {
            Repository.cleanup();
            JOptionPane.showMessageDialog(EditorFrame.this, "Die Inhalte der Bücherliste wurde gelöscht.");
        } else if(id.equals("about.manual")) {
            manualFrame.setVisible(true);
        } else if(id.equals("about.contact")) {
            contactFrame.setVisible(true);
        } else if(id.equals("about.license")) {
            licenseFrame.setVisible(true);
        }
    }
    
    private void generateTabular(boolean ting2code) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Tabelle (*.txt)", "txt"));
        if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = fc.getSelectedFile().getCanonicalPath();
                if(!file.toLowerCase().endsWith(".txt")) {
                    file = file + ".txt";
                }
                PrintWriter out = new PrintWriter(new FileWriter(file));
                for(int i = 0; i < 0x10000; i++) {
                    int t = Translator.code2ting(i);
                    if(ting2code) {
                        t = Translator.ting2code(i);
                    }
                    if(t != -1) {
                        out.println(i + "\t" + t);
                    }
                }
                out.close();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Die Tabelle konnte nicht gespeichert werden");
                log.error("unable to save tabular", e);
            }
        }
    }
    
    private boolean genTingCodes(int start) {
        boolean found = false;
        for(int i = start; i < start + 1000; i++) {
            if(Translator.ting2code(i) != -1) {
                found = true;
                break;
            }
        }
        if(!found) {
            return(false);
        }
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PostScript Datei (*.png)", "png"));
        fc.setDialogTitle("Zieldatei auswählen");
        if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = fc.getSelectedFile().getCanonicalPath();
                if(!file.toLowerCase().endsWith(".png")) {
                    file = file + ".png";
                }
                
                /*
                int[] idx = new int[1000];
                
                
                String[] lbs = new String[idx.length];
                for(int i = start; i < start + 1000; i++) {
                    int code = Translator.ting2code(i);
                    idx[i - start] = code;
                    lbs[i - start] = "" + i;
                }
                */
                OutputStream out = new FileOutputStream(file);
                //Codes.drawPage(idx, lbs, out);
                Codes.drawPagePNG(start, out);
                out.close();
            } catch(IOException ioe) {
                JOptionPane.showMessageDialog(this, "Codegenerierung fehlgeschlagen");
                log.error("unable to generate codes", ioe);
            }
        }
        return(true);
    }
    
}
