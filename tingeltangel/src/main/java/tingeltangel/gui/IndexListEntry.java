/*
    Copyright (C) 2016   Martin Dames <martin@bastionbytes.de>
  
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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.core.Codes;
import tingeltangel.core.Entry;
import tingeltangel.core.MP3Player;
import tingeltangel.core.Translator;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.Callback;
import tingeltangel.tools.Lang;

/**
 *
 * @author martin
 */
public class IndexListEntry extends JPanel {
    
    private final int ICON_MP3 = 0;
    private final int ICON_SCRIPT = 1;
    private final int ICON_SUB_SCRIPT = 2;
    private final int ICON_TTS = 3;
    private final int ICON_PLAY = 4;
    private final int ICON_EJECT = 5;
    private final int ICON_TEST = 6;
    private final int ICON_DELETE = 7;
    private final int ICON_SAVE_PATTERN = 8;
    private final int ICON_COPY_PATTERN = 9;
    private final int ICON_SAVE_MP3 = 10;
    
    private final String[] ICONS = {
        "mp3.png",
        "code.png",
        "sub.png",
        "tts.png",
        "play.png",
        "eject.png",
        "compile.png",
        "delete.png",
        "save-code.png",
        "copy-code.png",
        "save-mp3.png"
    };
    
    private final static String MP3 = "mp3";
    private final static String SCRIPT = "script";
    private final static String SUB = "sub";
    private final static String TTS = "tts";
    
    
    private static String lastChooseMp3DialogPath = null;
    private JLabel trackInfo = new JLabel(" ");
    private final Entry entry;
    private final JTextArea hint = new JTextArea();
    private final JCheckBox codeCheckBox = new JCheckBox();
    private final JTextField name = new JTextField();

    
    private final static Logger log = LogManager.getLogger(IndexListEntry.class);
    
    private ImageIcon getIcon(int res) {
        try {
            return(new ImageIcon(ImageIO.read(getClass().getResource("/icons/" + ICONS[res]))));
        } catch(IOException ioe) {
        }
        return(null);
    }
    
    
    public IndexListEntry(final Entry entry, final EditorPanel frame) {
        super();
        this.entry = entry;
        
        
        boolean unknownID = Translator.ting2code(entry.getTingID()) < 0;
        
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));
        
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        
        JPanel row = new JPanel();
        
        if(unknownID) {
            row.setBackground(Color.red);
        }
        
        final JButton sizeButton = new JButton("<");
        sizeButton.setMargin(new Insets(0, 0, 0, 0));
        sizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(hint.isVisible()) {
                    hint.setVisible(false);
                    sizeButton.setText(">");
                } else {
                    hint.setVisible(true);
                    sizeButton.setText("<");
                }
            }
        });
        row.add(sizeButton);
        
        
        JPanel space2 = new JPanel();
        Dimension spaceDim = new Dimension(20, 1);
        space2.setMinimumSize(spaceDim);
        space2.setMaximumSize(spaceDim);
        row.add(space2);
        
        JButton jboid = new JButton(Integer.toString(entry.getTingID()));
        jboid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                
            }
        });
        row.add(jboid);
        
        
        
        
        int iconRes = -1;
        if(entry.isMP3()) {
            iconRes = ICON_MP3;
        } else if(entry.isCode()) {
            iconRes = ICON_SCRIPT;
        } else if(entry.isSub()) {
            iconRes = ICON_SUB_SCRIPT;
        } else if(entry.isTTS()) {
            iconRes = ICON_TTS;
        }
        JButton icon = new JButton(getIcon(iconRes));
        icon.setToolTipText("Type ändern (MP3, Script, TTS)");
        icon.setMargin(new Insets(0, 0, 0, 0));
        icon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                JComponent source = (JComponent)ae.getSource();
                
                int px = source.getX() + getX() + frame.getMainFrame().getX() - frame.getScrollX();
                int py = source.getY() + getY() + frame.getMainFrame().getY() - frame.getScrollY();
                
                String[] options = {
                    Lang.get("indexFrame.dialog.mp3"),
                    Lang.get("indexFrame.dialog.script"),
                    Lang.get("indexFrame.dialog.sub"),
                    Lang.get("indexFrame.dialog.tts")
                };
                String[] actions = {MP3, SCRIPT, SUB, TTS};
                Callback<String> callback = new Callback<String>() {
                    @Override
                    public void callback(String s) {
                        
                        String oldVal = "";
                        if(entry.isCode() || entry.isSub()) {
                            oldVal = entry.getScript().toString();
                        } else if(entry.isMP3()) {
                            oldVal = entry.getHint();
                        } else if(entry.isTTS()) {
                            oldVal = entry.getTTS().text;
                        }
                        
                        if(s.equals(MP3)) {
                            entry.setMP3();
                            entry.setHint(oldVal);
                        } else if(s.equals(SCRIPT)) {
                            entry.setCode();
                            entry.getScript().setCode(oldVal);
                        } else if(s.equals(SUB)) {
                            entry.setSub();
                            entry.getScript().setCode(oldVal);
                        } else if(s.equals(TTS)) {
                            entry.setTTS();
                            entry.getTTS().text = oldVal;
                        }

                        // reinsert from gui
                        new Thread() {
                            @Override
                            public void run() {
                                JPanel p =  frame.getListPanel();

                                int rowNr = -1;
                                for(int i = 0; i < p.getComponentCount(); i++) {
                                    if(p.getComponent(i) == IndexListEntry.this) {
                                        rowNr = i;
                                        break;
                                    }
                                }
                                if(rowNr < 0) {
                                    throw new Error();
                                }
                                p.remove(IndexListEntry.this);
                                p.add(new IndexListEntry(entry, frame), rowNr);
                                p.revalidate();
                                p.repaint();

                            }
                        }.start();
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
                } else {
                    throw new Error();
                }
                MultipleChoiceDialog.show(frame.getMainFrame(), "Frage...", "Typ ändern", "OK", options, actions, preselection, callback, px, py);
            }
        });
        row.add(icon);
        
        JPanel space = new JPanel();
        space.setMinimumSize(spaceDim);
        space.setMaximumSize(spaceDim);
        row.add(space);
        
        JButton delete = new JButton(getIcon(ICON_DELETE));
        delete.setToolTipText("löschen");
        delete.setMargin(new Insets(0, 0, 0, 0));
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // remove from book
                entry.getBook().removeEntryByTingID(entry.getTingID());
                frame.stopTrack();
                // remove from gui
                new Thread() {
                    @Override
                    public void run() {
                        JPanel p =  frame.getListPanel();
                        p.remove(IndexListEntry.this);
                        p.revalidate();
                        p.repaint();
                    }
                }.start();
            }
        });
        row.add(delete);   

        
        JButton play = new JButton(getIcon(ICON_PLAY));
        play.setToolTipText("abspielen");
        play.setMargin(new Insets(0, 0, 0, 0));
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.setCurrentTrack(entry);
                new Thread() {
                    @Override
                    public void run() {
                        if(entry.isMP3() && (entry.getMP3() != null)) {
                            try {
                                MP3Player.getPlayer().play(entry.getMP3(), new Callback<Exception>() {
                                    @Override
                                    public void callback(Exception t) {
                                        JOptionPane.showMessageDialog(frame, "Fehler bein Abspielen des MP3 (" + entry.getMP3().getAbsolutePath() + "): " + t.getMessage());
                                    }
                                });
                            } catch (FileNotFoundException ex) {
                                JOptionPane.showMessageDialog(frame, "Fehler bein Abspielen des MP3: Die Datei " + entry.getMP3().getAbsolutePath() + " wurde nicht gefunden");
                            }
                        } else if(entry.isCode()) {
                            try {
                                entry.getScript().execute();
                            } catch(SyntaxError se) {
                                JOptionPane.showMessageDialog(frame, "Syntax Fehler (OID " + se.getTingID() + " Zeile " + se.getRow() + "): " + se.getMessage());
                            }
                        } else if(entry.isTTS()) {
                            try {
                                File tts = entry.getTTS().generateTTS(entry);
                                frame.setCurrentTrack(entry);
                                MP3Player.getPlayer().play(tts, new Callback<Exception>() {
                                    @Override
                                    public void callback(Exception t) {
                                        JOptionPane.showMessageDialog(frame, "Es ist ein Fehelr aufgetreten: " + t.getMessage());
                                    }
                                });
                                frame.setCurrentTrack(null);
                            } catch(IOException ioe) {
                                JOptionPane.showMessageDialog(frame, "Es ist ein Fehelr aufgetreten: " + ioe.getMessage());
                            }
                        }
                        frame.setCurrentTrack(null);
                    }
                }.start();
                
            }
        });
        row.add(play);
        
        
        // add eject icon
        JButton eject = new JButton(getIcon(ICON_EJECT));
        eject.setToolTipText("mp3 ändern");
        eject.setMargin(new Insets(0, 0, 0, 0));
        if(entry.isMP3()) {
            eject.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    MP3Player.getPlayer().stop();
                    JFileChooser fc = new JFileChooser(lastChooseMp3DialogPath);
                    fc.setFileFilter(new FileNameExtensionFilter("mp3", "mp3"));
                    if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        try {
                            File file = fc.getSelectedFile();
                            if(file.getParent() != null) {
                                lastChooseMp3DialogPath = file.getParent();
                            }
                            entry.setMP3(file);
                            trackInfo.setText(getTrackInfo(entry));
                            
                            
                        } catch(FileNotFoundException ex) {
                            JOptionPane.showMessageDialog(frame, "Die Datei '" + fc.getSelectedFile() + "' konnte nicht gefunden werden.");
                            log.error("unable to find mp3", ex);
                        } catch(IOException ex) {
                            JOptionPane.showMessageDialog(frame, "Die Datei '" + fc.getSelectedFile() + "' konnte nicht gelesen werden.");
                            log.error("unable to read mp3", ex);
                        }
                    }
                }
            });
        } else {
            eject.setEnabled(false);
        }
        row.add(eject);
        
        
        
        // add compile icon
        JButton compile = new JButton(getIcon(ICON_TEST));
        compile.setToolTipText("compilieren");
        compile.setMargin(new Insets(0, 0, 0, 0));
        if(entry.isCode() || entry.isSub()) {
            compile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        entry.getScript().compile();
                        JOptionPane.showMessageDialog(frame, "Alles OK");
                    } catch(SyntaxError se) {
                        JOptionPane.showMessageDialog(frame, "Syntax Fehler (OID " + se.getTingID() + " Zeile " + se.getRow() + "): " + se.getMessage());
                    }
                }
            });
        } else {
            compile.setEnabled(false);
        }
        row.add(compile);
        
        // save pattern icon
        JButton savePattern = new JButton(getIcon(ICON_SAVE_PATTERN));
        savePattern.setToolTipText("Code in Datei speichern");
        savePattern.setMargin(new Insets(0, 0, 0, 0));
        if((entry.isCode() || entry.isMP3() || entry.isTTS()) && !unknownID) {
            savePattern.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new FileNameExtensionFilter("Ting Pattern (*.png)", "png"));
                    if(fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        try {
                            String file = fc.getSelectedFile().getCanonicalPath();
                            if(!file.toLowerCase().endsWith(".png")) {
                                file = file + ".png";
                            }
                            FileOutputStream out = new FileOutputStream(file);
                            int w = 30;
                            int h = 30;
                            Codes.drawPng(Translator.ting2code(entry.getTingID()), w, h, out);
                            out.close();
                        } catch(Exception e) {
                            JOptionPane.showMessageDialog(frame, "Das Ting Pattern konnte nicht gespeichert werden");
                            log.error("unable to save code", e);
                        }
                    }
                    
                }
            });
        } else {
            savePattern.setEnabled(false);
        }
        row.add(savePattern);
        
        // copy pattern
        JButton copyPattern = new JButton(getIcon(ICON_COPY_PATTERN));
        copyPattern.setToolTipText("Code in die Zwischenablage kopieren");
        copyPattern.setMargin(new Insets(0, 0, 0, 0));
        if((entry.isCode() || entry.isMP3() || entry.isTTS()) && !unknownID) {
            copyPattern.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    BufferedImage image = Codes.generateCodeImage(Translator.ting2code(entry.getTingID()), 30, 30);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageSelection(image), null);
                }
            });
        } else {
            copyPattern.setEnabled(false);
        }
        row.add(copyPattern);
        
        // save mp3
        JButton saveMP3 = new JButton(getIcon(ICON_SAVE_MP3));
        saveMP3.setToolTipText("MP3 speichern");
        saveMP3.setMargin(new Insets(0, 0, 0, 0));
        if(entry.isMP3() || entry.isTTS()) {
            saveMP3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if(entry.getMP3() == null) {
                        JOptionPane.showMessageDialog(frame, "kein MP3 zum speichern vorhanden");
                        return;
                    }
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new FileNameExtensionFilter("MP3 (*.mp3)", "mp3"));
                    if(fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        InputStream is = null;
                        OutputStream os = null;
                        try {
                            String file = fc.getSelectedFile().getCanonicalPath();
                            if(!file.toLowerCase().endsWith(".mp3")) {
                                file = file + ".mp3";
                            }
                            
                            is = new FileInputStream(entry.getMP3());
                            os = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                os.write(buffer, 0, length);
                            }
                            
                        } catch(Exception e) {
                            JOptionPane.showMessageDialog(frame, "MP3 konnte nicht gespeichert werden");
                            log.error("unable to save mp3", e);
                        } finally {
                            try {
                                if(is != null) {
                                    is.close();
                                }
                            } catch (IOException ex) {
                            }
                            try {
                                if(os != null) {
                                    os.close();
                                }
                            } catch (IOException ex) {
                            }
                        }
                    }


                }
            });
        } else {
            saveMP3.setEnabled(false);
        }
        row.add(saveMP3);
        
        row.add(new JLabel("Code:"));
        row.add(codeCheckBox);
        codeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entry.setHasCode(codeCheckBox.isSelected());
            }
        });
        if(entry.isSub()) {
            codeCheckBox.setEnabled(false);
        }
        if(entry.hasCode()) {
            codeCheckBox.setSelected(true);
        }
        
        row.add(new JLabel("Name:"));
        name.setPreferredSize(new Dimension(100, 20));
        row.add(name);
        name.setText(entry.getName());
        name.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                entry.setName(name.getText());
            }
        });
        
        // track info
        trackInfo.setText(getTrackInfo(entry));
        row.add(trackInfo);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(row, BorderLayout.WEST);
        
        header.add(p);
        
        header.setBorder(BorderFactory.createLineBorder(Color.gray));
        
        add(header, BorderLayout.NORTH);
        
        hint.setRows(1);
        
        
        if(entry.isCode() || entry.isSub()) {
            hint.setText(entry.getScript().toString());
        } else if(entry.isTTS()) {
            hint.setText(entry.getTTS().text);
        } else if(entry.isMP3()) {
            hint.setText(entry.getHint());
        }
        
        hint.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                changedUpdate(de);
            }
            @Override
            public void removeUpdate(DocumentEvent de) {
                changedUpdate(de);
            }
            @Override
            public void changedUpdate(DocumentEvent de) {
                if(entry.isCode() || entry.isSub()) {
                    entry.getScript().setCode(hint.getText());
                } else if(entry.isTTS()) {
                    entry.getTTS().text = hint.getText();
                    entry.getTTS().invalidate(entry);
                } else if(entry.isMP3()) {
                    entry.setHint(hint.getText());
                }
            }
        });
        
        add(hint, BorderLayout.CENTER);
        
    }

    public int getOID() {
        return(entry.getTingID());
    }
    
    private String getTrackInfo(Entry entry) {
        if(entry.isMP3()) {
            int min = ((int)entry.getLength()) / 60;
            int sec = ((int)entry.getLength()) - min;
            
            String formatedTime = Integer.toString(sec);
            if(sec < 10) {
                formatedTime = "0" + formatedTime;
            }
            formatedTime = Integer.toString(min) + ":" + formatedTime;
            
            String trackName = "null";
            if(entry.getMP3() != null) {
                trackName = entry.getMP3().getName();
            } else {
                formatedTime = "-";
            }
            
            return(trackName + " (" + formatedTime + ")");
        }
        return(" ");
    }
    
    // This class is used to hold an image while on the clipboard.
    static class ImageSelection implements Transferable {
        private final Image image;

        public ImageSelection(Image image) {
          this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
          return(new DataFlavor[] {DataFlavor.imageFlavor});
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
          return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if(!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }
}