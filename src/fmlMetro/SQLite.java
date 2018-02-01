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
                     " day TEXT,\n" +
                     " mon TEXT,\n" +
                     " yr TEXT,\n" +
                     " wk TEXT,\n" +
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
    
    // add a record to the ledger table
    public int AddTransaction(int date8, String day, String mon, String yr,
            String wk, String type, String frq, String category, String name, 
            int amount, int budget)
    {
        String sql = "INSERT INTO ledger(date8, day, mon, yr, wk, type,\n" +
                     " frequency, category, name, amount, budget)\n" +
                     " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        int last_inserted_id = 0;
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, date8);
            pstmt.setString(2, day);
            pstmt.setString(3, mon);
            pstmt.setString(4, yr);
            pstmt.setString(5, wk);
            pstmt.setString(6, type);
            pstmt.setString(7, frq);
            pstmt.setString(8, category);
            pstmt.setString(9, name);
            pstmt.setInt(10, amount);
            pstmt.setInt(11, budget);
            pstmt.executeUpdate();
            
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
    
    //  This gets all the transactions that are income and expense to display
    //  puts them in an ArrayList object and creates a running total as balance
    //  Used in the Income and Expense pane
//    public ArrayList<Transaction> getAllObjects(){
//        
//       String sql = "select id, date, time, category, name, amount,\n" +
//                    " (select sum(t2.amount) from ledger t2 where\n" +
//                    " ((t2.date <= t1.date and t2.time <= t1.time) or\n" +
//                    " (t2.date < t1.date))\n" +
//                    " order by date ) as accumulated\n" +
//                    " from ledger t1 order by date, time;";
//       
//       //String url = "jdbc:sqlite:/Users/termaat/sqlite/db/Ledger.db";
//       //ArrayList<Object> objectList = new ArrayList<Object>();
//       ArrayList<Transaction> rows2 = new ArrayList<>();
//                    // create an ArrayList of the Transaction object and creates
//                    // the ledger/checkbook of the application
//       Transaction row2;
//                    // not sure what this actually does, but is needed based
//                    // on similar sample code I have studied
//        try {
//           Class.forName("org.sqlite.JDBC");
//           Connection conn = DriverManager.getConnection(url);
//           conn.setAutoCommit(false);
//           //System.out.println("Opened database successfully");
//           
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql);
//           
//           while ( rs.next() ) {
//              int id = rs.getInt("id");
//              Date date = rs.getDate("date");
//              String  category = rs.getString("category");
//              String  name = rs.getString("name");
//              int amount  = rs.getInt("amount");
//              int balance  = rs.getInt("accumulated");
//              
//              /**
//              System.out.println( "ID = " + id );
//              System.out.println( "DATE = " + date);
//              System.out.println( "CATEGORY = " + category );
//              System.out.println( "NAME = " + name );
//              System.out.println( "AMOUNT = " + amount);
//              System.out.println( "BALANCE = " + balance );
//              System.out.println();
//              */
//             
//              //objectList.add(object);
//              row2 = new Transaction(id, date, category, name, amount, balance);
//              rows2.add(row2);
//           }
//           
//            if(rs != null) {
//                rs.close();
//            }
//            if(stmt != null){
//                stmt.close();
//            }
//            if(conn != null) {
//                conn.close();
//            } 
//        }
//        catch ( Exception e ) {
//            //System.out.println("Get ArrayList Error: " + e.getMessage());           
//            //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//        }
//        
//        return rows2;
//    }
    
    
}
