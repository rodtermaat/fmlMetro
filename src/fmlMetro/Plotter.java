/*
 * Used to hold time series data for various charting
 */
package fmlMetro;

/**
 *
 * @author termaat
 */
public class Plotter {

    private final int mon;
    private final int wk;
    private final String type;
    private final int amount;

    public Plotter(int mon, int wk, String type, int amount){
        this.mon = mon;
        this.wk = wk;
        this.type = type;
        this.amount = amount;
    }
    
    public int GetMon(){
        return mon;
    }
    public int GetWeek(){
        return wk;
    }
    public String GetType(){
        return type;
    }
    public int GetAmt(){
        return amount;
    }

}
