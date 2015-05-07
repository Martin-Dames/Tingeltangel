
package tingeltangel.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tingeltangel.core.Entry;
import tingeltangel.core.Script;
import tingeltangel.core.scripting.SyntaxError;

public class CodeFrame extends JInternalFrame implements EntryListener, ActionListener {
    
    private JTextArea text = new JTextArea();
    private Script currentScript = null;
    private DocumentListener docListener;
    private MasterFrame frame;
    
    public CodeFrame(MasterFrame frame) {
        super("Code Editor", true, true, true, true);
        this.frame = frame;
        setVisible(true);
        setBounds(915, 5, 260, 590);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        text.setEnabled(false);
        
        docListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if(currentScript != null) {
                    currentScript.setCode(text.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(currentScript != null) {
                    currentScript.setCode(text.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        
        text.getDocument().addDocumentListener(docListener);
        
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(2, 1));
        
        JButton syntaxCheck = new JButton("Syntax Check (alle)");
        syntaxCheck.setActionCommand("check_all");
        syntaxCheck.addActionListener(this);
        buttons.add(syntaxCheck);
        
        JButton reference = new JButton("Syntax Check (nur dieses Skript)");
        reference.setActionCommand("check");
        reference.addActionListener(this);
        buttons.add(reference);
        
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(new JScrollPane(text), BorderLayout.CENTER);
        
        
        setContentPane(panel);
        frame.addEntryListener(this);
        
    }

    @Override
    public void entrySelected(Entry entry) {
        if(entry.isCode() || entry.isSub()) {
            text.setEnabled(true);
            currentScript = entry.getScript();
            if(currentScript == null) {
                currentScript = new Script("", entry);
                entry.setScript(currentScript);
            }
            text.setText(currentScript.toString());
        } else {
            text.getDocument().removeDocumentListener(docListener);
            text.setText("-");
            text.getDocument().addDocumentListener(docListener);
            text.setEnabled(false);
            currentScript = null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("check")) {
            if(currentScript != null) {
                try {
                    currentScript.compile();
                    JOptionPane.showMessageDialog(frame, "Keine Syntax Fehler gefunden", "Syntax Test", JOptionPane.INFORMATION_MESSAGE);
                } catch(SyntaxError se) {
                    JOptionPane.showMessageDialog(frame, "Syntax Fehler in Zeile " + se.getRow() + ": " + se.getMessage(), "Syntax Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if(e.getActionCommand().equals("check_all")) {
            StringBuilder sb = new StringBuilder();
            for(int id = 15001; id <= frame.getBook().getLastID(); id++) {
                Entry entry = frame.getBook().getEntryFromTingID(id);
                if(entry.isCode() || entry.isSub()) {
                    Script script = entry.getScript();
                    try {
                        script.compile();
                    } catch(SyntaxError se) {
                        sb.append("Skript ").append(id).append(" Zeile ").append(se.getRow()).append(": ").append(se.getMessage()).append("\n");
                    }
                }
            }
            if(sb.length() > 0) {
                JOptionPane.showMessageDialog(frame, sb.toString(), "Syntax Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Keine Syntax Fehler gefunden", "Syntax Test", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    
}