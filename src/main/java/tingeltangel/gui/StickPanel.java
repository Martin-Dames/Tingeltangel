/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import tingeltangel.core.Book;
import tingeltangel.core.Stick;
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
                                            File dest = Stick.getStickPath();
                                            if(dest != null) {
                                                File[] files = FileEnvironment.getDistDirectory(book.getID()).listFiles(new FilenameFilter() {
                                                    @Override
                                                    public boolean accept(File dir, String name) {
                                                        name = name.toLowerCase();
                                                        return(name.endsWith(".ouf") || name.endsWith(".src") || name.endsWith(".png") || name.endsWith(".txt"));
                                                    }
                                                });
                                                for(int i = 0; i < files.length; i++) {
                                                    fileCopy(files[i], new File(dest, files[i].getName()));
                                                }
                                                JOptionPane.showMessageDialog(frame, "Buch auf den Stift kopiert");
                                            }
                                        } catch(IOException e) {
                                            e.printStackTrace(System.out);
                                            JOptionPane.showMessageDialog(frame, "Buchgenerierung fehlgeschlagen");
                                        } catch(IllegalArgumentException e) {
                                            e.printStackTrace(System.out);
                                            JOptionPane.showMessageDialog(frame, "Buchgenerierung fehlgeschlagen: " + e.getMessage());
                                        } catch(SyntaxError e) {
                                            e.printStackTrace(System.out);
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
                    ex.printStackTrace(System.out);
                }
            }
        });
        add(button);
        
        Runnable task = new TimerTask() {
            @Override
            public void run() {
                try {
                    File stick = Stick.getStickPath();
                    if(online && (stick == null)) {
                        // go offline
                        online = false;
                        button.setEnabled(false);
                        label.setText("keinen Stift gefunden");
                    } else if((!online) && (stick != null)) {
                        // go online
                        online = true;
                        button.setEnabled(true);
                        label.setText("Stift gefunden");
                    }
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(task, 3, 3, TimeUnit.SECONDS);
    }
    
    
    private static void fileCopy(File source, File target) throws IOException {
        System.out.println("copy file from: " + source.getAbsolutePath() + " to " + target.getAbsolutePath());
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        byte[] buffer = new byte[4096];
        int k;
        while((k = in.read(buffer)) != -1) {
            out.write(buffer, 0, k);
        }
        out.close();
        in.close();
    }
    
}
