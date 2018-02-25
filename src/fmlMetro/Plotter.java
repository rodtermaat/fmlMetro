/*
 * Used to hold time series data for various charting
 */
package fmlMetro;

/**
 *
 * @author termaat
 */
public class Plotter {

    private final String date;
    private final String type;
    private final int amount;

    public Plotter(String date, String type, int amount){
        this.date = date;
        this.type = type;
        this.amount = amount;
    }
    
    public String GetDate(){
        return date;
    }
    public String GetType(){
        return type;
    }
    public int GetAmt(){
        return amount;
    }

}
