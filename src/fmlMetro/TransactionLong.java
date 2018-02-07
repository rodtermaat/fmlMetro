
package fmlMetro;

/**
 * This class is transaction.  It is composed of various finance related fields
 * And all encapsulated in a object.  
 *
 * @author (rod termaat)
 * @version (0.0)
 */
import java.util.Date;

public class TransactionLong
{
    private final int id;
    private final int date8;
    private final String day;
    private final String mon;
    private final String yr;
    private final String wk;
    private final String type;
    private final String frq;
    private final String category;
    private final String name;
    private final int amount;
    private final int budget;
    private final int balance;

    public TransactionLong(int id, int date8, String day, String mon, String yr,
            String wk, String type, String frq, String category, String name, 
            int amount, int budget, int balance)
    {
        this.id = id;
        this.date8 = date8;
        this.day = day;
        this.mon = mon;
        this.yr = yr;
        this.wk = wk;
        this.type = type;
        this.frq = frq;
        this.category = category;
        this.name = name;
        this.amount = amount;
        this.budget = budget;
        this.balance = balance;
    }

    
    public int getDate(){
        return date8;}

    public String getCategory(){
        return category;}
    
    public String getName(){
        return name;}

    public int getAmount(){
        return amount;}
    
    public int getBudget(){
        return budget;}
    
    public String getDay(){
        return day;
    }
    public String getMon(){
        return mon;
    }
    public String getYr(){
        return yr;
    }
    
    public int gettBalance(){
        return balance;}
    
    public int getID(){
        return id;}
}
