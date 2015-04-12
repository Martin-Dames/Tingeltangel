
package tingeltangel.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

public class InfoFrame extends JInternalFrame {
    
    private JEditorPane text = new JEditorPane();
    
    public InfoFrame(String title, String file) {
        super("", true, true, true, true);
        setTitle(title);
        setVisible(false);
        setBounds(315, 5, 600, 400);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        text.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        text.setEditable(false);
        setContentPane(new JScrollPane(text));
                
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String row;
            StringBuilder s = new StringBuilder();
            while((row = in.readLine()) != null) {
                s.append(row);
            }
            in.close();
            text.setText(s.toString());
            text.setCaretPosition(0);
        } catch(IOException e) {
            throw new Error(e);
        }
    }
    
}