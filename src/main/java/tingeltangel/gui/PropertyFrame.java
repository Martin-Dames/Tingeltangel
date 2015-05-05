
package tingeltangel.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tingeltangel.core.Book;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;

public class PropertyFrame extends JInternalFrame {
    
    private JTextField id = new JTextField();
    private JTextField name = new JTextField();
    private JTextField publisher = new JTextField();
    private JTextField author = new JTextField();
    private JTextField version = new JTextField();
    private JTextField url = new JTextField();
    
    private JTextField magicValue = new JTextField();
    private JTextField date = new JTextField();
    
    private final Book book;
    
    
    
    JTextField[] TEXT_FIELDS = {
        id, name, publisher, author, version, url, magicValue, date
    };
    String[] TEXT_FIELD_LABELS = {
        "ID", "Name", "Verleger", "Autor", "Version", "URL", "unbekannter Wert", "Datum"
    };
    
    
    private DocumentListener dl;
    
    public PropertyFrame(final Book book, final JFrame masterFrame) {
        super("Eigenschaften", true, true, true, true);
        this.book = book;
        setVisible(true);
        setBounds(610, 5, 300, 150);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        
        id.setText(Integer.toString(book.getID()));
        name.setText(book.getName());
        publisher.setText(book.getPublisher());
        author.setText(book.getAuthor());
        version.setText(Integer.toString(book.getVersion()));
        url.setText(book.getUrl());
        
        magicValue.setText(Long.toString(book.getMagicValue()));
        date.setText(Long.toString(book.getDate()));
        
        id.setEditable(false);
        dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        };
        
        
        enableListeners(true);
        
        /*
        id.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if((e.getClickCount() == 2) &&(e.getButton() == MouseEvent.BUTTON1)) {
                    
                    StringCallback callback = new StringCallback() {
                        @Override
                        public void callback(String s) {
                            
                            StringCallback cb = new StringCallback() {
                                @Override
                                public void callback(String s) {
                                    // extract id
                                    int p = s.indexOf(" ");
                                    if(p >= 0) {
                                        s = s.substring(0, p);
                                    }
                                    int _id = Integer.parseInt(s);
                                    book.setID(_id);
                                    id.setText(Integer.toString(_id));
                                }
                            };
                            
                            String[] options;
                            
                            if(s.equals("existing")) {
                                
                                Integer[] ids = Repository.getIDs();
                                options = new String[ids.length];
                                for(int i = 0; i < ids.length; i++) {
                                    String m = Integer.toString(ids[i]);
                                    while(m.length() < 5) {
                                        m = "0" + m;
                                    }
                                    m += " " + Repository.getBookTxt(ids[i]).get("Name");
                                    m += " (" + Repository.getBookTxt(ids[i]).get("Author") + ")";
                                    options[i] = m;
                                }
                                
                                
                            } else if(s.equals("free")) {
                                
                                LinkedList<Integer> free = new LinkedList<Integer>();
                                for(int i = 0; i <= 15000; i++) {
                                    if(Translator.ting2code(i) >= 0) {
                                        if(Repository.getBookTxt(i) == null) {
                                            free.add(i);
                                        }
                                    }
                                }
                                Integer[] ids = free.toArray(new Integer[0]);
                                        
                                options = new String[ids.length];
                                for(int i = 0; i < ids.length; i++) {
                                    String m = Integer.toString(ids[i]);
                                    while(m.length() < 5) {
                                        m = "0" + m;
                                    }
                                    options[i] = m;
                                }
                                
                                
                            } else if(s.equals("any")) {
                                
                                Integer[] ids = new Integer[15001];
                                int c = 0;
                                for(int i = 0; i <= 15000; i++) {
                                    ids[c++] = i;
                                }
                                
                                options = new String[ids.length];
                                for(int i = 0; i < ids.length; i++) {
                                    String m = Integer.toString(ids[i]);
                                    while(m.length() < 5) {
                                        m = "0" + m;
                                    }
                                    options[i] = m;
                                }
                                
                                
                            } else {
                                throw new Error();
                            }
                            
                            MultipleChoiceDialog.showDropdown(masterFrame, "MID auswählen", "Bitte wähle eine Buch-ID aus", "OK", options, 0, cb);
                        }
                    };
                    String[] options = {
                        "Freie MID (empfohlen für neue, eigene Bücher)",
                        "Bestehende MID (zur Nachvertonung offiziell existierender Bücher)",
                        "beliebige MID (für Profis, Vorsicht: der Ting-Code zur MID könnte unbekannt sein)"
                    };
                    String[] actions = {"free", "existing", "any"};
                    MultipleChoiceDialog.show(masterFrame, "Auswahl...", "Bitte wähle einen Buchtyp", "weiter", options, actions, 0, callback);
                }
            }
        });
        */
        
        JPanel labels = new JPanel();
        labels.setLayout(new GridLayout(TEXT_FIELD_LABELS.length, 1));
        for(int i = 0; i < TEXT_FIELD_LABELS.length; i++) {
            labels.add(new JLabel(TEXT_FIELD_LABELS[i] + ":"));
        }
        
        JPanel fields = new JPanel();
        fields.setLayout(new GridLayout(TEXT_FIELDS.length, 1));
        for(int i = 0; i < TEXT_FIELDS.length; i++) {
            fields.add(TEXT_FIELDS[i]);
        }
        
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(labels, BorderLayout.WEST);
        main.add(fields, BorderLayout.CENTER);
        
        setContentPane(main);
    }

    private void enableListeners(boolean enable) {
        for(int i = 0; i < TEXT_FIELDS.length; i++) {
            if(enable) {
                TEXT_FIELDS[i].getDocument().addDocumentListener(dl);
            } else {
                TEXT_FIELDS[i].getDocument().removeDocumentListener(dl);
            }
        }
    }
    
    public void refresh() {
        enableListeners(false);
        id.setText(Integer.toString(book.getID()));
        name.setText(book.getName());
        publisher.setText(book.getPublisher());
        author.setText(book.getAuthor());
        version.setText(Integer.toString(book.getVersion()));
        url.setText(book.getUrl());
        magicValue.setText(Long.toString(book.getMagicValue()));
        date.setText(Long.toString(book.getDate()));
        enableListeners(true);
    }
    
    public void update() {
        book.setName(name.getText());
        book.setPublisher(publisher.getText());
        book.setAuthor(author.getText());
        try {
            book.setVersion(Integer.parseInt(version.getText()));
        } catch(NumberFormatException nfe) {
        }
        book.setURL(url.getText());
        try {
            book.setMagicValue(Long.parseLong(magicValue.getText()));
        } catch(NumberFormatException nfe) {
        }
        try {
            book.setDate(Long.parseLong(date.getText()));
        } catch(NumberFormatException nfe) {
        }
    }
    
    
}