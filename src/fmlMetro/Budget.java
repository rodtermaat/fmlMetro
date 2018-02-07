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
public class Budget {
    
    private final int id;
    private final String BudgetItem;
    private final int BudgetAmt;
    private final int BudgetActual;
    private final int BudgetPercent;
    
    public Budget(int id, String BudgetItem, int BudgetAmt, int BudgetActual, int BudgetPercent){
        
        this.id = id;
        this.BudgetItem = BudgetItem;
        this.BudgetAmt = BudgetAmt;
        this.BudgetActual = BudgetActual;
        this.BudgetPercent = BudgetPercent;
    }
}
