package tingeltangel.gui;

import org.apache.log4j.LogManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import tingeltangel.core.Entry;
import tingeltangel.gui.EditorFrame;
import tingeltangel.gui.EditorPanel;
import tingeltangel.gui.IndexListEntry;

public class SearchPanel extends JPanel {

    private final static Logger log = LogManager.getLogger(SearchPanel.class);

    private JTextField name = new JTextField();
    private JTextField id = new JTextField();
    private final String[] types = {"", "MP3", "Script", "Script (Subroutine)", "TTS (Text to speech)"};
    private JComboBox<String> type = new JComboBox<>(types);

    private JButton search = new JButton("Search");
    private JButton clear = new JButton("Clear");


    Border border = BorderFactory.createTitledBorder("Search");
    public SearchPanel(EditorFrame mainFrame, EditorPanel editorPanel) {
        this.setBorder(this.border);
        this.add(new JLabel("Name: "));
        this.name.setPreferredSize(new Dimension(100, 20));
        this.add(this.name);

        this.add(new JLabel("ID: "));
        this.id.setPreferredSize(new Dimension(100, 20));
        this.add(this.id);

        this.add(new JLabel("Type: "));
        this.add(this.type);

        this.search.addActionListener((ActionEvent se) -> {
            log.info("Name : " + this.name.getText() + "\n"
                    + "ID: " + this.id.getText() + "\n"
                    + "type: " + this.type.getSelectedItem().toString()
                    + "selected index type: " + this.type.getSelectedIndex());
            List<Entry> result = new ArrayList<>(mainFrame.getBook().getIndexEntries().values());
            if (!this.name.getText().isEmpty()) {
                result = result
                        .stream()
                        .filter(
                                (entry) ->
                                        entry.getName().equals(this.name.getText()
                                        )
                        ).collect(Collectors.toList());
            }

            if (!this.id.getText().isEmpty() && this.id.getText().matches("-?\\d+")){
                result = result
                        .stream()
                        .filter(
                                (entry) -> Integer.valueOf(this.id.getText()).equals(entry.getTingID())
                        ).collect(Collectors.toList());
            }

            if (this.type.getSelectedIndex() != 0){
                result = result
                        .stream()
                        .filter(
                                (entry) -> (entry.getType() == this.type.getSelectedIndex())
                        ).collect(Collectors.toList());
            }

            editorPanel.getList().removeAll();

            editorPanel.getList()
                    .setBorder(
                            BorderFactory.createTitledBorder("Search results: " + result.size()));

            for (Entry entry: result) {
                editorPanel.getList().add(new IndexListEntry(entry, editorPanel));
            }

            Dimension size = mainFrame.getSize();

            mainFrame.pack();
            mainFrame.setSize(size);
            log.info(result);

        });

        this.add(this.search);

        this.clear.addActionListener((ActionEvent cl) -> {
            this.name.setText("");
            this.id.setText("");
            this.type.setSelectedIndex(0);
            editorPanel.getList().removeAll();

            editorPanel.getList()
                    .setBorder(
                            BorderFactory.createTitledBorder("Search results: "
                                    + mainFrame.getBook().getIndexEntries().size()));

            for (Entry entry: mainFrame.getBook().getIndexEntries().values()) {
                editorPanel.getList().add(new IndexListEntry(entry, editorPanel));
            }

            Dimension size = mainFrame.getSize();

            mainFrame.pack();
            mainFrame.setSize(size);
        });
        this.add(this.clear);


    }

}
