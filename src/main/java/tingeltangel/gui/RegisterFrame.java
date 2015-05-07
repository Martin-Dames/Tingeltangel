
package tingeltangel.gui;

import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import tingeltangel.core.Book;
import tingeltangel.core.SortedIntList;
import tingeltangel.core.scripting.RegisterListener;


public class RegisterFrame extends JInternalFrame implements RegisterListener {
    
    private SortedIntList registers = new SortedIntList();
    
    private JTable table;
    private RegisterTableModel model;
    private final MasterFrame frame;
    
    public RegisterFrame(MasterFrame frame) {
        super("Register", true, true, true, true);
        setVisible(true);
        setBounds(610, 345, 300, 250);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.frame = frame;
        model = new RegisterTableModel();
        table = new JTable(model);
        setContentPane(new JScrollPane(table));
    }

    @Override
    public void registerChanged(int register, int value) {
        registers.add(register);
        model.pushEvent(new TableModelEvent(model));
    }

    class RegisterTableModel implements TableModel {

        private HashSet<TableModelListener> listeners = new HashSet<TableModelListener>();
        
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