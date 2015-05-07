
package tingeltangel.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import tingeltangel.core.scripting.Command;
import tingeltangel.core.scripting.Commands;

public class ReferenceFrame extends JInternalFrame {
    
    private JEditorPane text = new JEditorPane();
    
    public ReferenceFrame(MasterFrame frame) {
        super("Code Referenz", true, true, true, true);
        setVisible(false);
        setBounds(315, 5, 600, 400);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        text.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        text.setEditable(false);
        setContentPane(new JScrollPane(text));
        
        StringBuilder r = new StringBuilder();
        Iterator<Command> i = Commands.iterator();
        while(i.hasNext()) {
            Command c = i.next();
            
            String args = "";
            if(c.getNumberOfArguments() > 0) {
                if(c.firstArgumentIsLabel()) {
                    args = "[Label]";
                } else if(c.firstArgumentIsRegister()) {
                    args = "[Register]";
                } else if(c.firstArgumentIsValue()) {
                    args = "[Value]";
                }
            }
            if(c.getNumberOfArguments() > 1) {
                if(c.secondArgumentIsRegister()) {
                    args += " [Register]";
                } else if(c.secondArgumentIsValue()) {
                    args += " [Value]";
                }
            }
            
            String hex = Integer.toHexString(c.getCode());
            while(hex.length() < 4) {
                hex = "0" + hex;
            }
            r.append("<tr><td>").append(c.getAsm()).append(" ").append(args).append("</td><td>");
            r.append(c.getDescription()).append("</td><td>0x").append(hex);
            r.append("</td></tr>");
        }
        
        try {
            BufferedReader in = new BufferedReader(new FileReader("html/reference.html"));
            String row;
            StringBuilder s = new StringBuilder();
            while((row = in.readLine()) != null) {
                s.append(row.replace("%%REFERENCE%%", r.toString()));
            }
            in.close();
            text.setText(s.toString());
            text.setCaretPosition(0);
        } catch(IOException e) {
            throw new Error(e);
        }
    }
    
}