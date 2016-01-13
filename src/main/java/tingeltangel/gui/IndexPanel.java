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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.MP3Player;


public final class IndexPanel extends JPanel {
    
    
     
    private JTextField id = new JTextField();
    private JTextField name = new JTextField();
    private JTextField publisher = new JTextField();
    private JTextField author = new JTextField();
    private JTextField version = new JTextField();
    private JTextField url = new JTextField();
    private JTextField magicValue = new JTextField();
    private JTextField date = new JTextField();
        
    private final int ICON_SKIP = 0;
    private final int ICON_STOP = 1;
    
    private final String[] ICONS = {
        "skip.png",
        "stop.png"
    };
    
    JTextField[] TEXT_FIELDS = {
        id, name, publisher, author, version, url, magicValue, date
    };
    String[] TEXT_FIELD_LABELS = {
        "ID", "Name", "Verleger", "Autor", "Version", "URL", "?", "Datum"
    };
    
    
    
    private final JTextField addEntriesCount;
    private final JTextField addEntriesStart;
    private final MasterFrame mainFrame;
    
    private final JLabel currentTrack = new JLabel();
    
    private final JPanel list = new JPanel();
    private DocumentListener dl;
    
    public Book getBook() {
        return(mainFrame.getBook());
    }
    
    public JFrame getMainFrame() {
        return(mainFrame);
    }
    
    public IndexPanel(final MasterFrame mainFrame) {
        super();
        
        this.mainFrame = mainFrame;
        
        
 
        JPanel right = new JPanel();
        right.setLayout(new PushBorderLayout());
        
        JPanel row = new JPanel();
        row.setLayout(new PushBorderLayout());
        
        
        addEntriesCount = new JTextField(3);
        addEntriesCount.setText("1");
        
        addEntriesStart = new JTextField(5);
        addEntriesStart.setText("15001");
        
        row.add(addEntriesCount, PushBorderLayout.LINE_START);
        row.add(new JLabel("Einträge ab OID"), PushBorderLayout.LINE_START);
        row.add(addEntriesStart, PushBorderLayout.LINE_START);
        
        JButton addEntriesButton = new JButton("hinzufügen");        
        addEntriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String _oid = addEntriesStart.getText().trim();
                int oid;
                String _count = addEntriesCount.getText().trim();
                int count;
                try {
                    oid = Integer.parseInt(_oid);
                    if((oid < 15001) || (oid >= 0x10000)) {
                        JOptionPane.showMessageDialog(mainFrame, "ungültige OID angegeben (15001 bis 65535)");
                        return;
                    }
                } catch(NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(mainFrame, "ungültige OID angegeben (15001 bis 65535)");
                    return;
                }
                try {
                    count = Integer.parseInt(_count);
                    if((count < 1) || (count >= 1000)) {
                        JOptionPane.showMessageDialog(mainFrame, "ungültige Anzahl angegeben (1-999)");
                        return;
                    }
                } catch(NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(mainFrame, "ungültige Anzahl angegeben (1-999)");
                    return;
                }
                int lastOid = oid + count - 1;
                if(lastOid >= 0x10000) {
                    JOptionPane.showMessageDialog(mainFrame, "ungültiges OID Interval angegeben (max 65535)");
                    return;
                }
                for(int i = oid; i <= lastOid; i++) {
                    if(mainFrame.getBook().entryForTingIDExists(i)) {
                        JOptionPane.showMessageDialog(mainFrame, "OID " + i + " schon belegt");
                        return;
                    }
                }
                for(int i = oid; i <= lastOid; i++) {
                    mainFrame.getBook().addEntry(i);
                    int rowNr = getPositionInIndex(i);
                    list.add(new IndexListEntry(mainFrame.getBook().getEntryFromTingID(i), IndexPanel.this), rowNr);
                }
                new Thread() {
                    @Override
                    public void run() {
                        list.revalidate();
                        list.repaint();
                    }
                }.start();
                
            }
        });
        row.add(addEntriesButton, PushBorderLayout.LINE_START);
        right.add(row, PushBorderLayout.PAGE_START);
        
        
        right.add(PushBorderLayout.pad(10), PushBorderLayout.PAGE_START);
        
        row = new JPanel();
        row.setLayout(new PushBorderLayout());
        
        JButton skip = new JButton(getIcon(ICON_SKIP));
        skip.setMargin(new Insets(0, 0, 0, 0));
        skip.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    MP3Player.getPlayer().stop();
                }
            });
        row.add(skip, PushBorderLayout.LINE_START);
        
        
        row.add(PushBorderLayout.pad(5), PushBorderLayout.LINE_START);
        
        JButton stop = new JButton(getIcon(ICON_STOP));
        stop.setMargin(new Insets(0, 0, 0, 0));
        stop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    String tid = currentTrack.getText();
                    if(tid != null) {
                        Entry entry = mainFrame.getBook().getEntryFromTingID(Integer.parseInt(tid));
                        if(entry.isCode()) {
                            entry.getScript().kill();
                        }
                        if(entry.isMP3() || entry.isTTS() || entry.isCode()) {
                            MP3Player.getPlayer().stop();
                        }
                            
                    }
                }
            });
        row.add(stop, PushBorderLayout.LINE_START);
        
        row.add(PushBorderLayout.pad(5), PushBorderLayout.LINE_START);
        row.add(new JLabel("Track:"), PushBorderLayout.LINE_START);
        row.add(PushBorderLayout.pad(5), PushBorderLayout.LINE_START);
        row.add(currentTrack, PushBorderLayout.LINE_START);
        
        
        right.add(row, PushBorderLayout.PAGE_START);
        
        right.add(PushBorderLayout.pad(10), PushBorderLayout.PAGE_START);
        
        
        // book properties
        row = new JPanel();
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
        row.setLayout(new BorderLayout());
        row.add(labels, BorderLayout.WEST);
        row.add(fields, BorderLayout.CENTER);
        row.add(new JLabel("Bucheigenschaften"), BorderLayout.NORTH);
        right.add(row, PushBorderLayout.PAGE_START);
        
        
        // add register panel
        right.add(PushBorderLayout.pad(10), PushBorderLayout.PAGE_START);
        right.add(new JLabel("Register"), PushBorderLayout.PAGE_START);
                
        RegisterPanel registerPanel = new RegisterPanel(mainFrame);
        getBook().addRegisterListener(registerPanel);
        right.add(registerPanel, PushBorderLayout.CENTER);
        
        
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
        
        
        //setSize(new Dimension(500, 500));
        
        
        updateList();
        
        Book book = mainFrame.getBook();
        
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
        
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
                Book book = mainFrame.getBook();
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                
                mainFrame.entrySelected(row);
                
                if (e.getClickCount() == 2) {
                    
                    if(column == 0) {
                        
                        // click on oid
                        String _id = Integer.toString(book.getEntry(row).getTingID());
                        while(_id.length() < 5) {
                            _id = "0" + _id;
                        }
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileNameExtensionFilter("Tabelle (*.eps)", "eps"));
                        fc.setSelectedFile(new File(_id + ".eps"));
                        
                        if(fc.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                            try {
                                String file = fc.getSelectedFile().getCanonicalPath();
                                if(!file.toLowerCase().endsWith(".eps")) {
                                    file += ".eps";
                                }
                                book.epsSingleExport(new File(file), book.getEntry(row).getTingID());
                            } catch(IOException ex) {
                                JOptionPane.showMessageDialog(mainFrame, "eps-Generierung fehlgeschlagen");
                                ex.printStackTrace(System.out);
                            } catch(IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog(mainFrame, "eps-Generierung fehlgeschlagen: " + ex.getMessage());
                            }
                        }
                        
                    } else if(column == 1) {
                        
                        
                    } else if(column == 3) {
                        if(book.getEntry(row).isMP3()) {
                            JFileChooser fc = new JFileChooser(lastChooseMp3DialogPath);
                            fc.setFileFilter(new FileNameExtensionFilter("mp3", "mp3"));
                            if(fc.showOpenDialog(IndexFrame2.this) == JFileChooser.APPROVE_OPTION) {
                                try {
                                    File file = fc.getSelectedFile();
                                    if(file.getParent() != null) {
                                        lastChooseMp3DialogPath = file.getParent();
                                    }
                                    book.getEntry(row).setMP3(file);
                                    update();
                                } catch(FileNotFoundException ex) {
                                    JOptionPane.showMessageDialog(IndexFrame2.this, "Die Datei '" + fc.getSelectedFile() + "' konnte nicht gefunden werden.");
                                    ex.printStackTrace(System.out);
                                } catch(IOException ex) {
                                    JOptionPane.showMessageDialog(IndexFrame2.this, "Die Datei '" + fc.getSelectedFile() + "' konnte nicht gelesen werden.");
                                    ex.printStackTrace(System.out);
                                }
                            }
                        } else if(book.getEntry(row).isTTS()) {
                            
                            TTSEntry entry = book.getEntry(row).getTTS();
                            if(entry == null) {
                                entry = new TTSEntry("");
                                book.getEntry(row).setTTS(entry);
                            }
                            final TTSEntry _entry = entry;
                            final Book _book = book;
                            final int _row = row;
                            new TTSDialog(mainFrame, true, entry, new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        _entry.generateTTS(_book.getEntry(_row));
                                    } catch(IOException ioe) {
                                        ioe.printStackTrace();
                                    }
                                }
                            }).setVisible(true);
                            
                            
                        }
                    }
                }
            }
        });
        */
    }

    private ImageIcon getIcon(int res) {
        try {
            return(new ImageIcon(ImageIO.read(getClass().getResource("/icons/" + ICONS[res]))));
        } catch(IOException ioe) {
        }
        return(null);
    }
    
    public int getPositionInIndex(int oid) {
        int rowNr = -1;
        for(int i = 0; i < list.getComponentCount(); i++) {
            int ioid = ((IndexListEntry)list.getComponent(i)).getOID();
            if(ioid > oid) {
                rowNr = i;
                break;
            }
        }
        if(rowNr < 0) {
            return(0);
        }
        return(rowNr);
    }
    
    public int getPositionInIndex(IndexListEntry entry) {

        int rowNr = -1;
        for(int i = 0; i < list.getComponentCount(); i++) {
            if(list.getComponent(i) == entry) {
                rowNr = i;
                break;
            }
        }
        if(rowNr < 0) {
            throw new Error();
        }
        return(rowNr);
    }
    
    JPanel getListPanel() {
        return(list);
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
        Book book = mainFrame.getBook();
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
        Book book = mainFrame.getBook();
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
    
    protected void updateList() {
        update();
        
        
        Book book = mainFrame.getBook();
                
        list.removeAll();
        
        for(int i = 0; i < book.getSize(); i++) {
            list.add(new IndexListEntry(book.getEntry(i), this));
        }
        
        Dimension size = mainFrame.getSize();
        
        mainFrame.pack();
        mainFrame.setSize(size);
    }

    void setCurrentTrack(Entry entry) {
        if(entry == null) {
            currentTrack.setText(null);
        } else {
            currentTrack.setText(Integer.toString(entry.getTingID()));
        }
    }
    
    
}
