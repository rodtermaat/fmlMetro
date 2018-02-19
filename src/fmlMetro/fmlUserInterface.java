/*
 * This is fml finance.  My first java application.  So bare will me as I 
 * learn Java and slowly increase my coding skills from spaghetti code to
 * something that resembles a real Java progam
 */
package fmlMetro;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.Timer;
import java.lang.String;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author termaat
 */
public class fmlUserInterface extends javax.swing.JFrame {
    int xx;                         // move screen
    int xy;                         // move screen
    int IEmonTracker = 0;           // used to track month back and forward
    int IEweekTracker = 7;          // used to track week back and forth
    int weekTracker = 7;            // used to move week in the checkbook
    int inTheBeginning = 20000101;  // used to get all transactions
    int inTheYear2525 = 25250101;   // used to get all tranactions

    SQLite sqlite = new SQLite();   // database object
    
    ArrayList<TransactionShort> ledgerList = new ArrayList<TransactionShort>();
 
    DefaultTableModel model;
    DefaultTableModel expModel;
    DefaultTableModel ledgerModel;
    
    FreakyDate dtx = new FreakyDate();
    
    SimpleDateFormat mdy = new SimpleDateFormat("MM/dd/YYYY");
    
    // date used globally to test against current date
    static java.util.Date today = new java.util.Date();
    String sToday = mdy.format(today.getTime());
    String sDay = new SimpleDateFormat("dd").format(today);
    String sMon = new SimpleDateFormat("MM").format(today);
    String sYr = new SimpleDateFormat("yyyy").format(today);
    String sYrYY = new SimpleDateFormat("yy").format(today);
    String sWk = new SimpleDateFormat("w").format(today);
    
    String inputToday = sMon + "/" + sDay + "/" + sYrYY;
    String sToday8 = sYr + sMon + sDay;
    int iToday8 = Integer.parseInt(sToday8);
    
    /**
     * Creates new form fmlUserInterface
     */
    public fmlUserInterface() {
        initComponents();
        
        //set up the model for the checkbook register
        ledgerModel = (DefaultTableModel) tblCheckbook.getModel();
        
//        tblCheckbook.getModel().addTableModelListener(new TableModelListener() {
//            public void tableChanged(TableModelEvent e) {
//                int i = tblCheckbook.getSelectedRow();
//                if(i>-1){
//                String stest = String.valueOf(ledgerModel.getValueAt(i, 1));
//                System.out.println("selected row is " + i + " and " + stest);
//                i = -1;
//                }
//            } 
//        });
        
        // Fire the app up on the Analytics screen
        pnlCarder.removeAll();
        pnlCarder.repaint();
        pnlCarder.validate();
        
        pnlCarder.add(pnlAnalytics);
        pnlCarder.repaint();
        pnlCarder.validate();
        
        // Budgeted amount
        Budget budx = sqlite.GetBudget();
        lblaibSave.setText(String.valueOf(budx.GetBudgetSave()));
        lblaibCash.setText(String.valueOf(budx.GetBudgetCash()));
        lblaibGroc.setText(String.valueOf(budx.GetBudgetGroc()));
        lblaibDine.setText(String.valueOf(budx.GetBudgetDine()));
        lblaibTrav.setText(String.valueOf(budx.GetBudgetTransp()));
        lblaibUnplan.setText(String.valueOf(budx.GetBudgetUnplan()));
        
        int aibTot = budx.GetBudgetCash() + budx.GetBudgetDine() + 
                budx.GetBudgetGroc() + budx.GetBudgetSave() + 
                budx.GetBudgetTransp() + budx.GetBudgetUnplan();
        lblaibTot.setText(String.valueOf(aibTot));
        
        // Actual Budget for the week
        int intFOW = dtx.getIntFOW(7);
        int intEOW = dtx.getIntEOW(7);
        Budget budgWeek = sqlite.GetBudgetTotals(intFOW, intEOW);
        lblaiaSave.setText("$" + String.valueOf(budgWeek.GetBudgetSave()));
        lblaiaCash.setText("$" + String.valueOf(budgWeek.GetBudgetCash()));
        lblaiaTrav.setText("$" + String.valueOf(budgWeek.GetBudgetTransp()));
        lblaiaGroc.setText("$" + String.valueOf(budgWeek.GetBudgetGroc()));
        lblaiaDine.setText("$" + String.valueOf(budgWeek.GetBudgetDine()));
        lblaiaUnplan.setText("$" + String.valueOf(budgWeek.GetBudgetUnplan()));
        
        int budgWeekTot = budgWeek.GetBudgetCash() + budgWeek.GetBudgetDine() +
                budgWeek.GetBudgetGroc() + budgWeek.GetBudgetSave() + 
                budgWeek.GetBudgetTransp() + budgWeek.GetBudgetUnplan();
        lblaiaTot.setText("$" + String.valueOf(budgWeekTot));
        
        
        // see of the db is already set up.  0 means there is no table
        int isReady = sqlite.IsAcctSetup();
        if(isReady == 0){
            sqlite.CreateDataBase();
            sqlite.CreateFMLtbl();
            sqlite.CreateBudgetTable();
            //displayMessage("Enter 1st transaction to set up Acct. balance");
        } 
        
        //set up model for tblLedger
        model = (DefaultTableModel) tblLedger.getModel();
        
        //set up model for tblBudgetSummary
        expModel = (DefaultTableModel) tblExpSummary.getModel();
        
        //set up the model for the checkbook register
        //ledgerModel = (DefaultTableModel) tblCheckbook.getModel();
        
        // Center on the screen
        //Toolkit tool = Toolkit.getDefaultToolkit();
        //Dimension dim = new Dimension(tool.getScreenSize());
        //int height = (int) dim.getHeight();
        //int width = (int) dim.getWidth();
        //setSize(width,height);
        //setLocation(width / 2 - getWidth() / 2, height / 2 - getHeight() / 2);
        
        // set up initial screen
        String htmlai = "<html>use this area as" +
                        "<br>a quick place to" +
                        "<br>add budgeted" +
                        "<br>items." +
                        "<br>Keeping a thumb" +
                        "<br>on your weekly" +
                        "<br>spending.";
        
        lblAIqAdd.setText(htmlai);
        txtAIbudgDate.setText(inputToday);
        txtAIbudgAmt.setText("");
        cmbAIbudgItem.setSelectedIndex(-1);
        txtAIbudgName.setText("");
        new AutoCompleteJComboBoxer(cmbAIbudgItem);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblFML = new javax.swing.JLabel();
        btnAnalytics = new javax.swing.JButton();
        btnIncomeExp = new javax.swing.JButton();
        btnBudget = new javax.swing.JButton();
        bntRegister = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        pnlCarder = new javax.swing.JPanel();
        pnlAnalytics = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        JLabeln = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        lblaibSave = new javax.swing.JLabel();
        lblaiaSave = new javax.swing.JLabel();
        lblaibCash = new javax.swing.JLabel();
        lblaiaCash = new javax.swing.JLabel();
        lblaibGroc = new javax.swing.JLabel();
        lblaiaGroc = new javax.swing.JLabel();
        lblaibDine = new javax.swing.JLabel();
        lblaiaDine = new javax.swing.JLabel();
        lblaibTrav = new javax.swing.JLabel();
        lblaiaTrav = new javax.swing.JLabel();
        lblaibUnplan = new javax.swing.JLabel();
        lblaiaUnplan = new javax.swing.JLabel();
        lblaibTot = new javax.swing.JLabel();
        lblaiaTot = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblAIMessage = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbAIbudgItem = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtAIbudgName = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtAIbudgAmt = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        btnAIbudgAdd = new javax.swing.JButton();
        lblAIqAdd = new javax.swing.JLabel();
        txtAIbudgDate = new javax.swing.JFormattedTextField();
        pnlLedger = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        lblcbStartBal = new javax.swing.JLabel();
        lblcbMessage = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCheckbook = new javax.swing.JTable();
        btnLedgerBack = new javax.swing.JButton();
        rdoLedgerAll = new javax.swing.JRadioButton();
        rdoLedgerMonth = new javax.swing.JRadioButton();
        rdoLedgerWeek = new javax.swing.JRadioButton();
        rdoLedgerActive = new javax.swing.JRadioButton();
        btnLedgerForward = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        lblcbInfo = new javax.swing.JLabel();
        txtcbDate = new javax.swing.JFormattedTextField();
        jLabel34 = new javax.swing.JLabel();
        txtcbAmt = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        txtcbName = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        cmbcbCategory = new javax.swing.JComboBox<>();
        btncbAdd = new javax.swing.JButton();
        btncbUpd = new javax.swing.JButton();
        btncbDel = new javax.swing.JButton();
        lblcbID = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        cmbcbType = new javax.swing.JComboBox<>();
        txtcbBalance = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtcbBank = new javax.swing.JTextField();
        txtcbNotCleared = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        lblisCleared = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        pnlReporter = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        pnlBudget = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        lblBudgetMonth = new javax.swing.JLabel();
        lblBudgetAvail = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lblBudgetToSpend = new javax.swing.JLabel();
        lblBudgetWeekNum = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        txtBudgetGroc = new javax.swing.JTextField();
        txtBudgetUnplan = new javax.swing.JTextField();
        txtBudgetSave = new javax.swing.JTextField();
        txtBudgetCash = new javax.swing.JTextField();
        txtBudgetTranp = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        txtBudgetDine = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        lblSaveThisWk = new javax.swing.JLabel();
        lblCashThisWk = new javax.swing.JLabel();
        lblGrocThisWk = new javax.swing.JLabel();
        lblTravThisWk = new javax.swing.JLabel();
        lblDineThisWk = new javax.swing.JLabel();
        lblUnplanThisWk = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        lblSaveThisMon = new javax.swing.JLabel();
        lblCashThisMon = new javax.swing.JLabel();
        lblTravThisMon = new javax.swing.JLabel();
        lblGrocThisMon = new javax.swing.JLabel();
        lblDineThisMon = new javax.swing.JLabel();
        lblUnplanThisMon = new javax.swing.JLabel();
        btnBudgetAdd = new javax.swing.JButton();
        lblBudgetMessages = new javax.swing.JLabel();
        lblBudgetTot = new javax.swing.JLabel();
        lblBudgetMon = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        lblSavePerc = new javax.swing.JLabel();
        lblCashPerc = new javax.swing.JLabel();
        lblTravPerc = new javax.swing.JLabel();
        lblGrocPerc = new javax.swing.JLabel();
        lblDinePerc = new javax.swing.JLabel();
        lblUnplanPerc = new javax.swing.JLabel();
        lblBudgetAct = new javax.swing.JLabel();
        lblTotPerc = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblBudgReadMe = new javax.swing.JLabel();
        lblBudgetExpSum = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExpSummary = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        pnlIncomeExp = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lblIEmaxBills = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblIEmonth = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblIEstartingBal = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblIEendingBal = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lblIEmaxIn = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblIEmaxBill = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        lblIEtoSpend = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        lblIEpercent = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblIEunplanned = new javax.swing.JLabel();
        lblIEsumMessage = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLedger = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        lblIEreadMe = new javax.swing.JLabel();
        txtIEdate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtIEamount = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtIEdescription = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        cmbIEcategory = new javax.swing.JComboBox<>();
        rdoIEincome = new javax.swing.JRadioButton();
        rdoIEexpense = new javax.swing.JRadioButton();
        btnIEadd = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        btnIEupd = new javax.swing.JButton();
        btnIEdel = new javax.swing.JButton();
        chkIErepeat = new javax.swing.JCheckBox();
        lblID = new javax.swing.JLabel();
        rdoIEunplanned = new javax.swing.JRadioButton();
        jPanel14 = new javax.swing.JPanel();
        lblIEmessage = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        btnIEbackwards = new javax.swing.JButton();
        btnIEforward = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        setMaximumSize(new java.awt.Dimension(700, 615));
        setMinimumSize(new java.awt.Dimension(700, 615));
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setMaximumSize(new java.awt.Dimension(900, 615));
        jPanel1.setMinimumSize(new java.awt.Dimension(900, 615));
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 615));

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));
        jPanel2.setForeground(new java.awt.Color(102, 102, 102));
        jPanel2.setPreferredSize(new java.awt.Dimension(900, 112));
        jPanel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel2MouseDragged(evt);
            }
        });
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel2MouseReleased(evt);
            }
        });

        lblFML.setBackground(new java.awt.Color(102, 102, 102));
        lblFML.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        lblFML.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFML.setText(" .fml");
        lblFML.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblFML.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblFML.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnAnalytics.setBackground(new java.awt.Color(102, 102, 102));
        btnAnalytics.setForeground(new java.awt.Color(102, 102, 102));
        btnAnalytics.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bar_black.png"))); // NOI18N
        btnAnalytics.setBorderPainted(false);
        btnAnalytics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalyticsActionPerformed(evt);
            }
        });

        btnIncomeExp.setBackground(new java.awt.Color(102, 102, 102));
        btnIncomeExp.setForeground(new java.awt.Color(102, 102, 102));
        btnIncomeExp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check_black.png"))); // NOI18N
        btnIncomeExp.setToolTipText("");
        btnIncomeExp.setBorderPainted(false);
        btnIncomeExp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIncomeExpActionPerformed(evt);
            }
        });

        btnBudget.setBackground(new java.awt.Color(102, 102, 102));
        btnBudget.setForeground(new java.awt.Color(102, 102, 102));
        btnBudget.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pig_black.png"))); // NOI18N
        btnBudget.setBorderPainted(false);
        btnBudget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBudgetActionPerformed(evt);
            }
        });

        bntRegister.setBackground(new java.awt.Color(102, 102, 102));
        bntRegister.setForeground(new java.awt.Color(102, 102, 102));
        bntRegister.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/register_black.png"))); // NOI18N
        bntRegister.setBorderPainted(false);
        bntRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntRegisterActionPerformed(evt);
            }
        });

        btnExit.setBackground(new java.awt.Color(102, 102, 102));
        btnExit.setForeground(new java.awt.Color(102, 102, 102));
        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit_black.png"))); // NOI18N
        btnExit.setBorder(null);
        btnExit.setBorderPainted(false);
        btnExit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnExit.setMaximumSize(new java.awt.Dimension(94, 90));
        btnExit.setMinimumSize(new java.awt.Dimension(94, 90));
        btnExit.setPreferredSize(new java.awt.Dimension(94, 90));
        btnExit.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblFML, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAnalytics, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIncomeExp, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBudget, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bntRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblFML, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnIncomeExp, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(btnBudget, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(btnAnalytics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bntRegister, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlCarder.setBackground(new java.awt.Color(102, 102, 102));
        pnlCarder.setPreferredSize(new java.awt.Dimension(900, 500));
        pnlCarder.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlCarderMouseDragged(evt);
            }
        });
        pnlCarder.setLayout(new java.awt.CardLayout());

        pnlAnalytics.setBackground(new java.awt.Color(102, 102, 102));
        pnlAnalytics.setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel4.setBackground(new java.awt.Color(102, 102, 102));

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel9.setText(" . up and coming");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel20.setBackground(new java.awt.Color(255, 204, 204));

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("weekly budget");

        jLabel19.setText("item");
        jLabel19.setToolTipText("");

        jLabel33.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("budget");
        jLabel33.setToolTipText("");

        jLabel35.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("actual");

        jLabel60.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel60.setText("savings");

        jLabel61.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel61.setText("cash");

        jLabel62.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel62.setText("groceries");

        JLabeln.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        JLabeln.setText("eating out");

        jLabel64.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel64.setText("travel");
        jLabel64.setToolTipText("");

        jLabel65.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel65.setText("unplanned");

        jLabel66.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel66.setText("totals");

        lblaibSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaibSave.setText("$1000");

        lblaiaSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaiaSave.setText("$1000");

        lblaibCash.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaibCash.setText("$1000");

        lblaiaCash.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaiaCash.setText("$1000");

        lblaibGroc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaibGroc.setText("$1000");

        lblaiaGroc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaiaGroc.setText("$1000");

        lblaibDine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaibDine.setText("$1000");

        lblaiaDine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaiaDine.setText("$1000");

        lblaibTrav.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaibTrav.setText("$1000");

        lblaiaTrav.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaiaTrav.setText("$1000");

        lblaibUnplan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaibUnplan.setText("$1000");

        lblaiaUnplan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaiaUnplan.setText("$1000");

        lblaibTot.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblaibTot.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaibTot.setText("$1000");

        lblaiaTot.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblaiaTot.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblaiaTot.setText("$1000");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel65, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addComponent(jLabel64, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(JLabeln, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblaibSave, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                                    .addComponent(lblaibCash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblaibDine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblaibGroc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblaibTrav, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblaiaTrav, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                                    .addComponent(lblaiaDine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblaiaCash, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblaiaGroc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel20Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(lblaibUnplan, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel20Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblaibTot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblaiaTot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblaiaUnplan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblaiaSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))))
                .addGap(12, 12, 12))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel33)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(lblaibSave)
                    .addComponent(lblaiaSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(lblaibCash)
                    .addComponent(lblaiaCash))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(lblaibGroc)
                    .addComponent(lblaiaGroc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabeln)
                    .addComponent(lblaibDine)
                    .addComponent(lblaiaDine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64)
                    .addComponent(lblaibTrav)
                    .addComponent(lblaiaTrav))
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel66)
                            .addComponent(lblaibTot)
                            .addComponent(lblaiaTot))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblaibUnplan)
                            .addComponent(lblaiaUnplan))
                        .addGap(34, 34, 34))))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));

        jLabel21.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel21.setText(". random analytics");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addContainerGap(187, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(102, 102, 102));

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel10.setText(". pretty chart to tell you nothing");

        lblAIMessage.setForeground(new java.awt.Color(0, 255, 51));
        lblAIMessage.setText(" . message center");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
                    .addComponent(lblAIMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 282, Short.MAX_VALUE)
                .addComponent(lblAIMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(". quick add");

        cmbAIbudgItem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "savings", "cash", "transportation", "groceries", "dining", "unplanned" }));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel4.setText("budget item");

        jLabel26.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel26.setText("description");

        txtAIbudgName.setText("optional description");

        jLabel30.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel30.setText("amount");

        txtAIbudgAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAIbudgAmt.setText("185");
        txtAIbudgAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAIbudgAmtKeyTyped(evt);
            }
        });

        jLabel31.setText("date");

        btnAIbudgAdd.setText("add");
        btnAIbudgAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAIbudgAddActionPerformed(evt);
            }
        });

        lblAIqAdd.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAIqAdd.setText("lblAIqAdd");
        lblAIqAdd.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        txtAIbudgDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("MM/dd/yy"))));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(txtAIbudgDate, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cmbAIbudgItem, 0, 173, Short.MAX_VALUE)
                                .addComponent(txtAIbudgName))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(txtAIbudgAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAIbudgAdd)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblAIqAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel31)
                    .addComponent(txtAIbudgDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbAIbudgItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(txtAIbudgName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(txtAIbudgAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAIbudgAdd)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lblAIqAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout pnlAnalyticsLayout = new javax.swing.GroupLayout(pnlAnalytics);
        pnlAnalytics.setLayout(pnlAnalyticsLayout);
        pnlAnalyticsLayout.setHorizontalGroup(
            pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAnalyticsLayout.createSequentialGroup()
                .addGroup(pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAnalyticsLayout.setVerticalGroup(
            pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAnalyticsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAnalyticsLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAnalyticsLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
        );

        pnlCarder.add(pnlAnalytics, "card2");

        pnlLedger.setBackground(new java.awt.Color(102, 102, 102));
        pnlLedger.setPreferredSize(new java.awt.Dimension(900, 500));

        jPanel15.setBackground(new java.awt.Color(102, 102, 102));
        jPanel15.setPreferredSize(new java.awt.Dimension(900, 496));

        jPanel16.setBackground(new java.awt.Color(102, 102, 102));
        jPanel16.setPreferredSize(new java.awt.Dimension(900, 34));

        jLabel24.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("   ... see all of your finances in one place. ");

        jLabel46.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel46.setText("Starting Balance");

        lblcbStartBal.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblcbStartBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblcbStartBal.setText("$1275");

        lblcbMessage.setForeground(new java.awt.Color(0, 255, 51));
        lblcbMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblcbMessage.setText(". message center");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblcbStartBal, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblcbMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addComponent(jLabel46)
                .addComponent(lblcbStartBal)
                .addComponent(lblcbMessage))
        );

        jPanel17.setBackground(new java.awt.Color(102, 102, 102));

        tblCheckbook.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "cleared", "date", "description", "category", "amount", "balance"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCheckbook.getTableHeader().setReorderingAllowed(false);
        tblCheckbook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCheckbookMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblCheckbook);
        if (tblCheckbook.getColumnModel().getColumnCount() > 0) {
            tblCheckbook.getColumnModel().getColumn(0).setResizable(false);
            tblCheckbook.getColumnModel().getColumn(0).setPreferredWidth(15);
            tblCheckbook.getColumnModel().getColumn(1).setResizable(false);
            tblCheckbook.getColumnModel().getColumn(1).setPreferredWidth(25);
            tblCheckbook.getColumnModel().getColumn(2).setResizable(false);
            tblCheckbook.getColumnModel().getColumn(2).setPreferredWidth(25);
            tblCheckbook.getColumnModel().getColumn(3).setResizable(false);
            tblCheckbook.getColumnModel().getColumn(3).setPreferredWidth(80);
            tblCheckbook.getColumnModel().getColumn(4).setResizable(false);
            tblCheckbook.getColumnModel().getColumn(4).setPreferredWidth(60);
            tblCheckbook.getColumnModel().getColumn(5).setResizable(false);
            tblCheckbook.getColumnModel().getColumn(5).setPreferredWidth(40);
            tblCheckbook.getColumnModel().getColumn(6).setResizable(false);
            tblCheckbook.getColumnModel().getColumn(6).setPreferredWidth(40);
        }

        btnLedgerBack.setText("<");
        btnLedgerBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLedgerBackActionPerformed(evt);
            }
        });

        rdoLedgerAll.setText("all");
        rdoLedgerAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoLedgerAllActionPerformed(evt);
            }
        });

        rdoLedgerMonth.setText("month");
        rdoLedgerMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoLedgerMonthActionPerformed(evt);
            }
        });

        rdoLedgerWeek.setText("week");
        rdoLedgerWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoLedgerWeekActionPerformed(evt);
            }
        });

        rdoLedgerActive.setText("active");
        rdoLedgerActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoLedgerActiveActionPerformed(evt);
            }
        });

        btnLedgerForward.setText(">");
        btnLedgerForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLedgerForwardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnLedgerBack, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rdoLedgerAll)
                .addGap(18, 18, 18)
                .addComponent(rdoLedgerMonth)
                .addGap(18, 18, 18)
                .addComponent(rdoLedgerWeek)
                .addGap(12, 12, 12)
                .addComponent(rdoLedgerActive)
                .addGap(18, 18, 18)
                .addComponent(btnLedgerForward, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLedgerBack)
                    .addComponent(rdoLedgerAll)
                    .addComponent(rdoLedgerMonth)
                    .addComponent(rdoLedgerWeek)
                    .addComponent(rdoLedgerActive)
                    .addComponent(btnLedgerForward))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel18.setBackground(new java.awt.Color(255, 204, 204));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel21.setBackground(new java.awt.Color(255, 204, 204));
        jPanel21.setPreferredSize(new java.awt.Dimension(344, 400));

        lblcbInfo.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblcbInfo.setText("For Income and Expenes we will look at life ");
        lblcbInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblcbInfo.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        txtcbDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("MM/dd/yy"))));

        jLabel34.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel34.setText(" . date");

        txtcbAmt.setText("123456");
        txtcbAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcbAmtKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtcbAmtKeyPressed(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel43.setText(" . amount");

        jLabel44.setToolTipText("");

        jLabel47.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel47.setText(" . description");

        jLabel48.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel48.setText(" . category");

        cmbcbCategory.setEditable(true);
        cmbcbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "income", "expense" }));

        btncbAdd.setText("add");
        btncbAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncbAddActionPerformed(evt);
            }
        });

        btncbUpd.setText("upd");
        btncbUpd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncbUpdActionPerformed(evt);
            }
        });

        btncbDel.setText("del");
        btncbDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncbDelActionPerformed(evt);
            }
        });

        lblcbID.setText("aaa");

        jLabel50.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel50.setText(". type");

        cmbcbType.setEditable(true);
        cmbcbType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "bill", "income", "unplanned", "budget" }));

        txtcbBalance.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        txtcbBalance.setText("10000");

        jLabel49.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("Bank");

        jLabel51.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("Balance");

        jLabel53.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel53.setText("Your");

        jLabel54.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel54.setText("Balance");

        jLabel56.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel56.setText("Not");

        jLabel57.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel57.setText("Cleared");

        txtcbBank.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        txtcbBank.setText("10000");

        txtcbNotCleared.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        txtcbNotCleared.setText("8888");

        jLabel59.setText("jLabel59");

        lblisCleared.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        lblisCleared.setText("Not Cleared");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblcbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(btncbDel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btncbUpd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btncbAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbcbType, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addComponent(lblisCleared, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(102, 102, 102)
                        .addComponent(jLabel59))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtcbDate, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(lblcbID, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtcbAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbcbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtcbName, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtcbBank, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtcbBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtcbNotCleared, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(155, 155, 155)))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(lblcbInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 22, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel51))
                    .addComponent(txtcbBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtcbBank, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtcbNotCleared, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel54))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel56)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel57)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(txtcbDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(lblcbID))
                .addGap(0, 4, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel43)
                    .addComponent(txtcbAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtcbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbcbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(cmbcbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btncbDel)
                    .addComponent(btncbUpd)
                    .addComponent(btncbAdd)
                    .addComponent(jLabel59)
                    .addComponent(lblisCleared))
                .addGap(8, 8, 8))
        );

        jPanel22.setBackground(new java.awt.Color(102, 102, 102));

        jLabel58.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel58.setText(" . print summary report");

        jButton1.setText("the best for last");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 412, Short.MAX_VALUE)
                                .addGap(42, 42, 42))))
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 908, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout pnlLedgerLayout = new javax.swing.GroupLayout(pnlLedger);
        pnlLedger.setLayout(pnlLedgerLayout);
        pnlLedgerLayout.setHorizontalGroup(
            pnlLedgerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLedgerLayout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 909, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlLedgerLayout.setVerticalGroup(
            pnlLedgerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLedgerLayout.createSequentialGroup()
                .addComponent(jPanel15, 494, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlCarder.add(pnlLedger, "card2");

        pnlReporter.setPreferredSize(new java.awt.Dimension(900, 496));
        pnlReporter.setRequestFocusEnabled(false);

        jLabel5.setText("Reserved for future html reporting");

        javax.swing.GroupLayout pnlReporterLayout = new javax.swing.GroupLayout(pnlReporter);
        pnlReporter.setLayout(pnlReporterLayout);
        pnlReporterLayout.setHorizontalGroup(
            pnlReporterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReporterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(974, Short.MAX_VALUE))
        );
        pnlReporterLayout.setVerticalGroup(
            pnlReporterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReporterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(474, Short.MAX_VALUE))
        );

        pnlCarder.add(pnlReporter, "card2");

        pnlBudget.setBackground(new java.awt.Color(102, 102, 102));
        pnlBudget.setPreferredSize(new java.awt.Dimension(900, 496));

        jPanel7.setBackground(new java.awt.Color(102, 102, 102));

        jLabel7.setBackground(new java.awt.Color(102, 102, 102));
        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("   ... flexible budgeting your spending is a key to improving your life");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(255, 204, 204));
        jPanel8.setPreferredSize(new java.awt.Dimension(480, 380));

        jLabel28.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel28.setText(" . the money you have available for ");

        lblBudgetMonth.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        lblBudgetMonth.setText("September");

        lblBudgetAvail.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        lblBudgetAvail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBudgetAvail.setText("$1450");

        jLabel32.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel32.setText(" . that means every week you have approximately");

        lblBudgetToSpend.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        lblBudgetToSpend.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBudgetToSpend.setText("$362");

        lblBudgetWeekNum.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        lblBudgetWeekNum.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBudgetWeekNum.setText(" ... don't forget our Calendar is a #%&*@ and sometimes has");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel8.setText(". savings");

        jLabel36.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel36.setText(". groceries");

        jLabel37.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel37.setText(". cash");

        jLabel38.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel38.setText(". travel");

        jLabel39.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel39.setText(". eating out");

        txtBudgetGroc.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        txtBudgetGroc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBudgetGroc.setText("175");
        txtBudgetGroc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBudgetGrocKeyTyped(evt);
            }
        });

        txtBudgetUnplan.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        txtBudgetUnplan.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBudgetUnplan.setText("125");
        txtBudgetUnplan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBudgetUnplanKeyTyped(evt);
            }
        });

        txtBudgetSave.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        txtBudgetSave.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBudgetSave.setText("50");
        txtBudgetSave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBudgetSaveKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBudgetSaveKeyPressed(evt);
            }
        });

        txtBudgetCash.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        txtBudgetCash.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBudgetCash.setText("40");
        txtBudgetCash.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBudgetCashKeyTyped(evt);
            }
        });

        txtBudgetTranp.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        txtBudgetTranp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBudgetTranp.setText("35");
        txtBudgetTranp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBudgetTranpKeyTyped(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("Weekly Budget");

        jLabel42.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel42.setText(". unplanned");

        txtBudgetDine.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        txtBudgetDine.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBudgetDine.setText("30");
        txtBudgetDine.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBudgetDineKeyTyped(evt);
            }
        });

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel45.setText("month");

        lblSaveThisWk.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblSaveThisWk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSaveThisWk.setText("50");

        lblCashThisWk.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblCashThisWk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCashThisWk.setText("60");

        lblGrocThisWk.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblGrocThisWk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrocThisWk.setText("180");

        lblTravThisWk.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblTravThisWk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTravThisWk.setText("42");

        lblDineThisWk.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblDineThisWk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDineThisWk.setText("22");

        lblUnplanThisWk.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblUnplanThisWk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUnplanThisWk.setText("98");

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("month");

        lblSaveThisMon.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblSaveThisMon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSaveThisMon.setText("$1600");

        lblCashThisMon.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblCashThisMon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCashThisMon.setText("$220");

        lblTravThisMon.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblTravThisMon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTravThisMon.setText("$87");

        lblGrocThisMon.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblGrocThisMon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrocThisMon.setText("$748");

        lblDineThisMon.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblDineThisMon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDineThisMon.setText("$129");

        lblUnplanThisMon.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblUnplanThisMon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUnplanThisMon.setText("$388");

        btnBudgetAdd.setText(". update");
        btnBudgetAdd.setActionCommand("update");
        btnBudgetAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBudgetAddActionPerformed(evt);
            }
        });

        lblBudgetMessages.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBudgetMessages.setText(" . fml budget messages   ");

        lblBudgetTot.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblBudgetTot.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBudgetTot.setText("$423");

        lblBudgetMon.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblBudgetMon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBudgetMon.setText("$872");

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel67.setText("budgeted");

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel68.setText("actual");

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel69.setText("%");

        lblSavePerc.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblSavePerc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSavePerc.setText("20%");

        lblCashPerc.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblCashPerc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCashPerc.setText("25%");

        lblTravPerc.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblTravPerc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTravPerc.setText("30%");

        lblGrocPerc.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblGrocPerc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrocPerc.setText("12%");

        lblDinePerc.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblDinePerc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDinePerc.setText("15%");

        lblUnplanPerc.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblUnplanPerc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUnplanPerc.setText("33%");

        lblBudgetAct.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblBudgetAct.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBudgetAct.setText("$325");

        lblTotPerc.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblTotPerc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotPerc.setText("112%");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(txtBudgetSave, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblSaveThisWk, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(txtBudgetTranp, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblTravThisWk, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(txtBudgetGroc, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblGrocThisWk, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(txtBudgetDine, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblDineThisWk, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(txtBudgetCash, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblCashThisWk, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGap(66, 66, 66)
                                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblBudgetTot)
                                            .addComponent(txtBudgetUnplan, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblUnplanThisWk, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addComponent(lblBudgetAct, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblCashThisMon, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTravThisMon, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblGrocThisMon, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDineThisMon, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblUnplanThisMon, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblTravPerc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblGrocPerc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblCashPerc, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                                    .addComponent(lblDinePerc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblUnplanPerc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(lblSaveThisMon, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblSavePerc, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(lblBudgetMon, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblTotPerc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(38, 38, 38))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblBudgetWeekNum, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel8Layout.createSequentialGroup()
                                    .addComponent(btnBudgetAdd)
                                    .addGap(104, 104, 104)
                                    .addComponent(lblBudgetMessages, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(jLabel32)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblBudgetToSpend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblBudgetMonth)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblBudgetAvail, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(lblBudgetMonth)
                    .addComponent(lblBudgetAvail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBudgetToSpend)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBudgetWeekNum)
                .addGap(41, 41, 41)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(jLabel68)
                    .addComponent(jLabel41))
                .addGap(1, 1, 1)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(jLabel52)
                    .addComponent(jLabel69))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtBudgetSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaveThisWk)
                    .addComponent(lblSaveThisMon)
                    .addComponent(lblSavePerc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCashThisMon)
                    .addComponent(lblCashThisWk)
                    .addComponent(txtBudgetCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(lblCashPerc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBudgetTranp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(lblTravThisWk)
                    .addComponent(lblTravThisMon)
                    .addComponent(lblTravPerc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBudgetGroc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(lblGrocThisWk)
                    .addComponent(lblGrocThisMon)
                    .addComponent(lblGrocPerc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBudgetDine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(lblDineThisWk)
                    .addComponent(lblDineThisMon)
                    .addComponent(lblDinePerc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBudgetUnplan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(lblUnplanThisWk)
                    .addComponent(lblUnplanThisMon)
                    .addComponent(lblUnplanPerc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBudgetTot)
                    .addComponent(lblBudgetMon)
                    .addComponent(lblBudgetAct)
                    .addComponent(lblTotPerc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBudgetAdd)
                    .addComponent(lblBudgetMessages))
                .addContainerGap())
        );

        jPanel9.setBackground(new java.awt.Color(102, 102, 102));
        jPanel9.setPreferredSize(new java.awt.Dimension(390, 380));

        lblBudgReadMe.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblBudgReadMe.setText("For Income and Expenes we will look at life ");
        lblBudgReadMe.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblBudgReadMe.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        lblBudgetExpSum.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblBudgetExpSum.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBudgetExpSum.setText("Take some time to examine your expenses");
        lblBudgetExpSum.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel3.setText(" . major Expenses this month");

        tblExpSummary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "expense", "mon Amt", "ann Amt", "cut 10%", "you'd save"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblExpSummary.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblExpSummary);
        if (tblExpSummary.getColumnModel().getColumnCount() > 0) {
            tblExpSummary.getColumnModel().getColumn(0).setResizable(false);
            tblExpSummary.getColumnModel().getColumn(0).setPreferredWidth(70);
            tblExpSummary.getColumnModel().getColumn(1).setResizable(false);
            tblExpSummary.getColumnModel().getColumn(1).setPreferredWidth(35);
            tblExpSummary.getColumnModel().getColumn(2).setResizable(false);
            tblExpSummary.getColumnModel().getColumn(2).setPreferredWidth(35);
            tblExpSummary.getColumnModel().getColumn(3).setResizable(false);
            tblExpSummary.getColumnModel().getColumn(3).setPreferredWidth(35);
            tblExpSummary.getColumnModel().getColumn(4).setResizable(false);
            tblExpSummary.getColumnModel().getColumn(4).setPreferredWidth(35);
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBudgReadMe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblBudgetExpSum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBudgReadMe, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBudgetExpSum, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlBudgetLayout = new javax.swing.GroupLayout(pnlBudget);
        pnlBudget.setLayout(pnlBudgetLayout);
        pnlBudgetLayout.setHorizontalGroup(
            pnlBudgetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(pnlBudgetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlBudgetLayout.setVerticalGroup(
            pnlBudgetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBudgetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBudgetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCarder.add(pnlBudget, "card2");

        pnlIncomeExp.setBackground(new java.awt.Color(102, 102, 102));
        pnlIncomeExp.setPreferredSize(new java.awt.Dimension(900, 496));

        jPanel11.setBackground(new java.awt.Color(102, 102, 102));

        jLabel17.setBackground(new java.awt.Color(102, 102, 102));
        jLabel17.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("     ... a place to add income and expense items that make up your life");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBackground(new java.awt.Color(102, 102, 102));
        jPanel12.setPreferredSize(new java.awt.Dimension(344, 400));

        lblIEmaxBills.setBackground(new java.awt.Color(255, 204, 204));

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel6.setText(" . month");

        lblIEmonth.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        lblIEmonth.setText("February");

        jLabel20.setText(" . starting balance");
        jLabel20.setToolTipText("");

        lblIEstartingBal.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblIEstartingBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIEstartingBal.setText("$1250");

        jLabel23.setText(" . ending balance");

        lblIEendingBal.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblIEendingBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIEendingBal.setText("$950");

        jLabel25.setText(" . income this month");

        lblIEmaxIn.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblIEmaxIn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIEmaxIn.setText("$3500");
        lblIEmaxIn.setToolTipText("");

        jLabel27.setText(" . bills this month");

        lblIEmaxBill.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblIEmaxBill.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIEmaxBill.setText("$2650");

        jLabel29.setText(" . % of income used for bills");

        lblIEtoSpend.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblIEtoSpend.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIEtoSpend.setText("$850");

        jLabel40.setText(" . money left for budget");

        lblIEpercent.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblIEpercent.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIEpercent.setText("76%");

        jLabel16.setText(" . unplanned");

        lblIEunplanned.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblIEunplanned.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIEunplanned.setText("$700");

        lblIEsumMessage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblIEsumMessage.setText("jLabel21");
        lblIEsumMessage.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout lblIEmaxBillsLayout = new javax.swing.GroupLayout(lblIEmaxBills);
        lblIEmaxBills.setLayout(lblIEmaxBillsLayout);
        lblIEmaxBillsLayout.setHorizontalGroup(
            lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                .addGroup(lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblIEmaxBillsLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblIEmaxBillsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblIEendingBal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblIEmonth, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblIEstartingBal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(lblIEunplanned, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIEmaxBill, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblIEmaxIn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblIEsumMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
                    .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIEtoSpend, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblIEpercent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        lblIEmaxBillsLayout.setVerticalGroup(
            lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblIEmaxBillsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblIEmonth))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(lblIEstartingBal))
                .addGap(12, 12, 12)
                .addGroup(lblIEmaxBillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(lblIEendingBal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIEmaxIn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIEmaxBill, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIEunplanned)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIEpercent)
                .addGap(4, 4, 4)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIEtoSpend)
                .addGap(18, 18, 18)
                .addComponent(lblIEsumMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        tblLedger.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id", "date", "name", "catgory", "amt", "bal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLedger.getTableHeader().setReorderingAllowed(false);
        tblLedger.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLedgerMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblLedger);
        if (tblLedger.getColumnModel().getColumnCount() > 0) {
            tblLedger.getColumnModel().getColumn(0).setResizable(false);
            tblLedger.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblLedger.getColumnModel().getColumn(1).setResizable(false);
            tblLedger.getColumnModel().getColumn(1).setPreferredWidth(25);
            tblLedger.getColumnModel().getColumn(2).setResizable(false);
            tblLedger.getColumnModel().getColumn(2).setPreferredWidth(70);
            tblLedger.getColumnModel().getColumn(3).setResizable(false);
            tblLedger.getColumnModel().getColumn(3).setPreferredWidth(55);
            tblLedger.getColumnModel().getColumn(4).setResizable(false);
            tblLedger.getColumnModel().getColumn(4).setPreferredWidth(35);
            tblLedger.getColumnModel().getColumn(5).setResizable(false);
            tblLedger.getColumnModel().getColumn(5).setPreferredWidth(35);
        }

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(lblIEmaxBills, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lblIEmaxBills, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel13.setBackground(new java.awt.Color(102, 102, 102));
        jPanel13.setPreferredSize(new java.awt.Dimension(344, 400));

        lblIEreadMe.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        lblIEreadMe.setText("For Income and Expenes we will look at life ");
        lblIEreadMe.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblIEreadMe.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        txtIEdate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("MM/dd/yy"))));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel2.setText(" . date");

        txtIEamount.setText("123456");
        txtIEamount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtIEamountKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtIEamountKeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel12.setText(" . amount");

        jLabel15.setText(". first occurance");

        jLabel18.setText(" . the usual amount");

        jLabel13.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel13.setText(" . description");

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel14.setText(" . category");

        cmbIEcategory.setEditable(true);
        cmbIEcategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "income", "expense" }));

        rdoIEincome.setText("income");
        rdoIEincome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoIEincomeActionPerformed(evt);
            }
        });

        rdoIEexpense.setText("bill");
        rdoIEexpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoIEexpenseActionPerformed(evt);
            }
        });

        btnIEadd.setText("add");
        btnIEadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIEaddActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabel22.setText(" . type");

        btnIEupd.setText("upd");
        btnIEupd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIEupdActionPerformed(evt);
            }
        });

        btnIEdel.setText("del");
        btnIEdel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIEdelActionPerformed(evt);
            }
        });

        chkIErepeat.setText(". repeat");
        chkIErepeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIErepeatActionPerformed(evt);
            }
        });

        lblID.setText("aaa");

        rdoIEunplanned.setText("unplanned");
        rdoIEunplanned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoIEunplannedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIEreadMe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIEdate, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbIEcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIEdescription, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rdoIEexpense, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdoIEincome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdoIEunplanned))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIEamount, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnIEdel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnIEupd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnIEadd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIErepeat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblID, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(lblIEreadMe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIEdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtIEamount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18))
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtIEdescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(cmbIEcategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdoIEexpense)
                        .addComponent(rdoIEincome)
                        .addComponent(rdoIEunplanned))
                    .addComponent(jLabel22))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIEdel)
                    .addComponent(btnIEupd)
                    .addComponent(btnIEadd)
                    .addComponent(chkIErepeat)
                    .addComponent(lblID))
                .addGap(34, 34, 34))
        );

        jPanel14.setBackground(new java.awt.Color(102, 102, 102));

        lblIEmessage.setBackground(new java.awt.Color(102, 102, 102));
        lblIEmessage.setForeground(new java.awt.Color(0, 255, 51));
        lblIEmessage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblIEmessage.setText(" . message center");
        lblIEmessage.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIEmessage, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(lblIEmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));

        btnIEbackwards.setText("<");
        btnIEbackwards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIEbackwardsActionPerformed(evt);
            }
        });

        btnIEforward.setText(">");
        btnIEforward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIEforwardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(223, Short.MAX_VALUE)
                .addComponent(btnIEbackwards, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIEforward, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIEbackwards)
                    .addComponent(btnIEforward))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlIncomeExpLayout = new javax.swing.GroupLayout(pnlIncomeExp);
        pnlIncomeExp.setLayout(pnlIncomeExpLayout);
        pnlIncomeExpLayout.setHorizontalGroup(
            pnlIncomeExpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlIncomeExpLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIncomeExpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlIncomeExpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlIncomeExpLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIncomeExpLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSeparator3)
        );
        pnlIncomeExpLayout.setVerticalGroup(
            pnlIncomeExpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIncomeExpLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIncomeExpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel12, 401, 401, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIncomeExpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCarder.add(pnlIncomeExp, "card2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCarder, javax.swing.GroupLayout.PREFERRED_SIZE, 1195, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 899, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlCarder, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // MAIN UI
    //code to move program around
    private void jPanel2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseDragged
        // TODO add your handling code here:
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x-xx, y-xy);
        
    }//GEN-LAST:event_jPanel2MouseDragged

    // MAIN UI
    // code to move program around
    private void jPanel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MousePressed
        // TODO add your handling code here:
        setOpacity((float)0.7);
        xx = evt.getX();
        xy = evt.getY();
    }//GEN-LAST:event_jPanel2MousePressed

    // MAIN UI
    // code to move program around
    private void jPanel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseReleased
        // TODO add your handling code here:
        setOpacity((float)1.0);
    }//GEN-LAST:event_jPanel2MouseReleased

    private void pnlCarderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlCarderMouseDragged
        // TODO add your handling code here:
        
    }//GEN-LAST:event_pnlCarderMouseDragged

    // MAIN UI
    // First screen to load showing summary analytics of your finances
    private void btnAnalyticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalyticsActionPerformed
        // Remove existing panel(s)
        pnlCarder.removeAll();;
        pnlCarder.repaint();;
        pnlCarder.validate();;
        
        // add new pane based on selection
        pnlCarder.add(pnlAnalytics);
        pnlCarder.repaint();
        pnlCarder.validate();
        
        txtAIbudgDate.setText(inputToday);
        
        // Budgeted Amounts
        Budget budx = sqlite.GetBudget();
        lblaibSave.setText(String.valueOf(budx.GetBudgetSave()));
        lblaibCash.setText(String.valueOf(budx.GetBudgetCash()));
        lblaibGroc.setText(String.valueOf(budx.GetBudgetGroc()));
        lblaibDine.setText(String.valueOf(budx.GetBudgetDine()));
        lblaibTrav.setText(String.valueOf(budx.GetBudgetTransp()));
        lblaibUnplan.setText(String.valueOf(budx.GetBudgetUnplan()));
        
        int aibTot = budx.GetBudgetCash() + budx.GetBudgetDine() + 
                budx.GetBudgetGroc() + budx.GetBudgetSave() + 
                budx.GetBudgetTransp() + budx.GetBudgetUnplan();
        lblaibTot.setText(String.valueOf(aibTot));
        
        // Actual Budget for the week
        int intFOW = dtx.getIntFOW(7);
        int intEOW = dtx.getIntEOW(7);
        Budget budgWeek = sqlite.GetBudgetTotals(intFOW, intEOW);
        lblaiaSave.setText("$" + String.valueOf(budgWeek.GetBudgetSave()));
        lblaiaCash.setText("$" + String.valueOf(budgWeek.GetBudgetCash()));
        lblaiaTrav.setText("$" + String.valueOf(budgWeek.GetBudgetTransp()));
        lblaiaGroc.setText("$" + String.valueOf(budgWeek.GetBudgetGroc()));
        lblaiaDine.setText("$" + String.valueOf(budgWeek.GetBudgetDine()));
        lblaiaUnplan.setText("$" + String.valueOf(budgWeek.GetBudgetUnplan()));
        
        int budgWeekTot = budgWeek.GetBudgetCash() + budgWeek.GetBudgetDine() +
                budgWeek.GetBudgetGroc() + budgWeek.GetBudgetSave() + 
                budgWeek.GetBudgetTransp() + budgWeek.GetBudgetUnplan();
        lblaiaTot.setText("$" + String.valueOf(budgWeekTot));
        
        
    }//GEN-LAST:event_btnAnalyticsActionPerformed

    // MAIN UI
    // INC and EXP where we add reoccurring bills and income as well as 
    //                                  one time events not planned for
    private void btnIncomeExpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIncomeExpActionPerformed
        // Remove existing panel(s)
        pnlCarder.removeAll();;
        pnlCarder.repaint();;
        pnlCarder.validate();;
        
        // add new pane based on selection
        pnlCarder.add(pnlIncomeExp);
        pnlCarder.repaint();
        pnlCarder.validate();
        ClearIEdataEntry();
        //jLabel16.setVisible(false);
        String html = "<html>Income and Expenses are tracked monthly." +
                       "<br>This is the first step in coming to terms" +
                       "<br>with your finances. Start by adding all" +
                       "<br>of the bills you have as well as income." +
                       "<br>What's left is what we will work with on" +
                       "<br>the budget section. Lowering your bills is" +
                       "<br>like giving yourself a RAISE - that's right!";
        
        lblIEreadMe.setText(html);
        txtIEdate.setText(inputToday);      // put todays date in IE date
        
        UpdateIECategories();       //fill with values from database
        //FUCK - why did this break all of a sudden???
        //new AutoCompleteJComboBoxer(cmbIEcategory);
        
        //IEmonTracker = 0;
        int ieFOM = dtx.getIntFOM(IEmonTracker);
        int ieEOM = dtx.getIntEOM(IEmonTracker);
        //int ieFOM = dtx.getIntFOM(0);
        //int ieEOM = dtx.getIntEOM(0);
        //System.out.println("month button beg " + ieFOM);
        //System.out.println("month button end " + ieEOM);
        
        ListIncExpByDate(ieFOM, ieEOM);
        RefreshAnalytics(ieFOM, ieEOM);
    }//GEN-LAST:event_btnIncomeExpActionPerformed

    // MAIN UI
    // BUDGET screen where we set up our weekly budget do some analysis
    private void btnBudgetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBudgetActionPerformed
        // Remove existing panel(s)
        pnlCarder.removeAll();;
        pnlCarder.repaint();;
        pnlCarder.validate();;
        
        // add new pane based on selection
        pnlCarder.add(pnlBudget);
        pnlCarder.repaint();
        pnlCarder.validate();
        //jLabel44.setVisible(false);
        
        String html = "<html>Of course bills and income are important, but its the $ you" + 
                      "<br>budget weekly that determines your relationship with money." +
                      "<br>Start by SAVING for that raining day - even just a little. Our" +
                      "<br>lives are set up to ignore the fact that everytime we pay off" +
                      "<br>a debt, lower a bill, or save a few bucks we are empowering" +
                      "<br>ourselves with more income, control of our lives, freedom," +
                      "<br>and self-esteem. Debt is Slavery - period";
        lblBudgReadMe.setText(html);
        
        String html2 = "<html>Lets also take some time to examine your expenses. If you" +
                       "<br> cut your expenes by 10% its like getting a serious raise in" +
                       "<br> pay.  When's the last time that happened without wiping a lot" +
                       "<br> of brown off your nose...";
        lblBudgetExpSum.setText(html2);
        
        int ieFOM = dtx.getIntFOM(IEmonTracker);
        int ieEOM = dtx.getIntEOM(IEmonTracker);
        //int ieFOM = dtx.getIntFOM(0);
        //int ieEOM = dtx.getIntEOM(0);
        RefreshBudget(ieFOM, ieEOM);
        
        
    }//GEN-LAST:event_btnBudgetActionPerformed

    // MAIN UI
    // LEDGER. this is the checkbook screen were we can balance to the bank
    private void bntRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntRegisterActionPerformed
        weekTracker = 7;

        // Remove existing panel(s)
        pnlCarder.removeAll();
        pnlCarder.repaint();
        pnlCarder.validate();
        
        // add new pane based on selection
        pnlCarder.add(pnlLedger);
        pnlCarder.repaint();
        pnlCarder.validate();
        
        String html = "<html>  ... for some balancing their account is a ritual and for" +
                       "<br>  others seen as a waste of time. Regardless, here you can" +
                       "<br>  see all of the gritty details. Use it or lose it." +
                       "<br>" +
                       "<br>  ... to balance your account enter the ending balance" +
                       "<br>  from the bank, and clear tranactions until you match.";
        
        lblcbInfo.setText(html);
        
        rdoLedgerMonth.setSelected(true);
        rdoLedgerAll.setSelected(false);
        rdoLedgerWeek.setSelected(false);
        rdoLedgerActive.setSelected(false);
        txtcbDate.setText("");
        txtcbAmt.setText("");
        txtcbName.setText("");
        //cmbcbCategory.setSelectedIndex(-1);
        cmbcbType.setSelectedIndex(-1);
        lblcbID.setText("");
        lblisCleared.setText("");
        txtcbBank.setText("    ?  ");
        txtcbBalance.setText("  ?  ");
        txtcbNotCleared.setText(" ? ");
        txtcbBalance.setEnabled(false);
        txtcbNotCleared.setEnabled(false);
        
        UpdateCBCategories();       //fill with values from database
        //new AutoCompleteJComboBoxer(cmbcbCategory);
        new AutoCompleteJComboBoxer(cmbcbType);
        
        //get and load up the transaction into the ledger
        int cbFOM = dtx.getIntFOM(IEmonTracker);
        int cbEOM = dtx.getIntEOM(IEmonTracker);
        GetLedgerTransByDate(cbFOM, cbEOM);
        
        //update starting balance
        CheckBookBalance();
        
    }//GEN-LAST:event_bntRegisterActionPerformed

    // MAIN UI
    // Just like it says - EXIT
    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // Close the Application
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed
    
    // INCOME EXPENSE
    // loads category dropdown
    private void UpdateIECategories(){
        if (cmbIEcategory.getMaximumRowCount()>2){
            
            cmbIEcategory.removeAllItems();
            ArrayList<String> catList = sqlite.getCategoryList();
            for(int i = 0; i < catList.size(); i++)
            {
                cmbIEcategory.addItem(catList.get(i));
            }
            cmbIEcategory.setSelectedIndex(-1);
        }
    }
    
    // Checkbook
    // loads category dropdown
    private void UpdateCBCategories(){
        if (cmbcbCategory.getMaximumRowCount()>2){
            
            cmbcbCategory.removeAllItems();
            ArrayList<String> catList = sqlite.getCategoryList();
            for(int i = 0; i < catList.size(); i++)
            {
                cmbcbCategory.addItem(catList.get(i));
            }
            cmbcbCategory.setSelectedIndex(-1);
        }
    }
   
    // GLOBAL
    // Add or update a transaction
    private String AddandUpdateTransaction(String action, int id, String userDate8,  
                                   String type, String cat, String name, 
                                   int amt, boolean cleared){
        
        Date jDate = null;
        String day; String mon; String yr; String wk;
        int newAmt = 0;
        
        // date is good to use
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        try
        {
            jDate = sdf.parse(userDate8);
        }
        catch(ParseException e) {
            return "FAIL";
        }
            
        day = new SimpleDateFormat("dd").format(jDate);
        mon = new SimpleDateFormat("MM").format(jDate);
        yr = new SimpleDateFormat("yyyy").format(jDate);
        wk = new SimpleDateFormat("w").format(jDate);
        String sDate8 = yr + mon + day;
        int intDate8 = Integer.parseInt(sDate8);

        // is today sunday?  if it is we need to subtract a week since our
        // program wants to start on Monday as the first day of the week
        int intwk = Integer.valueOf(wk);
        if(dtx.IsSunday(jDate)){
            if(intwk==1){
                intwk=52;
                wk = String.valueOf(intwk);
            }
            else{
                intwk--;
                wk = String.valueOf(intwk);
            }
        }
        
        if(type.equals("bill")){
            newAmt = amt*-1;
        }
        if(type.equals("budget")){
            newAmt = amt*-1;
        }
        if(type.equals("unplanned")){
            newAmt = amt*-1;
        }
        if(type.equals("income")){
            newAmt = amt;
        }
        
        if(action.equals("ADD")){
            int xID = sqlite.AddTransaction(intDate8, day, mon, yr, wk, 
                                            type, cat, name, newAmt, false);
            if(xID>0){}
            else { return "FAIL";}
        }
        else if(action.equals("UPD")){
            int xID = sqlite.UpdateTransaction(id, intDate8, day, mon, yr, wk, 
                    type, cat, name, newAmt, cleared);
        }
        
    return "PASS";    
    }
    
    
    
    // INCOME EXPENSE
    // Add or Update a new bill, income, unplanned expense
//    private void AddUpdateTransaction(String action, int id){
//        Date jDate = null;
//        String sDate8, day, mon, yr, wk, cat, name;
//        String type = "";
//        String ieDate = txtIEdate.getText();
//        int iDate8 = 0; int amt = 0; int budg = 0;
//        
//        // date is good to use
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
//        try
//        {
//            jDate = sdf.parse(ieDate);
//        }
//        catch(ParseException e) {
//            DisplayIEmessage("Enter date like 04/15/18");
//            return;
//        }
//            
//        day = new SimpleDateFormat("dd").format(jDate);
//        mon = new SimpleDateFormat("MM").format(jDate);
//        yr = new SimpleDateFormat("yyyy").format(jDate);
//        wk = new SimpleDateFormat("w").format(jDate);
//        sDate8 = yr + mon + day;
//        iDate8 = Integer.parseInt(sDate8);
//
//        // is today sunday?  if it is we need to subtract a week since our
//        // program wants to start on Monday as the first day of the week
//        int intwk = Integer.valueOf(wk);
//        if(dtx.IsSunday(jDate)){
//            if(intwk==1){
//                intwk=52;
//                wk = String.valueOf(intwk);
//            }
//            else{
//                intwk--;
//                wk = String.valueOf(intwk);
//            }
//        }
//        
//        String ieDEvalidate = ValidateIEdataEntry();
//        if(!ieDEvalidate.equals("FAIL")){
//            // data entry is good to use
//            cat = cmbIEcategory.getSelectedItem().toString();
//            name = txtIEdescription.getText();
//            amt = Integer.valueOf(txtIEamount.getText());
//            
//            if(rdoIEincome.isSelected()){
//                type = "income";
//            }
//            if(rdoIEexpense.isSelected()){
//                type = "bill";
//                amt = amt*-1;
//                budg = budg*-1;
//            }
//            if(rdoIEunplanned.isSelected()){
//                type = "unplanned";
//                amt = amt*-1;
//                budg = budg*-1;
//            }
//            
//            if(action.equals("ADD")){
//                int xID = sqlite.AddTransaction(iDate8, day, mon, yr, wk, 
//                                            type, cat, name, amt, false);
//            }
//            else {
//                sqlite.UpdateTran(id, iDate8, cat, name, type, amt);
//            }             
//        }
//        else {
//            DisplayIEmessage("Check your data entry cause something ain't right");
//            return;
//        }   
//    }
    
    // INCOME EXPENSE
    // adds income and expenses
    private void btnIEaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIEaddActionPerformed

        String type = "";
        String validDE = ValidateIEdataEntry();
        if(validDE.equals("PASS")){
            String action = "ADD";
            int id = 0;
            String userDate8 = txtIEdate.getText();
            
            if(rdoIEincome.isSelected()){
                type = "income";
            }
            if(rdoIEexpense.isSelected()){
                type = "bill";
            }
            if(rdoIEunplanned.isSelected()){
                type = "unplanned";
            }
            //String type = cmbcbType.getSelectedItem().toString();
            String cat = cmbIEcategory.getSelectedItem().toString();
            String name = txtIEdescription.getText();
            int amt = Integer.valueOf(txtIEamount.getText());
            boolean cleared = false;
            
            String success = AddandUpdateTransaction(action, id, userDate8,  
                                   type, cat, name, 
                                   amt, cleared);
            if(success.equals("FAIL")){
                DisplayCBmessage("Big time add error, fyl");
            }
            //ClearIEdataEntry();
            //IEmonTracker = 0;
            int ieFOM = dtx.getIntFOM(IEmonTracker);
            int ieEOM = dtx.getIntEOM(IEmonTracker);
            ListIncExpByDate(ieFOM, ieEOM);
            RefreshAnalytics(ieFOM, ieEOM);
            if (!chkIErepeat.isSelected()){
                ClearIEdataEntry();
            }
            else{
                txtIEdate.setText("");
            }
        }
        else{
            DisplayCBmessage("Check add data entry. Something not right");
        }
    }//GEN-LAST:event_btnIEaddActionPerformed
    
    // BUDGET
    // returns expenses to analyze
    private void ExpenseSummaryByDate(int dateStart, int dateEnd){
        expModel.setRowCount(0);
        ArrayList<ExpSummary> expList = sqlite.getExpSummaryByDate(dateStart, dateEnd);
        Object rowData[] = new Object[5];
        for(int i = 0; i < expList.size(); i++)
        {
            rowData[0] = expList.get(i).GetExpName();
            rowData[1] = expList.get(i).GetExpAmt();
            rowData[2] = expList.get(i).GetAnnAmt();
            rowData[3] = expList.get(i).GetSaveTen();
            rowData[4] = expList.get(i).GetSaveTenTot();
            expModel.addRow(rowData); 
        }
    }
    
    // Ledger
    // loads checkbook transactions
    private void GetLedgerTransByDate(int dateStart, int dateEnd){

      ledgerModel.setRowCount(0);
      
      ArrayList<TransactionLong> theLedger = sqlite.GetLedgerByDate(dateStart, dateEnd);
      Object rowData[] = new Object[7];
      String newDate = "";
      String oldDate = "";
      
      for(int i = 0; i < theLedger.size(); i++)
      {
        rowData[0] = theLedger.get(i).getID();
        rowData[1] = theLedger.get(i).getCleared();
        
        String sdate8 = String.valueOf(theLedger.get(i).getDate());
        String smon = sdate8.substring(4, 6);
        String sday = sdate8.substring(6);
        String shortDate = smon + "/" + sday;
        newDate = shortDate;
        if(newDate.equals(oldDate)){
            rowData[2] = "";
        } else {
            rowData[2] = newDate;
            oldDate = newDate;
        }
        //rowData[2] = shortDate;
        
        rowData[3] = theLedger.get(i).getName();
        rowData[4] = theLedger.get(i).getCategory();
        rowData[5] = theLedger.get(i).getAmount();
        rowData[6] = theLedger.get(i).getBalance();
        
        ledgerModel.addRow(rowData); 
      }
      tblCheckbook.getColumnModel().getColumn(0).setWidth(0);
      tblCheckbook.getColumnModel().getColumn(0).setMinWidth(0);
      tblCheckbook.getColumnModel().getColumn(0).setMaxWidth(0); 
        
    }
    
    // INCOME EXPENSE
    // lists income, bills, unplanned expenses in table
    private void ListIncExpByDate(int dateStart, int dateEnd){
      
      model.setRowCount(0);
      
      //ArrayList<TransactionShort> ledgerList = sqlite.getTransactionsByDate(dateStart, dateEnd);
      ArrayList<TransactionLong> ledgerList = sqlite.GetLedgerByDate(dateStart, dateEnd);
      Object rowData[] = new Object[6];
      for(int i = 0; i < ledgerList.size(); i++)
      {
        String type = ledgerList.get(i).getType();
        if(!type.equals("budget")){
            rowData[0] = ledgerList.get(i).getID();
        
            String sdate8 = String.valueOf(ledgerList.get(i).getDate());
            String smon = sdate8.substring(4, 6);
            String sday = sdate8.substring(6);
            String shortDate = smon + "/" + sday;
        
            rowData[1] = shortDate;
            rowData[2] = ledgerList.get(i).getName();
            rowData[3] = ledgerList.get(i).getCategory();
            rowData[4] = ledgerList.get(i).getAmount();
            rowData[5] = ledgerList.get(i).getBalance();
        
            model.addRow(rowData); 
        }
      }
      // do not need to see the ID in the table
      tblLedger.getColumnModel().getColumn(0).setWidth(0);
      tblLedger.getColumnModel().getColumn(0).setMinWidth(0);
      tblLedger.getColumnModel().getColumn(0).setMaxWidth(0);
      // do not need to see the balance in the table, but need for summary
      tblLedger.getColumnModel().getColumn(5).setWidth(0);
      tblLedger.getColumnModel().getColumn(5).setMinWidth(0);
      tblLedger.getColumnModel().getColumn(5).setMaxWidth(0);
    }
    
    // BUDGET
    // updates budget summary data
    private void RefreshBudget(int dateStart, int dateEnd){
        
        int aSize = 0;
        // budget month displayed
        lblBudgetMonth.setText(dtx.getMonthDesc(IEmonTracker));
        
        // available for budget
        int income = sqlite.getTypeSum(dateStart, dateEnd, "income");
        int bill = sqlite.getTypeSum(dateStart, dateEnd, "bill");
        int unplanned = sqlite.getTypeSum(dateStart, dateEnd, "unplanned");
        int budget = income + bill + unplanned;
        lblBudgetAvail.setText("$" + String.valueOf(budget));
        
        // how many weeks are in the month
        // week logic
        int toSpend = 0;
        if(budget != 0){
            String sdate8 = String.valueOf(dateStart);
            String syr =  sdate8.substring(0,4);
            String smon = sdate8.substring(4,6);
            int iyr = Integer.valueOf(syr);
            int imon = Integer.valueOf(smon);
            Integer[] array = dtx.getWeeksInMonth(imon, iyr);
            aSize = array.length;
            toSpend = budget/aSize;
            lblBudgetToSpend.setText("$" + String.valueOf(toSpend));
            lblBudgetWeekNum.setText(" ... " + lblBudgetMonth.getText() + 
                                " has " + String.valueOf(aSize) + " weeks" +
                                " so plan accordingly");
        }
        else {
            lblBudgetToSpend.setText("None");
        }
        
//        int year = 2014;
//        System.out.println(":::: YEAR " + year + " ::::");
//        for (int j = 1; j <= 12; j++) {
//            Integer[] array = dtx.getWeeksInMonth(j, year);
//            System.out.print("For month : " + j);
//            System.out.print(" [ ");
//            for (int i = 0; i < array.length; i++) {
//                System.out.print(array[i] + " ");
//            }
//            System.out.print("]");
//            System.out.println();
//        }
//        
        
        // load Expense summary
        ExpenseSummaryByDate(dateStart, dateEnd);
        
        // weekly budgeted amounts
        Budget budg = sqlite.GetBudget();
        txtBudgetSave.setText(String.valueOf(budg.GetBudgetSave()));
        txtBudgetCash.setText(String.valueOf(budg.GetBudgetCash()));
        txtBudgetTranp.setText(String.valueOf(budg.GetBudgetTransp()));
        txtBudgetGroc.setText(String.valueOf(budg.GetBudgetGroc()));
        txtBudgetDine.setText(String.valueOf(budg.GetBudgetDine()));
        txtBudgetUnplan.setText(String.valueOf(budg.GetBudgetUnplan()));
        if(toSpend>0){
        int amtRemain = toSpend - budg.GetBudgetSave() - budg.GetBudgetCash()
                - budg.GetBudgetTransp() - budg.GetBudgetGroc()
                - budg.GetBudgetDine() - budg.GetBudgetUnplan();
        int amtBudgeted = budg.GetBudgetSave() + budg.GetBudgetCash()
                + budg.GetBudgetTransp() + budg.GetBudgetGroc()
                + budg.GetBudgetDine() + budg.GetBudgetUnplan();
        lblBudgetTot.setText("$" + String.valueOf(amtBudgeted));
        }
        else{
        lblBudgetTot.setText("None");
        }
        
        // Budgeted monthly totals
        lblBudgetAct.setText("$0");
        int monSave = budg.GetBudgetSave() * aSize;
        int monCash = budg.GetBudgetCash() * aSize;
        int monTrav = budg.GetBudgetTransp() * aSize;
        int monGroc = budg.GetBudgetGroc() * aSize;
        int monDine = budg.GetBudgetDine() * aSize;
        int monUnplan = budg.GetBudgetUnplan() * aSize;
        lblSaveThisWk.setText("$" + String.valueOf(monSave));
        lblCashThisWk.setText("$" + String.valueOf(monCash));
        lblTravThisWk.setText("$" + String.valueOf(monTrav));
        lblGrocThisWk.setText("$" + String.valueOf(monGroc));
        lblDineThisWk.setText("$" + String.valueOf(monDine));
        lblUnplanThisWk.setText("$" + String.valueOf(monUnplan));
        
        int budgMonTot = monSave + monCash + monTrav + monGroc + monDine + monUnplan;
        lblBudgetAct.setText("$" + String.valueOf(budgMonTot));
        
        
        // Actual monthly budget totals
        lblBudgetMon.setText("0");
        Budget budgMon = sqlite.GetBudgetTotals(dateStart, dateEnd);
        int save = budgMon.GetBudgetSave();
        int cash = budgMon.GetBudgetCash();
        int trav = budgMon.GetBudgetTransp();
        int groc = budgMon.GetBudgetGroc();
        int dine = budgMon.GetBudgetDine();
        int unplan = budgMon.GetBudgetUnplan();
        
        lblSaveThisMon.setText("$" + String.valueOf(save));
        lblCashThisMon.setText("$" + String.valueOf(cash));
        lblTravThisMon.setText("$" + String.valueOf(trav));
        lblGrocThisMon.setText("$" + String.valueOf(groc));
        lblDineThisMon.setText("$" + String.valueOf(dine));
        lblUnplanThisMon.setText("$" + String.valueOf(unplan));
        
        int ActMonTot = budgMon.GetBudgetCash() + budgMon.GetBudgetDine() +
                budgMon.GetBudgetGroc() + budgMon.GetBudgetSave() + 
                budgMon.GetBudgetTransp() + budgMon.GetBudgetUnplan();
        lblBudgetMon.setText("$" + String.valueOf(ActMonTot));
        
        // Percentage of Budget to Actual
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        if(save>0 && monSave>0){
            double savePerc = ((double)save/(double)monSave)*100;
            lblSavePerc.setText(df.format(savePerc) + "%");
        } else { lblSavePerc.setText("NA");}
        if(cash>0 && monCash>0){
            double cashPerc = ((double)cash/(double)monCash)*100;
            lblCashPerc.setText(df.format(cashPerc) + "%");
        } else { lblCashPerc.setText("NA");}
        if (trav>0 && monTrav>0){
            double travPerc = ((double)trav/(double)monTrav)*100;
            lblTravPerc.setText(df.format(travPerc) + "%");
        } else { lblTravPerc.setText("NA");}
        if (groc>0 && monGroc>0){
            double grocPerc = ((double)groc/(double)monGroc)*100;  
            lblGrocPerc.setText(df.format(grocPerc) + "%");
        } else { lblGrocPerc.setText("NA");}
        if (dine>0 && monDine>0){
            double dinePerc = ((double)dine/(double)monDine)*100;
            lblDinePerc.setText(df.format(dinePerc) + "%");
        } else { lblDinePerc.setText("NA");}
        if (unplan>0 && monUnplan>0){
            double unplanPerc = ((double)unplan/(double)monUnplan)*100;
            lblUnplanPerc.setText(df.format(unplanPerc) + "%");
        } else { lblUnplanPerc.setText("NA");}
        
        if(ActMonTot>0 && budgMonTot>0){
            double TotPerc = ((double) ActMonTot / (double) budgMonTot)*100; 
            lblTotPerc.setText(df.format(TotPerc) + "%");
        } else { lblTotPerc.setText("NA");}
        
    }
    
    // CHECKBOOK
    // Update Starting Balance
    private void CheckBookBalance(){
        int row = tblCheckbook.getRowCount() - 1;
        if(row>-1){
            String sAmt = String.valueOf(ledgerModel.getValueAt(0,5));
            int amt = Integer.valueOf(sAmt);
            if(amt<0){ amt = amt*-1;}
            String sBal = String.valueOf(ledgerModel.getValueAt(0, 6));
            int bal = Integer.valueOf(sBal);
            int tot = amt + bal;
            lblcbStartBal.setText(String.valueOf(tot));
        }
    }
    
    // INCOME EXPENSES
    // updates the last panel of summary data specific to 
    private void RefreshAnalytics(int dateStart, int dateEnd){
        String sAmt = ""; String sBal = ""; 
        int bal = 0; int tot = 0; int amt = 0;
        int row = (tblLedger.getRowCount() -1);
        if(row>-1){
           //Analytics balances
           sAmt = String.valueOf(model.getValueAt(0, 4));
           amt = Integer.valueOf(sAmt);
           if(amt<0){
               amt = amt*-1;
               sBal = String.valueOf(model.getValueAt(0, 5));
               bal = Integer.valueOf(sBal);
               tot = bal + amt;
           }
           lblIEendingBal.setText("$" + String.valueOf(model.getValueAt(row, 5)));
           lblIEstartingBal.setText("$" + String.valueOf(tot));
        }
        else{
            lblIEendingBal.setText("0");
            lblIEstartingBal.setText("0");
        }
        
        lblIEmonth.setText(dtx.getMonthDesc(IEmonTracker));

        int income = sqlite.getTypeSum(dateStart, dateEnd, "income");
        lblIEmaxIn.setText("$" + String.valueOf(income));
        
        int bill = sqlite.getTypeSum(dateStart, dateEnd, "bill")*-1;
        lblIEmaxBill.setText("$" + String.valueOf(bill));
        
        int unplanned = sqlite.getTypeSum(dateStart, dateEnd, "unplanned")*-1;
        lblIEunplanned.setText("$" + String.valueOf(unplanned));
        
        int budget = income - bill - unplanned;
        lblIEtoSpend.setText("$" + String.valueOf(budget));
        
        if((bill + unplanned)>0 && (income>0)){
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        double billPercent = (((double)bill + (double)unplanned)/income)*100.0;
        String x = df.format(billPercent);
        lblIEpercent.setText(String.valueOf(x) + "%");
        }
        else if(income==0){
            lblIEpercent.setText("100%");
        }
        else{
            lblIEpercent.setText("0%");
        }
        
        String html = "<html>Remember these are bills and" +
                      "<br>expenes and not the full " +
                      "<br>picture. Budget setup is next";
        lblIEsumMessage.setText(html);
        
    }
    
    // GENERAL
    // adds a 0 to make a String 01 versus 1
    public static String leftPad(int n, int padding) {
        return String.format("%0" + padding + "d", n);
    }
    
    private void txtIEamountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIEamountKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIEamountKeyPressed

    // INCOME EXPENSE
    // only allow interges in these fields
    private void txtIEamountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIEamountKeyTyped
        // Code to only allow integers in amount fields
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume();
        }
    }//GEN-LAST:event_txtIEamountKeyTyped

    // INCOME EXPENSE
    private void rdoIEexpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoIEexpenseActionPerformed
        rdoIEincome.setSelected(false);
        rdoIEunplanned.setSelected(false);
    }//GEN-LAST:event_rdoIEexpenseActionPerformed
    
    // INCOME EXPENSE
    private void rdoIEincomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoIEincomeActionPerformed
        rdoIEexpense.setSelected(false);
        rdoIEunplanned.setSelected(false);
    }//GEN-LAST:event_rdoIEincomeActionPerformed

    // INCOME EXPENSE
    private void chkIErepeatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIErepeatActionPerformed
        if(!chkIErepeat.isSelected()){
            ClearIEdataEntry();
        }
    }//GEN-LAST:event_chkIErepeatActionPerformed

    // INCOME EXPENSE
    // handles interaction with the bills and income table
    // loads data entry when you click it
    private void tblLedgerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLedgerMouseClicked
        
        int i = tblLedger.getSelectedRow();
        
        String ids = String.valueOf(model.getValueAt(i, 0));
        int id = Integer.valueOf(ids);
        
        TransactionLong xTran = sqlite.GetTransaction(id);
        
        lblID.setText(String.valueOf(xTran.getID()));
        
        String s_type = xTran.getType();
        if (s_type.equals("bill")){
            rdoIEexpense.setSelected(true);
            rdoIEincome.setSelected(false);
            rdoIEunplanned.setSelected(false);
        }
        else if(s_type.equals("income")){
            rdoIEincome.setSelected(true);
            rdoIEexpense.setSelected(false);
            rdoIEunplanned.setSelected(false);
        }
        else if(s_type.equals("unplanned")){
            rdoIEunplanned.setSelected(true);
            rdoIEincome.setSelected(false);
            rdoIEexpense.setSelected(false);
        }
        else{
            rdoIEincome.setSelected(false);
            rdoIEexpense.setSelected(false);
            rdoIEunplanned.setSelected(false);
        }
        
        String s_amt = String.valueOf(xTran.getAmount());
        int amt = Integer.valueOf(s_amt);    
        if(amt < 0){
            amt = amt * -1;
        }
        
        txtIEamount.setText(String.valueOf(amt));

        txtIEdescription.setText(xTran.getName());
        cmbIEcategory.setSelectedItem(xTran.getCategory());
        
        int yr_i = Integer.valueOf(xTran.getYr()) - 2000;
        String yr_s = String.valueOf(yr_i);
        String mdyDate = xTran.getMon()+ "/" + xTran.getDay() + "/" + yr_s;
        txtIEdate.setText(mdyDate);
               
    }//GEN-LAST:event_tblLedgerMouseClicked

    // INCOME EXPENSE
    // delete a transaction
    private void btnIEdelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIEdelActionPerformed
        //make sure they have the DE screen filled befor deleting
        if(!lblID.getText().equals("aaa")){
            int delID = Integer.valueOf(lblID.getText());
            sqlite.deleteTran(delID);
            lblID.setText("");
            this.ClearIEdataEntry();
            
            //IEmonTracker = 0;
            int ieFOM = dtx.getIntFOM(IEmonTracker);
            int ieEOM = dtx.getIntEOM(IEmonTracker);
            ListIncExpByDate(ieFOM, ieEOM);
            RefreshAnalytics(ieFOM, ieEOM);
        }
        else{
            DisplayIEmessage("Select something to delete instead of trying to break the program");
        }
     
    }//GEN-LAST:event_btnIEdelActionPerformed

    // INCOME EXPENSE
    // update a transaction
    private void btnIEupdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIEupdActionPerformed
        String type = "";
        String validDE = ValidateIEdataEntry();
        if(validDE.equals("PASS")){
            String action = "UPD";
            int id = Integer.valueOf(lblID.getText());
            String userDate8 = txtIEdate.getText();
            if(rdoIEincome.isSelected()){
                type = "income";
            }
            if(rdoIEexpense.isSelected()){
                type = "bill";
            }
            if(rdoIEunplanned.isSelected()){
                type = "unplanned";
            }
            //String type = cmbcbType.getSelectedItem().toString();
            String cat = cmbIEcategory.getSelectedItem().toString();
            String name = txtIEdescription.getText();
            int amt = Integer.valueOf(txtIEamount.getText());
            boolean cleared = false;
            
            String success = AddandUpdateTransaction(action, id, userDate8,  
                                   type, cat, name, 
                                   amt, cleared);
            if(success.equals("FAIL")){
                DisplayCBmessage("Big time update error, fyl");
            }
            //ClearIEdataEntry();
            //IEmonTracker = 0;
            int ieFOM = dtx.getIntFOM(IEmonTracker);
            int ieEOM = dtx.getIntEOM(IEmonTracker);
            ListIncExpByDate(ieFOM, ieEOM);
            RefreshAnalytics(ieFOM, ieEOM);
            if (!chkIErepeat.isSelected()){
                ClearIEdataEntry();
            }
            else{
                txtIEdate.setText("");
            }
        }
        else{
            DisplayCBmessage("Check update data entry. Somethings not right");
        }
    }//GEN-LAST:event_btnIEupdActionPerformed

    // INCOME EXPENSE
    // move forward a month
    private void btnIEforwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIEforwardActionPerformed
        IEmonTracker++;
        int ieFOM = dtx.getIntFOM(IEmonTracker);
        int ieEOM = dtx.getIntEOM(IEmonTracker);
        ListIncExpByDate(ieFOM, ieEOM);
        RefreshAnalytics(ieFOM, ieEOM);
    }//GEN-LAST:event_btnIEforwardActionPerformed

    // INCOME EXPENSE
    // move back a moth
    private void btnIEbackwardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIEbackwardsActionPerformed
        IEmonTracker--;
        int ieFOM = dtx.getIntFOM(IEmonTracker);
        int ieEOM = dtx.getIntEOM(IEmonTracker);
        ListIncExpByDate(ieFOM, ieEOM); 
        RefreshAnalytics(ieFOM, ieEOM);
    }//GEN-LAST:event_btnIEbackwardsActionPerformed

    // INCOME EXPENSE
    // general UI management
    private void rdoIEunplannedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoIEunplannedActionPerformed
        rdoIEexpense.setSelected(false);
        rdoIEincome.setSelected(false);
    }//GEN-LAST:event_rdoIEunplannedActionPerformed

    // BUDGET
    // update the budget tracking panel and refresh results for the period  
    private void btnBudgetAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBudgetAddActionPerformed
        int addupd = sqlite.IsBudgetSetup();
        if (addupd==0){
            //add mode
            ValidateBudget();
            sqlite.AddBudget(201802, Integer.valueOf(txtBudgetSave.getText()), 
                    Integer.valueOf(txtBudgetCash.getText()), 
                    Integer.valueOf(txtBudgetTranp.getText()),
                    Integer.valueOf(txtBudgetGroc.getText()), 
                    Integer.valueOf(txtBudgetDine.getText()), 
                    Integer.valueOf(txtBudgetUnplan.getText()));
        }
        else{
            //update mode
            ValidateBudget();
            sqlite.UpdBudget(201802, Integer.valueOf(txtBudgetSave.getText()), 
                    Integer.valueOf(txtBudgetCash.getText()), 
                    Integer.valueOf(txtBudgetTranp.getText()),
                    Integer.valueOf(txtBudgetGroc.getText()), 
                    Integer.valueOf(txtBudgetDine.getText()), 
                    Integer.valueOf(txtBudgetUnplan.getText()));      
        }
        
        int ieFOM = dtx.getIntFOM(0);
        int ieEOM = dtx.getIntEOM(0);
        RefreshBudget(ieFOM, ieEOM);
    }//GEN-LAST:event_btnBudgetAddActionPerformed

    // BUDGET
    // validate data entry of budget items
    private void ValidateBudget(){
        if(txtBudgetSave.getText().equals("")){
            txtBudgetSave.setText("0");}    
        if(txtBudgetCash.getText().equals("")){
            txtBudgetCash.setText("0");}
        if(txtBudgetTranp.getText().equals("")){
            txtBudgetTranp.setText("0");}
        if(txtBudgetGroc.getText().equals("")){
            txtBudgetGroc.setText("0");}
        if(txtBudgetDine.getText().equals("")){
            txtBudgetDine.setText("0");}
        if(txtBudgetUnplan.getText().equals("")){
            txtBudgetUnplan.setText("0");}
    }

    private void txtBudgetSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBudgetSaveKeyPressed

    }//GEN-LAST:event_txtBudgetSaveKeyPressed
    
    // BUDGET
    // Code to only allow integers in amount fields
    private void txtBudgetSaveKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBudgetSaveKeyTyped
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume(); 
        }
    }//GEN-LAST:event_txtBudgetSaveKeyTyped
    
    // BUDGET
    // Code to only allow integers in amount fields
    private void txtBudgetCashKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBudgetCashKeyTyped
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume(); 
        }
    }//GEN-LAST:event_txtBudgetCashKeyTyped
    
    // BUDGET
    // Code to only allow integers in amount fields
    private void txtBudgetTranpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBudgetTranpKeyTyped
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume(); 
        }
    }//GEN-LAST:event_txtBudgetTranpKeyTyped
    
    // BUDGET
    // Code to only allow integers in amount fields
    private void txtBudgetGrocKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBudgetGrocKeyTyped
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume(); 
        }
    }//GEN-LAST:event_txtBudgetGrocKeyTyped
    
    // BUDGET
    // Code to only allow integers in amount fields
    private void txtBudgetDineKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBudgetDineKeyTyped
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume(); 
        }
    }//GEN-LAST:event_txtBudgetDineKeyTyped
    
    // BUDGET
    // Code to only allow integers in amount fields
    private void txtBudgetUnplanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBudgetUnplanKeyTyped
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume(); 
        }
    }//GEN-LAST:event_txtBudgetUnplanKeyTyped

    // AI 
    // budget item quick add
    private void btnAIbudgAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAIbudgAddActionPerformed
        
        // values not passed in budget add, but needed in ledger
        String type = "budget";
        String frq = "not used";
        int oldBudg = 0;
        Date jDate = null;
        String sDate8, day, mon, yr, wk, cat, name;
        String aiDate = txtAIbudgDate.getText();
        int iDate8 = 0; int amt = 0;
        
        // date is good to use
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        try
        {
            jDate = sdf.parse(aiDate);
        }
        catch(ParseException e) {
            DisplayAImessage("Enter date like 04/15/18");
            return;
        }
            
        day = new SimpleDateFormat("dd").format(jDate);
        mon = new SimpleDateFormat("MM").format(jDate);
        yr = new SimpleDateFormat("yyyy").format(jDate);
        wk = new SimpleDateFormat("w").format(jDate);
        sDate8 = yr + mon + day;
        iDate8 = Integer.parseInt(sDate8);

        // is today sunday?  if it is we need to subtract a week since our
        // program wants to start on Monday as the first day of the week
        int intwk = Integer.valueOf(wk);
        if(dtx.IsSunday(jDate)){
            if(intwk==1){
                intwk=52;
                wk = String.valueOf(intwk);
            }
            else{
                intwk--;
                wk = String.valueOf(intwk);
            }
        }
        
        if(txtAIbudgAmt.getText().equals("")){
            DisplayAImessage("Enter an amount at least");
            return;}
        if(cmbAIbudgItem.getSelectedItem() == null
                || cmbAIbudgItem.getSelectedItem().toString().isEmpty()){
            DisplayAImessage("Enter an category sucker");
            return;
        }
        amt = Integer.valueOf(txtAIbudgAmt.getText())*-1;
        cat = cmbAIbudgItem.getSelectedItem().toString();
        name = txtAIbudgName.getText();
        
        int y = sqlite.AddTransaction(iDate8, day, mon, yr,wk, type, cat, 
                            name, amt, false);
        
        if(y==0){
            // add failed
            DisplayAImessage("Error adding budget item, contact support");
        }
        else{
            // clear data entry
            txtAIbudgDate.setText(aiDate);
            cmbAIbudgItem.setSelectedIndex(-1);
            txtAIbudgName.setText("");
            txtAIbudgAmt.setText("");         
        }
            
    }//GEN-LAST:event_btnAIbudgAddActionPerformed

    // AI
    // Only allow integers to be typed
    private void txtAIbudgAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAIbudgAmtKeyTyped
            // Code to only allow integers in amount fields
        char vchar = evt.getKeyChar();
        if(!Character.isDigit(vchar)
            || (vchar == KeyEvent.VK_BACKSPACE)
            || (vchar == KeyEvent.VK_DELETE)){
            evt.consume();
        }
    }//GEN-LAST:event_txtAIbudgAmtKeyTyped

    // Checkbook
    // View all records
    private void rdoLedgerAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoLedgerAllActionPerformed
        rdoLedgerMonth.setSelected(false);
        rdoLedgerWeek.setSelected(false);
        rdoLedgerActive.setSelected(false);
        GetLedgerTransByDate(inTheBeginning, inTheYear2525);
        CheckBookBalance();
    }//GEN-LAST:event_rdoLedgerAllActionPerformed

    // Checkbook
    // View month records
    private void rdoLedgerMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoLedgerMonthActionPerformed
        rdoLedgerAll.setSelected(false);
        rdoLedgerWeek.setSelected(false);
        rdoLedgerActive.setSelected(false);
        int cbFOM = dtx.getIntFOM(IEmonTracker);    //monTracker should be global for the entire application
        int cbEOM = dtx.getIntEOM(IEmonTracker);
        GetLedgerTransByDate(cbFOM, cbEOM);
        CheckBookBalance();
    }//GEN-LAST:event_rdoLedgerMonthActionPerformed
    // Checkbook
    // View week record
    private void rdoLedgerWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoLedgerWeekActionPerformed
        rdoLedgerAll.setSelected(false);
        rdoLedgerMonth.setSelected(false);
        rdoLedgerActive.setSelected(false);
        weekTracker = 7;
        int cbFOW = dtx.getIntFOW(weekTracker);
        int cbEOW = dtx.getIntEOW(weekTracker);
        GetLedgerTransByDate(cbFOW, cbEOW);
        CheckBookBalance();
    }//GEN-LAST:event_rdoLedgerWeekActionPerformed
    // Checkbook
    // View all non reconciled
    private void rdoLedgerActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoLedgerActiveActionPerformed
        rdoLedgerAll.setSelected(false);
        rdoLedgerWeek.setSelected(false);
        rdoLedgerMonth.setSelected(false);
        // this one is more complicated as we need a new SQL
        
    }//GEN-LAST:event_rdoLedgerActiveActionPerformed
    // Checkbook
    // Move back a period, based on month or week radio selection
    private void btnLedgerBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLedgerBackActionPerformed
        if(rdoLedgerMonth.isSelected()){
            IEmonTracker--;
            int cbFOM = dtx.getIntFOM(IEmonTracker);    //monTracker should be global for the entire application
            int cbEOM = dtx.getIntEOM(IEmonTracker);
            GetLedgerTransByDate(cbFOM, cbEOM);
            CheckBookBalance();
        }
        if(rdoLedgerWeek.isSelected()){
            weekTracker = weekTracker - 7;
            int cbFOW = dtx.getIntFOW(weekTracker);
            int cbEOW = dtx.getIntEOW(weekTracker);
            GetLedgerTransByDate(cbFOW, cbEOW);
            CheckBookBalance();
        }
        
    }//GEN-LAST:event_btnLedgerBackActionPerformed
    // Checkbook
    // Move forward based on month or week radio selection
    private void btnLedgerForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLedgerForwardActionPerformed
        if(rdoLedgerMonth.isSelected()){
            IEmonTracker++;
            int cbFOM = dtx.getIntFOM(IEmonTracker);    //monTracker should be global for the entire application
            int cbEOM = dtx.getIntEOM(IEmonTracker);
            GetLedgerTransByDate(cbFOM, cbEOM);
            CheckBookBalance();
        }
        if(rdoLedgerWeek.isSelected()){
            weekTracker = weekTracker + 7;
            int cbFOW = dtx.getIntFOW(weekTracker);
            int cbEOW = dtx.getIntEOW(weekTracker);
            GetLedgerTransByDate(cbFOW, cbEOW);
            CheckBookBalance();
        }

    }//GEN-LAST:event_btnLedgerForwardActionPerformed

    private void txtcbAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcbAmtKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcbAmtKeyTyped

    private void txtcbAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcbAmtKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcbAmtKeyPressed

    // Checkbook
    // add transactions in checkbook screen
    private void btncbAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncbAddActionPerformed
        String validDE = ValidateCBdataEntry();
        if(validDE.equals("PASS")){
            String action = "ADD";
            int id = 0;
            String userDate8 = txtcbDate.getText();
            String type = cmbcbType.getSelectedItem().toString();
            String cat = cmbcbCategory.getSelectedItem().toString();
            String name = txtcbName.getText();
            int amt = Integer.valueOf(txtcbAmt.getText());
            boolean cleared = false;
            
            String success = AddandUpdateTransaction(action, id, userDate8,  
                                   type, cat, name, 
                                   amt, cleared);
            if(success.equals("FAIL")){
                DisplayCBmessage("Big time add error, fyl");
            }
            ClearCBdataEntry();
            int cbFOM = dtx.getIntFOM(IEmonTracker);
            int cbEOM = dtx.getIntEOM(IEmonTracker);
            GetLedgerTransByDate(cbFOM, cbEOM);
            CheckBookBalance();
        }
        else{
            DisplayCBmessage("Check add data entry. Something not right");
        }
    }//GEN-LAST:event_btncbAddActionPerformed
    // Checkbook
    // update transactions in checkbook screen
    private void btncbUpdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncbUpdActionPerformed
        String validDE = ValidateCBdataEntry();
        if(validDE.equals("PASS")){
            String action = "UPD";
            int id = Integer.valueOf(lblcbID.getText());
            String userDate8 = txtcbDate.getText();
            String type = cmbcbType.getSelectedItem().toString();
            String cat = cmbcbCategory.getSelectedItem().toString();
            String name = txtcbName.getText();
            int amt = Integer.valueOf(txtcbAmt.getText());
            boolean cleared = false;
            
            String success = AddandUpdateTransaction(action, id, userDate8,  
                                   type, cat, name, 
                                   amt, cleared);
            if(success.equals("FAIL")){
                DisplayCBmessage("Big time update error, fyl");
            }
            ClearCBdataEntry();
            int cbFOM = dtx.getIntFOM(IEmonTracker);
            int cbEOM = dtx.getIntEOM(IEmonTracker);
            GetLedgerTransByDate(cbFOM, cbEOM);
            CheckBookBalance();
        }
        else{
            DisplayCBmessage("Check update data entry. Somethings not right");
        }
    }//GEN-LAST:event_btncbUpdActionPerformed
    // Checkbook
    // delete a transaction
    private void btncbDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncbDelActionPerformed

         //make sure they have the DE screen filled befor deleting
        if(!lblcbID.getText().equals("aaa")){
            int delID = Integer.valueOf(lblcbID.getText());
            sqlite.deleteTran(delID);
            lblcbID.setText("");
            this.ClearCBdataEntry();
            
            int cbFOM = dtx.getIntFOM(IEmonTracker);
            int cbEOM = dtx.getIntEOM(IEmonTracker);
            GetLedgerTransByDate(cbFOM, cbEOM);
            CheckBookBalance();
        }
        else{
            DisplayIEmessage("Select something to delete instead of trying to break the program");
        }
    }//GEN-LAST:event_btncbDelActionPerformed
    // Checkbook
    // fill DE when user clicks on checkbook table
    private void tblCheckbookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCheckbookMouseClicked
        int i = tblCheckbook.getSelectedRow();
        
        String ids = String.valueOf(ledgerModel.getValueAt(i, 0));
        int id = Integer.valueOf(ids);
        
        String stest = String.valueOf(ledgerModel.getValueAt(i, 1));
        boolean test = Boolean.valueOf(stest);
        if(test){
            lblisCleared.setText("Cleared");
        }
        else{
            lblisCleared.setText("Uncleared");
        }
        
        TransactionLong xTran = sqlite.GetTransaction(id);
        
        lblcbID.setText(String.valueOf(xTran.getID()));
        
        String s_type = xTran.getType();
        if (s_type.equals("bill")){
            cmbcbType.setSelectedIndex(0);
        }
        else if(s_type.equals("income")){
            cmbcbType.setSelectedIndex(1);
        }
        else if(s_type.equals("unplanned")){
            cmbcbType.setSelectedIndex(2);
        }
        else if(s_type.equals("budget")){
            cmbcbType.setSelectedIndex(3);
        }
        else{
            cmbcbType.setSelectedIndex(-1);
        }
        
        String s_amt = String.valueOf(xTran.getAmount());
        int amt = Integer.valueOf(s_amt);    
        if(amt < 0){
            amt = amt * -1;
        }
        
        txtcbAmt.setText(String.valueOf(amt));

        txtcbName.setText(xTran.getName());
        cmbcbCategory.setSelectedItem(xTran.getCategory());
        
        int yr_i = Integer.valueOf(xTran.getYr()) - 2000;
        String yr_s = String.valueOf(yr_i);
        String mdyDate = xTran.getMon()+ "/" + xTran.getDay() + "/" + yr_s;
        txtcbDate.setText(mdyDate);
        
        boolean cleared = xTran.getCleared();
        if (cleared){
            lblisCleared.setText("Cleared");
        }
        else {
            lblisCleared.setText("Not Cleared");
        }
        
    }//GEN-LAST:event_tblCheckbookMouseClicked
    // CHECKBOOK
    // opens a new window and populates summary view for printing
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        PrintSummary ps = new PrintSummary();
        ps.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(fmlUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(fmlUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(fmlUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(fmlUserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new fmlUserInterface().setVisible(true);
            }
        });
    }
    
    
    // INCOME EXPENSE
    // Clears the data entry area of Income and Expense panel
    private void ClearIEdataEntry(){
        txtIEdate.setText("");
        txtIEamount.setText("");
        txtIEdescription.setText("");
        cmbIEcategory.setSelectedIndex(-1);
        rdoIEincome.setSelected(false);
        rdoIEexpense.setSelected(false);
        rdoIEunplanned.setSelected(false);
    }
    
    // Checkbook
    private void ClearCBdataEntry(){
        txtcbDate.setText("");
        txtcbAmt.setText("");
        txtcbName.setText("");
        cmbcbCategory.setSelectedIndex(-1);
        cmbcbType.setSelectedIndex(-1);
        lblisCleared.setText("");
    }
    
    // INCOME EXPENSE
    // Displays data entry error messages on Income and Expense panel
    private void DisplayIEmessage(String message){
        
        lblIEmessage.setText(message);
        Timer t = new Timer(2000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                lblIEmessage.setText(null);
            }
        });
        t.setRepeats(false);
        t.start();
        //lblIEmessage.setText(" . message center");
    }
    
    // AI
    // Displays data entry error messages on Budget panel
    private void DisplayAImessage(String message){
        
        lblAIMessage.setText(message);
        Timer t = new Timer(2000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                lblAIMessage.setText(null);
            }
        });
        t.setRepeats(false);
        t.start();
        //lblAIMessage.setText(" . message center");
    }
    
    private void DisplayCBmessage(String message){
        
        lblcbMessage.setText(message);
        Timer t = new Timer(2000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                lblcbMessage.setText(null);
            }
        });
        t.setRepeats(false);
        t.start();
        //lblcbMessage.setText(" . message center");
        
    }
    
    // INCOME EXPENSE
    // validates the DE part of the form and returns PASS or FAIL to caller
    private String ValidateIEdataEntry(){
        if(txtIEamount.getText().equals("")){
            return "FAIL";}
        else if(txtIEdescription.getText().equals("")){
            return "FAIL";}
        else if(cmbIEcategory.getSelectedItem() == null
                || cmbIEcategory.getSelectedItem().toString().isEmpty()){
            return "FAIL";
        } else if(!(rdoIEincome.isSelected() || rdoIEexpense.isSelected() ||
                rdoIEunplanned.isSelected())) {
            return "FAIL";
        }else{
            return "PASS";}
    }
    
    private String ValidateCBdataEntry(){
        if(txtcbAmt.getText().equals("")){
            return "FAIL";}
        else if(txtcbName.getText().equals("")){
            return "FAIL";}
        else if(cmbcbCategory.getSelectedItem() == null
                || cmbcbCategory.getSelectedItem().toString().isEmpty()){
            return "FAIL";
        } else if(cmbcbType.getSelectedItem() == null
                || cmbcbType.getSelectedItem().toString().isEmpty()) {
            return "FAIL";
        }else{
            return "PASS";}
    }
    

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabeln;
    private javax.swing.JButton bntRegister;
    private javax.swing.JButton btnAIbudgAdd;
    private javax.swing.JButton btnAnalytics;
    private javax.swing.JButton btnBudget;
    private javax.swing.JButton btnBudgetAdd;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnIEadd;
    private javax.swing.JButton btnIEbackwards;
    private javax.swing.JButton btnIEdel;
    private javax.swing.JButton btnIEforward;
    private javax.swing.JButton btnIEupd;
    private javax.swing.JButton btnIncomeExp;
    private javax.swing.JButton btnLedgerBack;
    private javax.swing.JButton btnLedgerForward;
    private javax.swing.JButton btncbAdd;
    private javax.swing.JButton btncbDel;
    private javax.swing.JButton btncbUpd;
    private javax.swing.JCheckBox chkIErepeat;
    private javax.swing.JComboBox<String> cmbAIbudgItem;
    private javax.swing.JComboBox<String> cmbIEcategory;
    private javax.swing.JComboBox<String> cmbcbCategory;
    private javax.swing.JComboBox<String> cmbcbType;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblAIMessage;
    private javax.swing.JLabel lblAIqAdd;
    private javax.swing.JLabel lblBudgReadMe;
    private javax.swing.JLabel lblBudgetAct;
    private javax.swing.JLabel lblBudgetAvail;
    private javax.swing.JLabel lblBudgetExpSum;
    private javax.swing.JLabel lblBudgetMessages;
    private javax.swing.JLabel lblBudgetMon;
    private javax.swing.JLabel lblBudgetMonth;
    private javax.swing.JLabel lblBudgetToSpend;
    private javax.swing.JLabel lblBudgetTot;
    private javax.swing.JLabel lblBudgetWeekNum;
    private javax.swing.JLabel lblCashPerc;
    private javax.swing.JLabel lblCashThisMon;
    private javax.swing.JLabel lblCashThisWk;
    private javax.swing.JLabel lblDinePerc;
    private javax.swing.JLabel lblDineThisMon;
    private javax.swing.JLabel lblDineThisWk;
    private javax.swing.JLabel lblFML;
    private javax.swing.JLabel lblGrocPerc;
    private javax.swing.JLabel lblGrocThisMon;
    private javax.swing.JLabel lblGrocThisWk;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblIEendingBal;
    private javax.swing.JLabel lblIEmaxBill;
    private javax.swing.JPanel lblIEmaxBills;
    private javax.swing.JLabel lblIEmaxIn;
    private javax.swing.JLabel lblIEmessage;
    private javax.swing.JLabel lblIEmonth;
    private javax.swing.JLabel lblIEpercent;
    private javax.swing.JLabel lblIEreadMe;
    private javax.swing.JLabel lblIEstartingBal;
    private javax.swing.JLabel lblIEsumMessage;
    private javax.swing.JLabel lblIEtoSpend;
    private javax.swing.JLabel lblIEunplanned;
    private javax.swing.JLabel lblSavePerc;
    private javax.swing.JLabel lblSaveThisMon;
    private javax.swing.JLabel lblSaveThisWk;
    private javax.swing.JLabel lblTotPerc;
    private javax.swing.JLabel lblTravPerc;
    private javax.swing.JLabel lblTravThisMon;
    private javax.swing.JLabel lblTravThisWk;
    private javax.swing.JLabel lblUnplanPerc;
    private javax.swing.JLabel lblUnplanThisMon;
    private javax.swing.JLabel lblUnplanThisWk;
    private javax.swing.JLabel lblaiaCash;
    private javax.swing.JLabel lblaiaDine;
    private javax.swing.JLabel lblaiaGroc;
    private javax.swing.JLabel lblaiaSave;
    private javax.swing.JLabel lblaiaTot;
    private javax.swing.JLabel lblaiaTrav;
    private javax.swing.JLabel lblaiaUnplan;
    private javax.swing.JLabel lblaibCash;
    private javax.swing.JLabel lblaibDine;
    private javax.swing.JLabel lblaibGroc;
    private javax.swing.JLabel lblaibSave;
    private javax.swing.JLabel lblaibTot;
    private javax.swing.JLabel lblaibTrav;
    private javax.swing.JLabel lblaibUnplan;
    private javax.swing.JLabel lblcbID;
    private javax.swing.JLabel lblcbInfo;
    private javax.swing.JLabel lblcbMessage;
    private javax.swing.JLabel lblcbStartBal;
    private javax.swing.JLabel lblisCleared;
    private javax.swing.JPanel pnlAnalytics;
    private javax.swing.JPanel pnlBudget;
    private javax.swing.JPanel pnlCarder;
    private javax.swing.JPanel pnlIncomeExp;
    private javax.swing.JPanel pnlLedger;
    private javax.swing.JPanel pnlReporter;
    private javax.swing.JRadioButton rdoIEexpense;
    private javax.swing.JRadioButton rdoIEincome;
    private javax.swing.JRadioButton rdoIEunplanned;
    private javax.swing.JRadioButton rdoLedgerActive;
    private javax.swing.JRadioButton rdoLedgerAll;
    private javax.swing.JRadioButton rdoLedgerMonth;
    private javax.swing.JRadioButton rdoLedgerWeek;
    private javax.swing.JTable tblCheckbook;
    private javax.swing.JTable tblExpSummary;
    private javax.swing.JTable tblLedger;
    private javax.swing.JTextField txtAIbudgAmt;
    private javax.swing.JFormattedTextField txtAIbudgDate;
    private javax.swing.JTextField txtAIbudgName;
    private javax.swing.JTextField txtBudgetCash;
    private javax.swing.JTextField txtBudgetDine;
    private javax.swing.JTextField txtBudgetGroc;
    private javax.swing.JTextField txtBudgetSave;
    private javax.swing.JTextField txtBudgetTranp;
    private javax.swing.JTextField txtBudgetUnplan;
    private javax.swing.JTextField txtIEamount;
    private javax.swing.JFormattedTextField txtIEdate;
    private javax.swing.JTextField txtIEdescription;
    private javax.swing.JTextField txtcbAmt;
    private javax.swing.JTextField txtcbBalance;
    private javax.swing.JTextField txtcbBank;
    private javax.swing.JFormattedTextField txtcbDate;
    private javax.swing.JTextField txtcbName;
    private javax.swing.JTextField txtcbNotCleared;
    // End of variables declaration//GEN-END:variables
}
