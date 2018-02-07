/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fmlMetro;

/**
 *
 * @author termaat
 */
public class ExpSummary {
    private final String ExpenseItem;
    private final int ExpenseAmt;
    private final int AnnualAmt;
    private final int SaveTenAmt;
    private final int SaveTenTotal;
    
    public ExpSummary(String ExpenseItem, int ExpenseAmt, int AnnualAmt, int SaveTenAmt, int SaveTenTotal){
        this.ExpenseItem = ExpenseItem;
        this.ExpenseAmt = ExpenseAmt;
        this.AnnualAmt = AnnualAmt;
        this.SaveTenAmt = SaveTenAmt;
        this.SaveTenTotal = SaveTenTotal;
    }

    public String GetExpName(){
        return ExpenseItem;
    }
    public int GetExpAmt(){
        return ExpenseAmt;
    }
    public int GetAnnAmt(){
        return AnnualAmt;
    }
    public int GetSaveTen(){
        return SaveTenAmt;
    }
    public int GetSaveTenTot(){
        return SaveTenTotal;
    }
}

