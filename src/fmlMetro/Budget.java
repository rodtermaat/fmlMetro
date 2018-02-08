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
    
    private int id;
    private int yyyymm;
    private int budgetSave;
    private int budgetCash;
    private int budgetTransp;
    private int budgetGroc;
    private int budgetDine;
    private int budgetUnplan;
    
    public Budget(int id, int yyyymm, int budgetSave, int budgetCash, 
            int budgetTransp, int budgetGroc, int budgetDine, 
            int budgetUnplan){
        
        this.id = id;
        this.budgetSave = budgetSave;
        this.budgetCash = budgetCash;
        this.budgetTransp = budgetTransp;
        this.budgetGroc = budgetGroc;
        this.budgetDine = budgetDine;
        this.budgetUnplan = budgetUnplan;
    }
    
    public int GetBudgetSave(){
        return budgetSave;
    }
    public int GetBudgetCash(){
        return budgetCash;
    }
    public int GetBudgetTransp(){
        return budgetTransp;
    }
    public int GetBudgetGroc(){
        return budgetGroc;
    }
    public int GetBudgetDine(){
        return budgetDine;
    }
    public int GetBudgetUnplan(){
        return budgetUnplan;
    }
}
