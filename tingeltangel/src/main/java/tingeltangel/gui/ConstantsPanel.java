/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import tingeltangel.core.scripting.Constants;


public class ConstantsPanel extends JPanel {
    
    
    private final ConstantsTableModel model = new ConstantsTableModel();
    private final JTable table = new JTable(model);
    private final EditorFrame frame;
    
    public ConstantsPanel(EditorFrame frame) {
        super();
        this.frame = frame;
        setLayout(new GridLayout(1, 1));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(1).setMinWidth(20);
        JScrollPane jScrollPane = new JScrollPane(table);
        jScrollPane.setPreferredSize(new Dimension(0, 100));
        add(jScrollPane);
    }


    class ConstantsTableModel implements TableModel {

        private final HashSet<TableModelListener> listeners = new HashSet<TableModelListener>();
        
        @Override
        public int getRowCount() {
            return(frame.getBook().getConstants().getNames().size());
        }

        @Override
        public int getColumnCount() {
            return(2);
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
                case 0: return("Name");
                case 1: return("Wert");
            }
            throw new Error();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return(String.class);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            switch(columnIndex) {
                case 0: return(false);
                case 1: return(true);
            }
            throw new Error();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Constants constants = frame.getBook().getConstants();
            String name = constants.getNameAt(rowIndex);
            switch(columnIndex) {
                case 0: return(name);
                case 1: return(constants.get(name));
            }
            throw new Error();
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            String value = (String)aValue;
            Constants constants = frame.getBook().getConstants();
            switch(columnIndex) {
                case 1:
                    String name = constants.getNameAt(rowIndex);
                    constants.set(name, value);
                    break;
            }
            pushEvent(new TableModelEvent(this, rowIndex, rowIndex, 1));
        }
        
        private void pushEvent(TableModelEvent event) {
            Iterator<TableModelListener> i = listeners.iterator();
            while(i.hasNext()) {
                i.next().tableChanged(event);
            }
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

    }
}