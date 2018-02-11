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
public class CodeStorage {
   
            // everything checks out, so add the transaction already
            // by default we will add out transactions for the entire year
            
            // get a calendar object using jDate (date entered)
            //Calendar cal = Calendar.getInstance(new Locale("en","UK"));
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(jDate);
//            System.out.println("input date is " + jDate);
//            System.out.println("caldendar date is " + cal.getTime().toString());
//            for (int i=0; i<12; i++){
//                cal.add((Calendar.MONTH), modifier);
//                
//                //String.format("%02d",1)
//                mon = leftPad((cal.get(Calendar.MONTH)+1),2);
//                day = leftPad(cal.get(Calendar.DAY_OF_MONTH),2);
//                yr = String.valueOf(cal.get(Calendar.YEAR));
//                sDate8 = yr + mon + day;
//                iDate8 = Integer.parseInt(sDate8);
//        
//                //System.out.println("cal mon is " + (cal.get(Calendar.MONTH)+1) +
//                //        " cal day " + cal.get(Calendar.DAY_OF_MONTH) +
//                //        " cal day " + strDays[cal.get(Calendar.DAY_OF_WEEK)-1] +
//                //        " cal year " + cal.get(Calendar.YEAR) + 
//                //        " cal week " + cal.get(Calendar.WEEK_OF_YEAR));
//                
//                if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//                    // if sunday subtract 1 week from week of the year. its 
//                    // important that the week always start on Monday
//                    wk = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR) - 1);
//                    //System.out.println("our week " + ourWK);
//                }
//                else {
//                    wk = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));  
//                }
//                
//                modifier=1;
//                int xID = sqlite.AddTransaction(iDate8, day, mon, yr, wk, type, frq, cat, name, amt, budg);
//            }    
 
    // display all transactions
//    rdoIEweek.setSelected(false);
//        rdoIEmonth.setSelected(false);
//        IEmonTracker = 0;
//        IEweekTracker = 7;
//        ListTransactionsByDate(inTheBeginning, inTheYear2525);
//        RefreshAnalytics(inTheBeginning, inTheYear2525);

//    display mpnnth
//        rdoIEweek.setSelected(false);
//        rdoIEall.setSelected(false);
//        IEmonTracker = 0;
//        int ieFOM = dtx.getIntFOM(0);
//        int ieEOM = dtx.getIntEOM(0);
//        //System.out.println("month button beg " + ieFOM);
//        //System.out.println("month button end " + ieEOM);
//        
//        ListTransactionsByDate(ieFOM, ieEOM);
//        RefreshAnalytics(ieFOM, ieEOM);    

//     display week
//        IEweekTracker = 7;
//        rdoIEmonth.setSelected(false);
//        rdoIEall.setSelected(false);
//        int ieFOW = dtx.getIntFOW(IEweekTracker);
//        int ieEOW = dtx.getIntEOW(IEweekTracker);
//        //System.out.println("month button beg " + ieFOW);
//        //System.out.println("month button end " + ieEOW);
//        
//        ListTransactionsByDate(ieFOW, ieEOW);
//        RefreshAnalytics(ieFOW, ieEOW);

//      moving forward in time
//        if(rdoIEmonth.isSelected()){
//            IEmonTracker++;
//            int ieFOM = dtx.getIntFOM(IEmonTracker);
//            int ieEOM = dtx.getIntEOM(IEmonTracker);
//            //System.out.println("mon forward beg " + ieFOM);
//            //System.out.println("mon forward end " + ieEOM);
//            ListTransactionsByDate(ieFOM, ieEOM);
//            RefreshAnalytics(ieFOM, ieEOM);
//        }
//        if(rdoIEweek.isSelected()){
//            IEweekTracker = IEweekTracker+7;
//            int ieFOW = dtx.getIntFOW(IEweekTracker);
//            int ieEOW = dtx.getIntEOW(IEweekTracker);
//            //System.out.println("week forward beg " + ieFOW);
//            //System.out.println("week forward end " + ieEOW);
//            ListTransactionsByDate(ieFOW, ieEOW);
//            RefreshAnalytics(ieFOW, ieEOW);
//        }

//        moving backwards
//        if(rdoIEmonth.isSelected()){
//            IEmonTracker--;
//            int ieFOM = dtx.getIntFOM(IEmonTracker);
//            int ieEOM = dtx.getIntEOM(IEmonTracker);
//            //System.out.println("mon backward beg " + ieFOM);
//            //System.out.println("mon backward end " + ieEOM);
//            ListTransactionsByDate(ieFOM, ieEOM); 
//            RefreshAnalytics(ieFOM, ieEOM);
//        }
//        if (rdoIEweek.isSelected()){
//            IEweekTracker = IEweekTracker-7;
//            int ieFOW = dtx.getIntFOW(IEweekTracker);
//            int ieEOW = dtx.getIntEOW(IEweekTracker);
//            //System.out.println("week backward beg " + ieFOW);
//            //System.out.println("week backward end " + ieEOW);
//            ListTransactionsByDate(ieFOW, ieEOW);
//            RefreshAnalytics(ieFOW, ieEOW);

//            code to add customization to table
//tblLedger = new javax.swing.JTable(){
//
//    public Component prepareRenderer(TableCellRenderer r, int rw, int col)
//    {
//        Component c = super.prepareRenderer(r,rw,col);
//        c.setBackground(Color.WHITE);
//        if(col==5){
//            c.setBackground(Color.PINK);
//        }
//        return c;
//    }
//};
    
//    private void ListTransactions(){
//      
//      //remove all the rows currently in the table
//      model.setRowCount(0);
//      
//      ArrayList<TransactionShort> ledgerList = sqlite.getAllObjects();
//      Object rowData[] = new Object[6];
//      for(int i = 0; i < ledgerList.size(); i++)
//      {
//        rowData[0] = ledgerList.get(i).getID();
//        
//        String sdate8 = String.valueOf(ledgerList.get(i).getDate());
//        String smon = sdate8.substring(4, 6);
//        String sday = sdate8.substring(6);
//        String shortDate = smon + "/" + sday;
//        
//        rowData[1] = shortDate;
//        //rowData[1] = ledgerList.get(i).getDate();
//        rowData[2] = ledgerList.get(i).getName();
//        rowData[3] = ledgerList.get(i).getCategory();
//        rowData[4] = ledgerList.get(i).getAmount();
//        rowData[5] = ledgerList.get(i).getBalance();
//        
//        model.addRow(rowData); 
//      }
//      tblLedger.getColumnModel().getColumn(0).setWidth(0);
//      tblLedger.getColumnModel().getColumn(0).setMinWidth(0);
//      tblLedger.getColumnModel().getColumn(0).setMaxWidth(0); 
//      //DisplayCatSummary();
//    }

//    //  RODT - format date to DE format
//    // takes the integer date YYYYMMDD and returns MM/DD/YY for the DE screen
//    private String FormatDate(String ymddate){
//        java.util.Date date;
//        String mdydate = "";
//        
//        SimpleDateFormat mdyFormattedDate = new SimpleDateFormat("MM/dd/yy");
//        SimpleDateFormat ymdFormattedDate = new SimpleDateFormat("yyyyMMdd");
//       
//        try
//        {
//          date = ymdFormattedDate.parse(ymddate);
//          mdydate = mdyFormattedDate.format(date);          
//        }
//          catch(ParseException e) {
//          DisplayIEmessage("Enter date like 04/15/18 " +
//                        " and not some other bogus format");
//          ClearIEdataEntry();
//        }
//
//        return mdydate;
//    }

}
