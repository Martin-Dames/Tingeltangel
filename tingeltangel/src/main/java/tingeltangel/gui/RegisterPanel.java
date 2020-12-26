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
import tingeltangel.core.Book;
import tingeltangel.core.SortedIntList;
import tingeltangel.core.scripting.RegisterListener;


public class RegisterPanel extends JPanel implements RegisterListener {
    
    private final SortedIntList registers = new SortedIntList();
    
    private final RegisterTableModel model = new RegisterTableModel();
    private final JTable table = new JTable(model);
    private final EditorFrame frame;
    
    public RegisterPanel(EditorFrame frame) {
        super();
        this.frame = frame;
        setLayout(new GridLayout(1, 1));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(1).setMinWidth(20);
        table.getColumnModel().getColumn(2).setPreferredWidth(0);
        JScrollPane jScrollPane = new JScrollPane(table);
        jScrollPane.setPreferredSize(new Dimension(0, 100));
        add(jScrollPane);
    }

    @Override
    public void registerChanged(int register, int value) {
        registers.add(register);
        model.pushEvent(new TableModelEvent(model));
    }

    class RegisterTableModel implements TableModel {

        private final HashSet<TableModelListener> listeners = new HashSet<TableModelListener>();
        
        @Override
        public int getRowCount() {
            return(registers.size());
        }

        @Override
        public int getColumnCount() {
            return(3);
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
                case 0: return("Register");
                case 1: return("Bemerkung");
                case 2: return("Wert");
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
                case 2: return(true);
            }
            throw new Error();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Book book = frame.getBook();
            switch(columnIndex) {
                case 0: return(registers.get(rowIndex));
                case 1: return(book.getEmulator().getHint(registers.get(rowIndex)));
                case 2: return(book.getEmulator().getRegister(registers.get(rowIndex)));
            }
            throw new Error();
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Book book = frame.getBook();
            switch(columnIndex) {
                case 1:
                    book.getEmulator().setHint(registers.get(rowIndex), (String)aValue);
                    pushEvent(new TableModelEvent(this, rowIndex, rowIndex, 1));
                    break;
                case 2:
                    int value = 0;
                    try {
                        value = Integer.parseInt((String)aValue);
                    } catch(NumberFormatException e) {
                    }
                    book.getEmulator().setRegister(registers.get(rowIndex), value);
                    pushEvent(new TableModelEvent(this, rowIndex, rowIndex, 1));
                    break;
            }
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