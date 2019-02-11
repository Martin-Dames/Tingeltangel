package tingeltangel.gui;

import org.apache.log4j.LogManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import tingeltangel.core.Entry;

public class SearchPanel extends JPanel {

    private static final String SEARCH_LABEL = "Suche";
    private static final String CLEAR_LABEL = "Löschen";
    private static final String MP3_TYPE = "MP3";
    private static final String SCRIPT_TYPE = "Script";
    private static final String SCRIPT_SUBROUTINE_TYPE = "Script (Subroutine)";
    private static final String TTS_TYPE = "TTS (Text to speech)";
    private static final String NAME_LABEL = "Name: ";
    private static final String ID_LABEL = "ID: ";
    private static final String TYPE_LABEL = "Type: ";
    private static final String SEARCH_RESULTS_LABEL = "Ergebnisse der Suche: ";
    private static final int SEARCH_FIELD_WIDTH = 100;
    private static final int SEARCH_FIELD_HIGH =  20;



    private final static Logger log = LogManager.getLogger(SearchPanel.class);

    private JTextField name = new JTextField();
    private JTextField id = new JTextField();
    private final String[] types = {"", MP3_TYPE, SCRIPT_TYPE, SCRIPT_SUBROUTINE_TYPE, TTS_TYPE};
    private JComboBox<String> type = new JComboBox<>(types);

    private JButton search = new JButton(SEARCH_LABEL);
    private JButton clear = new JButton(CLEAR_LABEL);


    Border border = BorderFactory.createTitledBorder(SEARCH_LABEL);
    public SearchPanel(final EditorFrame mainFrame, final EditorPanel editorPanel) {
        this.setBorder(this.border);

        this.add(new JLabel(ID_LABEL));
        this.id.setPreferredSize(new Dimension(SEARCH_FIELD_WIDTH, SEARCH_FIELD_HIGH));
        this.add(this.id);

        this.add(new JLabel(TYPE_LABEL));
        this.add(this.type);

        this.add(new JLabel(NAME_LABEL));
        this.name.setPreferredSize(new Dimension(SEARCH_FIELD_WIDTH, SEARCH_FIELD_HIGH));
        this.add(this.name);

        this.search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                log.info(NAME_LABEL + name.getText() + "\n"
                        + ID_LABEL + id.getText() + "\n"
                        + TYPE_LABEL + type.getSelectedItem().toString()
                        + "selected index type: " + type.getSelectedIndex());
                List<Entry> result = new ArrayList<>(mainFrame.getBook().getIndexEntries().values());
                List<Entry> filterName = new ArrayList<>();

                if (!name.getText().isEmpty()) {
                    for (Entry entry : result) {
                        if (entry.getName().equals(name.getText())) {
                            filterName.add(entry);
                        }
                    }
                } else {
                    filterName = result;
                }

                List<Entry> filterID = new ArrayList<>();
                if (!id.getText().isEmpty() && id.getText().matches("-?\\d+")) {
                    for (Entry entry : result) {
                        if (entry.getTingID() == Integer.valueOf(id.getText())) {
                            filterID.add(entry);
                        }
                    }
                } else {
                    filterID = result;
                }

                List<Entry> filterType = new ArrayList<>();
                if (type.getSelectedIndex() != 0) {
                    for (Entry entry : result) {
                        if (entry.getType() == type.getSelectedIndex()) {
                            filterType.add(entry);
                        }
                    }
                } else {
                    filterType = result;
                }

                List<Entry> filter = new ArrayList<>();
                filterName.retainAll(filterID);
                filterName.retainAll(filterType);
                filter = filterName;


                editorPanel.getList().removeAll();

                editorPanel.getList()
                        .setBorder(
                                BorderFactory.createTitledBorder(SEARCH_RESULTS_LABEL + filter.size()));

                for (Entry entry : filter) {
                    editorPanel.getList().add(new IndexListEntry(entry, editorPanel));
                }

                Dimension size = mainFrame.getSize();

                mainFrame.pack();
                mainFrame.setSize(size);
                log.info(result);
            }
        });

        this.add(this.search);

        this.clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                name.setText("");
                id.setText("");
                type.setSelectedIndex(0);
                editorPanel.getList().removeAll();

                editorPanel.getList()
                        .setBorder(
                                BorderFactory.createTitledBorder(SEARCH_RESULTS_LABEL
                                        + mainFrame.getBook().getIndexEntries().size()));

                for (Entry entry : mainFrame.getBook().getIndexEntries().values()) {
                    editorPanel.getList().add(new IndexListEntry(entry, editorPanel));
                }

                Dimension size = mainFrame.getSize();

                mainFrame.pack();
                mainFrame.setSize(size);
            }
        });
        this.add(this.clear);


    }

}
