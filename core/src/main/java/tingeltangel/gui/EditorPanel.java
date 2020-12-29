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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.MP3Player;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.Progress;
import tingeltangel.tools.ProgressDialog;


public final class EditorPanel extends JPanel {
    
    
     
    private JTextField id = new JTextField();
    private JTextField name = new JTextField();
    private JTextField publisher = new JTextField();
    private JTextField author = new JTextField();
    private JTextField version = new JTextField();
    private JTextField url = new JTextField();
    private JTextField magicValue = new JTextField();
    private JTextField date = new JTextField();
    private final JScrollPane jScrollPane;
    private JLabel cover = null;
        
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
    
    private final static Logger log = LogManager.getLogger(EditorPanel.class);
    
    private final JTextField addEntriesCount;
    private final JTextField addEntriesStart;
    private final EditorFrame mainFrame;
    
    private final JLabel currentTrack = new JLabel();
    
    private final JPanel list = new JPanel();
    private DocumentListener dl;
    
    public Book getBook() {
        return(mainFrame.getBook());
    }
    
    public JFrame getMainFrame() {
        return(mainFrame);
    }

    public JPanel getList() {
        return list;
    }
    
    public EditorPanel(final EditorFrame mainFrame) {
        super();
        
        this.mainFrame = mainFrame;
        
        Book book = mainFrame.getBook();
 
        JPanel right = new JPanel();
        right.setLayout(new PushBorderLayout());
        
        JPanel row = new JPanel();
        row.setLayout(new PushBorderLayout());
        
        
        addEntriesCount = new JTextField(3);
        addEntriesCount.setText("1");
        addEntriesCount.getAccessibleContext().setAccessibleDescription("Anzahl der hinzuzufügenden Einträge");
        
        addEntriesStart = new JTextField(5);
        addEntriesStart.setText("15001");
        addEntriesStart.getAccessibleContext().setAccessibleDescription("OID des ersten hinzuzufügenden Eintrags");
        
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
                    list.add(new IndexListEntry(mainFrame.getBook().getEntryByOID(i), EditorPanel.this), rowNr);
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
        skip.setToolTipText("MP3 überspringen");
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
        stop.setToolTipText("Ausgabe abbrechen");
        stop.setMargin(new Insets(0, 0, 0, 0));
        stop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    stopTrack();
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
            TEXT_FIELDS[i].getAccessibleContext().setAccessibleDescription(TEXT_FIELD_LABELS[i]);
        }
        row.setLayout(new BorderLayout());
        row.add(labels, BorderLayout.WEST);
        row.add(fields, BorderLayout.CENTER);
        row.add(new JLabel("Bucheigenschaften"), BorderLayout.NORTH);
        right.add(row, PushBorderLayout.PAGE_START);
        
        // cover
        right.add(PushBorderLayout.pad(10), PushBorderLayout.PAGE_START);
        cover = new JLabel("Cover kann nicht geladen werden");
        
        try {
            File coverImage = book.getCover();
            if(coverImage.exists()) {
                cover = new JLabel(new ImageIcon(ImageIO.read(coverImage)));
            } else {
                cover = new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/noCover.png"))));
            }
        } catch(IOException ioe) {
        }
        cover.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                loadCover();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        right.add(cover, PushBorderLayout.PAGE_START);
        
        
        // add stick panel
        right.add(PushBorderLayout.pad(10), PushBorderLayout.PAGE_START);
        right.add(new JLabel("Stift"), PushBorderLayout.PAGE_START);
        right.add(new StickPanel(mainFrame), PushBorderLayout.PAGE_START);
        
        
                
        /*
        // add constants panel
        right.add(PushBorderLayout.pad(10), PushBorderLayout.PAGE_START);
        right.add(new JLabel("Konstanten"), PushBorderLayout.PAGE_START);
        right.add(new ConstantsPanel(mainFrame), PushBorderLayout.PAGE_START);
        */
        
        // add register panel
        right.add(PushBorderLayout.pad(10), PushBorderLayout.PAGE_START);
        right.add(new JLabel("Register"), PushBorderLayout.PAGE_START);
        RegisterPanel registerPanel = new RegisterPanel(mainFrame);
        getBook().addRegisterListener(registerPanel);
        right.add(registerPanel, PushBorderLayout.CENTER);
        
        
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        setLayout(new BorderLayout());
        jScrollPane = new JScrollPane(list);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(jScrollPane, BorderLayout.CENTER);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(new SearchPanel(mainFrame, this), BorderLayout.NORTH);
        container.add(jScrollPane, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        updateList(null);
        
        
        
        id.setText(Integer.toString(book.getID()));
        name.setText(book.getName());
        publisher.setText(book.getPublisher());
        author.setText(book.getAuthor());
        version.setText(Integer.toString(book.getVersion()));
        url.setText(book.getUrl());
        
        magicValue.setText(Long.toString(book.getMagicValue()));
        date.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(book.getDate() * 1000)));
        
        id.setEditable(false);
        date.setEditable(false);
        
        id.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // change mid
                
                IDChooser ic = new IDChooser(mainFrame, new Callback<Integer>() {

                    @Override
                    public void callback(final Integer id) {
                        String _id = Integer.toString(id);
                        while(_id.length() < 5) {
                            _id = "0" + _id;
                        }
                        
                        // check if there is already a book with this id
                        if(new File(FileEnvironment.getBooksDirectory(), _id).exists()) {
                            JOptionPane.showMessageDialog(mainFrame, "Dieses Buch existiert schon");
                            return;
                        }
                        
                        
                        Progress pr = new Progress(mainFrame, "ändere mid") {
                            @Override
                            public void action(ProgressDialog progressDialog) {
                        
                                try {
                                    Book book = mainFrame.getBook();
                                    int oldID = book.getID();
                                    book.setID(id);
                                    book.save();
                                    
                                    // copy audio
                                    progressDialog.restart("kopiere mp3s");
                                    File[] audios = FileEnvironment.getAudioDirectory(oldID).listFiles(new FilenameFilter() {
                                        @Override
                                        public boolean accept(File dir, String name) {
                                            return(name.toLowerCase().endsWith(".mp3"));
                                        }
                                    });
                                    progressDialog.setMax(audios.length);
                                    File destAudioDir = FileEnvironment.getAudioDirectory(id);
                                    for(int i = 0; i < audios.length; i++) {
                                        progressDialog.setVal(i);
                                        FileEnvironment.copy(audios[i], new File(destAudioDir, audios[i].getName()));
                                    }
                                    
                                    book.clear();
                                    book.setID(id);
                                    Book.loadXML(FileEnvironment.getXML(id), book, progressDialog);
                                    
                                    progressDialog.restart("aktualisiere Liste");
                                    refresh();
                                    updateList(progressDialog);
                                    book.resetChangeMade();
                                    mainFrame.setBookOpened();
                                    book.deleteBook(oldID);
                                } catch(Exception ioe) {
                                    ioe.printStackTrace();
                                    JOptionPane.showMessageDialog(mainFrame, "Es ist ein Fehler aufgetreten: " + ioe.getMessage());
                                }
                            }
                        };
                        
                    }
                });
                
                
                
                
                
            }
        });
        
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
    }

    public void stopTrack() {
        String tid = currentTrack.getText();
        if((tid != null) && !tid.isEmpty()) {
            Entry entry = mainFrame.getBook().getEntryByOID(Integer.parseInt(tid));
            if(entry.isCode()) {
                entry.getScript().kill();
            }
            if(entry.isMP3() || entry.isTTS() || entry.isCode()) {
                MP3Player.getPlayer().stop();
            }

        }
    }
    
    public int getScrollX() {
        return(jScrollPane.getViewport().getViewPosition().x);
    }
    
    public int getScrollY() {
        return(jScrollPane.getViewport().getViewPosition().y);
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
            return(list.getComponentCount());
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
        date.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(book.getDate() * 1000)));
        
        try {
            File coverImage = book.getCover();
            if(coverImage.exists()) {
                cover.setIcon(new ImageIcon(ImageIO.read(coverImage)));
            } else {
                cover.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/noCover.png"))));
            }
        } catch(IOException ioe) {
            cover.setText("Cover kann nicht geladen werden");
        }
        
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
    }
    protected void updateList(ProgressDialog progress) {
        
        
        Book book = mainFrame.getBook();
                
        list.removeAll();
        
        if(progress != null) {
            progress.setMax(book.getSize());
        }
        
        for(int i = 0; i < book.getSize(); i++) {
            if(progress != null) {
                progress.setVal(i);
            }
            list.add(new IndexListEntry(book.getEntry(i), this));
        }
        
        if(progress != null) {
            progress.done();
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
    
    void loadCover() {
        
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PNG Coverbild (140px × 193px) (*.png)", "png"));
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                if(file.exists()) {
                    
                    InputStream input = null;
                    OutputStream output = null;
                    try {
                        input = new FileInputStream(file);
                        output = new FileOutputStream(mainFrame.getBook().getCover());
                        byte[] buf = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buf)) > 0) {
                            output.write(buf, 0, bytesRead);
                        }
                    } finally {
                        if(input != null) {
                            input.close();
                        }
                        if(output != null) {
                            output.close();
                        }
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Das Cover konnte nicht gefunden werden");
                }
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Das Cover konnte nicht geladen werden");
                log.error("unable to load cover", e);
            }
        }
        
        refresh();
    }
}
