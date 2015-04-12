
package tingeltangel.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MultipleChoiceDialog {
 
    public static void showDropdown(JFrame frame, String title, String question, String button, String[] options, int preSelected, final StringCallback callback) {
        String answer = (String)JOptionPane.showInputDialog(
                    frame,
                    question,
                    title,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[preSelected]);
        if(answer != null) {
            callback.callback(answer);
        }
    }
    
    public static void show(JFrame frame, String title, String question, String button, String[] options, String[] actions, int preSelected, final StringCallback callback) {
        JRadioButton[] buttons = new JRadioButton[options.length];
        final ButtonGroup group = new ButtonGroup();

        for(int i = 0; i < options.length; i++) {
            buttons[i] = new JRadioButton(options[i]);
            buttons[i].setActionCommand(actions[i]);
            group.add(buttons[i]);
        }

        buttons[preSelected].setSelected(true);

        final JDialog dialog = new JDialog(frame, title, true);

        JButton okButton = new JButton(button);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callback.callback(group.getSelection().getActionCommand());
                dialog.dispose();
            }
        });

        final JPanel box = new JPanel();
        JLabel label = new JLabel(question);

        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        box.add(label);
        for (int i = 0; i < actions.length; i++) {
            box.add(buttons[i]);
        }

        JPanel pane = new JPanel(new BorderLayout());
        pane.add(box, BorderLayout.PAGE_START);
        pane.add(okButton, BorderLayout.PAGE_END);


        dialog.add(pane);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    
}
