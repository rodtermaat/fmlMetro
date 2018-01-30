package fmlMetro;

/**
 * Starting to persist the data by using SQLite
 * 
 * @author (rod termaat)
 * @version (v03)
 */
import java.sql.Date;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.*;
import java.io.File;

public class SQLite
{
    // instance variables - replace the example below with your own
    String url = "jdbc:sqlite:/Users/termaat/sqlite/db/fml.db";
    
    /**
     * Constructor for objects of class SQLite
     */
    public SQLite()
    {
        // initialise instance variables

    }

    //create the connection to the Ledger.db
    public Connection connect() {
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Create Connection Error: " + e.getMessage());
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return conn;
    } 
    
    // create the Ledger.db
    // location seems like it should be the same place as the app is installed
    // and not some random location like below
    public void CreateDataBase(){
        final File f = new File(url);
        
        if (!f.exists()){
          try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }
 
          } catch (SQLException e) {
            //System.out.println("Create DB Error: " + e.getMessage());
            //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          }
       }
    }
    
    // create the ledger table in the Ledger DB
    public void CreateFMLtbl(){ 
        
        String sql = "CREATE TABLE IF NOT EXISTS ledger (\n" +
                     " id INTEGER PRIMARY KEY,\n" +
                     " date8 INTEGER,\n" +
                     " day INTEGER,\n" +
                     " mon INTEGER,\n" +
                     " yr INTEGER,\n" +
                     " wk INTEGER,\n" +
                     " type TEXT,\n" +
                     " frequency TEXT,\n" + 
                     " category TEXT,\n" +
                     " name TEXT,\n" +
                     " amount INTEGER,\n" +
                     " budget INTEGER,\n" +
                     " time DATETIME DEFAULT CURRENT_TIMESTAMP);";
   
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            if(stmt != null){
                stmt.close();
            }
            if(conn != null) {
                conn.close();
            }
            //System.out.println("Ledger table has been created.");            
        } catch (SQLException e) {
            //System.out.println("Create TranTbl Error: " + e.getMessage());
            //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return;
        }
 
    }
       
    // check to see if database and tables exist oe do we need to create it
    public int IsAcctSetup(){
        String sql="SELECT count(*) from ledger";
        int count=0;
        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully")
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery(sql);
           
            while(rs.next()){
                count=rs.getInt(1);
            }
            
             if(rs != null) {
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(conn != null) {
                conn.close();
            } 
            
        } catch (Exception e) {
        }
        return count;
    }
    
    // get tranaction to populate DE.  need when we started messing with 
    // the data display in the table
    public Transaction GetTransaction(int id){
        int idx = 0; String category = ""; String name = "";
        int amount = 0; int balance = 0;
        Transaction tran = null;
        
        String sql = "SELECT * from ledger WHERE id = ?";
        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully");
           
           //Statement stmt = conn.createStatement();
           PreparedStatement pstmt  = conn.prepareStatement(sql);
           pstmt.setInt(1,id);
           ResultSet rs = pstmt.executeQuery();
           
           while ( rs.next() ) {
              idx = rs.getInt("id");
              Date date = rs.getDate("date");
              category = rs.getString("category");
              name = rs.getString("name");
              amount = rs.getInt("amount");
              
              tran = new Transaction(idx, date, category, name, amount, 0);
           }
           
           //System.out.println("Check Balance successful");
            if(rs != null) {
                rs.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null) {
                conn.close();
            } 
        }
        catch ( Exception e ) {
           System.out.println("Check Balance Error: " + e.getMessage());
           System.out.println( e.getClass().getName() + ": " + e.getMessage() );
        }
      
        return tran;
    }
    // get balance based on date
    public int GetBalance(Date dater){
    String sql = "SELECT SUM(amount) AS balance FROM ledger WHERE date <= ?";
        int balance = 0;
        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully");
           
           //Statement stmt = conn.createStatement();
           PreparedStatement pstmt  = conn.prepareStatement(sql);
           pstmt.setDate(1,dater);
           ResultSet rs = pstmt.executeQuery();
           
           while ( rs.next() ) {
              balance = rs.getInt("balance");
           }
           
           //System.out.println("Check Balance successful");
            if(rs != null) {
                rs.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null) {
                conn.close();
            } 
        }
        catch ( Exception e ) {
           System.out.println("Check Balance Error: " + e.getMessage());
           System.out.println( e.getClass().getName() + ": " + e.getMessage() );
        }
      
        return balance;
    }
    
    public int getCategorySum(Date date1, Date dateTo, String cat){
        int amtSum = 0;
        
        String sql = "SELECT SUM(amount) as sumAmt FROM ledger WHERE\n" +
                 " category = ? and date > ? and date <= ?";
        
        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully");
           
           //Statement stmt = conn.createStatement();
           PreparedStatement pstmt  = conn.prepareStatement(sql);
           pstmt.setString(1,cat);
           pstmt.setDate(2, date1);
           pstmt.setDate(3, dateTo);
           ResultSet rs = pstmt.executeQuery();
           
           while ( rs.next() ) {
              amtSum = rs.getInt("sumAmt");
           }
           
           //System.out.println("Check Balance successful");
            if(rs != null) {
                rs.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null) {
                conn.close();
            } 
        }
        catch ( Exception e ) {
           System.out.println("Check Balance Error: " + e.getMessage());
           System.out.println( e.getClass().getName() + ": " + e.getMessage() );
        }
                
        return amtSum;
    }
    
    // add a record to the ledger table
    public int insertTran(Date date, String category, String name, int amount) {
        String sql = "INSERT INTO ledger(date,category,name,amount) VALUES(?,?,?,?)";
        int last_inserted_id = 0;
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, date);
            pstmt.setString(2, category);
            pstmt.setString(3, name);
            pstmt.setInt(4, amount);
            pstmt.executeUpdate();
            //System.out.println("Add Transaction successful");
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
              last_inserted_id = rs.getInt(1);
            }
            
            if(rs != null) {
                rs.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null) {
                conn.close();
            } 

        } catch (SQLException e) {
            //System.out.println("Insert Transaction Error: " + e.getMessage());
            //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return last_inserted_id;
        
    }
    
    public void UpdateTran(int id, Date date, String category, String name, int amount){
        String sql = "UPDATE ledger SET date = ? , "
                + "category = ? , "
                + "name = ? , "
                + "amount = ? "
                + "WHERE id = ?";
 
        try (Connection conn = DriverManager.getConnection(url);
          PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // set the corresponding param
            pstmt.setDate(1, date);
            pstmt.setString(2, category);
            pstmt.setString(3, name);
            pstmt.setInt(4, amount);
            pstmt.setInt(5, id);
            // update 
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }    
    }
    
    public void deleteTran(int id) {
        
        String sql = "DELETE FROM ledger WHERE id = ?";
        //String sql = "DELETE FROM ledger WHERE id = (SELECT MAX(id) FROM ledger)";

        //try (Connection conn = this.connect();
        try (Connection conn = DriverManager.getConnection(url);
          PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
           // set the corresponding param
           pstmt.setInt(1, id);
           // execute the delete statement
           pstmt.executeUpdate();
           //System.out.println("Delete Transaction Successful");
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null) {
                conn.close();
            } 

        } catch (SQLException e) {
            //System.out.println("Delete Transaction Error: " + e.getMessage());
        }
    }
   
public ArrayList<Transaction> getTransactionsByDate(Date date1, Date date30){
       
//String sql = "SELECT SUM(amount) AS balance FROM ledger WHERE date <= ?";
    ArrayList<Transaction> rows2 = new ArrayList<>();
                    // create an ArrayList of the Transaction object and creates
                    // the ledger/checkbook of the application
    Transaction row2;
                    // not sure what this actually does, but is needed based
                    // on similar sample code I have studied
    
    String sql = "select id, date, time, category, name, amount,\n" +
                    " (select sum(t2.amount) from ledger t2 where\n" +
                    " ((t2.date <= t1.date and t2.time <= t1.time) or\n" +
                    " (t2.date < t1.date))\n" +
                    " order by date ) as accumulated\n" +
                    " from ledger t1\n" +
                    " where date <= ?\n" +
                    " order by date, time;";

        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully");
           
           //Statement stmt = conn.createStatement();
           PreparedStatement pstmt  = conn.prepareStatement(sql);
           pstmt.setDate(1,date30);
           //pstmt.setDate(2,date1);
           ResultSet rs = pstmt.executeQuery();
           
           while ( rs.next() ) {
              int id = rs.getInt("id");
              Date date = rs.getDate("date");
              String  category = rs.getString("category");
              String  name = rs.getString("name");
              int amount  = rs.getInt("amount");
              int balance  = rs.getInt("accumulated");
              
              // when you do .compareTo if the number your comparing to 
              // is greater you will get a negative integer returned otherwise
              // it will be positive if your object is larger. 
              // It will be zero if they are equal.
              // date < date1 = negative 
              
              if(date.after(date1) && (date.before(date30))){
                row2 = new Transaction(id, date, category, name, amount, balance);
                rows2.add(row2);
              }
           }
           
            if(rs != null) {
                rs.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(conn != null) {
                conn.close();
            } 
        }
        catch ( Exception e ) {
           System.out.println("Check Balance Error: " + e.getMessage());
           System.out.println( e.getClass().getName() + ": " + e.getMessage() );
        }
      
        return rows2;
}

    //  This gets all the transactions and
    // puts them in an ArrayList object
    // it also created the running total
    // accumulated field and returns as the
    // balance field
    public ArrayList<Transaction> getAllObjects(){
       //String sql = "SELECT * from Ledger ORDER BY date ASC;";
       String sql = "select id, date, time, category, name, amount,\n" +
                    " (select sum(t2.amount) from ledger t2 where\n" +
                    " ((t2.date <= t1.date and t2.time <= t1.time) or\n" +
                    " (t2.date < t1.date))\n" +
                    " order by date ) as accumulated\n" +
                    " from ledger t1 order by date, time;";
       
       //String url = "jdbc:sqlite:/Users/termaat/sqlite/db/Ledger.db";
       //ArrayList<Object> objectList = new ArrayList<Object>();
       ArrayList<Transaction> rows2 = new ArrayList<>();
                    // create an ArrayList of the Transaction object and creates
                    // the ledger/checkbook of the application
       Transaction row2;
                    // not sure what this actually does, but is needed based
                    // on similar sample code I have studied
        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully");
           
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery(sql);
           
           while ( rs.next() ) {
              int id = rs.getInt("id");
              Date date = rs.getDate("date");
              String  category = rs.getString("category");
              String  name = rs.getString("name");
              int amount  = rs.getInt("amount");
              int balance  = rs.getInt("accumulated");
              
              /**
              System.out.println( "ID = " + id );
              System.out.println( "DATE = " + date);
              System.out.println( "CATEGORY = " + category );
              System.out.println( "NAME = " + name );
              System.out.println( "AMOUNT = " + amount);
              System.out.println( "BALANCE = " + balance );
              System.out.println();
              */
             
              //objectList.add(object);
              row2 = new Transaction(id, date, category, name, amount, balance);
              rows2.add(row2);
           }
           
            if(rs != null) {
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(conn != null) {
                conn.close();
            } 
        }
        catch ( Exception e ) {
            //System.out.println("Get ArrayList Error: " + e.getMessage());           
            //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        
        return rows2;
    }
    
    // Returns a summary by Category of the amount for a time period
//    public ArrayList<CatSummary> GetCatSummary(Date date1, Date date30){
//        String sql = "SELECT category, SUM(amount) AS totAmt FROM ledger\n" +
//                 " WHERE amount < 0 and\n" +
//                 " date > ? and date <= ? GROUP BY category\n" +
//                 " ORDER BY totAmt LIMIT 8";
//
//        ArrayList<CatSummary> CatSum = new ArrayList<>();
//                    // create an ArrayList of the Transaction object and creates
//                    // the ledger/checkbook of the application
//        CatSummary CatRow;
//
//        try {
//           Class.forName("org.sqlite.JDBC");
//           Connection conn = DriverManager.getConnection(url);
//           conn.setAutoCommit(false);
//           //System.out.println("Opened database successfully");
//           
//           //Statement stmt = conn.createStatement();
//           PreparedStatement pstmt  = conn.prepareStatement(sql);
//           pstmt.setDate(1,date1);
//           pstmt.setDate(2,date30);
//           ResultSet rs = pstmt.executeQuery();
//           
//           while ( rs.next() ) {
//              String category = rs.getString("category");
//              int totAmt = rs.getInt(("totAmt"));
//            
//              //System.out.println( "Category: " + category + " - " + totAmt);
//              
//              CatRow = new CatSummary(category, totAmt);
//              CatSum.add(CatRow);
//           }
//           
//           //System.out.println("Check Balance successful");
//            if(rs != null) {
//                rs.close();
//            }
//            if(pstmt != null){
//                pstmt.close();
//            }
//            if(conn != null) {
//                conn.close();
//            } 
//        }
//        catch ( Exception e ) {
//           System.out.println("Check Balance Error: " + e.getMessage());
//           System.out.println( e.getClass().getName() + ": " + e.getMessage() );
//        }
//      
//        return CatSum;
//    }
    
}
