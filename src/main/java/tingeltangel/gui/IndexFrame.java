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
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import tingeltangel.core.Book;
import tingeltangel.core.Entry;
import tingeltangel.core.TTSEntry;
import tingeltangel.core.Translator;
import tingeltangel.tools.Callback;
import tingeltangel.tools.Lang;


public class IndexFrame extends JInternalFrame implements ActionListener {
    
    private TableModel model;
    private final JTable table;
    
    private String lastChooseMp3DialogPath = null;
    
    private final static String MP3 = "MP3";
    private final static String EMPTY = "Leer";
    private final static String SCRIPT = "Skript";
    private final static String T2S = "TTS";
    private final static String SUB = "Skript (Subrutine)";
    
    private JTextField appendEntries;
    private JTextField newOID;
    private final MasterFrame mainFrame;
    
    public Book getBook() {
        return(mainFrame.getBook());
    }
    
    public IndexFrame(final MasterFrame mainFrame) {
        super("Index", true, true, true, true);
        
        this.mainFrame = mainFrame;
        
        model = new IndexTableModel(mainFrame.getBook());
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(65);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);
        table.setDefaultRenderer(String.class, new MyRenderer());
        
        setVisible(true);
        setBounds(5, 5, 600, 590);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        appendEntries = new JTextField(3);
        appendEntries.setText("10");
        JButton appendNewEntry = new JButton("+");
        appendNewEntry.setActionCommand("append_entry");
        appendNewEntry.addActionListener(this);
        
        newOID = new JTextField(10);
        JButton addNewEntry = new JButton("hinzufügen");
        addNewEntry.setActionCommand("add_entry");
        addNewEntry.addActionListener(this);
        
        JPanel topLeftBar = new JPanel();
        topLeftBar.setLayout(new FlowLayout());
        topLeftBar.add(appendEntries);
        topLeftBar.add(appendNewEntry);
        
        JPanel topRightBar = new JPanel();
        topRightBar.setLayout(new FlowLayout());
        topRightBar.add(new JLabel("OID:"));
        topRightBar.add(newOID);
        topRightBar.add(addNewEntry);
        
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
        topBar.add(topLeftBar, BorderLayout.WEST);
        topBar.add(topRightBar, BorderLayout.EAST);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(topBar, BorderLayout.NORTH);
        
        
        setContentPane(panel);
        
        table.addMouseListener(new MouseAdapter() {
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
                        
                        String[] options = {"MP3 Track", "Skript", "Skript (Unterprogramm)", "TTS", Lang.get("indexFrame.dialog.empty")};
                        String[] actions = {MP3, SCRIPT, SUB, T2S, EMPTY};
                        
                        final Entry entry = book.getEntry(row);
                        
                        Callback<String> callback = new Callback<String>() {
                            @Override
                            public void callback(String s) {
                                if(s.equals(MP3)) {
                                    entry.setMP3();
                                } else if(s.equals(SCRIPT)) {
                                    entry.setCode();
                                } else if(s.equals(SUB)) {
                                    entry.setSub();
                                } else if(s.equals(EMPTY)) {
                                    entry.setEmpty();
                                } else if(s.equals(T2S)) {
                                    entry.setTTS();
                                }
                                update();
                            }
                        };
                        
                        
                        int preselection = -1;
                        if(entry.isMP3()) {
                            preselection = 0;
                        } else if(entry.isCode()) {
                            preselection = 1;
                        } else if(entry.isSub()) {
                            preselection = 2;
                        } else if(entry.isTTS()) {
                            preselection = 3;
                        } else if(entry.isEmpty()) {
                            preselection = 4;
                        } else {
                            throw new Error();
                        }
                        int x = e.getXOnScreen();
                        int y = e.getYOnScreen();
                        MultipleChoiceDialog.show(mainFrame, "Frage...", "Bitte treffe eine Auswahl", "OK", options, actions, preselection, callback, x, y);
                        
                        
                    } else if(column == 3) {
                        if(book.getEntry(row).isMP3()) {
                            JFileChooser fc = new JFileChooser(lastChooseMp3DialogPath);
                            fc.setFileFilter(new FileNameExtensionFilter("mp3", "mp3"));
                            if(fc.showOpenDialog(IndexFrame.this) == JFileChooser.APPROVE_OPTION) {
                                try {
                                    File file = fc.getSelectedFile();
                                    if(file.getParent() != null) {
                                        lastChooseMp3DialogPath = file.getParent();
                                    }
                                    book.getEntry(row).setMP3(file);
                                    update();
                                } catch(FileNotFoundException ex) {
                                    JOptionPane.showMessageDialog(IndexFrame.this, "Die Datei '" + fc.getSelectedFile() + "' konnte nicht gefunden werden.");
                                    ex.printStackTrace(System.out);
                                } catch(IOException ex) {
                                    JOptionPane.showMessageDialog(IndexFrame.this, "Die Datei '" + fc.getSelectedFile() + "' konnte nicht gelesen werden.");
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if(action.equals("append_entry")) {
            String _count = appendEntries.getText().trim();
            int count;
            try {
                count = Integer.parseInt(_count);
            } catch(NumberFormatException nfe) {
                count = 1;
                appendEntries.setText("1");
            }
            // append "count" entries
            int start = mainFrame.getBook().getLastID() + 1;
            for(int i = start; i < start + count; i++) {
                mainFrame.getBook().addEntry(i);
            }
        } else if(action.equals("add_entry")) {
            String _oid = newOID.getText().trim();
            try {
                int oid = Integer.parseInt(_oid);
                if((oid < 15001) || (oid >= 0x10000)) {
                    JOptionPane.showMessageDialog(mainFrame, "ungültige OID angegeben (15001 bis 65535)");
                } else {
                    // add "oid" entry
                    if(!mainFrame.getBook().entryForTingIDExists(oid)) {
                        mainFrame.getBook().addEntry(oid);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "OID schon belegt");
                    }
                }
            } catch(NumberFormatException nfe) {
                JOptionPane.showMessageDialog(mainFrame, "ungültige OID angegeben");
            }
        }
        update();
    }
    
    public void update() {
        model = new IndexTableModel(mainFrame.getBook());
        table.setModel(model);
        ((IndexTableModel)model).update();
    }
    
    
    private static class MyRenderer extends DefaultTableCellRenderer {

        Color backgroundColor = getBackground();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            IndexTableModel model = (IndexTableModel) table.getModel();
            if(model.hasWarning(row)) {
                c.setBackground(Color.red);
            } else if (!isSelected) {
                c.setBackground(backgroundColor);
            }
            return c;
        }
    }
    
}



class IndexTableModel implements TableModel {

    
    private HashSet<TableModelListener> listener = new HashSet<TableModelListener>();
    private Book book;
    
    public IndexTableModel(Book book) {
        this.book = book;
    }
    
    @Override
    public int getRowCount() {
        return(book.getSize());
    }

    @Override
    public int getColumnCount() {
        /*
         * rows: index (int), type (Icon), note (String), extra (String)
         * extra enthaelt für mp3s den filenamen und die tracklänge
         * ansonsten ist das feld leer
         */
        return(4);
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0: return("OID");
            case 1: return("Typ");
            case 2: return("Bemerkung");
            case 3: return("Dateiname / TTS");
        }
        throw new Error();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0: return(String.class);
            case 1: return(String.class);
            case 2: return(String.class);
            case 3: return(String.class);
        }
        throw new Error();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 2: return(true);
        }
        return(false);
    }

    public boolean hasWarning(int rowIndex) {
        return(Translator.ting2code(book.getEntry(rowIndex).getTingID()) < 0);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Entry entry;
        switch(columnIndex) {
            case 0: return(Integer.toString(book.getEntry(rowIndex).getTingID()));
            case 1: 
                entry = book.getEntry(rowIndex);
                if(entry.isMP3()) {
                    return("MP3");
                } else if(entry.isCode()) {
                    return("SKRIPT");
                } else if(entry.isSub()) {
                    return("METHODE");
                } else if(entry.isTTS()) {
                    return("TTS");
                } else {
                    return("-");
                }
            case 2: 
                return(book.getEntry(rowIndex).getHint());
            case 3:
                entry = book.getEntry(rowIndex);
                if(entry.isMP3()) {
                    if(entry.getMP3() == null) {
                        return("kein MP3 ausgewählt");
                    }
                    return(book.getEntry(rowIndex).getMP3().getName());
                } else if(entry.isCode() || entry.isSub()) {
                    if(entry.getScript() == null) {
                        return("kein Skript vorhanden");
                    }
                    return("");
                } else if(entry.isTTS()) {
                    if(entry.getTTS() == null) {
                        return("kein TTS vorhanden");
                    }
                    return(entry.getTTS().text);
                } else {
                    return("");
                }
        }
        throw new Error();
    }

    @Override
    public void setValueAt(Object object, int rowIndex, int columnIndex) {
        if(columnIndex == 2) {
            book.getEntry(rowIndex).setHint((String)object);
        }
    }

    public void update() {
        TableModelEvent event = new TableModelEvent(this);
        Iterator<TableModelListener> i = listener.iterator();
        while(i.hasNext()) {
            i.next().tableChanged(event);
        }
    }
    
    @Override
    public void addTableModelListener(TableModelListener l) {
        listener.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listener.remove(l);
    }
    
}
