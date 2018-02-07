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
    String url = "jdbc:sqlite:/Users/termaat/sqlite/db/fml.db";   //Production
    //String url = "jdbc:sqlite:/Users/termaat/sqlite/db/fmltest.db";     //Test
    
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
    
    
    public ArrayList<TransactionShort> getTransactionsByDate(int dateStart, int dateEnd){
       
//String sql = "SELECT SUM(amount) AS balance FROM ledger WHERE date <= ?";
    ArrayList<TransactionShort> rows2 = new ArrayList<>();
                    // create an ArrayList of the Transaction object and creates
                    // the ledger/checkbook of the application
    TransactionShort row2;
                    // not sure what this actually does, but is needed based
                    // on similar sample code I have studied
    
    String sql = "select id, date8, time, category, name, amount,\n" +
                    " (select sum(t2.amount) from ledger t2 where\n" +
                    " ((t2.date8 <= t1.date8 and t2.time <= t1.time) or\n" +
                    " (t2.date8 < t1.date8))\n" +
                    " order by date8 ) as accumulated\n" +
                    " from ledger t1\n" +
                    " where date8 <= ?\n" +
                    " order by date8, time;";

        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully");
           
           //Statement stmt = conn.createStatement();
           PreparedStatement pstmt  = conn.prepareStatement(sql);
           pstmt.setInt(1,dateEnd);
           //pstmt.setDate(2,date1);
           ResultSet rs = pstmt.executeQuery();
           
           while ( rs.next() ) {
              int id = rs.getInt("id");
              int date8 = rs.getInt("date8");
              String  category = rs.getString("category");
              String  name = rs.getString("name");
              int amount  = rs.getInt("amount");
              int balance  = rs.getInt("accumulated");
                            
              if((date8>=dateStart) && (date8 <= dateEnd)) {
              //if(date8.after(dateStart) && (date8.before(dateEnd))){
                row2 = new TransactionShort(id, date8, category, name, amount, balance);
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

    //  This gets all the transactions that are income and expense to display
    //  puts them in an ArrayList object and creates a running total as balance
    //  Used in the Income and Expense pane
    public ArrayList<TransactionShort> getAllObjects(){
        
       String sql = "select id, date8, time, category, name, amount,\n" +
                    " (select sum(t2.amount) from ledger t2 where\n" +
                    " ((t2.date8 <= t1.date8 and t2.time <= t1.time) or\n" +
                    " (t2.date8 < t1.date8))\n" +
                    " order by date8 ) as accumulated\n" +
                    " from ledger t1 order by date8, time;";
       
       ArrayList<TransactionShort> rows2 = new ArrayList<>();
                    // create an ArrayList of the Transaction object and creates
                    // the ledger/checkbook of the application
       TransactionShort row2;
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
              int date8 = rs.getInt("date8");
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
              row2 = new TransactionShort(id, date8, category, name, amount, balance);
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
    
    // get tranaction to populate DE.  need when we started messing with 
    // the data display in the table
    public TransactionLong GetTransaction(int id){
        int idx = 0;
        int date8 = 0;
        String day = "";
        String mon = "";
        String yr = "";
        String wk = "";
        String type = "";
        String frq = "";
        String category = "";
        String name = "";
        int amount = 0;
        int budget = 0;
        
        TransactionLong lTran = null;
        
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
              date8 = rs.getInt("date8");
              day = rs.getString("day");
              mon = rs.getString("mon");
              yr = rs.getString("yr");
              wk = rs.getString("wk");
              type = rs.getString("type");
              frq = rs.getString("frequency");
              category = rs.getString("category");
              name = rs.getString("name");
              amount = rs.getInt("amount");
              budget = rs.getInt("budget");
              
              lTran = new TransactionLong(idx, date8, day, mon, yr, wk, type, 
                      frq, category, name, amount, budget, 0);
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
           System.out.println("Get Transaction Error: " + e.getMessage());
           System.out.println( e.getClass().getName() + ": " + e.getMessage() );
        }
      
        return lTran;
    }
    
    // update transaction from income and expense lanel
    public void UpdateTran(int id, int date8, String category, String name, 
                            String type, int amount, int budget){
        String sql = "UPDATE ledger SET date8 = ? , "
                + "category = ? , "
                + "name = ? , "
                + "type = ? , "
                + "amount = ? , "
                + "budget = ? "
                + "WHERE id = ?";
 
        try (Connection conn = DriverManager.getConnection(url);
          PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // set the corresponding param
            pstmt.setInt(1, date8);
            pstmt.setString(2, category);
            pstmt.setString(3, name);
            pstmt.setString(4, type);
            pstmt.setInt(5, amount);
            pstmt.setInt(6, budget);
            pstmt.setInt(7, id);
            // update 
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }    
    }
    
    // delete transaction from income and expense panel
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
    
    public int getTypeSum(int dateStart, int dateEnd, String type){
        int amtSum = 0;
        
        String sql = "SELECT SUM(amount) as sumAmt FROM ledger WHERE\n" +
                 " type = ? and date8 >= ? and date8 <= ?";
        
        try {
           Class.forName("org.sqlite.JDBC");
           Connection conn = DriverManager.getConnection(url);
           conn.setAutoCommit(false);
           //System.out.println("Opened database successfully");
           
           //Statement stmt = conn.createStatement();
           PreparedStatement pstmt  = conn.prepareStatement(sql);
           pstmt.setString(1,type);
           pstmt.setInt(2, dateStart);
           pstmt.setInt(3, dateEnd);
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
    
}
