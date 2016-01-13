/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.core.Entry;
import tingeltangel.core.MP3Player;
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
        "copy-code.png"
    };
    
    private final static String MP3 = "mp3";
    private final static String SCRIPT = "script";
    private final static String SUB = "sub";
    private final static String TTS = "tts";
    
    
    private String lastChooseMp3DialogPath = null;
    private JLabel trackInfo = new JLabel(" ");
    
    private ImageIcon getIcon(int res) {
        try {
            return(new ImageIcon(ImageIO.read(getClass().getResource("/icons/" + ICONS[res]))));
        } catch(IOException ioe) {
        }
        return(null);
    }
    
    private final Entry entry;
    
    public IndexListEntry(final Entry entry, final IndexPanel frame) {
        super();
        this.entry = entry;
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));
        
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        
        JPanel row = new JPanel();
        
        
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
        icon.setMargin(new Insets(0, 0, 0, 0));
        icon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
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
                        if(s.equals(MP3)) {
                            entry.setMP3();
                        } else if(s.equals(SCRIPT)) {
                            entry.setCode();
                        } else if(s.equals(SUB)) {
                            entry.setSub();
                        } else if(s.equals(TTS)) {
                            entry.setTTS();
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
                MultipleChoiceDialog.show(frame.getMainFrame(), "Frage...", "Typ ändern", "OK", options, actions, preselection, callback, 300, 300);
            }
        });
        row.add(icon);
        
        
        
        JButton delete = new JButton(getIcon(ICON_DELETE));
        delete.setMargin(new Insets(0, 0, 0, 0));
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // remove from book
                entry.getBook().removeEntryByOID(entry.getTingID());
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
        play.setMargin(new Insets(0, 0, 0, 0));
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(!entry.isSub()) {
                    frame.setCurrentTrack(entry);
                }
                new Thread() {
                    @Override
                    public void run() {
                        if(entry.isMP3() && (entry.getMP3() != null)) {
                            MP3Player.getPlayer().play(entry.getMP3());
                        } else if(entry.isCode()) {
                            try {
                                entry.getScript().execute();
                            } catch(SyntaxError se) {
                                JOptionPane.showMessageDialog(frame, "Syntax Fehler (OID " + se.getTingID() + " Zeile " + se.getRow() + "): " + se.getMessage());
                            }
                        } else if(entry.isTTS()) {
                            try {
                                File tts = entry.getTTS().generateTTS(entry);
                                MP3Player.getPlayer().play(tts);
                            } catch(IOException ioe) {
                                JOptionPane.showMessageDialog(frame, "Es ist ein Fehelr aufgetreten: " + ioe.getMessage());
                            }
                        }
                        System.out.println("track done");
                        frame.setCurrentTrack(null);
                    }
                }.start();
                
            }
        });
        row.add(play);
        
        
        if(entry.isMP3()) {
            // add eject icon
            JButton eject = new JButton(getIcon(ICON_EJECT));
            eject.setMargin(new Insets(0, 0, 0, 0));
            eject.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
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
                            ex.printStackTrace(System.out);
                        } catch(IOException ex) {
                            JOptionPane.showMessageDialog(frame, "Die Datei '" + fc.getSelectedFile() + "' konnte nicht gelesen werden.");
                            ex.printStackTrace(System.out);
                        }
                    }
                    
                    
                }
            });
            row.add(eject);
        } else if(entry.isCode() || entry.isSub()) {
            // add compile icon
            JButton compile = new JButton(getIcon(ICON_TEST));
            compile.setMargin(new Insets(0, 0, 0, 0));
            compile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        entry.getScript().compile();
                    } catch(SyntaxError se) {
                        JOptionPane.showMessageDialog(frame, "Syntax Fehler (OID " + se.getTingID() + " Zeile " + se.getRow() + "): " + se.getMessage());
                    }
                }
            });
            row.add(compile);
        }
        
        if(entry.isCode() || entry.isMP3() || entry.isTTS()) {
            // add save pattern icon
            JButton savePattern = new JButton(getIcon(ICON_SAVE_PATTERN));
            savePattern.setMargin(new Insets(0, 0, 0, 0));
            savePattern.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    
                    
                    
                }
            });
            row.add(savePattern);
            JButton copyPattern = new JButton(getIcon(ICON_COPY_PATTERN));
            copyPattern.setMargin(new Insets(0, 0, 0, 0));
            copyPattern.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    
                    
                    
                }
            });
            row.add(copyPattern);
        }
        
        // track info
        trackInfo.setText(getTrackInfo(entry));
        row.add(trackInfo);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(row, BorderLayout.WEST);
        
        header.add(p);
        
        header.setBorder(BorderFactory.createLineBorder(Color.gray));
        
        add(header, BorderLayout.NORTH);
        
        final JTextArea hint = new JTextArea();
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
            }
            
            return(trackName + " (" + formatedTime + ")");
        }
        return(" ");
    }
}
