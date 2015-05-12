
package tingeltangel.gui;

import tingeltangel.tools.ProgressDialog;
import tingeltangel.tools.Callback;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.Tingeltangel;
import tingeltangel.core.Book;
import tingeltangel.core.Codes;
import tingeltangel.core.Entry;
import tingeltangel.core.Importer;
import tingeltangel.core.MP3Player;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.Progress;

public class MasterFrame extends JFrame implements Callback<String> {

    private MP3Player mp3Player = new MP3Player();
    private Book book = new Book(15000, mp3Player);
    
    private JDesktopPane desktop;
    private IndexFrame indexFrame;
    private PlayerFrame playerFrame;
    private RegisterFrame registerFrame;
    private CodeFrame codeFrame;
    private PropertyFrame propertyFrame;
    private StickFrame stickFrame;
    private ReferenceFrame referenceFrame;
    private TranslatorFrame translatorFrame;
    private RepositoryManager repositoryFrame;
    
    private InfoFrame contactFrame = new InfoFrame("Kontakt", "html/contact.html");
    private InfoFrame licenseFrame = new InfoFrame("Kontakt", "html/license.html");

    
    
    public MasterFrame() {
        super(Tingeltangel.MAIN_FRAME_TITLE + Tingeltangel.MAIN_FRAME_VERSION);
        
        
        indexFrame = new IndexFrame(this);
        playerFrame = new PlayerFrame(this);
        registerFrame = new RegisterFrame(this);
        codeFrame = new CodeFrame(this);
        propertyFrame = new PropertyFrame(this);
        stickFrame = new StickFrame(this);
        referenceFrame = new ReferenceFrame(this);
        translatorFrame = new TranslatorFrame(this);
        repositoryFrame = new RepositoryManager();
        
        book.addRegisterListener(registerFrame);
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        setBounds(
                    Tingeltangel.MAIN_FRAME_POS_X,
                    Tingeltangel.MAIN_FRAME_POS_Y,
                    Tingeltangel.MAIN_FRAME_WIDTH + getInsets().left + getInsets().right,
                    Tingeltangel.MAIN_FRAME_HEIGHT + getInsets().top + getInsets().bottom
        );

        desktop = new JDesktopPane();
        MasterFrameMenu.setMenuCallback(this);
        setMenuBar(MasterFrameMenu.getMenuBar());
        
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeMasterFrame();
            }
        });
        setVisible(true);
        
        desktop.add(indexFrame);
        desktop.add(playerFrame);
        desktop.add(registerFrame);
        desktop.add(codeFrame);
        desktop.add(propertyFrame);
        desktop.add(stickFrame);
        desktop.add(referenceFrame);
        desktop.add(contactFrame);
        desktop.add(licenseFrame);
        desktop.add(translatorFrame);
        
        
        
        setContentPane(desktop);

    }
    
    public MP3Player getMP3Player() {
        return(mp3Player);
    }
    
    public void showReferenceFrame() {
        referenceFrame.setVisible(true);
    }
    
    public Book getBook() {
        return(book);
    }
    
    private LinkedList<EntryListener> listeners = new LinkedList<EntryListener>();
    
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
            int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem das Programm beenden?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (value == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
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
                            JOptionPane.showMessageDialog(MasterFrame.this, "Dieses Buch existiert schon");
                            return;
                        }
                        
                        book.clear();
                        book.setID(id);
                        propertyFrame.refresh();
                        indexFrame.update();
                        
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
                new IDChooser(this, new Callback<Integer>() {
                    @Override
                    public void callback(Integer t) {
                        
                    }
                });
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
                                JOptionPane.showMessageDialog(MasterFrame.this, "Fehler beim lesen der ouf Datei");
                                e.printStackTrace(System.out);
                                return;
                            }
                        }
                        
                        final int _id = id;
                        MasterFrame.this.setEnabled(false);
                        
                        
                        Progress pr = new Progress(MasterFrame.this, "importiere Buch") {
                            @Override
                            public void action(ProgressDialog progressDialog) {
                                try {
                                    book = new Book(_id, mp3Player);
                                    Importer.importOuf((File)data.get("ouf"), Repository.getBook((File)data.get("txt")), (File)data.get("src"), book, progressDialog);
                                } catch(IOException e) {
                                    JOptionPane.showMessageDialog(MasterFrame.this, "Import ist fehlgeschlagen");
                                    e.printStackTrace(System.out);
                                } catch(SyntaxError se) {
                                    JOptionPane.showMessageDialog(MasterFrame.this, "Import ist fehlgeschlagen");
                                    se.printStackTrace(System.out);
                                }
                                propertyFrame.refresh();
                                indexFrame.update();
                            }
                        };
                        
                    }
                };
                new ImportDialog(MasterFrame.this, true, callback).setVisible(true);
                
                
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
                    public void callback(Integer _id) {
                        try {
                            book.clear();
                            book.setID(_id);
                            //Book.load(FileEnvironment.getTBU(_id), book);
                            Book.loadXML(FileEnvironment.getXML(_id), book);
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                        }
                        propertyFrame.refresh();
                        indexFrame.update();
                    }
                });
                
                
            }
            
            
        } else if(id.equals("buch.save")) {
            
            
            try {
                book.save();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Das Buch konnte nicht gespeichert werden");
                e.printStackTrace(System.out);
            }
            
        } else if(id.equals("buch.generate")) {
            new Progress(MasterFrame.this, "erzeuge Codes") {
                @Override
                public void action(ProgressDialog progressDialog) {
                    try {
                        book.export(FileEnvironment.getDistDirectory(book.getID()), progressDialog);
                    } catch(IOException e) {
                        JOptionPane.showMessageDialog(MasterFrame.this, "Buchgenerierung fehlgeschlagen");
                        e.printStackTrace(System.out);
                    } catch(IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(MasterFrame.this, "Buchgenerierung fehlgeschlagen: " + e.getMessage());
                    } catch(SyntaxError e) {
                        e.printStackTrace(System.out);
                        JOptionPane.showMessageDialog(MasterFrame.this, "Buchgenerierung fehlgeschlagen: Syntax Error in Skript " + e.getTingID() + " in Zeile " + e.getRow() + " (" + e.getMessage() + ")");
                    }
                }
            };
            
            
            
        } else if(id.startsWith("buch.generateEpsCodes.")) {
            if(id.endsWith(".600")) {
                Codes.setResolution(Codes.DPI600);
            } else {
                Codes.setResolution(Codes.DPI1200);
            }
            new Progress(MasterFrame.this, "erzeuge Codes") {
                @Override
                public void action(ProgressDialog progressDialog) {
                    try {
                        book.epsExport(FileEnvironment.getCodesDirectory(book.getID()), progressDialog);
                    } catch(IOException e) {
                        JOptionPane.showMessageDialog(MasterFrame.this, "eps-Generierung fehlgeschlagen");
                        e.printStackTrace(System.out);
                    }
                }
            };
        } else if(id.startsWith("buch.generatePngCodes.")) {
            if(id.endsWith(".600")) {
                Codes.setResolution(Codes.DPI600);
            } else {
                Codes.setResolution(Codes.DPI1200);
            }
            new Progress(MasterFrame.this, "erzeuge Codes") {
                @Override
                public void action(ProgressDialog progressDialog) {
                    try {
                        book.pngExport(FileEnvironment.getCodesDirectory(book.getID()), progressDialog);
                    } catch(IOException e) {
                        JOptionPane.showMessageDialog(MasterFrame.this, "eps-Generierung fehlgeschlagen");
                        e.printStackTrace(System.out);
                    }
                }
                
            };
        } else if(id.equals("windows.stick")) {
            stickFrame.setVisible(true);
        } else if(id.equals("windows.player")) {
            playerFrame.setVisible(true);
        } else if(id.equals("windows.properties")) {
            propertyFrame.setVisible(true);
        } else if(id.equals("windows.code")) {
            codeFrame.setVisible(true);
        } else if(id.equals("windows.register")) {
            registerFrame.setVisible(true);
        } else if(id.equals("windows.index")) {
            indexFrame.setVisible(true);
        } else if(id.equals("windows.reference")) {
            referenceFrame.setVisible(true);
        } else if(id.equals("windows.translator")) {
            translatorFrame.setVisible(true);
        } else if(id.equals("windows.repository")) {
            repositoryFrame.setVisible(true);
        } else if(id.startsWith("codes.raw.")) {
            id = id.substring("codes.raw.".length());
            int start = Integer.parseInt(id.substring(0, 1)) * 10000 + Integer.parseInt(id.substring(2)) * 1000;
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Code Tabelle (*.ps)", "ps"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".ps")) {
                        file = file + ".ps";
                    }
                    PrintWriter out = new PrintWriter(new FileWriter(file));
                    Codes.drawPage(start, out);
                    out.close();
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "Die Codetabelle konnte nicht gespeichert werden");
                    e.printStackTrace(System.out);
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
        } else if(id.equals("books.search")) {
            Repository.search(null);
        } else if(id.equals("books.update")) {
            try {
                Repository.update(null);
            } catch(IOException ioe) {
                ioe.printStackTrace(System.out);
                JOptionPane.showMessageDialog(this, "Update der bekannten Bücher fehlgeschlagen: " + ioe.getMessage());
            }
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
                e.printStackTrace(System.out);
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
        fc.setFileFilter(new FileNameExtensionFilter("PostScript Datei (*.ps)", "ps"));
        fc.setDialogTitle("Zieldatei auswählen");
        if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = fc.getSelectedFile().getCanonicalPath();
                if(!file.toLowerCase().endsWith(".ps")) {
                    file = file + ".ps";
                }
                
                
                int[] idx = new int[1000];
                
                
                String[] lbs = new String[idx.length];
                for(int i = start; i < start + 1000; i++) {
                    int code = Translator.ting2code(i);
                    idx[i - start] = code;
                    lbs[i - start] = "" + i;
                }
                PrintWriter out = new PrintWriter(new FileWriter(file));
                Codes.drawPage(idx, lbs, out);
                out.close();
            } catch(IOException ioe) {
                JOptionPane.showMessageDialog(this, "Codegenerierung fehlgeschlagen");
                ioe.printStackTrace(System.out);
            }
        }
        return(true);
    }
    
}
