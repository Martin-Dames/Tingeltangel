
package tingeltangel.gui;

import tingeltangel.tools.Callback;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.core.Repository;
import tingeltangel.core.Stick;
import tingeltangel.core.scripting.SyntaxError;

public class StickFrame extends JInternalFrame implements ActionListener {
    
    private final MasterFrame frame;
    
    private File stick = null;
    private JPanel mainPanel = new JPanel();
    private JPanel activePanel = new JPanel();
    private JLabel inactivePanel = new JLabel("Kein Ting-Stift gefunden");
    
    private JLabel spaceLabel = new JLabel();
    private JLabel debugLabel = new JLabel();
    private JTextArea stickBookContent = new JTextArea();
    private JTextArea stickTBDContent = new JTextArea();
    
    public StickFrame(final MasterFrame frame) {
        super("Ting-Stift", true, true, true, true);
        this.frame = frame;
        setVisible(false);
        setBounds(300, 100, 300, 400);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        JButton refresh = new JButton("aktualisieren");
        refresh.setActionCommand("refresh");
        refresh.addActionListener(this);
        
        
        JPanel barPanel = new JPanel();
        barPanel.setLayout(new BorderLayout());
        barPanel.add(refresh, BorderLayout.EAST);
        
        mainPanel.setLayout(new GridLayout(1, 1));
        mainPanel.add(inactivePanel);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(barPanel, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);
        
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 2));
        infoPanel.add(new JLabel("freier Speicher"));
        infoPanel.add(spaceLabel);
        infoPanel.add(new JLabel("Debug-Modus"));
        infoPanel.add(debugLabel);
        
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1));
        
        JButton currentBookToStickButton = new JButton("aktuelles Buch auf den Stift kopieren");
        currentBookToStickButton.setActionCommand("current2stick");
        currentBookToStickButton.addActionListener(this);
        buttonPanel.add(currentBookToStickButton);
        
        JButton manualBookDownloadButton = new JButton("manueller Buchdownload");
        manualBookDownloadButton.setActionCommand("official2stick");
        manualBookDownloadButton.addActionListener(this);
        buttonPanel.add(manualBookDownloadButton);
        
        JButton toBeDownloadedButton = new JButton("automatischer Buchdownload");
        toBeDownloadedButton.setActionCommand("tbd2stick");
        toBeDownloadedButton.addActionListener(this);
        buttonPanel.add(toBeDownloadedButton);
        
        JButton toggleDebugButton = new JButton("Debugmodus an/aus");
        toggleDebugButton.setActionCommand("debug");
        toggleDebugButton.addActionListener(this);
        buttonPanel.add(toggleDebugButton);
        
        JButton deleteBookButton = new JButton("Buch vom Stift löschen");
        deleteBookButton.setActionCommand("delete");
        deleteBookButton.addActionListener(this);
        buttonPanel.add(deleteBookButton);
        
        JButton deleteFromTbdButton = new JButton("Buch aus TBD Liste löschen");
        deleteFromTbdButton.setActionCommand("deleteFromTbd");
        deleteFromTbdButton.addActionListener(this);
        buttonPanel.add(deleteFromTbdButton);
        
        JButton updateBooksButton = new JButton("Bücher updaten");
        updateBooksButton.setActionCommand("update");
        updateBooksButton.addActionListener(this);
        buttonPanel.add(updateBooksButton);
        
        JButton saveButton = new JButton("Stift sichern");
        saveButton.setActionCommand("saveStick");
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton);
        
        JButton restoreButton = new JButton("Stift wiederherstellen");
        restoreButton.setActionCommand("restoreStick");
        restoreButton.addActionListener(this);
        buttonPanel.add(restoreButton);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Bücher", new JScrollPane(stickBookContent));
        tabs.addTab("TBD", new JScrollPane(stickTBDContent));
        tabs.addTab("Aktionen", buttonPanel);
        
        activePanel.setLayout(new BorderLayout());
        activePanel.add(infoPanel, BorderLayout.NORTH);
                
        activePanel.add(tabs, BorderLayout.CENTER);
        
        
        setContentPane(panel);
        refresh();
    }
    
    private static File[] getMountPoints() throws IOException {
                
        if(System.getProperty("os.name").startsWith("Windows")) {
            return(File.listRoots());
        } else {
            LinkedList<File> mounts = new LinkedList<File>();
            Process process = new ProcessBuilder("/bin/mount").start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String row;
            while((row = in.readLine()) != null) {
                row = row.trim();
                if(row.startsWith("/dev/")) {
                    int p = row.indexOf(" on ");
                    row = row.substring(p + " on ".length());
                    p = row.indexOf(" ");
                    row = row.substring(0, p);
                    mounts.add(new File(row));
                }
            }
            return(mounts.toArray(new File[0]));
        }
        
    }
    
    private void refresh() {
        stick = null;
        File[] mounts = new File[0];
        try {
            mounts = getMountPoints();
        } catch(IOException ioe) {
            ioe.printStackTrace(System.out);
        }
        for(int i = 0; i < mounts.length; i++) {
            if(Stick.checkForStick(mounts[i])) {
                stick = mounts[i];
            }
        }
        mainPanel.removeAll();
        if(stick == null) {
            mainPanel.add(inactivePanel);
            pack();
        } else {
            try {
                HashMap<String, String> settings = Stick.getSettings(stick);
                boolean debug = false;
                if(settings.get("testpen").equals("yes")) {
                    debug = true;
                }

                spaceLabel.setText((Stick.getFreeSpace(stick) / 1048576) + " MB");
                if(debug) {
                    debugLabel.setText("an");
                } else {
                    debugLabel.setText("aus");
                }
                mainPanel.add(activePanel);
                stickBookContent.setText(Stick.getBookContent(stick));
                stickTBDContent.setText(Stick.getTBDContent(stick));
                pack();
            } catch(IOException ioe) {
                ioe.printStackTrace(System.out);
                mainPanel.add(inactivePanel);
                JOptionPane.showMessageDialog(frame, "Fehler: " + ioe.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        try {
            if(action.equals("refresh")) {
                refresh();
            } else if(action.equals("current2stick")) {
                current2stick();
                refresh();
            } else if(action.equals("official2stick")) {
                official2stick();
                refresh();
            } else if(action.equals("tbd2stick")) {
                tbd2stick();
                refresh();
            } else if(action.equals("debug")) {
                debug();
                refresh();
            } else if(action.equals("delete")) {
                delete();
                refresh();
            } else if(action.equals("deleteFromTbd")) {
                deleteFromTbd();
                refresh();
            } else if(action.equals("update")) {
                updateBooks();
                refresh();
            } else if(action.equals("saveStick")) {
                saveStick();
            } else if(action.equals("restoreStick")) {
                restoreStick();
                refresh();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace(System.out);
            JOptionPane.showMessageDialog(frame, "Aktion fehlgeschlagen: " + ioe.getMessage());
        } catch(SyntaxError se) {
            se.printStackTrace(System.out);
            JOptionPane.showMessageDialog(frame, "Aktion fehlgeschlagen: " + se.getMessage());
        }
    }

    private void saveStick() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Tabelle (*.zip)", "zip"));

        if(fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = fc.getSelectedFile().getCanonicalPath();
                if(!file.toLowerCase().endsWith(".zip")) {
                    file += ".zip";
                }
                Stick.saveStick(stick, new File(file));
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(frame, "Sicherung fehlgeschlagen");
                ex.printStackTrace(System.out);
            }
        }
        
    }
    
    private void restoreStick() {
        
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Tabelle (*.zip)", "zip"));

        if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = fc.getSelectedFile().getCanonicalPath();
                if(!file.toLowerCase().endsWith(".zip")) {
                    file += ".zip";
                }
                Stick.restoreStick(stick, new File(file));
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(frame, "wiederherstellen der Sicherung fehlgeschlagen");
                ex.printStackTrace(System.out);
            }
        }
    }
    
    private void current2stick() throws IOException, SyntaxError {
        frame.getBook().export(Stick.getBookDir(stick), null);
    }

    private void official2stick() {
        Integer[] ids = Repository.getIDs();
        String[] options = new String[ids.length];
        for(int i = 0; i < ids.length; i++) {
            String m = Integer.toString(ids[i]);
            while(m.length() < 5) {
                m = "0" + m;
            }
            m += " " + Repository.getBookTxt(ids[i]).get("Name");
            m += " (" + Repository.getBookTxt(ids[i]).get("Author") + ")";
            options[i] = m;
        }
        Callback<String> cb = new Callback<String>() {
            @Override
            public void callback(String s) {
                // extract id
                int p = s.indexOf(" ");
                if(p >= 0) {
                    s = s.substring(0, p);
                }
                try {
                    Stick.downloadOfficial(Stick.getBookDir(stick), Integer.parseInt(s));
                } catch(IOException ioe) {
                    ioe.printStackTrace(System.out);
                    JOptionPane.showMessageDialog(frame, "Fehler beim Herunterladen des Buchs: " + ioe.getMessage());
                }
            }
        };
        MultipleChoiceDialog.showDropdown(frame, "Buch auswählen", "Bitte wähle ein Buch aus", "OK", options, 0, cb);
    }

    private void debug() throws IOException {
        HashMap<String, String> settings = Stick.getSettings(stick);
        if(settings.get("testpen").equals("yes")) {
            settings.put("testpen", "no");
        } else {
            settings.put("testpen", "yes");
        }
        Stick.setSettings(stick, settings);
    }

    private void tbd2stick() throws IOException {
        Iterator<Integer> tbd = Stick.getTBD(stick).iterator();
        LinkedList<Integer> newTbd = new LinkedList<Integer>();
        while(tbd.hasNext()) {
            int id = tbd.next();
            if(Repository.getBookTxt(id) != null) {
                Stick.downloadOfficial(Stick.getBookDir(stick), id);
            } else {
                newTbd.add(id);
            }
        }
        Stick.setTBD(stick, newTbd);
    }

    private void updateBooks() throws IOException {
        Iterator<Integer> books = Stick.getBooks(stick).iterator();
        while(books.hasNext()) {
            int id = books.next();
            int currentVersion = Stick.getBookVersion(stick, id);
            int onlineVersion = Stick.getOnlineBookVersion(id);
            if(onlineVersion > currentVersion) {
                Stick.downloadOfficial(Stick.getBookDir(stick), id);
            }
        }
    }

    private void delete() throws IOException {
        Iterator<Integer> ids = Stick.getBooks(stick).iterator();
        String[] options = new String[Stick.getBooks(stick).size()];
        int i = 0;
        while(ids.hasNext()) {
            int id = ids.next();
            String m = Integer.toString(id);
            while(m.length() < 5) {
                m = "0" + m;
            }
            m += " " + Repository.getBookTxt(id).get("Name");
            m += " (" + Repository.getBookTxt(id).get("Author") + ")";
            options[i++] = m;
        }
        Callback<String> cb = new Callback<String>() {
            @Override
            public void callback(String s) {
                // extract id
                int p = s.indexOf(" ");
                if(p >= 0) {
                    s = s.substring(0, p);
                }
                Stick.delete(stick, Integer.parseInt(s));
            }
        };
        MultipleChoiceDialog.showDropdown(frame, "Buch auswählen", "Bitte wähle ein Buch zum Löschen aus", "OK", options, 0, cb);
    }

    private void deleteFromTbd() throws IOException {
        final LinkedList<Integer> tbd = Stick.getTBD(stick);
        Iterator<Integer> ids = tbd.iterator();
        String[] options = new String[tbd.size()];
        int i = 0;
        while(ids.hasNext()) {
            int id = ids.next();
            String m = Integer.toString(id);
            while(m.length() < 5) {
                m = "0" + m;
            }
            m += " " + Repository.getBookTxt(id).get("Name");
            m += " (" + Repository.getBookTxt(id).get("Author") + ")";
            options[i++] = m;
        }
        Callback<String> cb = new Callback<String>() {
            @Override
            public void callback(String s) {
                // extract id
                int p = s.indexOf(" ");
                if(p >= 0) {
                    s = s.substring(0, p);
                }
                int id = Integer.parseInt(s);
                tbd.remove(id);
                try {
                    Stick.setTBD(stick, tbd);
                } catch(IOException ioe) {
                    ioe.printStackTrace(System.out);
                    JOptionPane.showMessageDialog(frame, "Fehler beim Löschen des Buchs aus der TBD Liste: " + ioe.getMessage());
                }
            }
        };
        MultipleChoiceDialog.showDropdown(frame, "Buch auswählen", "Bitte wähle ein Buch zum Löschen aus", "OK", options, 0, cb);
    }

    
}