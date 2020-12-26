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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tingeltangel.core.Book;
import tingeltangel.core.Stick;
import tingeltangel.core.TingStick;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.Progress;
import tingeltangel.tools.ProgressDialog;

/**
 *
 * @author mdames
 */
public class StickPanel extends JPanel {
    
    private JLabel label;
    private JButton button;
    private boolean online = false;
    
    private final static Logger log = LogManager.getLogger(StickPanel.class);
    
    public StickPanel(final EditorFrame frame) {
        super();
    
        
        label = new JLabel("keinen Stift gefunden");
        add(label);
        
        button = new JButton("deployen");
        button.setEnabled(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                final Book book = frame.getBook();
                try {
                    book.save();
                    
                    new Progress(frame, "erzeuge Buch (TTS)") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                book.generateTTS(progressDialog);
                                new Progress(frame, "erzeuge Buch (ouf)") {
                                    @Override
                                    public void action(ProgressDialog progressDialog) {
                                        try {
                                            book.export(FileEnvironment.getDistDirectory(book.getID()), progressDialog);
                                            // now copy book to stick
                                            Stick stick = Stick.getAnyStick();
                                            if(stick != null) {
                                                File dest = stick.getBookDir();
                                                /*
                                                if(!dest.getAbsolutePath().contains("$ting")) {
                                                    dest = new File(stick.getBookDir(), "$ting");
                                                }
                                                */
                                                File[] files = FileEnvironment.getDistDirectory(book.getID()).listFiles(new FilenameFilter() {
                                                    @Override
                                                    public boolean accept(File dir, String name) {
                                                        name = name.toLowerCase();
                                                        return(name.endsWith(".ouf") || name.endsWith(".src") || name.endsWith(".png") || name.endsWith(".txt"));
                                                    }
                                                });
                                                for(int i = 0; i < files.length; i++) {
                                                    String destName = files[i].getName();
                                                    if(stick.isBookii()) {
                                                        destName = destName.substring(0, destName.length() - ".ouf".length()) + ".kii";
                                                    }
                                                    FileEnvironment.copy(files[i], new File(dest, files[i].getName()));
                                                }
                                                stick.activateBook(book.getID());
                                                JOptionPane.showMessageDialog(frame, "Buch auf den Stift kopiert");
                                            }
                                        } catch(IOException e) {
                                            log.error("failed to generate book", e);
                                            JOptionPane.showMessageDialog(frame, "Buchgenerierung fehlgeschlagen");
                                        } catch(IllegalArgumentException e) {
                                            log.error("failed to generate book", e);
                                            JOptionPane.showMessageDialog(frame, "Buchgenerierung fehlgeschlagen: " + e.getMessage());
                                        } catch(SyntaxError e) {
                                            log.error("failed to generate book", e);
                                            JOptionPane.showMessageDialog(frame, "Buchgenerierung fehlgeschlagen: Syntax Error in Skript " + e.getTingID() + " in Zeile " + e.getRow() + " (" + e.getMessage() + ")");
                                        }
                                    }
                                };
                            } catch(IOException ioe) {
                                JOptionPane.showMessageDialog(frame, "Buchgenerierung fehlgeschlagen: " + ioe.getMessage());
                            }
                        }
                    };
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Das Buch konnte nicht gespeichert werden");
                    log.error("failed to save book", ex);
                }
            }
        });
        add(button);
        
        Runnable task = new TimerTask() {
            @Override
            public void run() {
                try {
                    Stick stick = Stick.getAnyStick();
                    if(online && (stick == null)) {
                        // go offline
                        online = false;
                        button.setEnabled(false);
                        label.setText("keinen Stift gefunden");
                    } else if((!online) && (stick != null)) {
                        // go online
                        online = true;
                        button.setEnabled(true);
                        label.setText("Stift gefunden (" + stick.getType() + ")");
                    }
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(task, 3, 3, TimeUnit.SECONDS);
    }
    
    
}
