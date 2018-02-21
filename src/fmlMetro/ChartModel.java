/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fmlMetro;

//import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author termaat
 */
public class ChartModel {
    
    TableModel tm;
    
    public ChartModel(String datam[][]){
        //super("Simple JTable Test");
        //setSize(300, 200); 
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
            
        tm = new AbstractTableModel( ) { 
            String data[][] = datam;
            //String data[][] = {
            //{"House", "2125"},
            //    {"Food", "1250"}, 
            //    {"Debt", "725"}, 
            //    {"Unplanned", "914"}, 
            //    {"Another", "324"}
            //};
        
            //{"Ron", "0.00", "68.68", "77.34", "78.02"},
            //    {"Ravi", "0.00", "70.89", "64.17", "75.00"}, 
            //    {"Maria", "76.52", "71.12", "75.68", "74.14"}, 
            //    {"James", "70.00", "15.72", "26.40", "38.32"}, 
            //    {"Ellen", "80.32", "78.16", "83.80", "85.72"}
            //};
                
        //String headers[] = { "", "Q1", "Q2", "Q3", "Q4" };
        String headers[] = { "", "Amount" };
        public int getColumnCount( ) { 
            return headers.length; 
        } 
        
        public int getRowCount( ) { 
            return data.length;
        }
        
        public String getColumnName(int col) { 
            return headers[col]; 
        }
        
        public Class getColumnClass(int col) {
            return (col == 0) ? String.class : Number.class; 
        }

        public boolean isCellEditable(int row, int col) { 
            return true; 
        } 
        
        public Object getValueAt(int row, int col) { 
            return data[row][col]; 
        } 
        
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = (String)value;
            fireTableRowsUpdated(row,row); 
        }
        };  //end of AbstractTableModel
        
        //final TableChartPopup tcp = new TableChartPopup(tm); 
        //tcp.setVisible(true);
        
    }
}
