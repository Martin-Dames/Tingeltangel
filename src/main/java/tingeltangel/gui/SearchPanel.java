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

    private String SEARCH_LABEL = "Suche";
    private String CLEAR_LABEL = "Löschen";
    private String MP3_TYPE = "MP3";
    private String SCRIPT_TYPE = "Script";
    private String SCRIPT_SUBROUTINE_TYPE = "Script (Subroutine)";
    private String TTS_TYPE = "TTS (Text to speech)";
    private String NAME_LABEL = "Name: ";
    private String ID_LABEL = "ID: ";
    private String TYPE_LABEL = "Type: ";
    private String SEARCH_RESULTS_LABEL = "Ergebnisse der Suche: ";
    private int SEARCH_FIELD_WIDTH = 100;
    private int SEARCH_FIELD_HIGH =  20;



    private final static Logger log = LogManager.getLogger(SearchPanel.class);

    private JTextField name = new JTextField();
    private JTextField id = new JTextField();
    private final String[] types = {"", MP3_TYPE, SCRIPT_TYPE, SCRIPT_SUBROUTINE_TYPE, TTS_TYPE};
    private JComboBox<String> type = new JComboBox<>(types);

    private JButton search = new JButton(SEARCH_LABEL);
    private JButton clear = new JButton(CLEAR_LABEL);


    Border border = BorderFactory.createTitledBorder(SEARCH_LABEL);
    public SearchPanel(EditorFrame mainFrame, EditorPanel editorPanel) {
        this.setBorder(this.border);

        this.add(new JLabel(ID_LABEL));
        this.id.setPreferredSize(new Dimension(SEARCH_FIELD_WIDTH, SEARCH_FIELD_HIGH));
        this.add(this.id);

        this.add(new JLabel(TYPE_LABEL));
        this.add(this.type);

        this.add(new JLabel(NAME_LABEL));
        this.name.setPreferredSize(new Dimension(SEARCH_FIELD_WIDTH, SEARCH_FIELD_HIGH));
        this.add(this.name);

        this.search.addActionListener((ActionEvent se) -> {
            log.info(NAME_LABEL + this.name.getText() + "\n"
                    + ID_LABEL + this.id.getText() + "\n"
                    + TYPE_LABEL + this.type.getSelectedItem().toString()
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
                            BorderFactory.createTitledBorder(SEARCH_RESULTS_LABEL + result.size()));

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
                            BorderFactory.createTitledBorder( SEARCH_RESULTS_LABEL
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
