package pureplus;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

class SDQueTableModel extends AbstractTableModel implements SDQueListener  {
    SDQueClient  cli;

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return cli.getQueSize();
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                return row;
            case 1:
                return cli.getQueData(row).get("prompt");
            default:
                return null;
        } 
    }

    SDQueTableModel(SDQueClient c) {
        this.cli = c;
    }

    public void changeSDQue(SDQueEvent ev) {
        if (ev.type==SDQueEvent.REMOVE) {
            fireTableRowsDeleted(0,1);
        } else if (ev.type==SDQueEvent.ADD) {
            int  sz = cli.getQueSize();
            fireTableRowsInserted(sz-1,sz);
        }
    }
}

public class SDQueView implements SDQueListener {
    JTable  table;
    TableModel   model;

    public JComponent create(SDQueClient client) {
        model = new SDQueTableModel(client);
        table = new JTable(model);

        return table;
    }

    public void changeSDQue(SDQueEvent ev) {
        
    }
}
