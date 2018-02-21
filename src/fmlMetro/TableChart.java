/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fmlMetro;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *
 * @author termaat
 */
public class TableChart extends JComponent implements TableModelListener {
    protected TableModel model;
    protected ChartPainter cp;
    protected double[] percentages; // Pie slices 
    protected String[] labels; // Labels for slices 
    protected String[] tips; // Tooltips for slices

    protected java.text.NumberFormat formatter = java.text.NumberFormat.getPercentInstance( );
    
    public TableChart(TableModel tm) { 
        setUI(cp = new PieChartPainter()); 
        setModel(tm);
    }

public void setTextFont(Font f) {
    cp.setTextFont(f); 
} 
public Font getTextFont( ) {
    return cp.getTextFont( );
}

public void setTextColor(Color c) { 
    cp.setTextColor(c); 
} 

public Color getTextColor( ) {
    return cp.getTextColor( ); 
}

public void setColor(Color[] clist) {
    cp.setColor(clist); 
} 

public Color[] getColor( ) {
    return cp.getColor( ); 
}

public void setColor(int index, Color c) { 
    cp.setColor(index, c); 
} 

public Color getColor(int index) { 
    return cp.getColor(index); 
}

public String getToolTipText(MouseEvent me) { 
    if (tips != null) {
        int whichTip = cp.indexOfEntryAt(me); 
        
        if (whichTip != -1) {
            return tips[whichTip]; 
        }
    }
    return null; 
}

public void tableChanged(TableModelEvent tme) {
    // Rebuild the arrays only if the structure changed. 
    updateLocalValues(tme.getType( ) != TableModelEvent.UPDATE);
}

public void setModel(TableModel tm) { 
// Get listener code correct.
    if (tm != model) {
        if (model != null) { 
            model.removeTableModelListener(this);
        }
    
    model = tm; model.addTableModelListener(this); 
    updateLocalValues(true);
    
    } 
}

public TableModel getModel( ) { 
    return model; 
}

// Run through the model and count every cell (except the very first column, 
// which we assume is the slice label column).
protected void calculatePercentages( ) {
    double runningTotal = 0.0;

    for (int i = model.getRowCount( ) - 1; i >= 0; i--) {
        percentages[i] = 0.0;
        
        for (int j = model.getColumnCount( ) - 1; j >=0; j--) {
        // First, try the cell as a Number object. 
        Object val = model.getValueAt(i,j);

            if (val instanceof Number) {
                percentages[i] += ((Number)val).doubleValue( ); }
            else if (val instanceof String) {

                // Oops, it wasn't numeric, so try it as a string. 
                try {
                    percentages[i]+=Double.valueOf(val.toString( )).doubleValue( ); }
                catch(Exception e) {
                // Not a numeric string. Give up.
                } 
            }
        }

    runningTotal += percentages[i];

    }
// Make each entry a percentage of the total.
for (int i = model.getRowCount( ) - 1; i >= 0; i--) {
percentages[i] /= runningTotal; }
}

// This method just takes the percentages and formats them as tooltips. 
protected void createLabelsAndTips( ) {
    for (int i = model.getRowCount( ) - 1; i >= 0; i--) {
        labels[i] = (String)model.getValueAt(i, 0);
        tips[i] = formatter.format(percentages[i]);
    }   
}


// Call this method to update the chart. We try to be more efficient here by
// allocating new storage arrays only if the new table has a different number of 
// rows.
protected void updateLocalValues(boolean freshStart) {
    if (freshStart) {
        int count = model.getRowCount( );
        
        if ((tips == null) || (count != tips.length)) {
            percentages = new double[count]; 
            labels = new String[count];
            tips = new String[count];
        } 
    }
    calculatePercentages( ); createLabelsAndTips( );
    
    // Now that everything's up-to-date, reset the chart painter with the new // values.
    cp.setValues(percentages);
    cp.setLabels(labels);

    // Finally, repaint the chart.
    repaint( ); 
    }

}
